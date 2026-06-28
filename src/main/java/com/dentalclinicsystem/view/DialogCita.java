package com.dentalclinicsystem.view;

import com.dentalclinicsystem.controller.CitaController;
import com.dentalclinicsystem.controller.PacienteController;
import com.dentalclinicsystem.controller.ServicioController;
import com.dentalclinicsystem.controller.UsuariosController;
import com.dentalclinicsystem.model.Cita;
import com.dentalclinicsystem.model.Paciente;
import com.dentalclinicsystem.model.Servicio;
import com.dentalclinicsystem.model.Usuario;
import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DialogCita extends JDialog {
    
    private CitaController citaController;
    private PacienteController pacienteController;
    private UsuariosController usuariosController;
    private ServicioController servicioController;
    
    private Cita cita;
    private boolean guardado = false;
    private boolean esEdicion = false;
    private boolean datosCargados = false;
    
    private JComboBox<String> cbPaciente;
    private JComboBox<String> cbOdontologo;
    private JComboBox<String> cbServicio;
    private JComboBox<String> cbEstado;
    private DatePicker dpFecha;
    private JComboBox<String> cbHora;
    private JTextField txtDuracion;
    private JTextArea txtNota;
    private JButton btnGuardar, btnCancelar;
    private JLabel lblPacienteInfo, lblFechaInfo;
    
    private JProgressBar progressBar;
    private JLabel lblEstado;
    private JPanel progressPanel;
    
    private JLabel lblDisponibilidad;
    private Timer timerValidacion;
    private boolean inicializado = false;
    
    private Color darkBg = new Color(30, 30, 35);
    private Color darkCard = new Color(40, 40, 45);
    private Color textLight = new Color(220, 220, 230);
    private Color textGray = new Color(150, 150, 165);
    private Color accentGreen = new Color(60, 180, 110);
    private Color accentRed = new Color(210, 80, 80);
    private Color accentBlue = new Color(70, 130, 200);
    private Color accentOrange = new Color(230, 160, 50);
    private Color fieldBg = new Color(50, 50, 55);
    private Color fieldBorder = new Color(60, 60, 65);
    
    public DialogCita(JFrame parent, boolean edicion, Cita citaParaEditar) {
        super(parent, edicion ? "Editar Cita" : "Nueva Cita", true);
        this.citaController = new CitaController();
        this.pacienteController = new PacienteController();
        this.usuariosController = new UsuariosController();
        this.servicioController = new ServicioController();
        this.esEdicion = edicion;
        this.cita = citaParaEditar;
        
        System.out.println("📌 [DEBUG] Constructor DialogCita - Cita para editar: " + (citaParaEditar != null ? citaParaEditar.getId() : "null"));
        if (citaParaEditar != null) {
            System.out.println("   Paciente: " + citaParaEditar.getPacienteNombre() + " (ID: " + citaParaEditar.getPacienteId() + ")");
            System.out.println("   Odontólogo: " + citaParaEditar.getOdontologoNombre() + " (ID: " + citaParaEditar.getOdontologoId() + ")");
            System.out.println("   Servicio: " + citaParaEditar.getServicioNombre() + " (ID: " + citaParaEditar.getServicioId() + ")");
        }
        
        timerValidacion = new Timer(500, e -> realizarValidacionDisponibilidad());
        timerValidacion.setRepeats(false);
        
        initComponents();
        setSize(600, 750);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setModal(true);
        
        inicializado = true;
        
        cargarTodosLosDatos();
    }
    
    private void initComponents() {
        setBackground(darkBg);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(darkBg);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(darkBg);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        int row = 0;
        
        // Título
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        JLabel titleLabel = new JLabel(esEdicion ? "Editar Cita" : " Nueva Cita");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(textLight);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        formPanel.add(titleLabel, gbc);
        row++;
        gbc.gridwidth = 1;
        
        // Paciente
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(createLabel("Paciente *:"), gbc);
        gbc.gridx = 1;
        cbPaciente = new JComboBox<>();
        cbPaciente.setBackground(fieldBg);
        cbPaciente.setForeground(textLight);
        cbPaciente.setBorder(BorderFactory.createLineBorder(fieldBorder));
        cbPaciente.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cbPaciente.addActionListener(e -> {
            mostrarInfoPaciente();
            if (inicializado) {
                validarDisponibilidadEnTiempoReal();
            }
        });
        formPanel.add(cbPaciente, gbc);
        row++;
        
        // Información del paciente
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        lblPacienteInfo = new JLabel(" ");
        lblPacienteInfo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblPacienteInfo.setForeground(textGray);
        lblPacienteInfo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(fieldBorder),
            new EmptyBorder(5, 10, 5, 10)
        ));
        lblPacienteInfo.setBackground(fieldBg);
        lblPacienteInfo.setOpaque(true);
        formPanel.add(lblPacienteInfo, gbc);
        row++;
        gbc.gridwidth = 1;
        
        // Odontólogo
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(createLabel("Odontólogo *:"), gbc);
        gbc.gridx = 1;
        cbOdontologo = new JComboBox<>();
        cbOdontologo.setBackground(fieldBg);
        cbOdontologo.setForeground(textLight);
        cbOdontologo.setBorder(BorderFactory.createLineBorder(fieldBorder));
        cbOdontologo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cbOdontologo.addActionListener(e -> {
            actualizarHorasDisponibles();
            if (inicializado) {
                validarDisponibilidadEnTiempoReal();
            }
        });
        formPanel.add(cbOdontologo, gbc);
        row++;
        
        // Servicio
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(createLabel("Servicio *:"), gbc);
        gbc.gridx = 1;
        cbServicio = new JComboBox<>();
        cbServicio.setBackground(fieldBg);
        cbServicio.setForeground(textLight);
        cbServicio.setBorder(BorderFactory.createLineBorder(fieldBorder));
        cbServicio.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cbServicio.addActionListener(e -> {
            actualizarDuracion();
            if (inicializado) {
                validarDisponibilidadEnTiempoReal();
            }
        });
        formPanel.add(cbServicio, gbc);
        row++;
        
        // Fecha
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(createLabel("Fecha *:"), gbc);
        gbc.gridx = 1;
        
        DatePickerSettings settings = new DatePickerSettings();
        settings.setFormatForDatesCommonEra(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        settings.setAllowEmptyDates(false);
        
        dpFecha = new DatePicker(settings);
        dpFecha.setDate(LocalDate.now());
        dpFecha.setPreferredSize(new Dimension(180, 32));
        dpFecha.addDateChangeListener(e -> {
            actualizarHorasDisponibles();
            validarFechaSeleccionada();
            if (inicializado) {
                validarDisponibilidadEnTiempoReal();
            }
        });
        
        dpFecha.getComponentDateTextField().setBackground(fieldBg);
        dpFecha.getComponentDateTextField().setForeground(textLight);
        dpFecha.getComponentDateTextField().setCaretColor(textLight);
        dpFecha.getComponentDateTextField().setBorder(BorderFactory.createLineBorder(fieldBorder));
        dpFecha.getComponentDateTextField().setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        dpFecha.getComponentToggleCalendarButton().setBackground(fieldBg);
        dpFecha.getComponentToggleCalendarButton().setForeground(textLight);
        dpFecha.getComponentToggleCalendarButton().setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        formPanel.add(dpFecha, gbc);
        row++;
        
        // Información de fecha
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        lblFechaInfo = new JLabel("Seleccione una fecha (puede ser hoy o futuro)");
        lblFechaInfo.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblFechaInfo.setForeground(textGray);
        formPanel.add(lblFechaInfo, gbc);
        row++;
        gbc.gridwidth = 1;
        
        // Hora
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(createLabel("Hora *:"), gbc);
        gbc.gridx = 1;
        cbHora = new JComboBox<>();
        cbHora.setBackground(fieldBg);
        cbHora.setForeground(textLight);
        cbHora.setBorder(BorderFactory.createLineBorder(fieldBorder));
        cbHora.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cbHora.addActionListener(e -> {
            if (inicializado) {
                validarDisponibilidadEnTiempoReal();
            }
        });
        cargarHorasPorDefecto();
        formPanel.add(cbHora, gbc);
        row++;
        
        // Indicador de disponibilidad
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        lblDisponibilidad = new JLabel(" ");
        lblDisponibilidad.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblDisponibilidad.setHorizontalAlignment(SwingConstants.CENTER);
        formPanel.add(lblDisponibilidad, gbc);
        row++;
        gbc.gridwidth = 1;
        
        // Duración
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(createLabel("Duración (min):"), gbc);
        gbc.gridx = 1;
        txtDuracion = new JTextField(3);
        txtDuracion.setText("30");
        txtDuracion.setBackground(fieldBg);
        txtDuracion.setForeground(textLight);
        txtDuracion.setBorder(BorderFactory.createLineBorder(fieldBorder));
        txtDuracion.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtDuracion.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) { 
                validarDuracion();
                actualizarHorasDisponibles();
                if (inicializado) {
                    validarDisponibilidadEnTiempoReal();
                }
            }
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) { 
                validarDuracion();
                actualizarHorasDisponibles();
                if (inicializado) {
                    validarDisponibilidadEnTiempoReal();
                }
            }
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) { 
                validarDuracion();
                actualizarHorasDisponibles();
                if (inicializado) {
                    validarDisponibilidadEnTiempoReal();
                }
            }
        });
        formPanel.add(txtDuracion, gbc);
        row++;
        
        // Estado (solo en edición)
        if (esEdicion) {
            gbc.gridx = 0; gbc.gridy = row;
            formPanel.add(createLabel("Estado:"), gbc);
            gbc.gridx = 1;
            cbEstado = new JComboBox<>(Cita.getEstados());
            cbEstado.setBackground(fieldBg);
            cbEstado.setForeground(textLight);
            cbEstado.setBorder(BorderFactory.createLineBorder(fieldBorder));
            cbEstado.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            formPanel.add(cbEstado, gbc);
            row++;
        }
        
        // Nota
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(createLabel("Nota:"), gbc);
        gbc.gridx = 1;
        txtNota = new JTextArea(3, 20);
        txtNota.setLineWrap(true);
        txtNota.setWrapStyleWord(true);
        txtNota.setBackground(fieldBg);
        txtNota.setForeground(textLight);
        txtNota.setCaretColor(textLight);
        txtNota.setBorder(BorderFactory.createLineBorder(fieldBorder));
        txtNota.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JScrollPane scrollNota = new JScrollPane(txtNota);
        scrollNota.setBackground(fieldBg);
        scrollNota.setBorder(BorderFactory.createLineBorder(fieldBorder));
        formPanel.add(scrollNota, gbc);
        row++;
        
        JScrollPane scrollForm = new JScrollPane(formPanel);
        scrollForm.setBackground(darkBg);
        scrollForm.setBorder(BorderFactory.createEmptyBorder());
        scrollForm.getViewport().setBackground(darkBg);
        scrollForm.setPreferredSize(new Dimension(520, 550));
        
        // Panel de progreso
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
        
        lblEstado = new JLabel("Cargando datos...");
        lblEstado.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblEstado.setForeground(accentBlue);
        progressPanel.add(lblEstado, BorderLayout.SOUTH);
        
        // Botones
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnPanel.setBackground(darkBg);
        
        btnGuardar = createDialogButton("Guardar", accentGreen);
        btnGuardar.addActionListener(e -> guardarCita());
        btnPanel.add(btnGuardar);
        
        btnCancelar = createDialogButton("Cancelar", accentRed);
        btnCancelar.addActionListener(e -> dispose());
        btnPanel.add(btnCancelar);
        
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(darkBg);
        centerPanel.add(scrollForm, BorderLayout.CENTER);
        centerPanel.add(progressPanel, BorderLayout.SOUTH);
        
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(btnPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        
        validarFechaSeleccionada();
    }
    
    // ======================= CARGA DE DATOS =======================
    
    private void cargarTodosLosDatos() {
        System.out.println("🔄 [DEBUG] cargarTodosLosDatos() - INICIO");
        System.out.println("📌 Cita para cargar: " + (cita != null ? cita.getId() : "null"));
        
        mostrarProgreso("Cargando datos...");
        
        SwingWorker<Void, Integer> worker = new SwingWorker<Void, Integer>() {
            @Override
            protected Void doInBackground() throws Exception {
                System.out.println("🔄 [DEBUG] SwingWorker doInBackground - INICIO");
                
                // 1. Cargar pacientes
                System.out.println("📋 Cargando pacientes...");
                List<Paciente> pacientes = pacienteController.listarTodos();
                SwingUtilities.invokeLater(() -> {
                    cbPaciente.removeAllItems();
                    cbPaciente.addItem("");
                    if (pacientes != null) {
                        for (Paciente p : pacientes) {
                            cbPaciente.addItem(p.getNombreCompleto());
                        }
                        System.out.println("✅ Pacientes cargados: " + pacientes.size());
                    } else {
                        System.out.println("❌ No se cargaron pacientes");
                    }
                    System.out.println("📊 Items en cbPaciente: " + cbPaciente.getItemCount());
                });
                Thread.sleep(200);
                publish(25);
                
                // 2. Cargar odontólogos
                System.out.println("📋 Cargando odontólogos...");
                List<Usuario> odontologos = usuariosController.listarOdontologos();
                SwingUtilities.invokeLater(() -> {
                    cbOdontologo.removeAllItems();
                    cbOdontologo.addItem("");
                    if (odontologos != null) {
                        for (Usuario u : odontologos) {
                            cbOdontologo.addItem(u.getNombre());
                        }
                        System.out.println("✅ Odontólogos cargados: " + odontologos.size());
                    } else {
                        System.out.println("❌ No se cargaron odontólogos");
                    }
                    System.out.println("📊 Items en cbOdontologo: " + cbOdontologo.getItemCount());
                });
                Thread.sleep(200);
                publish(50);
                
                // 3. Cargar servicios
                System.out.println("📋 Cargando servicios...");
                List<Servicio> servicios = servicioController.listarActivos();
                SwingUtilities.invokeLater(() -> {
                    cbServicio.removeAllItems();
                    cbServicio.addItem("");
                    if (servicios != null && !servicios.isEmpty()) {
                        for (Servicio s : servicios) {
                            cbServicio.addItem(s.getNombre());
                        }
                        System.out.println("✅ Servicios cargados: " + servicios.size());
                    } else {
                        System.out.println("❌ No se cargaron servicios");
                        cbServicio.addItem("No hay servicios disponibles");
                    }
                    System.out.println("📊 Items en cbServicio: " + cbServicio.getItemCount());
                });
                Thread.sleep(200);
                publish(100);
                
                System.out.println("🔄 [DEBUG] SwingWorker doInBackground - FIN");
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
                System.out.println("🔄 [DEBUG] SwingWorker done() - INICIO");
                ocultarProgreso();
                datosCargados = true;
                
                System.out.println("📊 Estado final de combos:");
                System.out.println("   cbPaciente items: " + cbPaciente.getItemCount());
                System.out.println("   cbOdontologo items: " + cbOdontologo.getItemCount());
                System.out.println("   cbServicio items: " + cbServicio.getItemCount());
                
                // ===== AHORA CARGAR LA CITA =====
                if (cita != null && cita.getId() > 0) {
                    System.out.println("🔄 [DEBUG] Cargando cita para editar...");
                    System.out.println("   ID: " + cita.getId());
                    System.out.println("   Paciente: " + cita.getPacienteNombre());
                    System.out.println("   Odontólogo: " + cita.getOdontologoNombre());
                    System.out.println("   Servicio: " + cita.getServicioNombre());
                    cargarCitaEnFormulario(cita);
                } else {
                    System.out.println("📅 [DEBUG] No hay cita para editar");
                    limpiarCampos();
                }
                
                System.out.println("🔄 [DEBUG] SwingWorker done() - FIN");
            }
        };
        worker.execute();
    }
    
    private void cargarCitaEnFormulario(Cita cita) {
        System.out.println("========== [DEBUG] cargarCitaEnFormulario() ==========");
        System.out.println("Cita ID: " + cita.getId());
        System.out.println("Paciente: " + cita.getPacienteNombre() + " (ID: " + cita.getPacienteId() + ")");
        System.out.println("Odontólogo: " + cita.getOdontologoNombre() + " (ID: " + cita.getOdontologoId() + ")");
        System.out.println("Servicio: " + cita.getServicioNombre() + " (ID: " + cita.getServicioId() + ")");
        System.out.println("Fecha: " + cita.getFecha());
        System.out.println("Hora: " + cita.getHora());
        System.out.println("Estado: " + cita.getEstado());
        System.out.println("====================================================");
        
        // ===== SELECCIONAR PACIENTE =====
        if (cita.getPacienteNombre() != null && !cita.getPacienteNombre().isEmpty()) {
            System.out.println("🔍 Buscando paciente: '" + cita.getPacienteNombre() + "'");
            boolean encontrado = false;
            for (int i = 0; i < cbPaciente.getItemCount(); i++) {
                String item = cbPaciente.getItemAt(i);
                if (item != null && item.equals(cita.getPacienteNombre())) {
                    cbPaciente.setSelectedIndex(i);
                    encontrado = true;
                    System.out.println("✅ Paciente SELECCIONADO en índice: " + i);
                    break;
                }
            }
            if (!encontrado) {
                System.err.println("❌ Paciente NO encontrado: " + cita.getPacienteNombre());
                Paciente p = pacienteController.buscarPorId(cita.getPacienteId());
                if (p != null) {
                    System.out.println("   Agregando paciente al combo: " + p.getNombreCompleto());
                    cbPaciente.addItem(p.getNombreCompleto());
                    cbPaciente.setSelectedItem(p.getNombreCompleto());
                }
            }
        }
        
        // ===== SELECCIONAR ODONTÓLOGO =====
        if (cita.getOdontologoNombre() != null && !cita.getOdontologoNombre().isEmpty()) {
            System.out.println("🔍 Buscando odontólogo: '" + cita.getOdontologoNombre() + "'");
            boolean encontrado = false;
            for (int i = 0; i < cbOdontologo.getItemCount(); i++) {
                String item = cbOdontologo.getItemAt(i);
                if (item != null && item.equals(cita.getOdontologoNombre())) {
                    cbOdontologo.setSelectedIndex(i);
                    encontrado = true;
                    System.out.println("✅ Odontólogo SELECCIONADO en índice: " + i);
                    break;
                }
            }
            if (!encontrado) {
                System.err.println("❌ Odontólogo NO encontrado: " + cita.getOdontologoNombre());
                Usuario u = usuariosController.buscarPorId(cita.getOdontologoId());
                if (u != null) {
                    System.out.println("   Agregando odontólogo al combo: " + u.getNombre());
                    cbOdontologo.addItem(u.getNombre());
                    cbOdontologo.setSelectedItem(u.getNombre());
                }
            }
        }
        
        // ===== SELECCIONAR SERVICIO =====
        if (cita.getServicioNombre() != null && !cita.getServicioNombre().isEmpty()) {
            System.out.println("🔍 Buscando servicio: '" + cita.getServicioNombre() + "'");
            boolean encontrado = false;
            for (int i = 0; i < cbServicio.getItemCount(); i++) {
                String item = cbServicio.getItemAt(i);
                if (item != null && item.equals(cita.getServicioNombre())) {
                    cbServicio.setSelectedIndex(i);
                    encontrado = true;
                    System.out.println("✅ Servicio SELECCIONADO en índice: " + i);
                    break;
                }
            }
            if (!encontrado) {
                System.err.println("❌ Servicio NO encontrado: " + cita.getServicioNombre());
                Servicio s = servicioController.buscarPorId(cita.getServicioId());
                if (s != null) {
                    System.out.println("   Agregando servicio al combo: " + s.getNombre());
                    cbServicio.addItem(s.getNombre());
                    cbServicio.setSelectedItem(s.getNombre());
                }
            }
        }
        
        // ===== FECHA =====
        try {
            if (cita.getFecha() != null && !cita.getFecha().isEmpty()) {
                dpFecha.setDate(LocalDate.parse(cita.getFecha()));
                System.out.println("✅ Fecha seleccionada: " + cita.getFecha());
            }
        } catch (Exception e) {
            System.err.println("Error en fecha: " + e.getMessage());
            dpFecha.setDate(LocalDate.now());
        }
        
        // ===== HORA =====
        actualizarHorasDisponibles();
        if (cita.getHora() != null && !cita.getHora().isEmpty()) {
            boolean encontrado = false;
            for (int i = 0; i < cbHora.getItemCount(); i++) {
                String item = cbHora.getItemAt(i);
                if (item != null && item.equals(cita.getHora())) {
                    cbHora.setSelectedIndex(i);
                    encontrado = true;
                    System.out.println("✅ Hora seleccionada: " + cita.getHora());
                    break;
                }
            }
            if (!encontrado) {
                System.out.println("⚠️ Hora no encontrada, agregando: " + cita.getHora());
                cbHora.addItem(cita.getHora());
                cbHora.setSelectedItem(cita.getHora());
            }
        }
        
        // ===== DURACIÓN =====
        txtDuracion.setText(String.valueOf(cita.getDuracion() > 0 ? cita.getDuracion() : 30));
        
        // ===== NOTA =====
        txtNota.setText(cita.getNota() != null ? cita.getNota() : "");
        
        // ===== ESTADO =====
        if (esEdicion && cbEstado != null && cita.getEstado() != null) {
            cbEstado.setSelectedItem(cita.getEstado());
            System.out.println("✅ Estado seleccionado: " + cita.getEstado());
        }
        
        mostrarInfoPaciente();
        validarFechaSeleccionada();
        
        // Validar disponibilidad después de cargar todo
        SwingUtilities.invokeLater(() -> {
            validarDisponibilidadEnTiempoReal();
        });
        
        System.out.println("========== [DEBUG] FIN cargarCitaEnFormulario() ==========");
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
    
    private void setControlesEnabled(boolean enabled) {
        cbPaciente.setEnabled(enabled);
        cbOdontologo.setEnabled(enabled);
        cbServicio.setEnabled(enabled);
        dpFecha.setEnabled(enabled);
        cbHora.setEnabled(enabled);
        txtDuracion.setEnabled(enabled);
        txtNota.setEnabled(enabled);
        btnGuardar.setEnabled(enabled);
        if (cbEstado != null) {
            cbEstado.setEnabled(enabled);
        }
    }
    
    // ======================= VALIDACIÓN EN TIEMPO REAL =======================
    
    private void validarDisponibilidadEnTiempoReal() {
        if (timerValidacion != null) {
            timerValidacion.restart();
        }
    }
    
    private void realizarValidacionDisponibilidad() {
        String hora = (String) cbHora.getSelectedItem();
        if (hora == null || hora.isEmpty()) {
            lblDisponibilidad.setText("⏳ Seleccione una hora");
            lblDisponibilidad.setForeground(textGray);
            cbHora.setBorder(BorderFactory.createLineBorder(fieldBorder));
            return;
        }
        
        LocalDate fecha = dpFecha.getDate();
        if (fecha == null) {
            lblDisponibilidad.setText("Seleccione una fecha");
            lblDisponibilidad.setForeground(textGray);
            return;
        }
        
        int odontologoId = getSelectedOdontologoId();
        if (odontologoId <= 0) {
            lblDisponibilidad.setText("Seleccione un odontólogo");
            lblDisponibilidad.setForeground(textGray);
            return;
        }
        
        try {
            int duracion = Integer.parseInt(txtDuracion.getText().trim());
            
            boolean disponible = citaController.tieneDisponibilidad(odontologoId, fecha.toString(), hora, duracion);
            boolean tieneAtencion = citaController.tieneAtencion(fecha.toString());
            
            if (!tieneAtencion) {
                lblDisponibilidad.setText("No hay atención en esta fecha");
                lblDisponibilidad.setForeground(accentRed);
                cbHora.setBorder(BorderFactory.createLineBorder(accentRed, 2));
                return;
            }
            
            if (disponible) {
                lblDisponibilidad.setText("Horario disponible");
                lblDisponibilidad.setForeground(accentGreen);
                cbHora.setBorder(BorderFactory.createLineBorder(accentGreen, 2));
            } else {
                lblDisponibilidad.setText("Horario ocupado");
                lblDisponibilidad.setForeground(accentRed);
                cbHora.setBorder(BorderFactory.createLineBorder(accentRed, 2));
            }
            
        } catch (NumberFormatException e) {
            lblDisponibilidad.setText("Duración inválida");
            lblDisponibilidad.setForeground(accentRed);
        } catch (Exception e) {
            System.err.println("Error en validación: " + e.getMessage());
        }
    }
    
    // ========== MÉTODOS AUXILIARES ==========
    
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        label.setForeground(textGray);
        return label;
    }
    
    private JButton createDialogButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(120, 40));
        return btn;
    }
    
    private void cargarHorasPorDefecto() {
        cbHora.removeAllItems();
        cbHora.addItem("");
        String[] horasDefault = {"08:00", "08:30", "09:00", "09:30", "10:00", "10:30", 
                                 "11:00", "11:30", "12:00", "12:30", "13:00", "13:30",
                                 "14:00", "14:30", "15:00", "15:30", "16:00", "16:30", "17:00", "17:30"};
        for (String h : horasDefault) {
            cbHora.addItem(h);
        }
    }
    
    private void mostrarInfoPaciente() {
        String seleccion = (String) cbPaciente.getSelectedItem();
        if (seleccion != null && !seleccion.isEmpty()) {
            List<Paciente> pacientes = pacienteController.listarTodos();
            if (pacientes != null) {
                for (Paciente p : pacientes) {
                    if (p.getNombreCompleto().equals(seleccion)) {
                        String telefono = p.getTelefono() != null ? p.getTelefono() : "Sin teléfono";
                        String email = p.getEmail() != null ? p.getEmail() : "Sin email";
                        lblPacienteInfo.setText("📱 " + telefono + "  |  ✉️ " + email);
                        return;
                    }
                }
            }
        }
        lblPacienteInfo.setText(" ");
    }
    
    private void actualizarHorasDisponibles() {
        cbHora.removeAllItems();
        LocalDate fecha = dpFecha.getDate();
        int odontologoId = getSelectedOdontologoId();
        
        if (fecha == null || odontologoId <= 0) {
            cargarHorasPorDefecto();
            cbHora.setSelectedIndex(-1);
            return;
        }
        
        try {
            String fechaStr = fecha.toString();
            int duracion = Integer.parseInt(txtDuracion.getText().trim());
            List<String> horas = citaController.getHorasDisponibles(odontologoId, fechaStr, duracion);
            
            if (horas != null && !horas.isEmpty()) {
                cbHora.addItem("");
                for (String hora : horas) {
                    cbHora.addItem(hora);
                }
                cbHora.setSelectedIndex(-1);
                lblDisponibilidad.setText(" " + horas.size() + " horarios disponibles");
                lblDisponibilidad.setForeground(accentGreen);
            } else {
                cargarHorasPorDefecto();
                cbHora.setSelectedIndex(-1);
                lblDisponibilidad.setText("No hay horarios disponibles para este día");
                lblDisponibilidad.setForeground(accentRed);
            }
        } catch (Exception e) {
            cargarHorasPorDefecto();
            cbHora.setSelectedIndex(-1);
        }
    }
    
    private void actualizarDuracion() {
        String nombreServicio = (String) cbServicio.getSelectedItem();
        if (nombreServicio != null && !nombreServicio.isEmpty() && !nombreServicio.equals("No hay servicios disponibles")) {
            List<Servicio> servicios = servicioController.listarActivos();
            if (servicios != null) {
                for (Servicio s : servicios) {
                    if (s.getNombre().equals(nombreServicio)) {
                        txtDuracion.setText(String.valueOf(s.getDuracionMinutos()));
                        actualizarHorasDisponibles();
                        break;
                    }
                }
            }
        }
    }
    
    private int getSelectedPacienteId() {
        String seleccion = (String) cbPaciente.getSelectedItem();
        if (seleccion != null && !seleccion.isEmpty()) {
            List<Paciente> pacientes = pacienteController.listarTodos();
            if (pacientes != null) {
                for (Paciente p : pacientes) {
                    if (p.getNombreCompleto().equals(seleccion)) {
                        return p.getId();
                    }
                }
            }
        }
        return -1;
    }
    
    private int getSelectedOdontologoId() {
        String seleccion = (String) cbOdontologo.getSelectedItem();
        if (seleccion != null && !seleccion.isEmpty()) {
            List<Usuario> odontologos = usuariosController.listarOdontologos();
            if (odontologos != null) {
                for (Usuario u : odontologos) {
                    if (u.getNombre().equals(seleccion)) {
                        return u.getId();
                    }
                }
            }
        }
        return -1;
    }
    
    private int getSelectedServicioId() {
        String nombreServicio = (String) cbServicio.getSelectedItem();
        if (nombreServicio != null && !nombreServicio.isEmpty() && !nombreServicio.equals("No hay servicios disponibles")) {
            List<Servicio> servicios = servicioController.listarActivos();
            if (servicios != null) {
                for (Servicio s : servicios) {
                    if (s.getNombre().equals(nombreServicio)) {
                        return s.getId();
                    }
                }
            }
        }
        return -1;
    }
    
    private void validarFechaSeleccionada() {
        LocalDate fecha = dpFecha.getDate();
        if (fecha != null) {
            LocalDate hoy = LocalDate.now();
            
            String mensajeHorario = citaController.getMensajeHorario(fecha.toString());
            
            if (fecha.isBefore(hoy)) {
                lblFechaInfo.setForeground(accentRed);
                lblFechaInfo.setText("No se pueden agendar citas en fechas pasadas");
            } else if (mensajeHorario.contains("cerrada") || mensajeHorario.contains("Cerrada")) {
                lblFechaInfo.setForeground(accentRed);
                lblFechaInfo.setText(mensajeHorario);
            } else {
                lblFechaInfo.setForeground(new Color(60, 180, 110));
                lblFechaInfo.setText(" " + mensajeHorario);
            }
        } else {
            lblFechaInfo.setForeground(textGray);
            lblFechaInfo.setText(" Seleccione una fecha válida");
        }
    }
    
    private void validarDuracion() {
        try {
            int duracion = Integer.parseInt(txtDuracion.getText().trim());
            if (duracion < 15 || duracion > 240) {
                txtDuracion.setBorder(BorderFactory.createLineBorder(accentRed, 2));
            } else {
                txtDuracion.setBorder(BorderFactory.createLineBorder(fieldBorder));
            }
        } catch (NumberFormatException e) {
            txtDuracion.setBorder(BorderFactory.createLineBorder(accentRed, 2));
        }
    }
    
    private void limpiarCampos() {
        cbPaciente.setSelectedIndex(-1);
        cbOdontologo.setSelectedIndex(-1);
        cbServicio.setSelectedIndex(-1);
        dpFecha.setDate(LocalDate.now());
        cargarHorasPorDefecto();
        cbHora.setSelectedIndex(-1);
        txtDuracion.setText("30");
        txtNota.setText("");
        lblPacienteInfo.setText(" ");
        validarFechaSeleccionada();
        if (cbEstado != null) {
            cbEstado.setSelectedItem(Cita.ESTADO_PROGRAMADA);
        }
        lblDisponibilidad.setText(" ");
    }
    
    private String validarCampos() {
        if (getSelectedPacienteId() <= 0) {
            return "Seleccione un paciente válido";
        }
        
        if (getSelectedOdontologoId() <= 0) {
            return "Seleccione un odontólogo válido";
        }
        
        if (getSelectedServicioId() <= 0) {
            return "Seleccione un servicio válido";
        }
        
        LocalDate fecha = dpFecha.getDate();
        if (fecha == null) {
            return "Seleccione una fecha";
        }
        if (fecha.isBefore(LocalDate.now())) {
            return "No se pueden agendar citas en fechas pasadas";
        }
        
        if (!citaController.tieneAtencion(fecha.toString())) {
            return "No hay atención en esta fecha (domingo o festivo)";
        }
        
        String hora = (String) cbHora.getSelectedItem();
        if (hora == null || hora.isEmpty()) {
            return "Seleccione una hora disponible";
        }
        
        try {
            int duracion = Integer.parseInt(txtDuracion.getText().trim());
            if (duracion < 15 || duracion > 240) {
                return "La duración debe estar entre 15 y 240 minutos";
            }
        } catch (NumberFormatException e) {
            return "Ingrese una duración válida (número)";
        }
        
        int odontologoId = getSelectedOdontologoId();
        if (!citaController.tieneDisponibilidad(odontologoId, fecha.toString(), hora, 
                Integer.parseInt(txtDuracion.getText().trim()))) {
            return "❌ El odontólogo ya tiene una cita en este horario";
        }
        
        return null;
    }
    
    private void guardarCita() {
        String error = validarCampos();
        if (error != null) {
            JOptionPane.showMessageDialog(this, "⚠️ " + error, "Error de validación", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        mostrarProgreso("Guardando cita...");
        
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                int pacienteId = getSelectedPacienteId();
                int odontologoId = getSelectedOdontologoId();
                int servicioId = getSelectedServicioId();
                String fecha = dpFecha.getDate().toString();
                String hora = (String) cbHora.getSelectedItem();
                int duracion = Integer.parseInt(txtDuracion.getText().trim());
                String nota = txtNota.getText().trim();
                
                Cita nuevaCita = new Cita();
                
                if (esEdicion && cita != null && cita.getId() > 0) {
                    nuevaCita.setId(cita.getId());
                } else {
                    nuevaCita.setId(0);
                }
                
                nuevaCita.setPacienteId(pacienteId);
                nuevaCita.setOdontologoId(odontologoId);
                nuevaCita.setServicioId(servicioId);
                nuevaCita.setFecha(fecha);
                nuevaCita.setHora(hora);
                nuevaCita.setDuracion(duracion);
                nuevaCita.setNota(nota);
                nuevaCita.setModificadaPor("admin");
                
                if (esEdicion && cbEstado != null) {
                    nuevaCita.setEstado((String) cbEstado.getSelectedItem());
                } else {
                    nuevaCita.setEstado(Cita.ESTADO_PROGRAMADA);
                }
                
                System.out.println("💾 Guardando cita - ID: " + nuevaCita.getId() + 
                                 " | Servicio ID: " + servicioId + 
                                 " | Estado: " + nuevaCita.getEstado());
                
                Thread.sleep(300);
                
                return citaController.guardarCita(nuevaCita);
            }
            
            @Override
            protected void done() {
                try {
                    if (get()) {
                        guardado = true;
                        dispose();
                    }
                } catch (Exception e) {
                    System.err.println("❌ Error al guardar: " + e.getMessage());
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(DialogCita.this, 
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