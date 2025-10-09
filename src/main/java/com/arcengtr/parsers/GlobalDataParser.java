package com.arcengtr.parsers;

import com.arcengtr.common.Element;
import com.arcengtr.common.GlobalData;
import com.arcengtr.common.Grid;
import com.arcengtr.common.Node;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GlobalDataParser {

    @Getter
    @Setter
    @AllArgsConstructor
    public static class ParsedData {
        private final GlobalData globalData;
        private final Grid grid;
    }

    public ParsedData parse(Path filePath) throws IOException {
        List<String> lines = Files.readAllLines(filePath).stream()
                .map(String::trim)
                .filter(l -> !l.isEmpty())
                .toList();

        GlobalData.GlobalDataBuilder globalDataBuilder = GlobalData.builder();
        List<Node> nodes = new ArrayList<>();
        List<Element> elements = new ArrayList<>();

        int nN = 0;
        int nE = 0;

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);

            if (line.startsWith("SimulationTime")) globalDataBuilder.simulationTime(readValue(line));
            else if (line.startsWith("SimulationStepTime")) globalDataBuilder.simulationStepTime(readValue(line));
            else if (line.startsWith("Conductivity")) globalDataBuilder.conductivity(readValue(line));
            else if (line.startsWith("Alfa")) globalDataBuilder.alfa(readValue(line));
            else if (line.startsWith("Tot")) globalDataBuilder.tot(readValue(line));
            else if (line.startsWith("InitialTemp")) globalDataBuilder.initialTemp(readValue(line));
            else if (line.startsWith("Density")) globalDataBuilder.density(readValue(line));
            else if (line.startsWith("SpecificHeat")) globalDataBuilder.specificHeat(readValue(line));
            else if (line.startsWith("Nodes number")) {
                nN = (int) readValue(line);
                globalDataBuilder.nN(nN);
            }
            else if (line.startsWith("Elements number")) {
                nE = (int) readValue(line);
                globalDataBuilder.nE(nE);
            }
            else if (line.startsWith("*Node")) {
                for (int j = 0; j < nN; j++) {
                    i++;
                    String[] parts = lines.get(i).split(",");
                    if (parts.length >= 3) {
                        double x = Double.parseDouble(parts[1].trim());
                        double y = Double.parseDouble(parts[2].trim());
                        nodes.add(Node.builder().x(x).y(y).build());
                    }
                }
            }

            else if (line.startsWith("*Element")) {
                for (int j = 0; j < nE; j++) {
                    i++;
                    String[] parts = lines.get(i).split(",");
                    if (parts.length >= 5) {
                        int[] ids = new int[]{
                                Integer.parseInt(parts[1].trim()),
                                Integer.parseInt(parts[2].trim()),
                                Integer.parseInt(parts[3].trim()),
                                Integer.parseInt(parts[4].trim())
                        };
                        elements.add(Element.builder().ID(ids).build());
                    }
                }
            }
        }

        GlobalData globalData = globalDataBuilder.build();
        Grid grid = Grid.builder()
                .nN(nN)
                .nE(nE)
                .nodes(nodes)
                .elements(elements)
                .build();

        return new ParsedData(globalData, grid);

    }

    private double readValue(String line) {
        Pattern p = Pattern.compile("[-+]?[0-9]*\\.?[0-9]+");
        Matcher m = p.matcher(line);
        return m.find() ? Double.parseDouble(m.group()) : 0;
    }

}
