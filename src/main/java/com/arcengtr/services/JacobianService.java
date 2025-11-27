package com.arcengtr.services;

import com.arcengtr.common.Jacobian;
import lombok.experimental.UtilityClass;

@UtilityClass
public class JacobianService {

    public Jacobian calculateJacobian(double[] nodeX, double[] nodeY, double[] dNdXi, double[] dNdEta) {
        Jacobian jacobianData = new Jacobian();
        double[][] J = jacobianData.getJ();

        double J11 = 0, J12 = 0, J21 = 0, J22 = 0;
        int numNodes = nodeX.length;

        for (int i = 0; i < numNodes; i++) {
            J11 += dNdXi[i] * nodeX[i];
            J12 += dNdXi[i] * nodeY[i];
            J21 += dNdEta[i] * nodeX[i];
            J22 += dNdEta[i] * nodeY[i];
        }

        J[0][0] = J11;
        J[0][1] = J12;
        J[1][0] = J21;
        J[1][1] = J22;

        double detJ = J11 * J22 - J12 * J21;
        jacobianData.setDetJ(detJ);

        if (Math.abs(detJ) < 1e-9) {
            throw new IllegalStateException("Det(J) too small or negative: " + detJ);
        }

        double invDetJ = 1.0 / detJ;
        double[][] Jinv = jacobianData.getJinv();
        Jinv[0][0] = J22 * invDetJ;
        Jinv[0][1] = -J12 * invDetJ;
        Jinv[1][0] = -J21 * invDetJ;
        Jinv[1][1] = J11 * invDetJ;

        return jacobianData;
    }
}