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

    /*public static void printMatrix(double[][] matrix, String title) {
        if (title != null && !title.isEmpty()) {
            System.out.println("--- " + title + " ---");
        }
        if (matrix == null) {
            System.out.println("null");
            return;
        }

        for (double[] row : matrix) {
            System.out.print("[ ");
            for (double value : row) {
                System.out.printf(Locale.US, "%10.4f ", value);
            }
            System.out.println("]");
        }
    }

    public static double[][] multiplyVectorColumnByRow(double[] v1, double[] v2) {
        int n = v1.length;
        int m = v2.length;
        double[][] result = new double[n][m];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                result[i][j] = v1[i] * v2[j];
            }
        }
        return result;
    }

    public static double[][] addMatrices(double[][] A, double[][] B) {
        int rows = A.length;
        int cols = A[0].length;
        double[][] result = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result[i][j] = A[i][j] + B[i][j];
            }
        }
        return result;
    }

    public static double[][] multiplyMatrixByScalar(double[][] A, double scalar) {
        int rows = A.length;
        int cols = A[0].length;
        double[][] result = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result[i][j] = A[i][j] * scalar;
            }
        }
        return result;
    }

    // Новый метод для расчета матрицы Hbc для граничных условий
    public static double[][] calculateHbcForElement(Element element, Node[] allNodes, double alfa, List<Double> gauss1DPoints, List<Double> gauss1DWeights) {
        int numNodes = element.getNodeId().length;
        double[][] Hbc = new double[numNodes][numNodes];

        // Получаем координаты узлов элемента
        double[] nodeX = new double[numNodes];
        double[] nodeY = new double[numNodes];
        int[] nodeIds = element.getNodeId();

        for (int i = 0; i < numNodes; i++) {
            Node node = allNodes[nodeIds[i] - 1];
            nodeX[i] = node.getX();
            nodeY[i] = node.getY();
        }

        // Проверяем каждую грань элемента на наличие граничных условий
        for (int edge = 0; edge < 4; edge++) {
            int[] edgeNodes = getEdgeNodes(edge);
            boolean isBoundaryEdge = isBoundaryEdge(edgeNodes, nodeIds, allNodes);

            if (isBoundaryEdge) {
                // Рассчитываем длину грани
                int node1 = edgeNodes[0];
                int node2 = edgeNodes[1];
                double x1 = nodeX[node1];
                double y1 = nodeY[node1];
                double x2 = nodeX[node2];
                double y2 = nodeY[node2];
                double edgeLength = Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));

                // Интегрирование методом Гаусса для грани
                for (int gp = 0; gp < gauss1DPoints.size(); gp++) {
                    double xi = gauss1DPoints.get(gp);
                    double weight = gauss1DWeights.get(gp);

                    // Функции формы для грани
                    double[] N_edge = calculateShapeFunctionsForEdge(xi, edge);

                    // Матрица N*N^T для грани
                    double[][] NNT = multiplyVectorColumnByRow(N_edge, N_edge);

                    // Добавляем вклад в Hbc
                    double scalar = alfa * (edgeLength / 2.0) * weight;
                    double[][] Hbc_edge = multiplyMatrixByScalar(NNT, scalar);

                    Hbc = addMatrices(Hbc, Hbc_edge);
                }
            }
        }

        return Hbc;
    }

    // Получить узлы для конкретной грани
    private static int[] getEdgeNodes(int edge) {
        switch (edge) {
            case 0: return new int[]{0, 1}; // нижняя грань
            case 1: return new int[]{1, 2}; // правая грань
            case 2: return new int[]{2, 3}; // верхняя грань
            case 3: return new int[]{3, 0}; // левая грань
            default: return new int[]{0, 1};
        }
    }

    // Проверить, является ли грань граничной
    private static boolean isBoundaryEdge(int[] edgeNodes, int[] elementNodeIds, Node[] allNodes) {
        int globalNode1 = elementNodeIds[edgeNodes[0]];
        int globalNode2 = elementNodeIds[edgeNodes[1]];

        // Проверяем, оба ли узла грани являются граничными
        return allNodes[globalNode1 - 1].isBoundary() && allNodes[globalNode2 - 1].isBoundary();
    }

    // Вычислить функции формы для грани
    private static double[] calculateShapeFunctionsForEdge(double xi, int edge) {
        double[] N = new double[4];

        switch (edge) {
            case 0: // нижняя грань (eta = -1)
                N[0] = 0.5 * (1 - xi);
                N[1] = 0.5 * (1 + xi);
                N[2] = 0.0;
                N[3] = 0.0;
                break;
            case 1: // правая грань (xi = 1)
                N[0] = 0.0;
                N[1] = 0.5 * (1 - xi);
                N[2] = 0.5 * (1 + xi);
                N[3] = 0.0;
                break;
            case 2: // верхняя грань (eta = 1)
                N[0] = 0.0;
                N[1] = 0.0;
                N[2] = 0.5 * (1 + xi);
                N[3] = 0.5 * (1 - xi);
                break;
            case 3: // левая грань (xi = -1)
                N[0] = 0.5 * (1 + xi);
                N[1] = 0.0;
                N[2] = 0.0;
                N[3] = 0.5 * (1 - xi);
                break;
        }

        return N;
    }

    public static double[] calculatePForElement(Element element, Node[] allNodes, double alfa, double tot,
                                                List<Double> gauss1DPoints, List<Double> gauss1DWeights) {
        int numNodes = element.getNodeId().length;
        double[] P_element = new double[numNodes];

        // Получаем координаты узлов элемента
        double[] nodeX = new double[numNodes];
        double[] nodeY = new double[numNodes];
        int[] nodeIds = element.getNodeId();

        for (int i = 0; i < numNodes; i++) {
            Node node = allNodes[nodeIds[i] - 1];
            nodeX[i] = node.getX();
            nodeY[i] = node.getY();
        }

        // Проверяем каждую грань элемента на наличие граничных условий
        for (int edge = 0; edge < 4; edge++) {
            int[] edgeNodes = getEdgeNodes(edge);
            boolean isBoundaryEdge = isBoundaryEdge(edgeNodes, nodeIds, allNodes);

            if (isBoundaryEdge) {
                // Рассчитываем длину грани
                int node1 = edgeNodes[0];
                int node2 = edgeNodes[1];
                double x1 = nodeX[node1];
                double y1 = nodeY[node1];
                double x2 = nodeX[node2];
                double y2 = nodeY[node2];
                double edgeLength = Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));

                // Интегрирование методом Гаусса для грани
                for (int gp = 0; gp < gauss1DPoints.size(); gp++) {
                    double xi = gauss1DPoints.get(gp);
                    double weight = gauss1DWeights.get(gp);

                    // Функции формы для грани
                    double[] N_edge = calculateShapeFunctionsForEdge(xi, edge);

                    // Добавляем вклад в вектор P: α * t_ot * (L/2) * weight * N
                    double scalar = alfa * tot * (edgeLength / 2.0) * weight;
                    for (int i = 0; i < numNodes; i++) {
                        P_element[i] += scalar * N_edge[i];
                    }
                }
            }
        }

        return P_element;
    }

    public static void main(String[] args) {
        try {
            Path file = Path.of("src/main/resources/globalData/Test2_4_4_MixGrid.txt");

            GlobalDataParser parser = new GlobalDataParser();
            GlobalDataParser.ParsedData parsedData = parser.parse(file);

            GlobalData globalData = parsedData.getGlobalData();
            globalData.setNpc(2);
            Grid grid = parsedData.getGrid();

            System.out.println("=== Global Data ===");
            System.out.println(globalData);

            Node[] allNodes = grid.getNodes().toArray(new Node[0]);
            List<Element> elements = grid.getElements();

            List<Double> gauss1DPoints = GaussData.POINTS_2;
            List<Double> gauss1DWeights = GaussData.WEIGHTS_2;

            double cond = globalData.getConductivity();
            double alfa = globalData.getAlfa();
            double tot = globalData.getTot(); // Температура окружающей среды
            int numPoints1D = gauss1DPoints.size();
            int numGaussPoints2D = numPoints1D * numPoints1D;
            int numNodes = elements.getFirst().getNodeId().length;

            // Таблицы производных
            double[][] dNdXiPointTable = new double[numGaussPoints2D][numNodes];
            double[][] dNdEtaPointTable = new double[numGaussPoints2D][numNodes];

            int gaussIndex = 0;
            for (int i = 0; i < numPoints1D; i++) {
                double eta = gauss1DPoints.get(i);
                for (int j = 0; j < numPoints1D; j++) {
                    double xi = gauss1DPoints.get(j);
                    for (int k = 0; k < numNodes; k++) {
                        dNdXiPointTable[gaussIndex][k] = ElemUniv.dNdXi(k, xi);
                        dNdEtaPointTable[gaussIndex][k] = ElemUniv.dNdEta(k, eta);
                    }
                    gaussIndex++;
                }
            }

            int nN = globalData.getNN();
            GlobalMatrix globalMatrix = GlobalMatrix.builder()
                    .H_global(new double[nN][nN])
                    .Hbc_global(new double[nN][nN])
                    .P_global(new double[nN]) // Добавляем глобальный вектор P
                    .build();

            for (Element element : elements) {
                int[] nodeIds = element.getNodeId();

                double[] nodeX = new double[numNodes];
                double[] nodeY = new double[numNodes];

                for (int i = 0; i < numNodes; i++) {
                    Node node = allNodes[nodeIds[i] - 1];
                    nodeX[i] = node.getX();
                    nodeY[i] = node.getY();
                }

                List<Jacobian> elementJacobians = new ArrayList<>();
                double[][] H_element = new double[numNodes][numNodes];

                for (int p = 0; p < numGaussPoints2D; p++) {
                    Jacobian jacobianData = new Jacobian();
                    double[][] J = jacobianData.getJ();

                    double J11 = 0, J12 = 0, J21 = 0, J22 = 0;

                    for (int i = 0; i < numNodes; i++) {
                        double dNdXi = dNdXiPointTable[p][i];
                        double dNdEta = dNdEtaPointTable[p][i];

                        double X_i = nodeX[i];
                        double Y_i = nodeY[i];

                        J11 += dNdXi * X_i;
                        J12 += dNdXi * Y_i;
                        J21 += dNdEta * X_i;
                        J22 += dNdEta * Y_i;
                    }

                    J[0][0] = J11;
                    J[0][1] = J12;
                    J[1][0] = J21;
                    J[1][1] = J22;

                    double detJ = J11 * J22 - J12 * J21;
                    jacobianData.setDetJ(detJ);

                    if (Math.abs(detJ) < 1e-9) {
                        throw new IllegalStateException("Det(J) < 0");
                    }

                    double invDetJ = 1.0 / detJ;
                    double[][] Jinv = jacobianData.getJinv();
                    Jinv[0][0] = J22 * invDetJ;
                    Jinv[0][1] = -J12 * invDetJ;
                    Jinv[1][0] = -J21 * invDetJ;
                    Jinv[1][1] = J11 * invDetJ;

                    elementJacobians.add(jacobianData);

                    double[] dNdX = new double[numNodes];
                    double[] dNdY = new double[numNodes];

                    for (int i = 0; i < numNodes; i++) {
                        double dNdXi = dNdXiPointTable[p][i];
                        double dNdEta = dNdEtaPointTable[p][i];

                        dNdX[i] = Jinv[0][0] * dNdXi + Jinv[0][1] * dNdEta;
                        dNdY[i] = Jinv[1][0] * dNdXi + Jinv[1][1] * dNdEta;
                    }

                    int index1D_eta = p / numPoints1D;
                    int index1D_xi = p % numPoints1D;
                    double Wp = gauss1DWeights.get(index1D_eta) * gauss1DWeights.get(index1D_xi);

                    double[][] termX = multiplyVectorColumnByRow(dNdX, dNdX);
                    double[][] termY = multiplyVectorColumnByRow(dNdY, dNdY);

                    double[][] sumTerms = addMatrices(termX, termY);

                    double scalar = cond * detJ * Wp;
                    double[][] Hp = multiplyMatrixByScalar(sumTerms, scalar);

                    H_element = addMatrices(H_element, Hp);
                }

                element.setJacobians(elementJacobians);

                // Расчет матрицы Hbc для граничных условий
                double[][] Hbc_element = calculateHbcForElement(element, allNodes, alfa, gauss1DPoints, gauss1DWeights);
                element.setHbc(Hbc_element);

                // Расчет вектора P для граничных условий
                double[] P_element = calculatePForElement(element, allNodes, alfa, tot, gauss1DPoints, gauss1DWeights);
                element.setP(P_element); // Сохраняем в элемент

                // Добавляем в глобальные матрицы
                globalMatrix.addElementMatrix(H_element, element.getNodeId());
                globalMatrix.addElementMatrix(Hbc_element, element.getNodeId(), true);
                globalMatrix.addPVector(P_element, element.getNodeId());

                System.out.println("\n--- Macierz H dla elementu ID=" + Arrays.toString(element.getNodeId()) + " ---");
                printMatrix(H_element, null);

                System.out.println("\n--- Macierz Hbc dla elementu ID=" + Arrays.toString(element.getNodeId()) + " ---");
                printMatrix(Hbc_element, null);

                System.out.println("\n--- Wektor P dla elementu ID=" + Arrays.toString(element.getNodeId()) + " ---");
                System.out.println(Arrays.toString(P_element));
            }

            // Общая матрица H + Hbc
            double[][] H_total = addMatrices(globalMatrix.getH_global(), globalMatrix.getHbc_global());

            System.out.println("\n=== Macierz Globalna H + Hbc ===");
            printMatrix(H_total, null);

            System.out.println("\n=== Wektor Globalny P ===");
            System.out.println(Arrays.toString(globalMatrix.getP_global()));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    public static void main(String[] args) {
        try {

            // Dependency Injection
            ConductivityMatrixService conductivityService = new ConductivityMatrixService();
            BoundaryConditionService boundaryService = new BoundaryConditionService();
            ElementService elementService = new ElementService(conductivityService, boundaryService);

            Path file = Path.of("src/main/resources/globalData/Test1_4_4.txt");
            //Path file = Path.of("src/main/resources/globalData/Test2_4_4_MixGrid.txt");
            //Path file = Path.of("src/main/resources/globalData/Test3_31_31_kwadrat.txt");
            GlobalDataParser parser = new GlobalDataParser();
            GlobalDataParser.ParsedData parsedData = parser.parse(file);

            GlobalData globalData = parsedData.getGlobalData();
            Grid grid = parsedData.getGrid();
            Node[] allNodes = grid.getNodes().toArray(new Node[0]);

            System.out.println("=== Global Data ===");
            System.out.println(globalData);

            int nN = globalData.getNN();
            GlobalMatrix globalMatrix = GlobalMatrix.builder()
                    .H_global(new double[nN][nN])
                    .Hbc_global(new double[nN][nN])
                    .P_global(new double[nN])
                    .build();

            List<Double> points = GaussData.POINTS_2;
            List<Double> weights = GaussData.WEIGHTS_2;

            for (Element element : grid.getElements()) {

                elementService.processElement(element, allNodes, globalData, points, weights); // Calculates H, Hbc and P

                // Aggregate into Global Matrix
                globalMatrix.addElementMatrix(element.getH(), element.getNodeId(), false); // Add H
                globalMatrix.addElementMatrix(element.getHbc(), element.getNodeId(), true); // Add Hbc
                globalMatrix.addPVector(element.getP(), element.getNodeId()); // Add P

                System.out.println("\n--- Macierz H dla elementu ID=" + Arrays.toString(element.getNodeId()) + " ---");
                MatrixService.printMatrix(element.getH());

                System.out.println("\n--- Macierz Hbc dla elementu ID=" + Arrays.toString(element.getNodeId()) + " ---");
                MatrixService.printMatrix(element.getHbc());

                System.out.println("\n--- Wektor P dla elementu ID=" + Arrays.toString(element.getNodeId()) + " ---");
                MatrixService.printVector(element.getP());
            }

            double[][] H_total = MatrixService.addMatrices(globalMatrix.getH_global(), globalMatrix.getHbc_global());

            System.out.println("\n=== Macierz Globalna H + Hbc ===");
            MatrixService.printMatrix(H_total);

            System.out.println("\n=== Wektor Globalny P ===");
            MatrixService.printVector(globalMatrix.getP_global());

            System.out.println("=== Rozwiązanie H * {t} = P ===");
            double[] T_result = LinearSolver.solveLinearSystem(H_total, globalMatrix.getP_global());
            MatrixService.printVector(T_result);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}