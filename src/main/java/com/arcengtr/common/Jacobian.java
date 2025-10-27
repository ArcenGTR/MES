package com.arcengtr.common;

import lombok.Data;

@Data
public class Jacobian {
    private final double[][] J = new double[2][2];
    private final double[][] Jinv = new double[2][2];
    private double detJ;

    public void compute(double[] nodeX, double[] nodeY, double[] dN_dXi, double[] dN_dEta) {
        J[0][0] = J[0][1] = J[1][0] = J[1][1] = 0.0;

        for (int k = 0; k < 4; k++) {
            J[0][0] += dN_dXi[k] * nodeX[k];
            J[0][1] += dN_dXi[k] * nodeY[k];
            J[1][0] += dN_dEta[k] * nodeX[k];
            J[1][1] += dN_dEta[k] * nodeY[k];
        }

        detJ = J[0][0]*J[1][1] - J[0][1]*J[1][0];

        if (Math.abs(detJ) < 1e-12)
            throw new RuntimeException("Jacobian determinant is too small!");

        double invDet = 1.0 / detJ;
        Jinv[0][0] =  J[1][1] * invDet;
        Jinv[0][1] = -J[0][1] * invDet;
        Jinv[1][0] = -J[1][0] * invDet;
        Jinv[1][1] =  J[0][0] * invDet;
    }
}
