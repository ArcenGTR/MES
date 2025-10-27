package com.arcengtr.services;

import com.arcengtr.common.ElemUniv;
import com.arcengtr.common.Element;
import com.arcengtr.common.Jacobian;
import com.arcengtr.common.Node;

public class JacobianService {

    public static Jacobian computeJacobian(Element element, Node[] allNodes,
                                           ElemUniv univ, int gaussPointIndex) {

        double[] x = new double[4];
        double[] y = new double[4];

        for (int i = 0; i < 4; i++) {
            int nodeIndex = element.getNodeId()[i] - 1;
            Node node = allNodes[nodeIndex];
            x[i] = node.getX();
            y[i] = node.getY();
        }

        Jacobian J = new Jacobian();
        J.compute(x, y, univ.dN_dXi[gaussPointIndex], univ.dN_dEta[gaussPointIndex]);
        return J;
    }
}