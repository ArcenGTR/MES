package com.arcengtr.solvers.gaussLegendreQuadratureSolver;

import java.util.List;
import java.util.function.Function;

public abstract class AbstractGaussStrategy implements NumericalIntegrationStrategy {

    protected abstract List<Double> getPoints();
    protected abstract List<Double> getWeights();

    @Override
    public Double integrate(Function<List<Double>, Double> function) {
        List<Double> points = getPoints();
        List<Double> weights = getWeights();

        Double sum = 0.0;

        for (int i = 0; i < points.size(); i++) {
            double weight = weights.get(i);
            double xi = points.get(i);

            List<Double> currentArgs = List.of(xi);

            sum += weight * function.apply(currentArgs);
        }

        return sum;
    }
}
