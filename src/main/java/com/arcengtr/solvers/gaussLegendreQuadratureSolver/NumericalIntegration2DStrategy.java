package com.arcengtr.solvers.gaussLegendreQuadratureSolver;

import java.util.List;
import java.util.function.Function;

public interface NumericalIntegration2DStrategy {
    Double integrate(Function<List<Double>, Double> function);
}
