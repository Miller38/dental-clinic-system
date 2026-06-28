package com.dentalclinicsystem.view;

import com.dentalclinicsystem.controller.PacienteController;
import com.dentalclinicsystem.controller.ServicioController;
import com.dentalclinicsystem.controller.VentaController;
import com.dentalclinicsystem.model.DetalleVenta;
import com.dentalclinicsystem.model.Paciente;
import com.dentalclinicsystem.model.Servicio;
import com.dentalclinicsystem.model.Venta;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

public class DialogVenta extends JDialog {
    
    private VentaController ventaController;
    private PacienteController pacienteController;
    private ServicioController servicioController;
    
    private JComboBox<String> cbPaciente;
    private JComboBox<String> cbServicio;
    private JComboBox<String> cbMetodoPago;
    private JComboBox<String> cbTipoComprobante;
    private JTextField txtCantidad, txtPrecio;
    private JButton btnAgregar, btnEliminar, btnGuardar, btnCancelar;
    private JTable tablaDetalles;
    private DefaultTableModel modelDetalles;
    private JLabel lblSubtotal, lblImpuesto, lblTotal;
    private JTextArea txtNotas;
    private JScrollPane scrollDetalles;
    
    private JCheckBox chkEnviarFactura;
    private JLabel lblEmailInfo;
    
    // ======================= COMPONENTES DE PROGRESO =======================
    private JProgressBar progressBar;
    private JLabel lblEstado;
    private JPanel progressPanel;
    // ============================================================================
    
    private boolean guardado = false;
    private static final double IVA = 0.19;
    
    private DecimalFormat dfMostrar;
    
    private Color darkBg = new Color(30, 30, 35);
    private Color darkCard = new Color(40, 40, 45);
    private Color textLight = new Color(220, 220, 230);
    private Color textGray = new Color(150, 150, 165);
    private Color accentGreen = new Color(60, 180, 110);
    private Color accentRed = new Color(210, 80, 80);
    private Color accentBlue = new Color(70, 130, 200);
    private Color fieldBg = new Color(50, 50, 55);
    private Color fieldBorder = new Color(60, 60, 65);
    
    public DialogVenta(JFrame parent) {
        super(parent, "Nueva Venta", true);
        this.ventaController = new VentaController();
        this.pacienteController = new PacienteController();
        this.servicioController = new ServicioController();
        
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setDecimalSeparator('.');
        symbols.setGroupingSeparator(' ');
        dfMostrar = new DecimalFormat("$#0.00", symbols);
        dfMostrar.setGroupingUsed(false);
        
        initComponents();
        
        // Cargar datos con progreso
        cargarDatosIniciales();
        
        setSize(750, 720);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setModal(true);
    }
    
