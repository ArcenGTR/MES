package com.arcengtr.solvers.gaussLegendreQuadratureSolver;

import com.arcengtr.common.Element;
import com.arcengtr.common.Jacobian;

import java.util.List;
import java.util.function.Function;

public abstract class AbstractGauss2DStrategy implements NumericalIntegration2DStrategy{
    protected abstract List<Double> getPoints();
    protected abstract List<Double> getWeights();

    @Override
    public Double integrate(Function<List<Double>, Double> function) {
        List<Double> points = getPoints();
        List<Double> weights = getWeights();

        double sum = 0.0;

        for (int i = 0; i < points.size(); i++) {
            double xi = points.get(i);
            double wi = weights.get(i);

            for (int j = 0; j < points.size(); j++) {
                double xj = points.get(j);
                double wj = weights.get(j);

                List<Double> args = List.of(xi, xj);

                sum += wi * wj * function.apply(args);
            }
        }

        return sum;
    }
}
