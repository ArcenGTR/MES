package com.arcengtr.solvers.gaussLegendreQuadratureSolver;

import java.util.List;

public class Gauss2PointStrategy extends AbstractGaussStrategy{
    @Override
    protected List<Double> getPoints() {
        return GaussData.POINTS_2;
    }

    @Override
    protected List<Double> getWeights() {
        return GaussData.WEIGHTS_2;
    }
}