    private void initComponents() {
        setBackground(darkBg);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(darkBg);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // ========== PANEL SUPERIOR ==========
        JPanel topPanel = new JPanel(new GridBagLayout());
        topPanel.setBackground(darkBg);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        
        int row = 0;
        
        // Título
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 4;
        JLabel titleLabel = new JLabel("Nueva Venta");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(textLight);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        topPanel.add(titleLabel, gbc);
        row++;
        gbc.gridwidth = 1;
        
        // Paciente
        gbc.gridx = 0; gbc.gridy = row;
        topPanel.add(createLabel("Paciente *:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3;
        cbPaciente = new JComboBox<>();
        cbPaciente.setBackground(fieldBg);
        cbPaciente.setForeground(textLight);
        cbPaciente.setBorder(BorderFactory.createLineBorder(fieldBorder));
        cbPaciente.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cbPaciente.addActionListener(e -> mostrarInfoPaciente());
        topPanel.add(cbPaciente, gbc);
        row++;
        gbc.gridwidth = 1;
        
        // Tipo Comprobante y Método Pago
        gbc.gridx = 0; gbc.gridy = row;
        topPanel.add(createLabel("Tipo:"), gbc);
        gbc.gridx = 1;
        cbTipoComprobante = new JComboBox<>(ventaController.getTiposComprobante());
        cbTipoComprobante.setBackground(fieldBg);
        cbTipoComprobante.setForeground(textLight);
        cbTipoComprobante.setBorder(BorderFactory.createLineBorder(fieldBorder));
        cbTipoComprobante.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        topPanel.add(cbTipoComprobante, gbc);
        
        gbc.gridx = 2;
        topPanel.add(createLabel("Método Pago *:"), gbc);
        gbc.gridx = 3;
        cbMetodoPago = new JComboBox<>(ventaController.getMetodosPago());
        cbMetodoPago.setBackground(fieldBg);
        cbMetodoPago.setForeground(textLight);
        cbMetodoPago.setBorder(BorderFactory.createLineBorder(fieldBorder));
        cbMetodoPago.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        topPanel.add(cbMetodoPago, gbc);
        row++;
        
        // Servicio, Cantidad, Precio
        gbc.gridx = 0; gbc.gridy = row;
        topPanel.add(createLabel("Servicio:"), gbc);
        gbc.gridx = 1;
        cbServicio = new JComboBox<>();
        cbServicio.setBackground(fieldBg);
        cbServicio.setForeground(textLight);
        cbServicio.setBorder(BorderFactory.createLineBorder(fieldBorder));
        cbServicio.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cbServicio.addActionListener(e -> actualizarPrecio());
        topPanel.add(cbServicio, gbc);
        
        gbc.gridx = 2;
        topPanel.add(createLabel("Cantidad:"), gbc);
        gbc.gridx = 3;
        txtCantidad = new JTextField(3);
        txtCantidad.setText("1");
        txtCantidad.setBackground(fieldBg);
        txtCantidad.setForeground(textLight);
        txtCantidad.setBorder(BorderFactory.createLineBorder(fieldBorder));
        txtCantidad.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        topPanel.add(txtCantidad, gbc);
        row++;
        
        // Precio y Botón Agregar
        gbc.gridx = 0; gbc.gridy = row;
        topPanel.add(createLabel("Precio:"), gbc);
        gbc.gridx = 1;
        txtPrecio = new JTextField(10);
        txtPrecio.setText("0.00");
        txtPrecio.setBackground(fieldBg);
        txtPrecio.setForeground(textLight);
        txtPrecio.setBorder(BorderFactory.createLineBorder(fieldBorder));
        txtPrecio.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        topPanel.add(txtPrecio, gbc);
        
        gbc.gridx = 2; gbc.gridwidth = 2;
        btnAgregar = createActionButton("Agregar", accentBlue);
        btnAgregar.addActionListener(e -> agregarDetalle());
        topPanel.add(btnAgregar, gbc);
        row++;
        gbc.gridwidth = 1;
        
        mainPanel.add(topPanel, BorderLayout.NORTH);
        
        // ========== TABLA DE DETALLES ==========
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(darkCard);
        tablePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(50, 50, 55), 1),
            new EmptyBorder(10, 10, 10, 10)
        ));
        tablePanel.setPreferredSize(new Dimension(0, 180));
        tablePanel.setMinimumSize(new Dimension(0, 150));
        
        String[] columnas = {"Item", "Cantidad", "Precio", "Subtotal"};
        modelDetalles = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaDetalles = new JTable(modelDetalles);
        tablaDetalles.setBackground(new Color(45, 45, 50));
        tablaDetalles.setForeground(textLight);
        tablaDetalles.setGridColor(new Color(55, 55, 60));
        tablaDetalles.setRowHeight(30);
        tablaDetalles.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tablaDetalles.getTableHeader().setBackground(new Color(50, 50, 55));
        tablaDetalles.getTableHeader().setForeground(textLight);
        tablaDetalles.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tablaDetalles.setSelectionBackground(new Color(60, 60, 70));
        tablaDetalles.setSelectionForeground(textLight);
        tablaDetalles.setShowGrid(false);
        
