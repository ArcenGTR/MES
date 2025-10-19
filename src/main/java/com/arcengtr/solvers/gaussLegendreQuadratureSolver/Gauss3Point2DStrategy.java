package com.arcengtr.solvers.gaussLegendreQuadratureSolver;

import java.util.List;

public class Gauss3Point2DStrategy extends AbstractGauss2DStrategy{
    @Override
    protected List<Double> getPoints() {
        return GaussData.POINTS_3;
    }

    @Override
    protected List<Double> getWeights() {
        return GaussData.WEIGHTS_3;
    }
}
