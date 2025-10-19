package com.arcengtr.solvers.gaussLegendreQuadratureSolver;

import java.util.List;
import java.util.function.Function;

public interface NumericalIntegrationStrategy {
    public Double integrate(Function<List<Double>, Double>function);
}
