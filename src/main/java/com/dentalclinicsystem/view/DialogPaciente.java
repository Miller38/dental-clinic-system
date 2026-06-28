package com.dentalclinicsystem.view;

import com.dentalclinicsystem.controller.PacienteController;
import com.dentalclinicsystem.model.Paciente;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class DialogPaciente extends JDialog {
    
    private PacienteController controller;
    private Paciente paciente;
    private boolean guardado = false;
    private boolean esEdicion = false;
    
    // Componentes del formulario
    private JTextField txtNombre, txtApellido, txtDocumento, txtTelefono;
    private JTextField txtTelefonoAlt, txtEmail, txtDireccion, txtOcupacion;
    private JTextField txtEdad, txtContactoEmergencia, txtTelefonoEmergencia;
    private JComboBox<String> cbGenero, cbEstadoCivil;
    private JTextArea txtAlergias, txtEnfermedades, txtMedicamentos;
    private JButton btnGuardar, btnCancelar;
    private JLabel lblDocumentoValido, lblTelefonoValido;
    
    private Color darkBg = new Color(30, 30, 35);
    private Color darkCard = new Color(40, 40, 45);
    private Color textLight = new Color(220, 220, 230);
    private Color textGray = new Color(150, 150, 165);
    private Color accentGreen = new Color(60, 180, 110);
    private Color accentRed = new Color(210, 80, 80);
    private Color fieldBg = new Color(50, 50, 55);
    private Color fieldBorder = new Color(60, 60, 65);
    
    public DialogPaciente(JFrame parent, boolean edicion) {
        super(parent, edicion ? "Editar Paciente" : " Nuevo Paciente", true);
        this.controller = new PacienteController();
        this.esEdicion = edicion;
        initComponents();
        setSize(650, 750);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setModal(true);
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
        JLabel titleLabel = new JLabel(esEdicion ? "Editar Paciente" : " Nuevo Paciente");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(textLight);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        formPanel.add(titleLabel, gbc);
        row++;
        
        gbc.gridwidth = 1;
        
        // ========== NOMBRE ==========
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(createLabel("Nombre *:"), gbc);
        gbc.gridx = 1;
        txtNombre = createTextField(25);
        txtNombre.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isLetter(c) && c != KeyEvent.VK_SPACE && c != KeyEvent.VK_BACK_SPACE) {
                    e.consume();
                }
            }
        });
        formPanel.add(txtNombre, gbc);
        row++;
        
        // ========== APELLIDO ==========
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(createLabel("Apellido *:"), gbc);
        gbc.gridx = 1;
        txtApellido = createTextField(25);
        txtApellido.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isLetter(c) && c != KeyEvent.VK_SPACE && c != KeyEvent.VK_BACK_SPACE) {
                    e.consume();
                }
            }
        });
        formPanel.add(txtApellido, gbc);
        row++;
        
        // ========== DOCUMENTO ==========
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(createLabel("Documento *:"), gbc);
        gbc.gridx = 1;
        txtDocumento = createTextField(15);
        aplicarFiltroNumerico(txtDocumento, 15);
        txtDocumento.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { validarDocumento(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { validarDocumento(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { validarDocumento(); }
        });
        formPanel.add(txtDocumento, gbc);
        row++;
        
        // Indicador de validación de documento
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        lblDocumentoValido = new JLabel("Ingrese el número de documento (7-15 dígitos)");
        lblDocumentoValido.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblDocumentoValido.setForeground(textGray);
        formPanel.add(lblDocumentoValido, gbc);
        row++;
        gbc.gridwidth = 1;
        
        // ========== TELÉFONO ==========
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(createLabel("Teléfono *:"), gbc);
        gbc.gridx = 1;
        txtTelefono = createTextField(10);
        aplicarFiltroNumerico(txtTelefono, 10);
        txtTelefono.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { validarTelefono(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { validarTelefono(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { validarTelefono(); }
        });
        formPanel.add(txtTelefono, gbc);
        row++;
        
        // Indicador de validación de teléfono
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        lblTelefonoValido = new JLabel("Ingrese el número de teléfono (7-10 dígitos)");
        lblTelefonoValido.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblTelefonoValido.setForeground(textGray);
        formPanel.add(lblTelefonoValido, gbc);
        row++;
        gbc.gridwidth = 1;
        
        // ========== TELÉFONO ALTERNATIVO ==========
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(createLabel("Teléfono Alt:"), gbc);
        gbc.gridx = 1;
        txtTelefonoAlt = createTextField(10);
        aplicarFiltroNumerico(txtTelefonoAlt, 10);
        formPanel.add(txtTelefonoAlt, gbc);
        row++;
        
        // ========== EMAIL ==========
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(createLabel("Email:"), gbc);
        gbc.gridx = 1;
        txtEmail = createTextField(25);
        formPanel.add(txtEmail, gbc);
        row++;
        
        // ========== EDAD ==========
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(createLabel("Edad:"), gbc);
        gbc.gridx = 1;
        txtEdad = createTextField(3);
        aplicarFiltroNumerico(txtEdad, 3);
        formPanel.add(txtEdad, gbc);
        row++;
        
        // ========== GÉNERO ==========
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(createLabel("Género:"), gbc);
        gbc.gridx = 1;
        cbGenero = new JComboBox<>(new String[]{"", "M", "F", "OTRO"});
        cbGenero.setBackground(fieldBg);
        cbGenero.setForeground(textLight);
        cbGenero.setBorder(BorderFactory.createLineBorder(fieldBorder));
        cbGenero.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formPanel.add(cbGenero, gbc);
        row++;
        
        // ========== ESTADO CIVIL ==========
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(createLabel("Estado Civil:"), gbc);
        gbc.gridx = 1;
        cbEstadoCivil = new JComboBox<>(new String[]{"", "Soltero/a", "Casado/a", "Divorciado/a", "Viudo/a", "Unión Libre"});
        cbEstadoCivil.setBackground(fieldBg);
        cbEstadoCivil.setForeground(textLight);
        cbEstadoCivil.setBorder(BorderFactory.createLineBorder(fieldBorder));
        cbEstadoCivil.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formPanel.add(cbEstadoCivil, gbc);
        row++;
        
        // ========== OCUPACIÓN ==========
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(createLabel("Ocupación:"), gbc);
        gbc.gridx = 1;
        txtOcupacion = createTextField(25);
        formPanel.add(txtOcupacion, gbc);
        row++;
        
        // ========== DIRECCIÓN ==========
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(createLabel("Dirección:"), gbc);
        gbc.gridx = 1;
        txtDireccion = createTextField(30);
        formPanel.add(txtDireccion, gbc);
        row++;
        
        // ========== CONTACTO EMERGENCIA ==========
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(createLabel("Contacto Emergencia:"), gbc);
        gbc.gridx = 1;
        txtContactoEmergencia = createTextField(25);
        formPanel.add(txtContactoEmergencia, gbc);
        row++;
        
        // ========== TELÉFONO EMERGENCIA ==========
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(createLabel("Teléfono Emergencia:"), gbc);
        gbc.gridx = 1;
        txtTelefonoEmergencia = createTextField(10);
        aplicarFiltroNumerico(txtTelefonoEmergencia, 10);
        formPanel.add(txtTelefonoEmergencia, gbc);
        row++;
        
        // ========== ALERGIAS ==========
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(createLabel("Alergias:"), gbc);
        gbc.gridx = 1;
        txtAlergias = createTextArea(2, 20);
        JScrollPane scrollAlergias = new JScrollPane(txtAlergias);
        scrollAlergias.setBackground(fieldBg);
        scrollAlergias.setBorder(BorderFactory.createLineBorder(fieldBorder));
        formPanel.add(scrollAlergias, gbc);
        row++;
        
        // ========== ENFERMEDADES ==========
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(createLabel("Enfermedades:"), gbc);
        gbc.gridx = 1;
        txtEnfermedades = createTextArea(2, 20);
        JScrollPane scrollEnfermedades = new JScrollPane(txtEnfermedades);
        scrollEnfermedades.setBackground(fieldBg);
        scrollEnfermedades.setBorder(BorderFactory.createLineBorder(fieldBorder));
        formPanel.add(scrollEnfermedades, gbc);
        row++;
        
        // ========== MEDICAMENTOS ==========
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(createLabel("Medicamentos:"), gbc);
        gbc.gridx = 1;
        txtMedicamentos = createTextArea(2, 20);
        JScrollPane scrollMedicamentos = new JScrollPane(txtMedicamentos);
        scrollMedicamentos.setBackground(fieldBg);
        scrollMedicamentos.setBorder(BorderFactory.createLineBorder(fieldBorder));
        formPanel.add(scrollMedicamentos, gbc);
        row++;
        
        JScrollPane scrollForm = new JScrollPane(formPanel);
        scrollForm.setBackground(darkBg);
        scrollForm.setBorder(BorderFactory.createEmptyBorder());
        scrollForm.getViewport().setBackground(darkBg);
        scrollForm.setPreferredSize(new Dimension(600, 500));
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnPanel.setBackground(darkBg);
        
        btnGuardar = createDialogButton("💾 Guardar", accentGreen);
        btnGuardar.addActionListener(e -> guardarPaciente());
        btnPanel.add(btnGuardar);
        
        btnCancelar = createDialogButton("❌ Cancelar", accentRed);
        btnCancelar.addActionListener(e -> dispose());
        btnPanel.add(btnCancelar);
        
        mainPanel.add(scrollForm, BorderLayout.CENTER);
        mainPanel.add(btnPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    // ================================================================
    // ========== VALIDACIONES EN TIEMPO REAL ==========
    // ================================================================
    
    private void validarDocumento() {
        String doc = txtDocumento.getText().trim();
        if (doc.isEmpty()) {
            lblDocumentoValido.setText("📌 Ingrese el número de documento (7-15 dígitos)");
            lblDocumentoValido.setForeground(textGray);
            txtDocumento.setBorder(BorderFactory.createLineBorder(fieldBorder));
            return;
        }
        
        if (!doc.matches("\\d+")) {
            lblDocumentoValido.setText("❌ Solo números");
            lblDocumentoValido.setForeground(accentRed);
            txtDocumento.setBorder(BorderFactory.createLineBorder(accentRed, 2));
        } else if (doc.length() < 7) {
            lblDocumentoValido.setText("⚠️ Mínimo 7 dígitos");
            lblDocumentoValido.setForeground(accentRed);
            txtDocumento.setBorder(BorderFactory.createLineBorder(accentRed, 2));
        } else if (doc.length() > 15) {
            lblDocumentoValido.setText("⚠️ Máximo 15 dígitos");
            lblDocumentoValido.setForeground(accentRed);
            txtDocumento.setBorder(BorderFactory.createLineBorder(accentRed, 2));
        } else {
            lblDocumentoValido.setText("✅ Documento válido");
            lblDocumentoValido.setForeground(accentGreen);
            txtDocumento.setBorder(BorderFactory.createLineBorder(fieldBorder));
        }
    }
    
    private void validarTelefono() {
        String tel = txtTelefono.getText().trim();
        if (tel.isEmpty()) {
            lblTelefonoValido.setText("📌 Ingrese el número de teléfono (7-10 dígitos)");
            lblTelefonoValido.setForeground(textGray);
            txtTelefono.setBorder(BorderFactory.createLineBorder(fieldBorder));
            return;
        }
        
        if (!tel.matches("\\d+")) {
            lblTelefonoValido.setText("❌ Solo números");
            lblTelefonoValido.setForeground(accentRed);
            txtTelefono.setBorder(BorderFactory.createLineBorder(accentRed, 2));
        } else if (tel.length() < 7) {
            lblTelefonoValido.setText("⚠️ Mínimo 7 dígitos");
            lblTelefonoValido.setForeground(accentRed);
            txtTelefono.setBorder(BorderFactory.createLineBorder(accentRed, 2));
        } else if (tel.length() > 10) {
            lblTelefonoValido.setText("⚠️ Máximo 10 dígitos");
            lblTelefonoValido.setForeground(accentRed);
            txtTelefono.setBorder(BorderFactory.createLineBorder(accentRed, 2));
        } else {
            lblTelefonoValido.setText("✅ Teléfono válido");
            lblTelefonoValido.setForeground(accentGreen);
            txtTelefono.setBorder(BorderFactory.createLineBorder(fieldBorder));
        }
    }
    
    // ================================================================
    // ========== FILTRO PARA SOLO NÚMEROS ==========
    // ================================================================
    
    private void aplicarFiltroNumerico(JTextField field, int maxLength) {
        ((AbstractDocument) field.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) 
                    throws BadLocationException {
                if (string == null) return;
                if (string.matches("\\d*")) {
                    String newStr = fb.getDocument().getText(0, fb.getDocument().getLength()) + string;
                    if (newStr.length() <= maxLength) {
                        super.insertString(fb, offset, string, attr);
                    }
                }
            }
            
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) 
                    throws BadLocationException {
                if (text == null) return;
                if (text.matches("\\d*")) {
                    String newStr = fb.getDocument().getText(0, fb.getDocument().getLength()) + text;
                    if (newStr.length() <= maxLength) {
                        super.replace(fb, offset, length, text, attrs);
                    }
                }
            }
        });
        
        field.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c) && c != KeyEvent.VK_BACK_SPACE && c != KeyEvent.VK_DELETE) {
                    e.consume();
                }
            }
        });
    }
    
    // ================================================================
    // ========== MÉTODOS AUXILIARES ==========
    // ================================================================
    
    private JTextField createTextField(int columns) {
        JTextField field = new JTextField(columns);
        field.setBackground(fieldBg);
        field.setForeground(textLight);
        field.setCaretColor(textLight);
        field.setBorder(BorderFactory.createLineBorder(fieldBorder));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        return field;
    }
    
    private JTextArea createTextArea(int rows, int columns) {
        JTextArea area = new JTextArea(rows, columns);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBackground(fieldBg);
        area.setForeground(textLight);
        area.setCaretColor(textLight);
        area.setBorder(BorderFactory.createLineBorder(fieldBorder));
        area.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        return area;
    }
    
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
    
    // ================================================================
    // ========== SET PACIENTE Y GUARDAR ==========
    // ================================================================
    
    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
        if (paciente != null) {
            txtNombre.setText(paciente.getNombre() != null ? paciente.getNombre() : "");
            txtApellido.setText(paciente.getApellido() != null ? paciente.getApellido() : "");
            txtDocumento.setText(paciente.getNumeroDocumento() != null ? paciente.getNumeroDocumento() : "");
            txtTelefono.setText(paciente.getTelefono() != null ? paciente.getTelefono() : "");
            txtTelefonoAlt.setText(paciente.getTelefonoAlternativo() != null ? paciente.getTelefonoAlternativo() : "");
            txtEmail.setText(paciente.getEmail() != null ? paciente.getEmail() : "");
            txtDireccion.setText(paciente.getDireccion() != null ? paciente.getDireccion() : "");
            txtOcupacion.setText(paciente.getOcupacion() != null ? paciente.getOcupacion() : "");
            txtEdad.setText(paciente.getEdad() > 0 ? String.valueOf(paciente.getEdad()) : "");
            txtContactoEmergencia.setText(paciente.getContactoEmergenciaNombre() != null ? paciente.getContactoEmergenciaNombre() : "");
            txtTelefonoEmergencia.setText(paciente.getContactoEmergenciaTelefono() != null ? paciente.getContactoEmergenciaTelefono() : "");
            
            if (paciente.getGenero() != null) {
                cbGenero.setSelectedItem(paciente.getGenero());
            }
            if (paciente.getEstadoCivil() != null) {
                cbEstadoCivil.setSelectedItem(paciente.getEstadoCivil());
            }
            
            txtAlergias.setText(paciente.getAlergias() != null ? paciente.getAlergias() : "");
            txtEnfermedades.setText(paciente.getEnfermedadesSistema() != null ? paciente.getEnfermedadesSistema() : "");
            txtMedicamentos.setText(paciente.getMedicamentos() != null ? paciente.getMedicamentos() : "");
            
            validarDocumento();
            validarTelefono();
        }
    }
    
    private void guardarPaciente() {
        try {
            // Construir el paciente desde el formulario
            Paciente nuevoPaciente = construirPacienteDesdeFormulario();
            
            // Delegar toda la validación al Controller
            if (controller.guardarPaciente(nuevoPaciente)) {
                guardado = true;
                dispose();
            }
            
        } catch (Exception e) {
            System.err.println("Error al guardar paciente: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error al guardar: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private Paciente construirPacienteDesdeFormulario() {
        Paciente p = new Paciente();
        p.setId(paciente != null ? paciente.getId() : 0);
        p.setNombre(capitalizar(txtNombre.getText().trim()));
        p.setApellido(capitalizar(txtApellido.getText().trim()));
        p.setNumeroDocumento(txtDocumento.getText().trim());
        p.setTelefono(txtTelefono.getText().trim());
        p.setTelefonoAlternativo(txtTelefonoAlt.getText().trim());
        p.setEmail(txtEmail.getText().trim());
        p.setDireccion(txtDireccion.getText().trim());
        p.setOcupacion(txtOcupacion.getText().trim());
        p.setContactoEmergenciaNombre(txtContactoEmergencia.getText().trim());
        p.setContactoEmergenciaTelefono(txtTelefonoEmergencia.getText().trim());
        p.setGenero((String) cbGenero.getSelectedItem());
        p.setEstadoCivil((String) cbEstadoCivil.getSelectedItem());
        
        try {
            String edadStr = txtEdad.getText().trim();
            p.setEdad(edadStr.isEmpty() ? 0 : Integer.parseInt(edadStr));
        } catch (NumberFormatException e) {
            p.setEdad(0);
        }
        
        p.setAlergias(txtAlergias.getText().trim());
        p.setEnfermedadesSistema(txtEnfermedades.getText().trim());
        p.setMedicamentos(txtMedicamentos.getText().trim());
        p.setEstado(1);
        
        return p;
    }
    
    // ================================================================
    // ========== MÉTODOS DE UTILIDAD ==========
    // ================================================================
    
    private String capitalizar(String texto) {
        if (texto == null || texto.isEmpty()) return texto;
        texto = texto.toLowerCase().trim();
        String[] palabras = texto.split(" ");
        StringBuilder result = new StringBuilder();
        for (String palabra : palabras) {
            if (!palabra.isEmpty()) {
                result.append(Character.toUpperCase(palabra.charAt(0)))
                      .append(palabra.substring(1))
                      .append(" ");
            }
        }
        return result.toString().trim();
    }
    
    public boolean isGuardado() {
        return guardado;
    }
}