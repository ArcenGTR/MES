package com.arcengtr.solvers.gaussLegendreQuadratureSolver;

import com.arcengtr.common.Element;

import java.util.List;
import java.util.function.Function;

public interface NumericalIntegration2DStrategy {
    Double integrate(Function<List<Double>, Double> function);
}