        scrollDetalles = new JScrollPane(tablaDetalles);
        scrollDetalles.setBackground(darkCard);
        scrollDetalles.getViewport().setBackground(darkCard);
        scrollDetalles.setBorder(BorderFactory.createEmptyBorder());
        scrollDetalles.setPreferredSize(new Dimension(0, 150));
        
        tablePanel.add(scrollDetalles, BorderLayout.CENTER);
        
        // Botón Eliminar
        JPanel btnEliminarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnEliminarPanel.setBackground(darkCard);
        btnEliminar = createActionButton("Eliminar seleccionado", accentRed);
        btnEliminar.addActionListener(e -> eliminarDetalle());
        btnEliminarPanel.add(btnEliminar);
        tablePanel.add(btnEliminarPanel, BorderLayout.SOUTH);
        
        mainPanel.add(tablePanel, BorderLayout.CENTER);
        
        // ========== PANEL INFERIOR ==========
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(darkBg);
        bottomPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        // Resumen
        JPanel resumenPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        resumenPanel.setBackground(darkBg);
        resumenPanel.setPreferredSize(new Dimension(0, 40));
        
        lblSubtotal = new JLabel("Subtotal: $0.00");
        lblSubtotal.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblSubtotal.setForeground(textLight);
        lblSubtotal.setHorizontalAlignment(SwingConstants.CENTER);
        resumenPanel.add(lblSubtotal);
        
        lblImpuesto = new JLabel("IVA (19%): $0.00");
        lblImpuesto.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblImpuesto.setForeground(textLight);
        lblImpuesto.setHorizontalAlignment(SwingConstants.CENTER);
        resumenPanel.add(lblImpuesto);
        
        lblTotal = new JLabel("Total: $0.00");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTotal.setForeground(accentGreen);
        lblTotal.setHorizontalAlignment(SwingConstants.CENTER);
        resumenPanel.add(lblTotal);
        
        bottomPanel.add(resumenPanel, BorderLayout.NORTH);
        
        // ======================= PANEL DE PROGRESO =======================
        progressPanel = new JPanel(new BorderLayout(10, 5));
        progressPanel.setBackground(darkBg);
        progressPanel.setBorder(new EmptyBorder(5, 10, 5, 10));
        progressPanel.setVisible(false);
        
        progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setPreferredSize(new Dimension(0, 18));
        progressBar.setBackground(new Color(50, 50, 55));
        progressBar.setForeground(accentBlue);
        progressBar.setBorderPainted(false);
        progressPanel.add(progressBar, BorderLayout.CENTER);
        
        lblEstado = new JLabel("Procesando...");
        lblEstado.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblEstado.setForeground(accentBlue);
        progressPanel.add(lblEstado, BorderLayout.SOUTH);
        
        bottomPanel.add(progressPanel, BorderLayout.CENTER);
        // ========================================================================
        
        // Notas y Envío de Factura
        JPanel notasPanel = new JPanel(new GridBagLayout());
        notasPanel.setBackground(darkBg);
        notasPanel.setBorder(new EmptyBorder(5, 0, 0, 0));
        GridBagConstraints gbcNotas = new GridBagConstraints();
        gbcNotas.insets = new Insets(3, 5, 3, 5);
        gbcNotas.fill = GridBagConstraints.HORIZONTAL;
        gbcNotas.weightx = 1.0;
        
        // Etiqueta Notas
        gbcNotas.gridx = 0; gbcNotas.gridy = 0;
        gbcNotas.gridwidth = 1;
        gbcNotas.weightx = 0.1;
        JLabel lblNotas = new JLabel("Notas:");
        lblNotas.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblNotas.setForeground(textGray);
        notasPanel.add(lblNotas, gbcNotas);
        
