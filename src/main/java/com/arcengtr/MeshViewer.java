package com.arcengtr;

import com.arcengtr.common.Element;
import com.arcengtr.common.Node;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MeshViewer extends JFrame {
    private final List<Node> nodes;
    private final List<Element> elements;

    public MeshViewer(List<Node> nodes, List<Element> elements) {
        this.nodes = nodes;
        this.elements = elements;
        setTitle("Visual");
        setSize(700, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Color.WHITE);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int padding = 150;
        int scale = 6000;

        int offsetX = padding;
        int offsetY = padding;

        for (Element el : elements) {
            int[] nodeIds = el.getNodeId();
            int[] px = new int[4];
            int[] py = new int[4];

            for (int i = 0; i < 4; i++) {
                Node node = nodes.get(nodeIds[i] - 1);
                px[i] = offsetX + (int) (node.getX() * scale);
                py[i] = offsetY - (int) (node.getY() * scale);
            }

            g2.setColor(new Color(173, 216, 230, 100));
            g2.fillPolygon(px, py, 4);

            g2.setColor(Color.DARK_GRAY);
            g2.setStroke(new BasicStroke(2));
            g2.drawPolygon(px, py, 4);
        }

        for (int i = 0; i < nodes.size(); i++) {
            Node node = nodes.get(i);
            int x = offsetX + (int) (node.getX() * scale);
            int y = offsetY - (int) (node.getY() * scale);

            if (node.isBoundary()) {
                g2.setColor(Color.ORANGE);
                g2.fillOval(x - 5, y - 5, 10, 10);
            } else {
                g2.setColor(Color.RED);
                g2.fillOval(x - 4, y - 4, 8, 8);
            }

            g2.setColor(Color.BLACK);
            g2.setFont(new Font("Arial", Font.BOLD, 12));
            g2.drawString(String.valueOf(i + 1), x + 8, y - 8);
        }
    }
}