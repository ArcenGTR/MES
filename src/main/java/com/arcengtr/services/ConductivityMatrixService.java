package com.arcengtr.services;

import com.arcengtr.common.Jacobian;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ConductivityMatrixService {

    public double[][] calculateLocalHPoint(Jacobian jacobian, double[] dNdXi, double[] dNdEta, double conductivity, double weight) {
        double[][] Jinv = jacobian.getJinv();
        double detJ = jacobian.getDetJ();
        int numNodes = dNdXi.length;

        double[] dNdX = new double[numNodes];
        double[] dNdY = new double[numNodes];

        for (int i = 0; i < numNodes; i++) {
            dNdX[i] = Jinv[0][0] * dNdXi[i] + Jinv[0][1] * dNdEta[i];
            dNdY[i] = Jinv[1][0] * dNdXi[i] + Jinv[1][1] * dNdEta[i];
        }

        double[][] xPart = MatrixService.multiplyVectorByTransposed(dNdX, dNdX);
        double[][] yPart = MatrixService.multiplyVectorByTransposed(dNdY, dNdY);
        double[][] sum = MatrixService.addMatrices(xPart, yPart);

        double scalar = conductivity * detJ * weight;
        return MatrixService.multiplyMatrixByScalar(sum, scalar);
    }
}