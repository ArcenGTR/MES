package com.arcengtr.common;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@ToString
@Builder
public class GlobalMatrix {
    private final double[][] H_global;

    public void addElementMatrix(double[][] H_element, int[] nodeIds) {
        for (int i = 0; i < nodeIds.length; i++) {
            int I = nodeIds[i] - 1;
            for (int j = 0; j < nodeIds.length; j++) {
                int J = nodeIds[j] - 1;
                H_global[I][J] += H_element[i][j];
            }
        }
    }
}