        // TextArea Notas
        gbcNotas.gridx = 1; gbcNotas.gridy = 0;
        gbcNotas.gridwidth = 3;
        gbcNotas.weightx = 0.9;
        txtNotas = new JTextArea(2, 20);
        txtNotas.setLineWrap(true);
        txtNotas.setWrapStyleWord(true);
        txtNotas.setBackground(fieldBg);
        txtNotas.setForeground(textLight);
        txtNotas.setCaretColor(textLight);
        txtNotas.setBorder(BorderFactory.createLineBorder(fieldBorder));
        txtNotas.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JScrollPane scrollNotas = new JScrollPane(txtNotas);
        scrollNotas.setBackground(fieldBg);
        scrollNotas.setBorder(BorderFactory.createLineBorder(fieldBorder));
        scrollNotas.setPreferredSize(new Dimension(0, 50));
        notasPanel.add(scrollNotas, gbcNotas);
        
        // Checkbox para enviar factura
        gbcNotas.gridx = 0; gbcNotas.gridy = 1;
        gbcNotas.gridwidth = 4;
        gbcNotas.weightx = 1.0;
        
        JPanel envioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        envioPanel.setBackground(darkBg);
        
        chkEnviarFactura = new JCheckBox("   Enviar factura al correo del paciente");
        chkEnviarFactura.setBackground(darkBg);
        chkEnviarFactura.setForeground(textLight);
        chkEnviarFactura.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        chkEnviarFactura.setFocusPainted(false);
        chkEnviarFactura.setSelected(true);
        
        lblEmailInfo = new JLabel("");
        lblEmailInfo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblEmailInfo.setForeground(textGray);
        
        envioPanel.add(chkEnviarFactura);
        envioPanel.add(lblEmailInfo);
        notasPanel.add(envioPanel, gbcNotas);
        
        bottomPanel.add(notasPanel, BorderLayout.SOUTH);
        
        // Botones
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnPanel.setBackground(darkBg);
        
        btnGuardar = createActionButton("Guardar Venta", accentGreen);
        btnGuardar.setPreferredSize(new Dimension(150, 40));
        btnGuardar.addActionListener(e -> guardarVenta());
        btnPanel.add(btnGuardar);
        
        btnCancelar = createActionButton("Cancelar", accentRed);
        btnCancelar.setPreferredSize(new Dimension(120, 40));
        btnCancelar.addActionListener(e -> dispose());
        btnPanel.add(btnCancelar);
        
        bottomPanel.add(btnPanel, BorderLayout.SOUTH);
        
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    // ======================= MÉTODOS DE PROGRESO =======================
    
