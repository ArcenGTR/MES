package com.arcengtr.solvers.gaussLegendreQuadratureSolver;

import java.util.List;

public class Gauss1PointStrategy extends AbstractGaussStrategy {

    @Override
    protected List<Double> getPoints() {
        return GaussData.POINTS_1;
    }

    @Override
    protected List<Double> getWeights() {
        return GaussData.WEIGHTS_1;
    }
}
