package com.dentalclinicsystem.view;

import com.dentalclinicsystem.controller.PacienteController;
import com.dentalclinicsystem.model.Paciente;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class PanelPacientes extends JPanel {
    
    private PacienteController controller;
    private JTable tablaPacientes;
    private DefaultTableModel model;
    private JTextField txtBuscar;
    private JButton btnNuevo, btnEditar, btnEliminar, btnBuscar, btnRefrescar;
    private JLabel lblTotalRegistros;
    private JLabel lblEstado; // NUEVO: Para mostrar mensajes de estado
    private JComboBox<String> cbFiltro;
    private List<Paciente> listaPacientesActual;
    
    // NUEVO: Barra de progreso
    private JProgressBar progressBar;
    private JPanel footerPanel;
    
    // Colores del tema oscuro
    private Color darkBg = new Color(30, 30, 35);
    private Color darkCard = new Color(40, 40, 45);
    private Color textLight = new Color(220, 220, 230);
    private Color textGray = new Color(150, 150, 165);
    private Color accentBlue = new Color(70, 130, 200);
    private Color accentGreen = new Color(60, 180, 110);
    private Color accentRed = new Color(210, 80, 80);
    private Color accentPurple = new Color(150, 80, 200);
    
    public PanelPacientes() {
        this.controller = new PacienteController();
        initComponents();
        cargarPacientes();
        actualizarContador();
    }
    
    private void initComponents() {
        setBackground(darkBg);
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Panel superior con título y botones
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Panel central con la tabla
        JPanel tablePanel = createTablePanel();
        add(tablePanel, BorderLayout.CENTER);
        
        // Panel inferior con el contador y barra de progreso (MODIFICADO)
        JPanel footerPanel = createFooterPanel();
        add(footerPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(darkBg);
        panel.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        // ===== TÍTULO =====
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titlePanel.setBackground(darkBg);
        
        JLabel titleLabel = new JLabel("Gestión de Pacientes");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(textLight);
        titlePanel.add(titleLabel);
        
        panel.add(titlePanel, BorderLayout.NORTH);
        
        // ===== PANEL DE BÚSQUEDA Y BOTONES =====
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        actionPanel.setBackground(darkBg);
        actionPanel.setBorder(new EmptyBorder(5, 0, 0, 0));
        
        // Filtro
        cbFiltro = new JComboBox<>(new String[]{"Todos", "Nombre", "Documento", "Teléfono"});
        cbFiltro.setBackground(new Color(50, 50, 55));
        cbFiltro.setForeground(textLight);
        cbFiltro.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cbFiltro.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 65)));
        cbFiltro.setPreferredSize(new Dimension(100, 32));
        actionPanel.add(cbFiltro);
        
        // Campo de búsqueda
        txtBuscar = new JTextField(20);
        txtBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtBuscar.setPreferredSize(new Dimension(200, 32));
        txtBuscar.setBackground(new Color(50, 50, 55));
        txtBuscar.setForeground(textLight);
        txtBuscar.setCaretColor(textLight);
        txtBuscar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 65)),
            new EmptyBorder(0, 12, 0, 12)
        ));
        txtBuscar.addActionListener(e -> buscarPacientes());
        txtBuscar.addKeyListener(new KeyAdapter() {
            private javax.swing.Timer timer;
            @Override
            public void keyReleased(KeyEvent e) {
                if (txtBuscar.getText().trim().isEmpty()) {
                    cargarPacientes();
                    return;
                }
                if (timer != null && timer.isRunning()) {
                    timer.stop();
                }
                timer = new javax.swing.Timer(300, evt -> buscarPacientes());
                timer.setRepeats(false);
                timer.start();
            }
        });
        actionPanel.add(txtBuscar);
        
        // Botón Buscar
        btnBuscar = createActionButton("Buscar", accentBlue);
        btnBuscar.addActionListener(e -> buscarPacientes());
        actionPanel.add(btnBuscar);
        
        // Botón Refrescar
        btnRefrescar = createActionButton("Refrescar", accentPurple);
        btnRefrescar.addActionListener(e -> {
            txtBuscar.setText("");
            cargarPacientes();
        });
        actionPanel.add(btnRefrescar);
        
        // Separador
        JSeparator separator = new JSeparator(SwingConstants.VERTICAL);
        separator.setPreferredSize(new Dimension(1, 30));
        separator.setBackground(new Color(60, 60, 65));
        actionPanel.add(separator);
        
        // Botón Nuevo
        btnNuevo = createActionButton("Nuevo", accentGreen);
        btnNuevo.addActionListener(e -> abrirFormularioPaciente(null));
        actionPanel.add(btnNuevo);
        
        // Botón Editar
        btnEditar = createActionButton("Editar", accentBlue);
        btnEditar.addActionListener(e -> editarPaciente());
        actionPanel.add(btnEditar);
        
        // Botón Eliminar
        btnEliminar = createActionButton("Eliminar", accentRed);
        btnEliminar.addActionListener(e -> eliminarPaciente());
        actionPanel.add(btnEliminar);
        
        panel.add(actionPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(darkCard);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(50, 50, 55), 1),
            new EmptyBorder(10, 10, 10, 10)
        ));
        
        // Columnas de la tabla
        String[] columnas = {"ID", "Nombre Completo", "Documento", "Teléfono", "Email", "Género", "Edad"};
        model = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaPacientes = new JTable(model);
        tablaPacientes.setBackground(new Color(45, 45, 50));
        tablaPacientes.setForeground(textLight);
        tablaPacientes.setGridColor(new Color(55, 55, 60));
        tablaPacientes.setRowHeight(32);
        tablaPacientes.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tablaPacientes.getTableHeader().setBackground(new Color(50, 50, 55));
        tablaPacientes.getTableHeader().setForeground(textLight);
        tablaPacientes.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tablaPacientes.setSelectionBackground(new Color(60, 60, 70));
        tablaPacientes.setSelectionForeground(textLight);
        tablaPacientes.setShowGrid(false);
        tablaPacientes.setIntercellSpacing(new Dimension(0, 0));
        
        // Doble click para editar
        tablaPacientes.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editarPaciente();
                }
            }
        });
        
        // Menú contextual
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem itemEditar = new JMenuItem("Editar");
        itemEditar.addActionListener(e -> editarPaciente());
        popupMenu.add(itemEditar);
        
        JMenuItem itemEliminar = new JMenuItem("Eliminar");
        itemEliminar.addActionListener(e -> eliminarPaciente());
        popupMenu.add(itemEliminar);
        
        popupMenu.addSeparator();
        
        JMenuItem itemVerDetalle = new JMenuItem("Ver Detalle");
        itemVerDetalle.addActionListener(e -> verDetallePaciente());
        popupMenu.add(itemVerDetalle);
        
        tablaPacientes.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    int row = tablaPacientes.rowAtPoint(e.getPoint());
                    if (row >= 0) {
                        tablaPacientes.setRowSelectionInterval(row, row);
                        popupMenu.show(tablaPacientes, e.getX(), e.getY());
                    }
                }
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    int row = tablaPacientes.rowAtPoint(e.getPoint());
                    if (row >= 0) {
                        tablaPacientes.setRowSelectionInterval(row, row);
                        popupMenu.show(tablaPacientes, e.getX(), e.getY());
                    }
                }
            }
        });
        
        JScrollPane scroll = new JScrollPane(tablaPacientes);
        scroll.setBackground(darkCard);
        scroll.getViewport().setBackground(darkCard);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        
        panel.add(scroll, BorderLayout.CENTER);
        
        return panel;
    }
    
    // ================================================================
    // ========== FOOTER PANEL CON BARRA DE PROGRESO ==========
    // ================================================================
    
    private JPanel createFooterPanel() {
        footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(darkBg);
        footerPanel.setPreferredSize(new Dimension(0, 50));
        footerPanel.setBorder(new EmptyBorder(5, 10, 5, 10));
        
        // Panel izquierdo: Contador y estado
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftPanel.setBackground(darkBg);
        
        lblTotalRegistros = new JLabel("Total: 0 registros");
        lblTotalRegistros.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblTotalRegistros.setForeground(textGray);
        leftPanel.add(lblTotalRegistros);
        
        // NUEVO: Barra de progreso (oculta por defecto)
        progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setPreferredSize(new Dimension(150, 18));
        progressBar.setVisible(false);
        progressBar.setBackground(new Color(50, 50, 55));
        progressBar.setForeground(accentBlue);
        progressBar.setBorderPainted(false);
        leftPanel.add(progressBar);
        
        // NUEVO: Label de estado
        lblEstado = new JLabel("");
        lblEstado.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblEstado.setForeground(accentBlue);
        leftPanel.add(lblEstado);
        
        footerPanel.add(leftPanel, BorderLayout.WEST);
        
        // Panel derecho: Información de atajos
        JLabel lblInfo = new JLabel("Doble click para editar | Click derecho para opciones");
        lblInfo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblInfo.setForeground(textGray);
        footerPanel.add(lblInfo, BorderLayout.EAST);
        
        return footerPanel;
    }
    
    // ================================================================
    // ========== MÉTODOS DE CONTROL DE PROGRESO ==========
    // ================================================================
    
    /**
     * Muestra la barra de progreso con un mensaje
     */
    private void mostrarProgreso(String mensaje) {
        SwingUtilities.invokeLater(() -> {
            progressBar.setVisible(true);
            progressBar.setIndeterminate(true);
            lblEstado.setText(mensaje);
            lblEstado.setForeground(accentBlue);
            // Deshabilitar controles durante la carga
            setControlesEnabled(false);
            // Cambiar cursor
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        });
    }
    
    /**
     * Oculta la barra de progreso
     */
    private void ocultarProgreso() {
        SwingUtilities.invokeLater(() -> {
            progressBar.setVisible(false);
            lblEstado.setText("");
            // Habilitar controles
            setControlesEnabled(true);
            // Restaurar cursor
            setCursor(Cursor.getDefaultCursor());
        });
    }
    
    /**
     * Actualiza el mensaje de la barra de progreso
     */
    private void actualizarMensajeProgreso(String mensaje) {
        SwingUtilities.invokeLater(() -> {
            lblEstado.setText(mensaje);
        });
    }
    
    /**
     * Habilita/Deshabilita los controles durante operaciones largas
     */
    private void setControlesEnabled(boolean enabled) {
        btnNuevo.setEnabled(enabled);
        btnEditar.setEnabled(enabled);
        btnEliminar.setEnabled(enabled);
        btnBuscar.setEnabled(enabled);
        btnRefrescar.setEnabled(enabled);
        txtBuscar.setEnabled(enabled);
        cbFiltro.setEnabled(enabled);
        tablaPacientes.setEnabled(enabled);
    }
    
    // ================================================================
    // ========== MÉTODOS DE CARGA CON PROGRESO ==========
    // ================================================================
    
    // Carga todos los pacientes desde la base de datos
    private void cargarPacientes() {
        mostrarProgreso("Cargando pacientes...");
        
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                // Simular progreso (opcional)
                for (int i = 0; i < 10; i++) {
                    if (isCancelled()) break;
                    Thread.sleep(50); // Simular carga
                    final int progress = (i + 1) * 10;
                    SwingUtilities.invokeLater(() -> {
                        progressBar.setIndeterminate(false);
                        progressBar.setValue(progress);
                        actualizarMensajeProgreso("Cargando pacientes... " + progress + "%");
                    });
                }
                
                // Operación real
                listaPacientesActual = controller.listarTodos();
                return null;
            }
            
            @Override
            protected void done() {
                try {
                    cargarTablaConLista(listaPacientesActual);
                    actualizarContador();
                    
                    if (listaPacientesActual == null || listaPacientesActual.isEmpty()) {
                        JOptionPane.showMessageDialog(PanelPacientes.this, 
                            "No hay pacientes registrados", 
                            "Información", JOptionPane.INFORMATION_MESSAGE);
                    }
                    
                } catch (Exception e) {
                    System.err.println("Error al cargar pacientes: " + e.getMessage());
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(PanelPacientes.this, 
                        "Error al cargar pacientes: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                } finally {
                    ocultarProgreso();
                }
            }
        };
        worker.execute();
    }
    
    // Busca pacientes por texto y filtro
    private void buscarPacientes() {
        String texto = txtBuscar.getText().trim();
        String filtro = (String) cbFiltro.getSelectedItem();
        
        if (texto.isEmpty()) {
            cargarPacientes();
            return;
        }
        
        mostrarProgreso("Buscando: '" + texto + "'...");
        
        SwingWorker<List<Paciente>, Void> worker = new SwingWorker<List<Paciente>, Void>() {
            @Override
            protected List<Paciente> doInBackground() throws Exception {
                // Simular progreso
                for (int i = 0; i < 5; i++) {
                    if (isCancelled()) break;
                    Thread.sleep(80);
                    final int progress = (i + 1) * 20;
                    SwingUtilities.invokeLater(() -> {
                        progressBar.setIndeterminate(false);
                        progressBar.setValue(progress);
                        actualizarMensajeProgreso("Buscando... " + progress + "%");
                    });
                }
                
                return controller.buscar(texto);
            }
            
            @Override
            protected void done() {
                try {
                    List<Paciente> resultados = get();
                    
                    // Aplicar filtro adicional si es necesario
                    if (!"Todos".equals(filtro) && resultados != null && !resultados.isEmpty()) {
                        String textoLower = texto.toLowerCase();
                        List<Paciente> filtrados = new java.util.ArrayList<>();
                        
                        for (Paciente p : resultados) {
                            boolean coincide = false;
                            switch (filtro) {
                                case "Nombre":
                                    coincide = p.getNombreCompleto().toLowerCase().contains(textoLower);
                                    break;
                                case "Documento":
                                    coincide = p.getNumeroDocumento().contains(texto);
                                    break;
                                case "Teléfono":
                                    coincide = p.getTelefono().contains(texto);
                                    if (p.getTelefonoAlternativo() != null) {
                                        coincide = coincide || p.getTelefonoAlternativo().contains(texto);
                                    }
                                    break;
                                default:
                                    coincide = true;
                                    break;
                            }
                            if (coincide) {
                                filtrados.add(p);
                            }
                        }
                        resultados = filtrados;
                    }
                    
                    cargarTablaConLista(resultados);
                    actualizarContador();
                    
                    if (resultados == null || resultados.isEmpty()) {
                        JOptionPane.showMessageDialog(PanelPacientes.this, 
                            "No se encontraron pacientes con: '" + texto + "'",
                            "Sin resultados", JOptionPane.INFORMATION_MESSAGE);
                    }
                    
                } catch (Exception e) {
                    System.err.println("Error al buscar: " + e.getMessage());
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(PanelPacientes.this, 
                        "Error al buscar: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                } finally {
                    ocultarProgreso();
                }
            }
        };
        worker.execute();
    }
    
    // Carga la tabla con una lista de pacientes
    private void cargarTablaConLista(List<Paciente> pacientes) {
        SwingUtilities.invokeLater(() -> {
            model.setRowCount(0);
            
            if (pacientes == null || pacientes.isEmpty()) {
                return;
            }
            
            for (Paciente p : pacientes) {
                model.addRow(new Object[]{
                    p.getId(),
                    p.getNombreCompleto(),
                    p.getNumeroDocumento(),
                    p.getTelefono(),
                    p.getEmail() != null ? p.getEmail() : "",
                    p.getGenero() != null ? p.getGenero() : "",
                    p.getEdad() > 0 ? p.getEdad() : ""
                });
            }
        });
    }
    
    // Actualiza el contador de registros
    private void actualizarContador() {
        SwingUtilities.invokeLater(() -> {
            int total = model.getRowCount();
            int totalGeneral = controller.contarPacientes();
            lblTotalRegistros.setText("Mostrando " + total + " de " + totalGeneral + " registros");
        });
    }
    
    // ================================================================
    // ========== MÉTODOS DE ACCIÓN ==========
    // ================================================================
    
    // Abre el formulario para crear o editar un paciente
    private void abrirFormularioPaciente(Paciente paciente) {
        DialogPaciente dialog = new DialogPaciente((JFrame) SwingUtilities.getWindowAncestor(this), paciente != null);
        dialog.setPaciente(paciente);
        dialog.setVisible(true);
        
        if (dialog.isGuardado()) {
            cargarPacientes();
        }
    }
    
    // Obtiene el paciente seleccionado y abre para editar
    private void editarPaciente() {
        int row = tablaPacientes.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, 
                "Seleccione un paciente para editar", 
                "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int id = (int) model.getValueAt(row, 0);
        Paciente paciente = controller.buscarPorId(id);
        if (paciente != null) {
            abrirFormularioPaciente(paciente);
        }
    }
    
    // Elimina el paciente seleccionado
    private void eliminarPaciente() {
        int row = tablaPacientes.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, 
                "Seleccione un paciente para eliminar", 
                "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String nombre = (String) model.getValueAt(row, 1);
        String documento = (String) model.getValueAt(row, 2);
        int id = (int) model.getValueAt(row, 0);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "¿Está seguro de eliminar al paciente?\n\n" +
            "Nombre: " + nombre + "\n" +
            "Documento: " + documento + "\n\n" +
            "⚠️ Esta acción no se puede deshacer",
            "Confirmar Eliminación",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            mostrarProgreso("Eliminando paciente...");
            
            SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    // Simular proceso
                    Thread.sleep(300);
                    return controller.eliminarPaciente(id);
                }
                
                @Override
                protected void done() {
                    try {
                        if (get()) {
                            cargarPacientes();
                        }
                    } catch (Exception e) {
                        System.err.println("Error al eliminar: " + e.getMessage());
                        JOptionPane.showMessageDialog(PanelPacientes.this, 
                            "Error al eliminar: " + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                    } finally {
                        ocultarProgreso();
                    }
                }
            };
            worker.execute();
        }
    }
    
    // Muestra el detalle del paciente seleccionado
    private void verDetallePaciente() {
        int row = tablaPacientes.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, 
                "Seleccione un paciente para ver detalles", 
                "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int id = (int) model.getValueAt(row, 0);
        Paciente paciente = controller.buscarPorId(id);
        if (paciente != null) {
            mostrarDetallePaciente(paciente);
        }
    }
    
    // ================================================================
    // ========== MÉTODOS DE UTILIDAD ==========
    // ================================================================
    
    // Crea un botón con estilo consistente
    private JButton createActionButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(95, 32));
        
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
    
    // Muestra un diálogo con el detalle completo del paciente
    private void mostrarDetallePaciente(Paciente p) {
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Detalle del Paciente", true);
        dialog.setSize(500, 600);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(darkBg);
        tabbedPane.setForeground(textLight);
        
        tabbedPane.addTab("📋 Información", crearPanelInfoPersonal(p));
        tabbedPane.addTab("🏥 Datos Médicos", crearPanelDatosMedicos(p));
        
        dialog.add(tabbedPane, BorderLayout.CENTER);
        
        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.setBackground(accentBlue);
        btnCerrar.setForeground(Color.WHITE);
        btnCerrar.setBorderPainted(false);
        btnCerrar.addActionListener(e -> dialog.dispose());
        
        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(darkBg);
        btnPanel.add(btnCerrar);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        
        dialog.setVisible(true);
    }
    
    // Crea el panel de información personal
    private JPanel crearPanelInfoPersonal(Paciente p) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(darkBg);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        
        JLabel nombreLabel = new JLabel(" " + p.getNombreCompleto());
        nombreLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        nombreLabel.setForeground(textLight);
        panel.add(nombreLabel, gbc);
        
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        
        String[][] datos = {
            {" Documento:", p.getNumeroDocumento()},
            {" Teléfono:", p.getTelefono()},
            {" Email:", p.getEmail() != null ? p.getEmail() : "No registrado"},
            {" Dirección:", p.getDireccion() != null ? p.getDireccion() : "No registrada"},
            {" Género:", p.getGenero() != null ? p.getGenero() : "No registrado"},
            {" Edad:", p.getEdad() > 0 ? p.getEdad() + " años" : "No registrada"},
            {" Estado Civil:", p.getEstadoCivil() != null ? p.getEstadoCivil() : "No registrado"},
            {" Ocupación:", p.getOcupacion() != null ? p.getOcupacion() : "No registrada"},
            {" Registro:", p.getFechaRegistro() != null ? p.getFechaRegistro() : "No registrada"}
        };
        
        int y = 1;
        for (String[] dato : datos) {
            gbc.gridy = y;
            gbc.gridx = 0;
            gbc.weightx = 0.3;
            
            JLabel label = new JLabel(dato[0]);
            label.setFont(new Font("Segoe UI", Font.BOLD, 13));
            label.setForeground(textGray);
            panel.add(label, gbc);
            
            gbc.gridx = 1;
            gbc.weightx = 0.7;
            
            JLabel value = new JLabel(dato[1]);
            value.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            value.setForeground(textLight);
            panel.add(value, gbc);
            
            y++;
        }
        
        return panel;
    }
    
    // Crea el panel de datos médicos
    private JPanel crearPanelDatosMedicos(Paciente p) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(darkBg);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        
        // Alergias
        JLabel lblAlergias = new JLabel("️ Alergias:");
        lblAlergias.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblAlergias.setForeground(textGray);
        panel.add(lblAlergias, gbc);
        gbc.gridy++;
        
        JTextArea alergias = new JTextArea(p.getAlergias() != null && !p.getAlergias().isEmpty() ? p.getAlergias() : "Ninguna");
        alergias.setEditable(false);
        alergias.setBackground(new Color(50, 50, 55));
        alergias.setForeground(textLight);
        alergias.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 65)));
        JScrollPane scrollAlergias = new JScrollPane(alergias);
        scrollAlergias.setPreferredSize(new Dimension(300, 60));
        panel.add(scrollAlergias, gbc);
        
        // Enfermedades
        gbc.gridy++;
        JLabel lblEnfermedades = new JLabel(" Enfermedades:");
        lblEnfermedades.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblEnfermedades.setForeground(textGray);
        panel.add(lblEnfermedades, gbc);
        gbc.gridy++;
        
        JTextArea enfermedades = new JTextArea(p.getEnfermedadesSistema() != null && !p.getEnfermedadesSistema().isEmpty() ? p.getEnfermedadesSistema() : "Ninguna");
        enfermedades.setEditable(false);
        enfermedades.setBackground(new Color(50, 50, 55));
        enfermedades.setForeground(textLight);
        enfermedades.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 65)));
        JScrollPane scrollEnfermedades = new JScrollPane(enfermedades);
        scrollEnfermedades.setPreferredSize(new Dimension(300, 60));
        panel.add(scrollEnfermedades, gbc);
        
        // Medicamentos
        gbc.gridy++;
        JLabel lblMedicamentos = new JLabel(" Medicamentos:");
        lblMedicamentos.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblMedicamentos.setForeground(textGray);
        panel.add(lblMedicamentos, gbc);
        gbc.gridy++;
        
        JTextArea medicamentos = new JTextArea(p.getMedicamentos() != null && !p.getMedicamentos().isEmpty() ? p.getMedicamentos() : "Ninguno");
        medicamentos.setEditable(false);
        medicamentos.setBackground(new Color(50, 50, 55));
        medicamentos.setForeground(textLight);
        medicamentos.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 65)));
        JScrollPane scrollMedicamentos = new JScrollPane(medicamentos);
        scrollMedicamentos.setPreferredSize(new Dimension(300, 60));
        panel.add(scrollMedicamentos, gbc);
        
        // Contacto de emergencia
        gbc.gridy++;
        JLabel lblContacto = new JLabel(" Contacto Emergencia:");
        lblContacto.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblContacto.setForeground(textGray);
        panel.add(lblContacto, gbc);
        gbc.gridy++;
        
        JPanel contactoPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        contactoPanel.setBackground(darkBg);
        
        JLabel lblContactoNombre = new JLabel("Nombre: " + (p.getContactoEmergenciaNombre() != null ? p.getContactoEmergenciaNombre() : "No registrado"));
        lblContactoNombre.setForeground(textLight);
        contactoPanel.add(lblContactoNombre);
        
        JLabel lblContactoTelefono = new JLabel("Teléfono: " + (p.getContactoEmergenciaTelefono() != null ? p.getContactoEmergenciaTelefono() : "No registrado"));
        lblContactoTelefono.setForeground(textLight);
        contactoPanel.add(lblContactoTelefono);
        
        panel.add(contactoPanel, gbc);
        
        return panel;
    }
}