package com.arcengtr;

import com.arcengtr.solvers.gaussLegendreQuadratureSolver.*;

import java.util.List;
import java.util.function.Function;

import static java.lang.Math.pow;

public class Main {
    public static void main(String[] args) {

        // Global Data parser demo
        /*
        try {

            Path file = Path.of("src/main/resources/globalData/Test3_31_31_kwadrat.txt");

            GlobalDataParser parser = new GlobalDataParser();
            GlobalDataParser.ParsedData parsedData = parser.parse(file);

            GlobalData globalData = parsedData.getGlobalData();
            Grid grid = parsedData.getGrid();

            System.out.println("=== Global Data ===");
            System.out.println(globalData);

            System.out.println("\n=== Nodes ===");
            grid.getNodes().forEach(System.out::println);

            System.out.println("\n=== Elements ===");
            grid.getElements().forEach(System.out::println);

        } catch (Exception e) {
            e.printStackTrace();
        }
         */

        // Numerical Integral Solver

        Function<List<Double>, Double> demoFun = (arguments) -> {
            Double x = arguments.getFirst();
            return 5 * pow(x, 2) + 3 * x + 6;
        };

        NumericalIntegrationStrategy strategy2 = new Gauss2PointStrategy();
        double result2 = strategy2.integrate(demoFun);

        NumericalIntegrationStrategy strategy4 = new Gauss4PointStrategy();
        double result4 = strategy4.integrate(demoFun);

        System.out.println("2 Points: " + result2);
        System.out.println("4 Points: " + result4);

        Function<List<Double>, Double> demoFun2 = (arguments) -> {
            Double x = arguments.get(0);
            Double t = arguments.get(1);

            return 5 * pow(x, 2) * pow(t, 2) + 3 * x * t + 6;
        };

        NumericalIntegration2DStrategy strategy3_2d = new Gauss3Point2DStrategy();
        NumericalIntegration2DStrategy strategy4_2d = new Gauss4Point2DStrategy();
        double result3_2d = strategy3_2d.integrate(demoFun2);
        double result4_2d = strategy4_2d.integrate(demoFun2);

        System.out.println("3 Points: " + result3_2d);
        System.out.println("4 Points: " + result4_2d);
    }
}