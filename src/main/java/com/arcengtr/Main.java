package com.arcengtr;


import com.arcengtr.common.GlobalData;
import com.arcengtr.common.Grid;
import com.arcengtr.parsers.GlobalDataParser;

import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {
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
    }
}