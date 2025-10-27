package com.arcengtr.common;

public class ElemUniv {

    public final int npc;
    public final double[] gaussPoints;
    public final double[] gaussWeights;
    public final double[][] dN_dXi;
    public final double[][] dN_dEta;

    public ElemUniv(int npc) {
        this.npc = npc;
        if (npc == 2) { // zamienic moze
            this.gaussPoints = new double[]{-1.0 / Math.sqrt(3.0), 1.0 / Math.sqrt(3.0)};
            this.gaussWeights = new double[]{1.0, 1.0};
        } else {
            throw new IllegalArgumentException("Unsupported Gauss points: " + npc);
        }

        int totalPoints = npc * npc;
        dN_dXi = new double[totalPoints][4];
        dN_dEta = new double[totalPoints][4];

        int idx = 0;
        for (double eta : gaussPoints) {
            for (double xi : gaussPoints) {
                for (int k = 0; k < 4; k++) {
                    dN_dXi[idx][k] = dNdXi(k + 1, xi, eta);
                    dN_dEta[idx][k] = dNdEta(k + 1, xi, eta);
                }
                idx++;
            }
        }
    }

    // 4 Npc
    private double dNdXi(int k, double xi, double eta) {
        return switch (k) {
            case 1 -> -0.25 * (1 - eta);
            case 2 -> 0.25 * (1 - eta);
            case 3 -> 0.25 * (1 + eta);
            case 4 -> -0.25 * (1 + eta);
            default -> throw new IllegalArgumentException();
        };
    }

    private double dNdEta(int k, double xi, double eta) {
        return switch (k) {
            case 1 -> -0.25 * (1 - xi);
            case 2 -> -0.25 * (1 + xi);
            case 3 -> 0.25 * (1 + xi);
            case 4 -> 0.25 * (1 - xi);
            default -> throw new IllegalArgumentException();
        };
    }
}