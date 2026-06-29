package com.dentalclinicsystem.view;

import com.dentalclinicsystem.controller.ReporteController;
import com.dentalclinicsystem.model.Cita;
import com.dentalclinicsystem.model.Paciente;
import com.dentalclinicsystem.model.Venta;
import com.dentalclinicsystem.util.ExportUtil;
import com.dentalclinicsystem.util.FechaUtil;
import com.dentalclinicsystem.view.components.ChartPanel;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public class PanelReportes extends JPanel {

    private ReporteController controller;

    // Componentes
    private JTable tablaPacientes, tablaCitas, tablaVentas;
    private DefaultTableModel modelPacientes, modelCitas, modelVentas;
    private JLabel lblTotalPacientes, lblTotalCitas, lblTotalVentas;
    private JLabel lblResumenPacientes, lblResumenCitas, lblResumenVentas;
    private JLabel lblResumenServicios, lblResumenInsumos, lblResumenStockBajo;
    private JDateChooser dateInicio;
    private JDateChooser dateFin;
    private JTextField txtCitasInicio, txtCitasFin;
    private JTextField txtVentasInicio, txtVentasFin;
    private ChartPanel chartPanel;
    private JComboBox<String> cbGraficas;

    // Colores
    private final Color darkBg = new Color(30, 30, 35);
    private final Color darkCard = new Color(40, 40, 45);
    private final Color textLight = new Color(220, 220, 230);
    private final Color textGray = new Color(150, 150, 165);
    private final Color accentBlue = new Color(70, 130, 200);
    private final Color accentGreen = new Color(60, 180, 110);
    private final Color accentOrange = new Color(230, 160, 50);
    private final Color accentPurple = new Color(150, 80, 200);
    private final Color accentRed = new Color(210, 80, 80);

    private final Color hoverPacientes = new Color(70, 130, 200);
    private final Color hoverCitas = new Color(60, 180, 110);
    private final Color hoverVentas = new Color(230, 160, 50);
    private final Color hoverServicios = new Color(150, 80, 200);
    private final Color hoverInsumos = new Color(200, 100, 150);
    private final Color hoverStockBajo = new Color(210, 80, 80);

    public PanelReportes() {
        this.controller = new ReporteController();
        initComponents();
        cargarResumenGeneral();
        cargarReportePacientes();
        cargarReporteCitas();
        cargarReporteVentas();
    }

    // ================================================================
    // ============== INICIALIZACIÓN ===================================
    // ================================================================

    private void initComponents() {
        setBackground(darkBg);
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel resumenPanel = createResumenPanel();
        add(resumenPanel, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(darkBg);
        tabbedPane.setForeground(textLight);
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        tabbedPane.addTab(" Pacientes", createPanelPacientes());
        tabbedPane.addTab(" Citas", createPanelCitas());
        tabbedPane.addTab(" Ventas", createPanelVentas());
        tabbedPane.addTab(" Resumen", createPanelResumenDetallado());
        tabbedPane.addTab(" Gráficas", createPanelGraficas());

        add(tabbedPane, BorderLayout.CENTER);
    }

    // ================================================================
    // ============== PANEL DE PACIENTES ===============================
    // ================================================================

    private JPanel createPanelPacientes() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(darkBg);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Panel de filtros
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        filterPanel.setBackground(darkBg);

        JLabel lblInicio = new JLabel("Desde:");
        lblInicio.setForeground(textGray);
        filterPanel.add(lblInicio);

        dateInicio = new JDateChooser();
        dateInicio.setDate(Date.from(LocalDate.now().withDayOfMonth(1).atStartOfDay(java.time.ZoneId.systemDefault()).toInstant()));
        dateInicio.setDateFormatString("yyyy-MM-dd");
        dateInicio.setBackground(new Color(50, 50, 55));
        dateInicio.setForeground(textLight);
        dateInicio.getCalendarButton().setBackground(new Color(60, 60, 65));
        dateInicio.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        filterPanel.add(dateInicio);

        JLabel lblFin = new JLabel("Hasta:");
        lblFin.setForeground(textGray);
        filterPanel.add(lblFin);

        dateFin = new JDateChooser();
        dateFin.setDate(new Date());
        dateFin.setDateFormatString("yyyy-MM-dd");
        dateFin.setBackground(new Color(50, 50, 55));
        dateFin.setForeground(textLight);
        dateFin.getCalendarButton().setBackground(new Color(60, 60, 65));
        dateFin.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        filterPanel.add(dateFin);

        JButton btnFiltrar = createActionButton(" Filtrar", accentBlue);
        btnFiltrar.addActionListener(e -> cargarReportePacientes());
        filterPanel.add(btnFiltrar);

        JButton btnExportar = createActionButton(" Exportar", accentGreen);
        btnExportar.addActionListener(e -> {
            if (modelPacientes.getRowCount() > 0) {
                ExportUtil.exportarConDialogo(modelPacientes, "Reporte_Pacientes", this);
            } else {
                JOptionPane.showMessageDialog(this, "No hay datos para exportar", "Advertencia", JOptionPane.WARNING_MESSAGE);
            }
        });
        filterPanel.add(btnExportar);

        panel.add(filterPanel, BorderLayout.NORTH);

        // Tabla
        String[] columnas = {"ID", "Nombre", "Documento", "Teléfono", "Email", "Género", "Edad", "Registro"};
        modelPacientes = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaPacientes = new JTable(modelPacientes);
        tablaPacientes.setBackground(new Color(45, 45, 50));
        tablaPacientes.setForeground(textLight);
        tablaPacientes.setGridColor(new Color(55, 55, 60));
        tablaPacientes.setRowHeight(28);
        tablaPacientes.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaPacientes.getTableHeader().setBackground(new Color(50, 50, 55));
        tablaPacientes.getTableHeader().setForeground(textLight);
        tablaPacientes.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        JScrollPane scroll = new JScrollPane(tablaPacientes);
        scroll.setBackground(darkCard);
        scroll.getViewport().setBackground(darkCard);

        panel.add(scroll, BorderLayout.CENTER);

        // Footer
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setBackground(darkBg);
        lblTotalPacientes = new JLabel("Total: 0 pacientes");
        lblTotalPacientes.setForeground(textGray);
        footerPanel.add(lblTotalPacientes);
        panel.add(footerPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void cargarReportePacientes() {
        modelPacientes.setRowCount(0);

        String inicio = dateInicio.getDate() != null ?
                FechaUtil.dateToString(dateInicio.getDate()) : FechaUtil.primerDiaDelMes();
        String fin = dateFin.getDate() != null ?
                FechaUtil.dateToString(dateFin.getDate()) : FechaUtil.hoy();

        List<Paciente> pacientes = controller.getReportePacientesPorFechas(inicio, fin);

        if (pacientes != null) {
            for (Paciente p : pacientes) {
                modelPacientes.addRow(new Object[]{
                        p.getId(),
                        p.getNombreCompleto(),
                        p.getNumeroDocumento(),
                        p.getTelefono(),
                        p.getEmail() != null ? p.getEmail() : "",
                        p.getGenero() != null ? p.getGenero() : "",
                        p.getEdad() > 0 ? p.getEdad() : "",
                        p.getFechaRegistro() != null && p.getFechaRegistro().length() >= 10 ?
                                p.getFechaRegistro().substring(0, 10) : ""
                });
            }
        }

        lblTotalPacientes.setText("Total: " + modelPacientes.getRowCount() + " pacientes");
    }

    // ================================================================
    // ============== PANEL DE CITAS ===================================
    // ================================================================

    private JPanel createPanelCitas() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(darkBg);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Panel de filtros
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        filterPanel.setBackground(darkBg);

        JLabel lblInicio = new JLabel("Desde:");
        lblInicio.setForeground(textGray);
        filterPanel.add(lblInicio);

        txtCitasInicio = new JTextField(10);
        txtCitasInicio.setText(LocalDate.now().withDayOfMonth(1).toString());
        txtCitasInicio.setBackground(new Color(50, 50, 55));
        txtCitasInicio.setForeground(textLight);
        txtCitasInicio.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 65)));
        txtCitasInicio.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        filterPanel.add(txtCitasInicio);

        JLabel lblFin = new JLabel("Hasta:");
        lblFin.setForeground(textGray);
        filterPanel.add(lblFin);

        txtCitasFin = new JTextField(10);
        txtCitasFin.setText(LocalDate.now().toString());
        txtCitasFin.setBackground(new Color(50, 50, 55));
        txtCitasFin.setForeground(textLight);
        txtCitasFin.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 65)));
        txtCitasFin.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        filterPanel.add(txtCitasFin);

        JButton btnFiltrarCitas = createActionButton(" Filtrar", accentBlue);
        btnFiltrarCitas.addActionListener(e -> cargarReporteCitas());
        filterPanel.add(btnFiltrarCitas);

        JButton btnExportarCitas = createActionButton(" Exportar", accentGreen);
        btnExportarCitas.addActionListener(e -> {
            if (modelCitas.getRowCount() > 0) {
                ExportUtil.exportarConDialogo(modelCitas, "Reporte_Citas", this);
            } else {
                JOptionPane.showMessageDialog(this, "No hay datos para exportar", "Advertencia", JOptionPane.WARNING_MESSAGE);
            }
        });
        filterPanel.add(btnExportarCitas);

        panel.add(filterPanel, BorderLayout.NORTH);

        // Tabla
        String[] columnas = {"ID", "Fecha", "Hora", "Paciente", "Odontólogo", "Servicio", "Estado"};
        modelCitas = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaCitas = new JTable(modelCitas);
        tablaCitas.setBackground(new Color(45, 45, 50));
        tablaCitas.setForeground(textLight);
        tablaCitas.setGridColor(new Color(55, 55, 60));
        tablaCitas.setRowHeight(28);
        tablaCitas.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaCitas.getTableHeader().setBackground(new Color(50, 50, 55));
        tablaCitas.getTableHeader().setForeground(textLight);
        tablaCitas.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        JScrollPane scroll = new JScrollPane(tablaCitas);
        scroll.setBackground(darkCard);
        scroll.getViewport().setBackground(darkCard);

        panel.add(scroll, BorderLayout.CENTER);

        // Footer
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setBackground(darkBg);
        lblTotalCitas = new JLabel("Total: 0 citas");
        lblTotalCitas.setForeground(textGray);
        footerPanel.add(lblTotalCitas);
        panel.add(footerPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void cargarReporteCitas() {
        modelCitas.setRowCount(0);

        String inicio = txtCitasInicio.getText().trim();
        String fin = txtCitasFin.getText().trim();

        if (inicio.isEmpty()) {
            inicio = FechaUtil.primerDiaDelMes();
        }
        if (fin.isEmpty()) {
            fin = FechaUtil.hoy();
        }

        List<Cita> citas = controller.getReporteCitas(inicio, fin);

        if (citas != null) {
            for (Cita c : citas) {
                modelCitas.addRow(new Object[]{
                        c.getId(),
                        c.getFecha() != null ? c.getFecha() : "",
                        c.getHora() != null ? c.getHora() : "",
                        c.getPacienteNombre() != null ? c.getPacienteNombre() : "N/A",
                        c.getOdontologoNombre() != null ? c.getOdontologoNombre() : "N/A",
                        c.getServicioNombre() != null ? c.getServicioNombre() : "N/A",
                        c.getEstado() != null ? c.getEstado() : ""
                });
            }
        }

        lblTotalCitas.setText("Total: " + modelCitas.getRowCount() + " citas");
    }

    // ================================================================
    // ============== PANEL DE VENTAS ==================================
    // ================================================================

    private JPanel createPanelVentas() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(darkBg);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Panel de filtros
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        filterPanel.setBackground(darkBg);

        JLabel lblInicio = new JLabel("Desde:");
        lblInicio.setForeground(textGray);
        filterPanel.add(lblInicio);

        txtVentasInicio = new JTextField(10);
        txtVentasInicio.setText(LocalDate.now().withDayOfMonth(1).toString());
        txtVentasInicio.setBackground(new Color(50, 50, 55));
        txtVentasInicio.setForeground(textLight);
        txtVentasInicio.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 65)));
        txtVentasInicio.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        filterPanel.add(txtVentasInicio);

        JLabel lblFin = new JLabel("Hasta:");
        lblFin.setForeground(textGray);
        filterPanel.add(lblFin);

        txtVentasFin = new JTextField(10);
        txtVentasFin.setText(LocalDate.now().toString());
        txtVentasFin.setBackground(new Color(50, 50, 55));
        txtVentasFin.setForeground(textLight);
        txtVentasFin.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 65)));
        txtVentasFin.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        filterPanel.add(txtVentasFin);

        JButton btnFiltrarVentas = createActionButton(" Filtrar", accentBlue);
        btnFiltrarVentas.addActionListener(e -> cargarReporteVentas());
        filterPanel.add(btnFiltrarVentas);

        JButton btnExportarVentas = createActionButton(" Exportar", accentGreen);
        btnExportarVentas.addActionListener(e -> {
            if (modelVentas.getRowCount() > 0) {
                ExportUtil.exportarConDialogo(modelVentas, "Reporte_Ventas", this);
            } else {
                JOptionPane.showMessageDialog(this, "No hay datos para exportar", "Advertencia", JOptionPane.WARNING_MESSAGE);
            }
        });
        filterPanel.add(btnExportarVentas);

        panel.add(filterPanel, BorderLayout.NORTH);

        // Tabla
        String[] columnas = {"ID", "Fecha", "Paciente", "Tipo", "Total", "Pago", "Estado"};
        modelVentas = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaVentas = new JTable(modelVentas);
        tablaVentas.setBackground(new Color(45, 45, 50));
        tablaVentas.setForeground(textLight);
        tablaVentas.setGridColor(new Color(55, 55, 60));
        tablaVentas.setRowHeight(28);
        tablaVentas.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaVentas.getTableHeader().setBackground(new Color(50, 50, 55));
        tablaVentas.getTableHeader().setForeground(textLight);
        tablaVentas.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        JScrollPane scroll = new JScrollPane(tablaVentas);
        scroll.setBackground(darkCard);
        scroll.getViewport().setBackground(darkCard);

        panel.add(scroll, BorderLayout.CENTER);

        // Footer
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setBackground(darkBg);
        lblTotalVentas = new JLabel("Total: 0 ventas");
        lblTotalVentas.setForeground(textGray);
        footerPanel.add(lblTotalVentas);
        panel.add(footerPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void cargarReporteVentas() {
        modelVentas.setRowCount(0);

        String inicio = txtVentasInicio.getText().trim();
        String fin = txtVentasFin.getText().trim();

        if (inicio.isEmpty()) {
            inicio = FechaUtil.primerDiaDelMes();
        }
        if (fin.isEmpty()) {
            fin = FechaUtil.hoy();
        }

        List<Venta> ventas = controller.getReporteVentas(inicio, fin);

        if (ventas != null) {
            for (Venta v : ventas) {
                modelVentas.addRow(new Object[]{
                        v.getId(),
                        v.getFecha() != null && v.getFecha().length() >= 10 ?
                                v.getFecha().substring(0, 10) : "",
                        v.getPacienteNombre() != null ? v.getPacienteNombre() : "N/A",
                        v.getTipoComprobante() != null ? v.getTipoComprobante() : "",
                        v.getTotalFormateado() != null ? v.getTotalFormateado() : "0.00",
                        v.getMetodoPago() != null ? v.getMetodoPago() : "",
                        v.getEstado() != null ? v.getEstado() : ""
                });
            }
        }

        lblTotalVentas.setText("Total: " + modelVentas.getRowCount() + " ventas");
    }

    // ================================================================
    // ============== PANEL DE GRÁFICAS ================================
    // ================================================================

    private JPanel createPanelGraficas() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(darkBg);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        controlPanel.setBackground(darkBg);

        JLabel lblTitulo = new JLabel("Seleccione una gráfica:");
        lblTitulo.setForeground(textLight);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        controlPanel.add(lblTitulo);

        String[] opcionesGraficas = {
                "Pacientes por Mes",
                "Citas por Odontólogo",
                "Distribución de Pagos",
                "Ingresos Diarios",
                "Resumen General"
        };

        cbGraficas = new JComboBox<>(opcionesGraficas);
        cbGraficas.setBackground(new Color(50, 50, 55));
        cbGraficas.setForeground(textLight);
        cbGraficas.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cbGraficas.setPreferredSize(new Dimension(200, 30));
        controlPanel.add(cbGraficas);

        JButton btnGenerar = createActionButton("Generar Gráfica", accentBlue);
        btnGenerar.addActionListener(e -> generarGrafica(cbGraficas.getSelectedIndex()));
        controlPanel.add(btnGenerar);

        JButton btnLimpiar = createActionButton("Limpiar", accentOrange);
        btnLimpiar.addActionListener(e -> {
            if (chartPanel != null) {
                chartPanel.clear();
            }
        });
        controlPanel.add(btnLimpiar);

        panel.add(controlPanel, BorderLayout.NORTH);

        chartPanel = new ChartPanel();
        JScrollPane scrollChart = new JScrollPane(chartPanel);
        scrollChart.setBackground(darkBg);
        scrollChart.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        scrollChart.getViewport().setBackground(darkBg);

        panel.add(scrollChart, BorderLayout.CENTER);

        return panel;
    }

    private void generarGrafica(int tipo) {
        if (chartPanel == null) return;

        String[] resumen = controller.getResumenGeneral();
        if (resumen == null || resumen.length < 6) {
            JOptionPane.showMessageDialog(this, "No hay datos para generar la gráfica", "Sin Datos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            switch (tipo) {
                case 0: // Pacientes por Mes
                    int[] pacientesPorMes = controller.getPacientesPorMes();
                    if (pacientesPorMes != null) {
                        chartPanel.createPacientesChart(pacientesPorMes);
                    }
                    break;

                case 1: // Citas por Odontólogo
                    String inicio = FechaUtil.primerDiaDelMes();
                    String fin = FechaUtil.hoy();
                    List<Object[]> citasPorOdontologo = controller.getCitasPorOdontologo(inicio, fin);
                    if (citasPorOdontologo != null && !citasPorOdontologo.isEmpty()) {
                        String[] odontologos = new String[citasPorOdontologo.size()];
                        int[] cantidades = new int[citasPorOdontologo.size()];
                        for (int i = 0; i < citasPorOdontologo.size(); i++) {
                            Object[] row = citasPorOdontologo.get(i);
                            odontologos[i] = row[0].toString();
                            cantidades[i] = Integer.parseInt(row[1].toString());
                        }
                        chartPanel.createCitasPorOdontologoChart(odontologos, cantidades);
                    } else {
                        JOptionPane.showMessageDialog(this, "No hay datos de citas por odontólogo", "Sin Datos", JOptionPane.WARNING_MESSAGE);
                    }
                    break;

                case 2: // Distribución de Pagos
                    List<Object[]> pagos = controller.getIngresosPorMetodoPago(
                            FechaUtil.primerDiaDelMes(),
                            FechaUtil.hoy()
                    );
                    if (pagos != null && !pagos.isEmpty()) {
                        String[] metodos = new String[pagos.size()];
                        double[] montos = new double[pagos.size()];
                        for (int i = 0; i < pagos.size(); i++) {
                            Object[] row = pagos.get(i);
                            metodos[i] = row[0].toString();
                            montos[i] = Double.parseDouble(row[1].toString());
                        }
                        chartPanel.createPagosChart(metodos, montos);
                    } else {
                        JOptionPane.showMessageDialog(this, "No hay datos de pagos", "Sin Datos", JOptionPane.WARNING_MESSAGE);
                    }
                    break;

                case 3: // Ingresos Diarios
                    List<Object[]> ingresos = controller.getIngresosPorDia(
                            FechaUtil.primerDiaDelMes(),
                            FechaUtil.hoy()
                    );
                    if (ingresos != null && !ingresos.isEmpty()) {
                        String[] fechas = new String[ingresos.size()];
                        double[] montos = new double[ingresos.size()];
                        for (int i = 0; i < ingresos.size(); i++) {
                            Object[] row = ingresos.get(i);
                            fechas[i] = row[0].toString();
                            montos[i] = Double.parseDouble(row[1].toString());
                        }
                        chartPanel.createIngresosChart(fechas, montos);
                    } else {
                        JOptionPane.showMessageDialog(this, "No hay datos de ingresos", "Sin Datos", JOptionPane.WARNING_MESSAGE);
                    }
                    break;

                case 4: // Resumen General
                    double ingresosDouble = FechaUtil.parseMoneda(resumen[2]);
                    chartPanel.createResumenChart(
                            Integer.parseInt(resumen[0]),
                            Integer.parseInt(resumen[1]),
                            ingresosDouble,
                            Integer.parseInt(resumen[3]),
                            Integer.parseInt(resumen[4]),
                            Integer.parseInt(resumen[5])
                    );
                    break;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Error al convertir datos numéricos: " + e.getMessage(), "Error de Datos", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al generar gráfica: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    // ================================================================
    // ============== PANEL DE RESUMEN =================================
    // ================================================================

    private JPanel createResumenPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 3, 15, 8));
        panel.setBackground(darkBg);
        panel.setBorder(new EmptyBorder(0, 0, 15, 0));
        panel.setPreferredSize(new Dimension(0, 80));

        lblResumenPacientes = createResumenCard(" Pacientes ", "0", hoverPacientes);
        lblResumenCitas = createResumenCard(" Citas Hoy ", "0", hoverCitas);
        lblResumenVentas = createResumenCard(" Ingresos Hoy ", "$0.00", hoverVentas);
        lblResumenServicios = createResumenCard(" Servicios ", "0", hoverServicios);
        lblResumenInsumos = createResumenCard(" Insumos ", "0", hoverInsumos);
        lblResumenStockBajo = createResumenCard(" Stock Bajo ", "0", hoverStockBajo);

        panel.add(lblResumenPacientes);
        panel.add(lblResumenCitas);
        panel.add(lblResumenVentas);
        panel.add(lblResumenServicios);
        panel.add(lblResumenInsumos);
        panel.add(lblResumenStockBajo);

        return panel;
    }

    private JLabel createResumenCard(String titulo, String valor, Color hoverColor) {
        JLabel label = new JLabel();
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        label.setForeground(textLight);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(50, 50, 55), 1),
                new EmptyBorder(8, 10, 8, 10)
        ));
        label.setBackground(darkCard);
        label.setOpaque(true);
        label.setText(titulo + ": " + valor);
        label.setCursor(new Cursor(Cursor.HAND_CURSOR));

        Color colorOriginal = darkCard;
        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                label.setBackground(hoverColor);
                label.setForeground(Color.WHITE);
                label.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(hoverColor.brighter(), 2),
                        new EmptyBorder(8, 10, 8, 10)
                ));
                label.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                label.setBackground(colorOriginal);
                label.setForeground(textLight);
                label.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(50, 50, 55), 1),
                        new EmptyBorder(8, 10, 8, 10)
                ));
                label.repaint();
            }
        });
        return label;
    }

    private JPanel createPanelResumenDetallado() {
        JPanel panel = new JPanel(new GridLayout(3, 3, 15, 15));
        panel.setBackground(darkBg);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        String[] titulos = {" Pacientes", " Citas", " Ingresos", " Servicios", " Insumos", " Stock Bajo"};
        Color[] coloresHover = {hoverPacientes, hoverCitas, hoverVentas, hoverServicios, hoverInsumos, hoverStockBajo};

        for (int i = 0; i < titulos.length; i++) {
            JPanel card = createCardConHover(titulos[i], "0", coloresHover[i]);
            panel.add(card);
        }

        cargarResumenDetallado(panel);
        return panel;
    }

    private JPanel createCardConHover(String titulo, String valor, Color hoverColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(darkCard);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(50, 50, 55), 1),
                new EmptyBorder(20, 20, 20, 20)
        ));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        Color colorOriginal = darkCard;

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblTitulo.setForeground(textGray);
        card.add(lblTitulo, BorderLayout.NORTH);

        JLabel lblValor = new JLabel(valor);
        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblValor.setForeground(textLight);
        lblValor.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(lblValor, BorderLayout.CENTER);

        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBackground(hoverColor);
                lblTitulo.setForeground(Color.WHITE);
                lblValor.setForeground(Color.WHITE);
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(hoverColor.brighter(), 2),
                        new EmptyBorder(20, 20, 20, 20)
                ));
                card.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card.setBackground(colorOriginal);
                lblTitulo.setForeground(textGray);
                lblValor.setForeground(textLight);
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(50, 50, 55), 1),
                        new EmptyBorder(20, 20, 20, 20)
                ));
                card.repaint();
            }
        });
        return card;
    }

    private void cargarResumenDetallado(JPanel panel) {
        String[] resumen = controller.getResumenGeneral();
        if (resumen != null && resumen.length >= 6) {
            Component[] components = panel.getComponents();
            for (int i = 0; i < components.length && i < 6; i++) {
                if (components[i] instanceof JPanel) {
                    JPanel card = (JPanel) components[i];
                    Component[] cardComponents = card.getComponents();
                    for (Component comp : cardComponents) {
                        if (comp instanceof JLabel) {
                            JLabel label = (JLabel) comp;
                            if (label.getFont().getStyle() == Font.BOLD) {
                                label.setText(resumen[i]);
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    // ================================================================
    // ============== UTILIDADES =======================================
    // ================================================================

    private JButton createActionButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(120, 30));

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(color.brighter());
            }

            public void mouseExited(MouseEvent e) {
                btn.setBackground(color);
            }
        });
        return btn;
    }

    private void cargarResumenGeneral() {
        String[] resumen = controller.getResumenGeneral();
        if (resumen != null && resumen.length >= 6) {
            lblResumenPacientes.setText(" Pacientes :  " + resumen[0]);
            lblResumenCitas.setText(" Citas Hoy :  " + resumen[1]);
            lblResumenVentas.setText(" Ingresos Hoy :  $" + resumen[2]);
            lblResumenServicios.setText(" Servicios :  " + resumen[3]);
            lblResumenInsumos.setText(" Insumos :  " + resumen[4]);
            lblResumenStockBajo.setText(" Stock Bajo :  " + resumen[5]);
        }
    }
}