    private void mostrarProgreso(String mensaje) {
        SwingUtilities.invokeLater(() -> {
            progressPanel.setVisible(true);
            progressBar.setIndeterminate(true);
            lblEstado.setText(mensaje);
            setControlesEnabled(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        });
    }
    
    private void ocultarProgreso() {
        SwingUtilities.invokeLater(() -> {
            progressPanel.setVisible(false);
            progressBar.setIndeterminate(true);
            lblEstado.setText("");
            setControlesEnabled(true);
            setCursor(Cursor.getDefaultCursor());
        });
    }
    
    private void actualizarMensajeProgreso(String mensaje) {
        SwingUtilities.invokeLater(() -> {
            lblEstado.setText(mensaje);
        });
    }
    
    private void setControlesEnabled(boolean enabled) {
        cbPaciente.setEnabled(enabled);
        cbServicio.setEnabled(enabled);
        cbMetodoPago.setEnabled(enabled);
        cbTipoComprobante.setEnabled(enabled);
        txtCantidad.setEnabled(enabled);
        txtPrecio.setEnabled(enabled);
        btnAgregar.setEnabled(enabled);
        btnEliminar.setEnabled(enabled);
        btnGuardar.setEnabled(enabled);
        txtNotas.setEnabled(enabled);
        chkEnviarFactura.setEnabled(enabled);
        tablaDetalles.setEnabled(enabled);
    }
    
    // ======================= MÉTODOS DE CARGA CON PROGRESO =======================
    
    private void cargarDatosIniciales() {
        mostrarProgreso("Cargando datos...");
        
        SwingWorker<Void, Integer> worker = new SwingWorker<Void, Integer>() {
            @Override
            protected Void doInBackground() throws Exception {
                // Simular progreso
                for (int i = 0; i < 5; i++) {
                    Thread.sleep(80);
                    publish((i + 1) * 20);
                }
                
                // Cargar pacientes (en segundo plano)
                List<Paciente> pacientes = pacienteController.listarTodos();
                SwingUtilities.invokeLater(() -> {
                    cbPaciente.removeAllItems();
                    if (pacientes != null && !pacientes.isEmpty()) {
                        for (Paciente p : pacientes) {
                            cbPaciente.addItem(p.getNombreCompleto());
                        }
                    }
                    cbPaciente.setSelectedIndex(-1);
                });
                
                // Cargar servicios (en segundo plano)
                List<Servicio> servicios = servicioController.listarActivos();
                SwingUtilities.invokeLater(() -> {
                    cbServicio.removeAllItems();
                    if (servicios != null && !servicios.isEmpty()) {
                        for (Servicio s : servicios) {
                            cbServicio.addItem(s.getNombre());
                        }
                    }
                    cbServicio.setSelectedIndex(-1);
                });
                
                return null;
            }
            
            @Override
            protected void process(java.util.List<Integer> chunks) {
                int progress = chunks.get(chunks.size() - 1);
                progressBar.setIndeterminate(false);
                progressBar.setValue(progress);
                lblEstado.setText("Cargando datos... " + progress + "%");
            }
            
            @Override
            protected void done() {
                try {
                    // Verificar que haya datos
                    if (cbPaciente.getItemCount() == 0) {
                        JOptionPane.showMessageDialog(DialogVenta.this,
                            "No hay pacientes registrados. Cree un paciente primero.",
                            "Aviso", JOptionPane.WARNING_MESSAGE);
                    }
                    if (cbServicio.getItemCount() == 0) {
                        JOptionPane.showMessageDialog(DialogVenta.this,
                            "No hay servicios registrados. Cree un servicio primero.",
                            "Aviso", JOptionPane.WARNING_MESSAGE);
                    }
                } catch (Exception e) {
                    System.err.println("Error al cargar datos iniciales: " + e.getMessage());
                } finally {
                    ocultarProgreso();
                }
            }
        };
        worker.execute();
    }
    
    // ========== MÉTODOS AUXILIARES ==========
    
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        label.setForeground(textGray);
        return label;
    }
    
    private JButton createActionButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
    
    private void mostrarInfoPaciente() {
        String seleccion = (String) cbPaciente.getSelectedItem();
        if (seleccion != null && !seleccion.isEmpty()) {
            List<Paciente> pacientes = pacienteController.listarTodos();
            if (pacientes != null) {
                for (Paciente p : pacientes) {
                    if (p.getNombreCompleto().equals(seleccion)) {
                        if (p.getEmail() != null && !p.getEmail().isEmpty()) {
                            chkEnviarFactura.setEnabled(true);
                            chkEnviarFactura.setText("Enviar factura a: " + p.getEmail());
                            chkEnviarFactura.setSelected(true);
                            lblEmailInfo.setText("");
                        } else {
                            chkEnviarFactura.setEnabled(false);
                            chkEnviarFactura.setText("El paciente no tiene email registrado");
                            chkEnviarFactura.setSelected(false);
                            lblEmailInfo.setText(" ️");
                        }
                        return;
                    }
                }
            }
        }
        chkEnviarFactura.setEnabled(true);
        chkEnviarFactura.setText("Enviar factura al correo del paciente");
        chkEnviarFactura.setSelected(true);
        lblEmailInfo.setText("");
    }
    
    private void actualizarPrecio() {
        String nombreServicio = (String) cbServicio.getSelectedItem();
        if (nombreServicio != null && !nombreServicio.isEmpty()) {
            Servicio servicio = servicioController.buscarPorNombre(nombreServicio);
            if (servicio != null) {
                txtPrecio.setText(String.format("%.2f", servicio.getPrecio()));
            }
        }
    }
    
