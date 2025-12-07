package com.arcengtr.services;

import com.arcengtr.common.*;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class ElementService {

    private final ConductivityMatrixService conductivityService;
    private final BoundaryConditionService boundaryService;
    private final CapacityMatrixService capacityService;

    public void processElement(Element element, Node[] allNodes, GlobalData globalData,
                               List<Double> gaussPoints, List<Double> gaussWeights) {

        int[] nodeIds = element.getNodeId();
        int numNodes = nodeIds.length;

        double[] nodeX = new double[numNodes];
        double[] nodeY = new double[numNodes];
        for (int i = 0; i < numNodes; i++) {
            Node node = allNodes[nodeIds[i] - 1];
            nodeX[i] = node.getX();
            nodeY[i] = node.getY();
        }

        double[][] H_element = new double[numNodes][numNodes];
        List<Jacobian> jacobians = new ArrayList<>();
        int numPoints1D = gaussPoints.size();

        for (int i = 0; i < numPoints1D; i++) { // eta
            double eta = gaussPoints.get(i);
            double weightEta = gaussWeights.get(i);

            for (int j = 0; j < numPoints1D; j++) { // xi
                double xi = gaussPoints.get(j);
                double weightXi = gaussWeights.get(j);

                double[] dNdXi = new double[numNodes];
                double[] dNdEta = new double[numNodes];
                for(int k=0; k<numNodes; k++) {
                    dNdXi[k] = ElemUniv.dNdXi(k, xi);
                    dNdEta[k] = ElemUniv.dNdEta(k, eta);
                }

                Jacobian jac = JacobianService.calculateJacobian(nodeX, nodeY, dNdXi, dNdEta);
                jacobians.add(jac);

                double weight2D = weightEta * weightXi;
                double[][] Hp = conductivityService.calculateLocalHPoint(jac, dNdXi, dNdEta, globalData.getConductivity(), weight2D);

                H_element = MatrixService.addMatrices(H_element, Hp);
            }
        }

        double[][] Hbc_element = boundaryService.calculateHbc(nodeIds, allNodes, globalData.getAlfa(), gaussPoints, gaussWeights);
        double[] P_element = boundaryService.calculatePVector(nodeIds, allNodes, globalData.getAlfa(), globalData.getTot(), gaussPoints, gaussWeights);
        double[][] C_element = capacityService.calculateC(jacobians, nodeX, globalData.getDensity(), globalData.getSpecificHeat(), gaussPoints, gaussWeights);

        element.setH(H_element);
        element.setJacobians(jacobians);
        element.setHbc(Hbc_element);
        element.setP(P_element);
        element.setC(C_element);
    }
}