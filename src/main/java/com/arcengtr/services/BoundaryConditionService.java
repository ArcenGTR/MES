package com.arcengtr.services;

import com.arcengtr.common.ElemUniv;
import com.arcengtr.common.Node;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class BoundaryConditionService {
    public double[][] calculateHbc(int[] nodeIds, Node[] allNodes, double alfa, List<Double> gaussPoints, List<Double> gaussWeights) {
        int numNodes = nodeIds.length;
        double[][] Hbc = new double[numNodes][numNodes];

        for (int edge = 0; edge < 4; edge++) {
            int[] edgeNodesMap = getEdgeNodes(edge);
            int globalNodeId1 = nodeIds[edgeNodesMap[0]];
            int globalNodeId2 = nodeIds[edgeNodesMap[1]];

            if (isBoundaryEdge(globalNodeId1, globalNodeId2, allNodes)) {
                double edgeLength = calculateEdgeLength(globalNodeId1, globalNodeId2, allNodes);
                double detJ_surf = edgeLength / 2.0;

                for (int i = 0; i < gaussPoints.size(); i++) {
                    double xi_1D = gaussPoints.get(i);
                    double weight = gaussWeights.get(i);

                    double[] coords = get2DCoordinatesForEdge(xi_1D, edge);
                    double xi = coords[0];
                    double eta = coords[1];

                    double[] N = new double[numNodes];
                    for (int k = 0; k < numNodes; k++) {
                        N[k] = ElemUniv.formN(k, xi, eta);
                    }

                    double[][] NNT = MatrixService.multiplyVectorColumnByRow(N, N);

                    double scalar = alfa * weight * detJ_surf;
                    double[][] Hbc_edge = MatrixService.multiplyMatrixByScalar(NNT, scalar);

                    Hbc = MatrixService.addMatrices(Hbc, Hbc_edge);
                }
            }
        }
        return Hbc;
    }

    public double[] calculatePVector(int[] nodeIds, Node[] allNodes, double alfa, double tot, List<Double> gaussPoints, List<Double> gaussWeights) {
        int numNodes = nodeIds.length;
        double[] P = new double[numNodes];

        for (int edge = 0; edge < 4; edge++) {
            int[] edgeNodesMap = getEdgeNodes(edge);
            int globalNodeId1 = nodeIds[edgeNodesMap[0]];
            int globalNodeId2 = nodeIds[edgeNodesMap[1]];

            if (isBoundaryEdge(globalNodeId1, globalNodeId2, allNodes)) {
                double edgeLength = calculateEdgeLength(globalNodeId1, globalNodeId2, allNodes);
                double detJ_surf = edgeLength / 2.0;

                for (int i = 0; i < gaussPoints.size(); i++) {
                    double xi_1D = gaussPoints.get(i);
                    double weight = gaussWeights.get(i);

                    double[] coords = get2DCoordinatesForEdge(xi_1D, edge);
                    double xi = coords[0];
                    double eta = coords[1];

                    double[] N = new double[numNodes];
                    for (int k = 0; k < numNodes; k++) {
                        N[k] = ElemUniv.formN(k, xi, eta);
                    }

                    double scalar = alfa * tot * weight * detJ_surf;

                    for (int n = 0; n < numNodes; n++) {
                        P[n] += scalar * N[n];
                    }
                }
            }
        }
        return P;
    }

    private boolean isBoundaryEdge(int globalId1, int globalId2, Node[] allNodes) {
        return allNodes[globalId1 - 1].isBoundary() && allNodes[globalId2 - 1].isBoundary();
    }

    private double calculateEdgeLength(int globalId1, int globalId2, Node[] allNodes) {
        Node n1 = allNodes[globalId1 - 1];
        Node n2 = allNodes[globalId2 - 1];
        return Math.sqrt(Math.pow(n2.getX() - n1.getX(), 2) + Math.pow(n2.getY() - n1.getY(), 2));
    }

    private int[] getEdgeNodes(int edge) {
        return switch (edge) {
            case 0 -> new int[]{0, 1}; // Bottom
            case 1 -> new int[]{1, 2}; // Right
            case 2 -> new int[]{2, 3}; // Top
            case 3 -> new int[]{3, 0}; // Left
            default -> throw new IllegalArgumentException("Invalid edge index: " + edge);
        };
    }

    private double[] get2DCoordinatesForEdge(double xi_1D, int edge) {
        double xi, eta;

        switch (edge) {
            case 0 -> {
                xi = xi_1D;
                eta = -1.0;
            }
            case 1 -> {
                xi = 1.0;
                eta = xi_1D;
            }
            case 2 -> {
                xi = xi_1D;
                eta = 1.0;
            }
            case 3 -> {
                xi = -1.0;
                eta = xi_1D;
            }
            default -> throw new IllegalArgumentException("Invalid edge index: " + edge);
        }
        return new double[]{xi, eta};
    }
}