    private int getServicioIdByNombre(String nombre) {
        Servicio servicio = servicioController.buscarPorNombre(nombre);
        if (servicio != null) {
            return servicio.getId();
        }
        return -1;
    }
    
    private double parsearPrecio(String texto) {
        try {
            String limpio = texto.replace("$", "").replace(" ", "").replace(",", ".").trim();
            if (limpio.isEmpty()) return 0.0;
            return Double.parseDouble(limpio);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
    
    private String formatearPrecio(double valor) {
        return String.format("$%.2f", valor);
    }
    
    // ========== AGREGAR DETALLE ==========
    
    private void agregarDetalle() {
        String servicio = (String) cbServicio.getSelectedItem();
        if (servicio == null || servicio.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Seleccione un servicio", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            int cantidad = Integer.parseInt(txtCantidad.getText().trim());
            double precio = parsearPrecio(txtPrecio.getText().trim());
            int servicioId = getServicioIdByNombre(servicio);
            
            if (cantidad <= 0) {
                JOptionPane.showMessageDialog(this, "La cantidad debe ser mayor a 0", "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (precio <= 0) {
                JOptionPane.showMessageDialog(this, "El precio debe ser mayor a 0", "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (servicioId <= 0) {
                JOptionPane.showMessageDialog(this, "Servicio no encontrado", "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            DetalleVenta detalle = new DetalleVenta();
            detalle.setTipoItem("SERVICIO");
            detalle.setItemId(servicioId);
            detalle.setNombre(servicio);
            detalle.setCantidad(cantidad);
            detalle.setPrecioUnitario(precio);
            detalle.setSubtotal(cantidad * precio);
            
            modelDetalles.addRow(new Object[]{
                detalle.getNombre(),
                detalle.getCantidad(),
                formatearPrecio(detalle.getPrecioUnitario()),
                formatearPrecio(detalle.getSubtotal())
            });
            
            modelDetalles.fireTableDataChanged();
            tablaDetalles.revalidate();
            tablaDetalles.repaint();
            scrollDetalles.revalidate();
            scrollDetalles.repaint();
            
            SwingUtilities.getWindowAncestor(tablaDetalles).revalidate();
            SwingUtilities.getWindowAncestor(tablaDetalles).repaint();
            
            System.out.println("Detalle agregado: " + detalle.getNombre());
            System.out.println("Filas en modelo: " + modelDetalles.getRowCount());
            
            actualizarTotales();
            
            txtCantidad.setText("1");
            txtPrecio.setText("0.00");
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "Ingrese valores numéricos válidos (ej: 50000)", 
                "Error de formato", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void eliminarDetalle() {
        int row = tablaDetalles.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un detalle para eliminar", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        modelDetalles.removeRow(row);
        modelDetalles.fireTableDataChanged();
        tablaDetalles.revalidate();
        tablaDetalles.repaint();
        scrollDetalles.revalidate();
        scrollDetalles.repaint();
        actualizarTotales();
    }
    
    private void actualizarTotales() {
        double subtotal = 0;
        for (int i = 0; i < modelDetalles.getRowCount(); i++) {
            String subtotalStr = (String) modelDetalles.getValueAt(i, 3);
            subtotal += parsearPrecio(subtotalStr);
        }
        
        double impuesto = subtotal * IVA;
        double total = subtotal + impuesto;
        
        lblSubtotal.setText("Subtotal: $" + String.format("%.2f", subtotal));
        lblImpuesto.setText("IVA (19%): $" + String.format("%.2f", impuesto));
        lblTotal.setText("Total: $" + String.format("%.2f", total));
    }
    
    private int getSelectedPacienteId() {
        String nombre = (String) cbPaciente.getSelectedItem();
        if (nombre != null) {
            List<Paciente> pacientes = pacienteController.listarTodos();
            if (pacientes != null) {
                for (Paciente p : pacientes) {
                    if (p.getNombreCompleto().equals(nombre)) {
                        return p.getId();
                    }
                }
            }
        }
        return -1;
    }
    
    // ========== GUARDAR VENTA CON PROGRESO ==========
    
    private void guardarVenta() {
        int pacienteId = getSelectedPacienteId();
        if (pacienteId <= 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un paciente", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (modelDetalles.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Agregue al menos un detalle a la venta", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String metodoPago = (String) cbMetodoPago.getSelectedItem();
        if (metodoPago == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un método de pago", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // ========== MOSTRAR PROGRESO ==========
        mostrarProgreso("Preparando venta...");
        
        SwingWorker<Boolean, Integer> worker = new SwingWorker<Boolean, Integer>() {
            private Venta venta;
            
            @Override
            protected Boolean doInBackground() throws Exception {
                // Paso 1: Crear la venta
                publish(20);
                SwingUtilities.invokeLater(() -> actualizarMensajeProgreso("Creando venta..."));
                
                venta = new Venta();
                venta.setPacienteId(pacienteId);
                venta.setMetodoPago(metodoPago);
                venta.setTipoComprobante((String) cbTipoComprobante.getSelectedItem());
                venta.setNotas(txtNotas.getText().trim());
                venta.setImpuesto(IVA);
                venta.setFecha(LocalDate.now().toString());
                
                // Paso 2: Agregar detalles
                publish(40);
                SwingUtilities.invokeLater(() -> actualizarMensajeProgreso("Agregando detalles..."));
                
                for (int i = 0; i < modelDetalles.getRowCount(); i++) {
                    String nombre = (String) modelDetalles.getValueAt(i, 0);
                    int cantidad = (int) modelDetalles.getValueAt(i, 1);
                    String precioStr = (String) modelDetalles.getValueAt(i, 2);
                    
                    double precio = parsearPrecio(precioStr);
                    int servicioId = getServicioIdByNombre(nombre);
                    
                    DetalleVenta detalle = new DetalleVenta();
                    detalle.setTipoItem("SERVICIO");
                    detalle.setItemId(servicioId);
                    detalle.setNombre(nombre);
                    detalle.setCantidad(cantidad);
                    detalle.setPrecioUnitario(precio);
                    detalle.setSubtotal(cantidad * precio);
                    venta.agregarDetalle(detalle);
                }
                
                venta.recalcularTotales();
                
                // Paso 3: Guardar en BD
                publish(60);
                SwingUtilities.invokeLater(() -> actualizarMensajeProgreso("Guardando en la base de datos..."));
                Thread.sleep(300);
                
                boolean enviarFactura = chkEnviarFactura.isSelected() && chkEnviarFactura.isEnabled();
                
                // Paso 4: Enviar factura si corresponde
                if (enviarFactura) {
                    publish(80);
                    SwingUtilities.invokeLater(() -> actualizarMensajeProgreso("Enviando factura por email..."));
                    Thread.sleep(500);
                }
                
                return ventaController.guardarVenta(venta, enviarFactura);
            }
            
            @Override
            protected void process(java.util.List<Integer> chunks) {
                int progress = chunks.get(chunks.size() - 1);
                progressBar.setIndeterminate(false);
                progressBar.setValue(progress);
            }
            
            @Override
            protected void done() {
                try {
                    boolean exito = get();
                    if (exito) {
                        SwingUtilities.invokeLater(() -> {
                            progressBar.setValue(100);
                            actualizarMensajeProgreso("¡Venta completada con éxito!");
                        });
                        Thread.sleep(300);
                        guardado = true;
                        dispose();
                    }
                } catch (Exception e) {
                    System.err.println("Error al guardar venta: " + e.getMessage());
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(DialogVenta.this, 
                        "Error al guardar: " + e.getMessage(), 
                        "Error", JOptionPane.ERROR_MESSAGE);
                } finally {
                    ocultarProgreso();
                }
            }
        };
        
        worker.execute();
    }
    
    public boolean isGuardado() {
        return guardado;
    }
}