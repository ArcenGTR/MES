package com.arcengtr.services;

import com.arcengtr.common.ElemUniv;
import com.arcengtr.common.GlobalData;
import com.arcengtr.common.Jacobian;

import java.util.List;

public class CapacityMatrixService {
    public double[][] calculateC(List<Jacobian> jacobians,
                                 double[] nodeX,
                                 double[] elementTemperatures,
                                 GlobalData globalData,
                                 List<Double> gaussPoints,
                                 List<Double> gaussWeights) {

        int numNodes = nodeX.length;
        double[][] C_element = new double[numNodes][numNodes];
        int numPoints1D = gaussPoints.size();

        double density = globalData.getDensity();
        double baseSpecificHeat = globalData.getSpecificHeat();
        double latentHeat = globalData.getLatentHeat();
        double tm = globalData.getMeltingTemp();
        double dt = globalData.getMeltingRange();

        int p = 0;
        for (double eta : gaussPoints) {
            for (double xi : gaussPoints) {
                Jacobian jacobianData = jacobians.get(p);
                double detJ = jacobianData.getDetJ();

                double[] N = new double[numNodes];
                double temperatureAtGaussPoint = 0.0;

                for (int k = 0; k < numNodes; k++) {
                    N[k] = ElemUniv.formN(k, xi, eta);
                    // T(xi, eta) = sum(Ni * Ti)
                    temperatureAtGaussPoint += N[k] * elementTemperatures[k];
                }

                double effectiveSpecificHeat = baseSpecificHeat;

                if (temperatureAtGaussPoint >= (tm - dt) && temperatureAtGaussPoint <= (tm + dt)) {
                    // C_eff = C_base + L / (2 * dt)
                    effectiveSpecificHeat += latentHeat / (2.0 * dt);
                }
                // --------------------------------

                double[][] multN = MatrixService.multiplyVectorByTransposed(N, N);

                int index1D_eta = p / numPoints1D;
                int index1D_xi = p % numPoints1D;
                double Wp = gaussWeights.get(index1D_eta) * gaussWeights.get(index1D_xi);

                double scalar = density * effectiveSpecificHeat * detJ * Wp;

                double[][] Cp = MatrixService.multiplyMatrixByScalar(multN, scalar);
                C_element = MatrixService.addMatrices(C_element, Cp);

                p++;
            }
        }

        return C_element;
    }
}
