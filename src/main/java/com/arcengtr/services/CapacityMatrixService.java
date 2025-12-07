package com.arcengtr.services;

import com.arcengtr.common.ElemUniv;
import com.arcengtr.common.Jacobian;

import java.util.List;

public class CapacityMatrixService {
    public double[][] calculateC(List<Jacobian> jacobians,
                                 double[] nodeX,
                                 double density,
                                 double specificHeat,
                                 List<Double> gaussPoints,
                                 List<Double> gaussWeights) {

        int numNodes = nodeX.length;
        double[][] C_element = new double[numNodes][numNodes];
        int numPoints1D = gaussPoints.size();

        int p = 0;
        for (double eta : gaussPoints) {
            for (double xi : gaussPoints) {
                Jacobian jacobianData = jacobians.get(p);
                double detJ = jacobianData.getDetJ();

                double[] N_vector = new double[numNodes];
                for (int k = 0; k < numNodes; k++) {
                    N_vector[k] = ElemUniv.formN(k, xi, eta);
                }

                double[][] NNT = MatrixService.multiplyVectorColumnByRow(N_vector, N_vector);

                int index1D_eta = p / numPoints1D;
                int index1D_xi = p % numPoints1D;
                double Wp = gaussWeights.get(index1D_eta) * gaussWeights.get(index1D_xi);

                double scalar = density * specificHeat * detJ * Wp;

                double[][] Cp = MatrixService.multiplyMatrixByScalar(NNT, scalar);
                C_element = MatrixService.addMatrices(C_element, Cp);

                p++;
            }
        }

        return C_element;
    }
}
