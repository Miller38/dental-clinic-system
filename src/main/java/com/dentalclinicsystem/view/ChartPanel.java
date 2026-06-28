package com.dentalclinicsystem.view.components;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import java.awt.*;

public class ChartPanel extends JPanel {
    
    // ================================================================
    // ============== COLORES =========================================
    // ================================================================
    
    private Color darkBg = new Color(30, 30, 35);
    private Color darkCard = new Color(40, 40, 45);
    private Color textLight = new Color(220, 220, 230);
    private Color textGray = new Color(150, 150, 165);
    private Color accentBlue = new Color(70, 130, 200);
    private Color accentGreen = new Color(60, 180, 110);
    private Color accentOrange = new Color(230, 160, 50);
    private Color accentPurple = new Color(150, 80, 200);
    private Color accentRed = new Color(210, 80, 80);
    
    private org.jfree.chart.ChartPanel jfreeChartPanel;

    // ================================================================
    // ============== CONSTRUCTOR =====================================
    // ================================================================
    
    public ChartPanel() {
        setLayout(new GridBagLayout());
        setBackground(darkBg);
        this.jfreeChartPanel = null;
    }

    // ================================================================
    // ============== MÉTODOS PÚBLICOS ================================
    // ================================================================

    public void createPacientesChart(int[] datosPorMes) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        String[] meses = {"Ene", "Feb", "Mar", "Abr", "May", "Jun", 
                          "Jul", "Ago", "Sep", "Oct", "Nov", "Dic"};
        
        for (int i = 0; i < datosPorMes.length && i < 12; i++) {
            dataset.addValue(datosPorMes[i], "Pacientes", meses[i]);
        }
        
        JFreeChart chart = ChartFactory.createBarChart(
            "📈 Pacientes Nuevos por Mes",
            "Mes",
            "Cantidad",
            dataset,
            PlotOrientation.VERTICAL,
            true,
            true,
            false
        );
        
