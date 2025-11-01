package com.arcengtr;

import com.arcengtr.common.*;
import com.arcengtr.parsers.GlobalDataParser;
import com.arcengtr.services.JacobianService;
import com.arcengtr.solvers.gaussLegendreQuadratureSolver.GaussData;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class Main {

    public static void printMatrix(double[][] matrix, String title) {
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

    public static void main(String[] args) {
        try {

            Path file = Path.of("src/main/resources/globalData/Test1.txt");

            GlobalDataParser parser = new GlobalDataParser();
            GlobalDataParser.ParsedData parsedData = parser.parse(file);

            GlobalData globalData = parsedData.getGlobalData();
            globalData.setNpc(2);
            Grid grid = parsedData.getGrid();

            System.out.println("=== Global Data ===");
            System.out.println(globalData);

            System.out.println("\n=== Nodes ===");
            grid.getNodes().forEach(System.out::println);

            System.out.println("\n=== Elements ===");
            grid.getElements().forEach(System.out::println);

            Node[] allNodes = grid.getNodes().toArray(new Node[0]);
            List<Element> elements = grid.getElements();

            List<Double> gauss1DPoints = GaussData.POINTS_3;
            List<Double> gauss1DWeights = GaussData.WEIGHTS_3;

            double cond = globalData.getConductivity();
            int numPoints1D = gauss1DPoints.size();
            int numGaussPoints2D = numPoints1D * numPoints1D;
            int numNodes = elements.getFirst().getNodeId().length;

            // [Znaczenie_w_punkcie_i][pc_j]
            double[][] dNdXiPointTable = new double[numGaussPoints2D][numNodes];
            double[][] dNdEtaPointTable = new double[numGaussPoints2D][numNodes];

            // Tablice pochodnych po zmiennym lokalym
            int gaussIndex = 0;
            for (int i = 0; i < numPoints1D; i++) {
                double eta = gauss1DPoints.get(i);

                for (int j = 0; j < numPoints1D; j++) {
                    double xi = gauss1DPoints.get(j);

                    for (int k = 0; k < numNodes; k++) {

                        dNdXiPointTable[gaussIndex][k] = ElemUniv.dNdXi(k, eta);
                        dNdEtaPointTable[gaussIndex][k] = ElemUniv.dNdEta(k, xi);
                    }
                    gaussIndex++;
                }
            }

            System.out.println();
            printMatrix(dNdXiPointTable, "dXiPointTable");
            printMatrix(dNdEtaPointTable, "dEtaPointTable");

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

                    // Tablice z pochodnymi po współrzędnych globalnych
                    double[] dNdX = new double[numNodes];
                    double[] dNdY = new double[numNodes];

                    for (int i = 0; i < numNodes; i++) {
                        double dNdXi = dNdXiPointTable[p][i];
                        double dNdEta = dNdEtaPointTable[p][i];

                        dNdX[i] = Jinv[0][0] * dNdXi + Jinv[0][1] * dNdEta;
                        dNdY[i] = Jinv[1][0] * dNdXi + Jinv[1][1] * dNdEta;
                    }

                    // Wp = W_eta * W_xi
                    int index1D_eta = p / numPoints1D; // eta
                    int index1D_xi = p % numPoints1D;  // xi
                    double Wp = gauss1DWeights.get(index1D_eta) * gauss1DWeights.get(index1D_xi);

                    // dN/dx * (dN/dx)^T
                    double[][] termX = multiplyVectorColumnByRow(dNdX, dNdX);

                    // dN/dy * (dN/dy)^T
                    double[][] termY = multiplyVectorColumnByRow(dNdY, dNdY);

                    // dN/dx * (dN/dx)^T + dN/dy * (dN/dy)^T)
                    double[][] sumTerms = addMatrices(termX, termY);

                    // k * |J| * Wp
                    double scalar = cond * detJ * Wp;
                    double[][] Hp = multiplyMatrixByScalar(sumTerms, scalar);

                    H_element = addMatrices(H_element, Hp);

                    System.out.println();
                    if (element == elements.getFirst()) {
                        System.out.println("--- Punkt Całkowania " + (p + 1) + " ---");
                        System.out.println("dNdX: " + Arrays.toString(dNdX));
                        System.out.println("dNdY: " + Arrays.toString(dNdY));
                        System.out.println("k*|J|*Wp: " + String.format(Locale.US, "%.4f", scalar));
                        printMatrix(Hp, null);
                    }
                }

                element.setJacobians(elementJacobians);

                if (element == elements.getFirst()) {
                    System.out.println("\n--- Macierz dla pierwszego elementu ---");
                    printMatrix(H_element, null);
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}