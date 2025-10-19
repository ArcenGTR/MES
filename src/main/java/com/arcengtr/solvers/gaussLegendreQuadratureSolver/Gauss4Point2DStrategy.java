package com.arcengtr.solvers.gaussLegendreQuadratureSolver;

import java.util.List;

public class Gauss4Point2DStrategy extends AbstractGauss2DStrategy{
    @Override
    protected List<Double> getPoints() {
        return GaussData.POINTS_4;
    }

    @Override
    protected List<Double> getWeights() {
        return GaussData.WEIGHTS_4;
    }
}