        aplicarEstiloDark(chart);
        agregarChart(chart);
    }

    public void createCitasPorOdontologoChart(Object[] odontologos, int[] cantidades) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        for (int i = 0; i < odontologos.length; i++) {
            dataset.addValue(cantidades[i], "Citas", odontologos[i].toString());
        }
        
        JFreeChart chart = ChartFactory.createBarChart(
            "📊 Citas por Odontólogo",
            "Odontólogo",
            "Cantidad de Citas",
            dataset,
            PlotOrientation.HORIZONTAL,
            true,
            true,
            false
        );
        
        aplicarEstiloDark(chart);
        agregarChart(chart);
    }

    public void createPagosChart(String[] metodos, double[] montos) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        
        for (int i = 0; i < metodos.length; i++) {
            dataset.setValue(metodos[i], montos[i]);
        }
        
        JFreeChart chart = ChartFactory.createPieChart(
            "💰 Distribución de Pagos",
            dataset,
            true,
            true,
            false
        );
        
        aplicarEstiloDark(chart);
        agregarChart(chart);
    }

    public void createIngresosChart(String[] fechas, double[] montos) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        for (int i = 0; i < fechas.length; i++) {
            dataset.addValue(montos[i], "Ingresos", fechas[i]);
        }
        
        JFreeChart chart = ChartFactory.createLineChart(
            "📈 Ingresos Diarios",
            "Fecha",
            "Monto ($)",
            dataset,
            PlotOrientation.VERTICAL,
            true,
            true,
            false
        );
        
        aplicarEstiloDark(chart);
        agregarChart(chart);
    }

    public void createResumenChart(int totalPacientes, int totalCitas, double totalIngresos, 
                                   int totalServicios, int totalInsumos, int stockBajo) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        dataset.addValue(totalPacientes, "Total", "Pacientes");
        dataset.addValue(totalCitas, "Total", "Citas");
        dataset.addValue(totalIngresos / 100, "Total", "Ingresos (x100)");
        dataset.addValue(totalServicios, "Total", "Servicios");
        dataset.addValue(totalInsumos, "Total", "Insumos");
        dataset.addValue(stockBajo, "Total", "Stock Bajo");
        
        JFreeChart chart = ChartFactory.createBarChart(
            "📊 Resumen General",
            "Categoría",
            "Cantidad",
            dataset,
            PlotOrientation.VERTICAL,
            true,
            true,
            false
        );
        
        aplicarEstiloDark(chart);
        agregarChart(chart);
    }

    public void clear() {
        removeAll();
        this.jfreeChartPanel = null;
        revalidate();
        repaint();
    }

    // ================================================================
    // ============== MÉTODOS PRIVADOS ================================
    // ================================================================

    private void aplicarEstiloDark(JFreeChart chart) {
        chart.setBackgroundPaint(darkBg);
        chart.getTitle().setPaint(textLight);
        chart.getTitle().setFont(new Font("Segoe UI", Font.BOLD, 16));
        
        if (chart.getLegend() != null) {
            chart.getLegend().setBackgroundPaint(darkCard);
            chart.getLegend().setItemPaint(textLight);
        }
        
        Plot plot = chart.getPlot();
        
        if (plot instanceof CategoryPlot) {
            CategoryPlot categoryPlot = (CategoryPlot) plot;
            categoryPlot.setBackgroundPaint(darkCard);
            categoryPlot.setDomainGridlinePaint(new Color(60, 60, 65));
            categoryPlot.setRangeGridlinePaint(new Color(60, 60, 65));
            
            categoryPlot.getDomainAxis().setLabelPaint(textLight);
            categoryPlot.getDomainAxis().setTickLabelPaint(textGray);
            categoryPlot.getRangeAxis().setLabelPaint(textLight);
            categoryPlot.getRangeAxis().setTickLabelPaint(textGray);
            
            if (categoryPlot.getRenderer() instanceof BarRenderer) {
                BarRenderer renderer = (BarRenderer) categoryPlot.getRenderer();
                renderer.setSeriesPaint(0, accentBlue);
            }
            
        } else if (plot instanceof PiePlot) {
            PiePlot piePlot = (PiePlot) plot;
            piePlot.setBackgroundPaint(darkBg);
            piePlot.setOutlineVisible(false);
            piePlot.setShadowPaint(null);
            
            Color[] colores = {accentBlue, accentGreen, accentOrange, accentPurple, accentRed};
            int index = 0;
            for (Object key : piePlot.getDataset().getKeys()) {
                if (index < colores.length) {
                    piePlot.setSectionPaint(key.toString(), colores[index]);
                    index++;
                }
            }
            
            piePlot.setLabelFont(new Font("Segoe UI", Font.PLAIN, 11));
            piePlot.setLabelPaint(textLight);
            piePlot.setLabelBackgroundPaint(new Color(40, 40, 45, 200));
        }
    }

    private void agregarChart(JFreeChart chart) {
        removeAll();
        
        org.jfree.chart.ChartPanel jfreePanel = new org.jfree.chart.ChartPanel(chart);
        jfreePanel.setBackground(darkBg);
        jfreePanel.setPreferredSize(new Dimension(600, 400));
        jfreePanel.setMouseWheelEnabled(true);
        
        jfreePanel.setZoomInFactor(2.0);
        jfreePanel.setZoomOutFactor(0.5);
        jfreePanel.setZoomAroundAnchor(true);
        
        this.jfreeChartPanel = jfreePanel;
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        add(jfreePanel, gbc);
        
        revalidate();
        repaint();
    }

    // ================================================================
    // ============== GETTERS =========================================
    // ================================================================

    public org.jfree.chart.ChartPanel getJFreeChartPanel() {
        return jfreeChartPanel;
    }

    public boolean hasChart() {
        return jfreeChartPanel != null;
    }
}