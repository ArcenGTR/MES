package com.arcengtr;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;

public class ChartWindow extends JFrame {
    private final XYSeries internalSeries = new XYSeries("Inner layer");
    private final XYSeries externalSeries = new XYSeries("Outer layer");
    private final XYSeries middleSeries = new XYSeries("Middle layer");

    public ChartWindow(String title) {
        super(title);
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(internalSeries);
        dataset.addSeries(externalSeries);
        dataset.addSeries(middleSeries);

        JFreeChart chart = ChartFactory.createXYLineChart(
                "Outer and Inner temperature",
                "Time (s)", "Temperature (K)",
                dataset
        );

        ChartPanel chartPanel = new ChartPanel(chart);
        this.setLayout(new BorderLayout());
        this.add(chartPanel, BorderLayout.CENTER);

        this.setSize(800, 500);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void addData(double time, double tInt, double tExt, double tMiddle) {
        internalSeries.add(time, tInt);
        externalSeries.add(time, tExt);
        middleSeries.add(time, tMiddle);
    }
}
