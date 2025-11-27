package com.arcengtr.solvers.GaussEliminationLinearSolver;

import lombok.experimental.UtilityClass;

import java.util.Arrays;

@UtilityClass
public class LinearSolver {

    private static final double EPSILON = 1e-10;

    public static double[] solveLinearSystem(double[][] A, double[] b) {
        int N = A.length;

        double[][] L_A = new double[N][N];
        double[] L_b = Arrays.copyOf(b, N);

        for (int i = 0; i < N; i++) {
            L_A[i] = Arrays.copyOf(A[i], N);
        }

        for (int k = 0; k < N; k++) {

            int max = k;
            for (int i = k + 1; i < N; i++) {
                if (Math.abs(L_A[i][k]) > Math.abs(L_A[max][k])) {
                    max = i;
                }
            }

            double[] tempA = L_A[k];
            L_A[k] = L_A[max];
            L_A[max] = tempA;

            double tempB = L_b[k];
            L_b[k] = L_b[max];
            L_b[max] = tempB;

            if (Math.abs(L_A[k][k]) <= EPSILON) {
                throw new ArithmeticException("Division by zero in linear solver (H_total)!");
            }

            for (int i = k + 1; i < N; i++) {
                double factor = L_A[i][k] / L_A[k][k];

                L_b[i] -= factor * L_b[k];

                for (int j = k; j < N; j++) {
                    L_A[i][j] -= factor * L_A[k][j];
                }
            }
        }

        double[] x = new double[N];

        for (int i = N - 1; i >= 0; i--) {
            double sum = 0.0;
            for (int j = i + 1; j < N; j++) {
                sum += L_A[i][j] * x[j];
            }

            x[i] = (L_b[i] - sum) / L_A[i][i];
        }

        return x;
    }
}
