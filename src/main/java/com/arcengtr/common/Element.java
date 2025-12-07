package com.arcengtr.common;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
public class Element {
    private int[] nodeId;
    private List<Jacobian> jacobians;
    private double[][] H;
    private double[][] Hbc;
    private double[] P;
    private double[][] C;

    public int getNumNodes() {
        return nodeId.length;
    }
}
