package com.arcengtr.solvers.gaussLegendreQuadratureSolver;

import java.util.List;

public class GaussData {
    public static final List<Double> POINTS_1 = List.of(0.0);
    public static final List<Double> WEIGHTS_1 = List.of(2.0);

    public static final List<Double> POINTS_2 = List.of(
            -1.0 / Math.sqrt(3.0),
            1.0 / Math.sqrt(3.0));
    public static final List<Double> WEIGHTS_2 = List.of(
            1.0,
            1.0);

    public static final List<Double> POINTS_3 = List.of(
            -Math.sqrt(3.0 / 5.0),
            0.0,
            Math.sqrt(3.0 / 5.0));

    public static final List<Double> WEIGHTS_3 = List.of(
            5.0 / 9.0,
            8.0 / 9.0,
            5.0 / 9.0);

    public static final List<Double> POINTS_4 = List.of(
            -Math.sqrt((3.0 - 2.0 * Math.sqrt(6.0 / 5.0)) / 7.0),
            Math.sqrt((3.0 - 2.0 * Math.sqrt(6.0 / 5.0)) / 7.0),
            -Math.sqrt((3.0 + 2.0 * Math.sqrt(6.0 / 5.0)) / 7.0),
            Math.sqrt((3.0 + 2.0 * Math.sqrt(6.0 / 5.0)) / 7.0)
    );

    public static final List<Double> WEIGHTS_4 = List.of(
            (18.0 + Math.sqrt(30.0)) / 36.0,
            (18.0 + Math.sqrt(30.0)) / 36.0,
            (18.0 - Math.sqrt(30.0)) / 36.0,
            (18.0 - Math.sqrt(30.0)) / 36.0
    );
}
