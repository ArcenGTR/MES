package com.arcengtr.services;

import lombok.experimental.UtilityClass;

import java.util.Locale;

@UtilityClass
public class MatrixService {

    public void printMatrix(double[][] matrix) {
        if (matrix == null) {
            System.out.println("null");
            return;
        }

        for (double[] row : matrix) {
            System.out.print("[ ");
            for (double value : row) {
                System.out.printf(Locale.US, "%10.4f ", value);
            }
            System.out.println("]");
        }
    }

    public void printVector(double[] vector) {
        if (vector == null) {
            System.out.println("null");
            return;
        }

        System.out.print("[ ");
        for (double value : vector) {
            System.out.printf(Locale.US, "%10.4f ", value);
        }
        System.out.println(" ]");
    }

    public double[][] multiplyVectorColumnByRow(double[] v1, double[] v2) {
        int n = v1.length;
        int m = v2.length;
        double[][] result = new double[n][m];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                result[i][j] = v1[i] * v2[j];
            }
        }
        return result;
    }

    public double[][] addMatrices(double[][] A, double[][] B) {
        int rows = A.length;
        int cols = A[0].length;
        double[][] result = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result[i][j] = A[i][j] + B[i][j];
            }
        }
        return result;
    }

    public double[][] multiplyMatrixByScalar(double[][] A, double scalar) {
        int rows = A.length;
        int cols = A[0].length;
        double[][] result = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result[i][j] = A[i][j] * scalar;
            }
        }
        return result;
    }
}
