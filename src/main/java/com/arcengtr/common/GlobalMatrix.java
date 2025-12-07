package com.arcengtr.common;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@ToString
@Builder
public class GlobalMatrix {
    private final double[][] H_global;
    private double[][] Hbc_global;
    private double[] P_global;
    private double[][] C_global;

    public void addElementMatrix(double[][] elementMatrix, int[] nodeIds) {
        addElementMatrix(elementMatrix, nodeIds, false);
    }

    public void addElementMatrix(double[][] elementMatrix, int[] nodeIds, boolean isHbc) {
        double[][] globalMatrix = isHbc ? Hbc_global : H_global;

        for (int i = 0; i < nodeIds.length; i++) {
            for (int j = 0; j < nodeIds.length; j++) {
                int globalI = nodeIds[i] - 1;
                int globalJ = nodeIds[j] - 1;
                globalMatrix[globalI][globalJ] += elementMatrix[i][j];
            }
        }
    }

    public void addPVector(double[] elementP, int[] nodeIds) {
        for (int i = 0; i < nodeIds.length; i++) {
            int globalI = nodeIds[i] - 1;
            P_global[globalI] += elementP[i];
        }
    }

    public void addCMatrix(double[][] elementC, int[] nodeIds) {
        for (int i = 0; i < nodeIds.length; i++) {
            for (int j = 0; j < nodeIds.length; j++) {
                int globalI = nodeIds[i] - 1;
                int globalJ = nodeIds[j] - 1;
                this.C_global[globalI][globalJ] += elementC[i][j];
            }
        }
    }
}