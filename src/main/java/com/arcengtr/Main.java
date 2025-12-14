package com.arcengtr;

import com.arcengtr.common.*;
import com.arcengtr.parsers.GlobalDataParser;
import com.arcengtr.services.*;
import com.arcengtr.solvers.GaussEliminationLinearSolver.LinearSolver;
import com.arcengtr.solvers.gaussLegendreQuadratureSolver.GaussData;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        try {

            // Dependency Injection
            ConductivityMatrixService conductivityService = new ConductivityMatrixService();
            BoundaryConditionService boundaryService = new BoundaryConditionService();
            CapacityMatrixService capacityService = new CapacityMatrixService();
            ElementService elementService = new ElementService(conductivityService, boundaryService, capacityService);

            Path file = Path.of("src/main/resources/globalData/Test1_4_4.txt");
            // file = Path.of("src/main/resources/globalData/Test2_4_4_MixGrid.txt");
            //Path file = Path.of("src/main/resources/globalData/Test3_31_31_kwadrat.txt");
            GlobalDataParser parser = new GlobalDataParser();
            GlobalDataParser.ParsedData parsedData = parser.parse(file);

            GlobalData globalData = parsedData.getGlobalData();
            Grid grid = parsedData.getGrid();
            Node[] allNodes = grid.getNodes().toArray(new Node[0]);

            System.out.println("=== Global Data ===");
            System.out.println(globalData);

            // Liczba węzłów
            int nN = globalData.getNN();

            // wypełnienie temperatur początkowych
            double[] T0 = new double[nN];
            Arrays.fill(T0, globalData.getInitialTemp());

            // inicjalizacja tablic globalnych
            GlobalMatrix globalMatrix = GlobalMatrix.builder()
                    .H_global(new double[nN][nN])
                    .Hbc_global(new double[nN][nN])
                    .P_global(new double[nN])
                    .C_global(new double[nN][nN])
                    .build();

            List<Double> points = GaussData.POINTS_3;
            List<Double> weights = GaussData.WEIGHTS_3;

            for (Element element : grid.getElements()) {

                elementService.processElement(element, allNodes, globalData, points, weights); // Calculates H, Hbc and P

                // Aggregate into Global Matrix
                globalMatrix.addElementMatrix(element.getH(), element.getNodeId(), false); // Add H
                globalMatrix.addElementMatrix(element.getHbc(), element.getNodeId(), true); // Add Hbc
                globalMatrix.addPVector(element.getP(), element.getNodeId()); // Add P
                globalMatrix.addCMatrix(element.getC(), element.getNodeId()); //Add C

                /*System.out.println("\n--- Macierz H dla elementu ID=" + Arrays.toString(element.getNodeId()) + " ---");
                MatrixService.printMatrix(element.getH());

                System.out.println("\n--- Macierz Hbc dla elementu ID=" + Arrays.toString(element.getNodeId()) + " ---");
                MatrixService.printMatrix(element.getHbc());

                System.out.println("\n--- Wektor P dla elementu ID=" + Arrays.toString(element.getNodeId()) + " ---");
                MatrixService.printVector(element.getP());

                System.out.println("\n--- Macierz C dla elementu ID=" + Arrays.toString(element.getNodeId()) + " ---");
                MatrixService.printMatrix(element.getC());*/
            }

            double dt = globalData.getSimulationStepTime();
            double[][] C_global = globalMatrix.getC_global();
            double[] P_global = globalMatrix.getP_global();

            // C_scaled = C_global / Δt
            double[][] C_scaled = MatrixService.multiplyMatrixByScalar(C_global, 1.0 / dt);

            // H* = (H + Hbc) + C/Δt
            double[][] H_total = MatrixService.addMatrices(globalMatrix.getH_global(), globalMatrix.getHbc_global());
            double[][] H_star = MatrixService.addMatrices(H_total, C_scaled); // H* = (H + Hbc) + C/Δt

            // P* = (C/Δt) * {T0} + {P_global}
            double[] P_C_term = MatrixService.multiplyMatrixByVector(C_scaled, T0); // (C/Δt) * {T0}
            double[] P_star = MatrixService.addVectors(P_C_term, P_global); // P*

//            System.out.println("\n=== Macierz Globalna H* = H + Hbc + C/Δt ===");
//            MatrixService.printMatrix(H_star);
//
//            System.out.println("\n=== Wektor Globalny P* ===");
//            MatrixService.printVector(P_star);

//            System.out.println("=== Rozwiązanie H* * {t1} = P* ===");
//            double[] T1_result = LinearSolver.solveLinearSystem(H_star, P_star);
//            MatrixService.printVector(T1_result);

            double T_sim = globalData.getSimulationTime();

            double[] T_current = new double[nN];
            Arrays.fill(T_current, globalData.getInitialTemp());

            double currentTime = 0.0;
            int step = 0;

            System.out.println("\n=== Symulacja (T_sim = " + T_sim + ", dt = " + dt + ") ===");

            while (currentTime < T_sim) {
                step++;

                double stepTime = Math.min(dt, T_sim - currentTime);

                P_C_term = MatrixService.multiplyMatrixByVector(C_scaled, T_current);

                P_star = MatrixService.addVectors(P_C_term, P_global);

                double[] T_next = LinearSolver.solveLinearSystem(H_star, P_star);

                T_current = T_next;
                currentTime += stepTime;

                System.out.printf("\n--- d %d, t = %.4f sek. ---%n", step, currentTime);
                if (currentTime >= T_sim) {
                    System.out.println("Ostateczna temperaturaр:");
                    MatrixService.printVector(T_current);
                } else if (step % 1 == 0) {
                    System.out.println("Temperatura:");
                    MatrixService.printVector(T_current);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}