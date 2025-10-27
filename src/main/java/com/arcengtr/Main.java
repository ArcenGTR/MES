package com.arcengtr;

import com.arcengtr.common.*;
import com.arcengtr.parsers.GlobalDataParser;
import com.arcengtr.services.JacobianService;

import java.nio.file.Path;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        // Global Data parser demo

        try {

            Path file = Path.of("src/main/resources/globalData/Test2_4_4_MixGrid.txt");

            GlobalDataParser parser = new GlobalDataParser();
            GlobalDataParser.ParsedData parsedData = parser.parse(file);

            GlobalData globalData = parsedData.getGlobalData();
            globalData.setNpc(2);
            Grid grid = parsedData.getGrid();

            System.out.println("=== Global Data ===");
            System.out.println(globalData);

            System.out.println("\n=== Nodes ===");
            grid.getNodes().forEach(System.out::println);

            ElemUniv univ = new ElemUniv(globalData.getNpc());

            Node[] allNodes = grid.getNodes().toArray(new Node[0]);
            List<Element> elements = grid.getElements();

            for (Element element : elements) {
                int npc2 = univ.npc * univ.npc;
                Jacobian[] jacobians = new Jacobian[npc2];

                for (int gp = 0; gp < npc2; gp++) {
                    jacobians[gp] = JacobianService.computeJacobian(element, allNodes, univ, gp);
                }

                element.setJacobians(jacobians);

                System.out.println("\n=== Element " + element);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }



        // Numerical Integral Solver

        /*

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
         */
    }
}