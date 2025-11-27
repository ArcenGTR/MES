package com.arcengtr.common;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ElemUniv {

    public double formN(int k, double xi, double eta) {
        return switch (k) {
            case 0 -> 0.25 * (1 - xi) * (1 - eta);
            case 1 -> 0.25 * (1 + xi) * (1 - eta);
            case 2 -> 0.25 * (1 + xi) * (1 + eta);
            case 3 -> 0.25 * (1 - xi) * (1 + eta);
            default -> throw new IllegalArgumentException("Invalid node index: " + k);
        };
    }

    public double dNdXi(int k, double eta) {
        return switch (k) {
            case 0 -> -0.25 * (1 - eta);
            case 1 -> 0.25 * (1 - eta);
            case 2 -> 0.25 * (1 + eta);
            case 3 -> -0.25 * (1 + eta);
            default -> throw new IllegalArgumentException();
        };
    }

    public double dNdEta(int k, double xi) {
        return switch (k) {
            case 0 -> -0.25 * (1 - xi);
            case 1 -> -0.25 * (1 + xi);
            case 2 -> 0.25 * (1 + xi);
            case 3 -> 0.25 * (1 - xi);
            default -> throw new IllegalArgumentException();
        };
    }
}