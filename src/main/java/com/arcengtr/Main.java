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
            ChartWindow chart = new ChartWindow("Outer and Inner temperature");
            chart.setVisible(true);

            ConductivityMatrixService conductivityService = new ConductivityMatrixService();
            BoundaryConditionService boundaryService = new BoundaryConditionService();
            CapacityMatrixService capacityService = new CapacityMatrixService();
            ElementService elementService = new ElementService(conductivityService, boundaryService, capacityService);

            //Path file = Path.of("src/main/resources/globalData/venus.txt");
            //Path file = Path.of("src/main/resources/globalData/Test1_4_4.txt");
            //Path file = Path.of("src/main/resources/globalData/Test2_4_4_MixGrid.txt");
            Path file = Path.of("src/main/resources/globalData/Test3_31_31_kwadrat.txt");

            GlobalDataParser parser = new GlobalDataParser();
            GlobalDataParser.ParsedData parsedData = parser.parse(file);

            GlobalData globalData = parsedData.getGlobalData();
            Grid grid = parsedData.getGrid();
            Node[] allNodes = grid.getNodes().toArray(new Node[0]);

            System.out.println("=== Global Data ===");
            System.out.println(globalData);

            int nN = globalData.getNN();
            double dt = globalData.getSimulationStepTime();
            double T_sim = globalData.getSimulationTime();

            List<Double> points = GaussData.POINTS_3;
            List<Double> weights = GaussData.WEIGHTS_3;

            double[] T_current = new double[nN];
            Arrays.fill(T_current, globalData.getInitialTemp());

            double currentTime = 0.0;
            int step = 0;

            System.out.println("\n=== Simulation ===");

            while (currentTime < T_sim) {
                step++;

                // Cleaning global matrix every iteration
                GlobalMatrix globalMatrix = GlobalMatrix.builder()
                        .H_global(new double[nN][nN])
                        .Hbc_global(new double[nN][nN])
                        .P_global(new double[nN])
                        .C_global(new double[nN][nN])
                        .build();

                for (Element element : grid.getElements()) {

                    elementService.processElement(element, allNodes, globalData, points, weights, T_current);

                    globalMatrix.addElementMatrix(element.getH(), element.getNodeId(), false);
                    globalMatrix.addElementMatrix(element.getHbc(), element.getNodeId(), true);
                    globalMatrix.addPVector(element.getP(), element.getNodeId());
                    globalMatrix.addCMatrix(element.getC(), element.getNodeId());
                }

                double[][] C_global = globalMatrix.getC_global();
                double[] P_global = globalMatrix.getP_global();
                double[][] H_global = globalMatrix.getH_global();
                double[][] Hbc_global = globalMatrix.getHbc_global();

                // C_scaled = C / dt
                double[][] C_scaled = MatrixService.multiplyMatrixByScalar(C_global, 1.0 / dt);

                // H* = H + Hbc + C/dt
                double[][] H_total = MatrixService.addMatrices(H_global, Hbc_global);
                double[][] H_star = MatrixService.addMatrices(H_total, C_scaled);

                // P* = (C/dt * T0) + P
                // T_current = T0
                double[] P_C_term = MatrixService.multiplyMatrixByVector(C_scaled, T_current);
                double[] P_star = MatrixService.addVectors(P_C_term, P_global);

                double[] T_next = LinearSolver.solveLinearSystem(H_star, P_star);

                double stepTime = Math.min(dt, T_sim - currentTime);
                currentTime += stepTime;
                T_current = T_next;

                System.out.printf("Step %d (t=%.2f): Min Temp=%.2f, Max Temp=%.2f%n",
                        step, currentTime, Arrays.stream(T_current).min().getAsDouble(), Arrays.stream(T_current).max().getAsDouble());

                if (step % 5 == 0 || currentTime >= T_sim) {
                    MatrixService.printVector(T_current);
                }


                /*if (step % 10 == 0) {
                    double tInternal = (T_current[0] + T_current[11]) / 2.0;
                    double tMiddle = (T_current[7] + T_current[18]) / 2.0;
                    double tExternal = (T_current[10] + T_current[21]) / 2.0;
                    chart.addData(currentTime, tInternal, tExternal, tMiddle);
                }

                System.out.printf("\n--- Step %d (t=%.2f) ---%n", step, currentTime);
                System.out.print("Layers Temp: [");
                for (int i = 0; i < 11; i++) {
                    double layerTemp = (T_current[i] + T_current[i + 11]) / 2.0;
                    System.out.printf("%6.1f ", layerTemp);
                }
                System.out.println("]");

                System.out.print("Progress:    |");
                for (int i = 0; i < 11; i++) {
                    double temp = (T_current[i] + T_current[i + 11]) / 2.0;
                    if (temp < 273) System.out.print("░");      // Cold
                    else if (temp < 303) System.out.print("▒"); // Hot
                    else if (temp < 305) System.out.print("█"); // Melting
                    else System.out.print("!");                 // Destroyed
                }
                System.out.println("|");*/
            }

            MeshViewer viewer = new MeshViewer(grid.getNodes(), grid.getElements());
            viewer.setVisible(true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}