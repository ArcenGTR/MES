package com.arcengtr.common;

import lombok.Data;

@Data
public class Jacobian {
    private final double[][] J = new double[2][2];
    private final double[][] Jinv = new double[2][2];
    private double detJ;
}
