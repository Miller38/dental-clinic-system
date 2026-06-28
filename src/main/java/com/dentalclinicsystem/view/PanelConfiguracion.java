package com.dentalclinicsystem.view;

import com.dentalclinicsystem.controller.ConfiguracionController;
import com.dentalclinicsystem.controller.PacienteController;
import com.dentalclinicsystem.service.ConfiguracionService;
import com.dentalclinicsystem.service.EmailService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

public class PanelConfiguracion extends JPanel {
    
    private ConfiguracionController controller;
    private JFrame parentFrame;
    
    // ========== COMPONENTES DE INFORMACIÓN (EXISTENTES) ==========
    private JLabel lblVersion;
    private JLabel lblPacientes;
    private JLabel lblCitas;
    private JLabel lblEstado;
    
    // ========== COMPONENTES DE CORREO ==========
    private JTextField txtCorreo;           // Campo para el correo del remitente
    private JPasswordField txtPasswordApp;  // Campo para la contraseña de aplicación
    private JTextField txtSmtpHost;         // Campo para el servidor SMTP
    private JTextField txtSmtpPort;         // Campo para el puerto SMTP
    private JCheckBox chkTLS;               // Checkbox para activar TLS
    private JButton btnGuardarCorreo;       // Botón para guardar configuración
    private JButton btnProbarCorreo;        // Botón para probar envío
    private JLabel lblEstadoCorreo;         // Label de estado de la configuración
    private JLabel lblInfoCorreo;           // Label con información de ayuda
    
    // ========== SERVICIOS ==========
    private ConfiguracionService configService;  // Servicio de configuración
    private EmailService emailService;           // Servicio de envío de correos
    
    // ========== COLORES ==========
    private Color darkBg = new Color(30, 30, 35);
    private Color darkCard = new Color(40, 40, 45);
    private Color textLight = new Color(220, 220, 230);
    private Color textGray = new Color(150, 150, 165);
    private Color accentBlue = new Color(70, 130, 200);
    private Color accentGreen = new Color(60, 180, 110);
    
    public PanelConfiguracion(JFrame parent) {
        this.parentFrame = parent;
        this.controller = new ConfiguracionController();
        
        // ====== INICIALIZAR SERVICIOS ======
        this.configService = ConfiguracionService.getInstance();
        this.emailService = EmailService.getInstance();
        
        initComponents();
        cargarConfiguracionCorreo();
        cargarInformacion();
    }
    
    private void initComponents() {
        setBackground(darkBg);
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(darkBg);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        
        int row = 0;
        
        // ==========================================
        // TÍTULO CON BOTÓN DE AYUDA
        // ==========================================
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        
        JPanel tituloPanel = new JPanel(new BorderLayout(15, 0));
        tituloPanel.setBackground(darkBg);
        
        JLabel titleLabel = new JLabel("Configuración del Sistema");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(textLight);
        tituloPanel.add(titleLabel, BorderLayout.WEST);
        
        // Botón de ayuda
        tituloPanel.add(crearBotonAyuda(), BorderLayout.EAST);
        
        mainPanel.add(tituloPanel, gbc);
        row++;
        
        // ==========================================
        // PANEL DE CORREO
        // ==========================================
        gbc.gridy = row; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JPanel correoPanel = crearPanelCorreo();
        mainPanel.add(correoPanel, gbc);
        row++;
        
        // ==========================================
        // PANEL DE INFORMACIÓN (EXISTENTE)
        // ==========================================
        gbc.gridy = row; gbc.gridwidth = 2;
        JPanel infoPanel = crearPanelInformacion();
        mainPanel.add(infoPanel, gbc);
        row++;
        
        // ==========================================
        // PANEL DE ACCIONES (EXISTENTE)
        // ==========================================
        gbc.gridy = row; gbc.gridwidth = 2;
        JPanel accionesPanel = crearPanelAcciones();
        mainPanel.add(accionesPanel, gbc);
        
        JScrollPane scroll = new JScrollPane(mainPanel);
        scroll.setBackground(darkBg);
        scroll.getViewport().setBackground(darkBg);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        
        add(scroll, BorderLayout.CENTER);
    }
    
    // ============================================================
    // ========== BOTÓN DE AYUDA ===================================
    // ============================================================
    
    /**
     * Crea el botón de ayuda con el ícono de pregunta
     */
    private JButton crearBotonAyuda() {
        JButton btnAyuda = new JButton("Guía Rápida");
        btnAyuda.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnAyuda.setBackground(new Color(255, 193, 7));  // Amarillo
        btnAyuda.setForeground(Color.BLACK);
        btnAyuda.setFocusPainted(false);
        btnAyuda.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAyuda.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btnAyuda.setToolTipText("Ver guía de configuración paso a paso");
        
        // Efecto hover
        btnAyuda.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btnAyuda.setBackground(new Color(255, 200, 50));
            }
            public void mouseExited(MouseEvent e) {
                btnAyuda.setBackground(new Color(255, 193, 7));
            }
        });
        
        btnAyuda.addActionListener(e -> mostrarGuiaAyuda());
        return btnAyuda;
    }
    
    /**
     * Muestra la guía de ayuda con instrucciones detalladas
     * Versión corregida con tamaño fijo y colores visibles
     */
    private void mostrarGuiaAyuda() {
        // ==========================================
        // CONFIGURACIÓN DE COLORES
        // ==========================================
        Color fondoVentana = new Color(30, 30, 35);      // Fondo oscuro
        Color fondoPanel = new Color(40, 40, 45);        // Fondo de tarjeta
        Color textoClaro = new Color(220, 220, 230);     // Texto blanco/gris claro
        
        // ==========================================
        // CREAR EL PANEL PRINCIPAL
        // ==========================================
        JPanel panelContenido = new JPanel(new BorderLayout());
        panelContenido.setBackground(fondoVentana);
        panelContenido.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // ==========================================
        // ÁREA DE TEXTO CON HTML (CORREGIDO)
        // ==========================================
        JEditorPane editorPane = new JEditorPane();
        editorPane.setContentType("text/html");
        editorPane.setEditable(false);
        editorPane.setBackground(fondoPanel);
        editorPane.setForeground(textoClaro);
        editorPane.setText(obtenerGuiaHTML());
        
        // Configurar tamaño fijo
        editorPane.setPreferredSize(new Dimension(700, 450));
        editorPane.setMinimumSize(new Dimension(650, 400));
        
        // ==========================================
        // SCROLL PANEL
        // ==========================================
        JScrollPane scrollPane = new JScrollPane(editorPane);
        scrollPane.setPreferredSize(new Dimension(700, 450));
        scrollPane.setMinimumSize(new Dimension(650, 400));
        scrollPane.setBackground(fondoPanel);
        scrollPane.getViewport().setBackground(fondoPanel);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 65), 1));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        panelContenido.add(scrollPane, BorderLayout.CENTER);
        
        // ==========================================
        // PANEL DE BOTÓN INFERIOR
        // ==========================================
        JPanel panelBoton = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panelBoton.setBackground(fondoVentana);
        panelBoton.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        
        JButton btnCerrar = new JButton("Entendido, ¡a configurar!");
        btnCerrar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCerrar.setBackground(new Color(60, 180, 110));
        btnCerrar.setForeground(Color.WHITE);
        btnCerrar.setFocusPainted(false);
        btnCerrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCerrar.setPreferredSize(new Dimension(250, 45));
        btnCerrar.addActionListener(e -> {
            // Cerrar el diálogo
            Window window = SwingUtilities.getWindowAncestor(btnCerrar);
            if (window != null) {
                window.dispose();
            }
        });
        
        panelBoton.add(btnCerrar);
        
        // ==========================================
        // CREAR DIÁLOGO CON TAMAÑO FIJO
        // ==========================================
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), 
                                     "Guía de Configuración de Correo", true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setLayout(new BorderLayout(5, 5));
        dialog.setBackground(fondoVentana);
        
        // ==========================================
        // AGREGAR COMPONENTES AL DIÁLOGO
        // ==========================================
        dialog.add(panelContenido, BorderLayout.CENTER);
        dialog.add(panelBoton, BorderLayout.SOUTH);
        
        // ==========================================
        // CONFIGURAR TAMAÑO Y POSICIÓN
        // ==========================================
        dialog.setSize(750, 550);                    // Tamaño fijo
        dialog.setMinimumSize(new Dimension(700, 500));  // Tamaño mínimo
        dialog.setPreferredSize(new Dimension(750, 550)); // Tamaño preferido
        
        // Centrar en la pantalla
        dialog.setLocationRelativeTo(null);          // Centrado absoluto
        
        // ==========================================
        // MOSTRAR DIÁLOGO
        // ==========================================
        dialog.setVisible(true);
    }
    
    /**
     * Genera el contenido HTML de la guía de ayuda
     * Versión con colores corregidos para fondo oscuro
     */
    private String obtenerGuiaHTML() {
        return """
        <html>
        <body style='
            font-family: "Segoe UI", Arial, sans-serif; 
            padding: 15px; 
            background-color: #28282D; 
            color: #DCDCE6;
            margin: 0;
        '>
            
            <h1 style='
                color: #DCDCE6; 
                text-align: center; 
                border-bottom: 2px solid #3498db; 
                padding-bottom: 10px;
                margin-top: 0;
            '>
                 Guía de Configuración de Correo
            </h1>
            
            <div style='
                background-color: #3D3D42; 
                border-left: 4px solid #ffc107; 
                padding: 10px; 
                margin: 10px 0; 
                border-radius: 4px;
            '>
                <p style='margin: 0; color: #DCDCE6;'>
                    <strong>️ Tiempo estimado:</strong> 3 minutos
                </p>
                <p style='margin: 0; font-size: 13px; color: #9696A5;'>
                    Sigue estos pasos y tendrás tu correo configurado en minutos
                </p>
            </div>
            
            <h2 style='color: #DCDCE6; margin-top: 20px;'>📌 ¿Qué necesitas?</h2>
            <ul style='font-size: 14px; line-height: 1.8; color: #DCDCE6;'>
                <li>✅ Una cuenta de correo electrónico (Gmail, Outlook, etc.)</li>
                <li>✅ Contraseña de aplicación (para Gmail es obligatoria)</li>
                <li>✅ Conexión a internet</li>
            </ul>
            
            <div style='
                background-color: #1A3A5C; 
                border-radius: 8px; 
                padding: 15px; 
                margin: 15px 0; 
                border: 1px solid #2A5A7C;
            '>
                <h3 style='color: #6BA3D9; margin-top: 0;'>
                    🔐 Para Gmail - Generar Contraseña de Aplicación
                </h3>
                <ol style='font-size: 14px; line-height: 2.2; color: #DCDCE6;'>
                    <li>🔗 Abre: <strong style='color: #6BA3D9;'>
                        myaccount.google.com/apppasswords</strong></li>
                    <li>✅ Inicia sesión con tu cuenta de Gmail</li>
                    <li>📝 Selecciona <strong>"Otra"</strong> y escribe <strong>"Dental Clinic"</strong></li>
                    <li>🔄 Click en <strong>"Generar"</strong></li>
                    <li>📋 <strong>COPIA</strong> la contraseña de 16 dígitos que aparece</li>
                    <li>⚠️ <strong>NO CIERRES</strong> la página hasta que la hayas guardado</li>
                </ol>
                <div style='
                    background-color: #3D3D42; 
                    border-left: 4px solid #ffc107; 
                    padding: 8px; 
                    margin-top: 10px; 
                    border-radius: 4px;
                '>
                    <p style='margin: 0; color: #DCDCE6;'>
                        <strong>💡 Importante:</strong> Esta NO es tu contraseña de Gmail, 
                        es una contraseña especial para la aplicación.
                    </p>
                </div>
            </div>
            
            <h2 style='color: #DCDCE6; margin-top: 20px;'>⚙️ Configurar en la Aplicación</h2>
            <ol style='font-size: 14px; line-height: 2.2; color: #DCDCE6;'>
                <li><strong>📧 Correo:</strong> Escribe tu correo electrónico completo
                    <br><span style='color: #9696A5; font-size: 13px;'>
                        Ejemplo: tuempresa@gmail.com
                    </span>
                </li>
                <li><strong>🔑 Contraseña app:</strong> Pega la contraseña de aplicación generada
                    <br><span style='color: #9696A5; font-size: 13px;'>
                        Ejemplo: abcd efgh ijkl mnop
                    </span>
                </li>
                <li><strong>🌐 Servidor SMTP:</strong> Deja 
                    <code style='
                        background: #3D3D42; 
                        padding: 2px 6px; 
                        border-radius: 3px; 
                        color: #6BA3D9;
                    '>smtp.gmail.com</code> (por defecto)
                    <br><span style='color: #9696A5; font-size: 13px;'>
                        Para otros: smtp.outlook.com, smtp.office365.com
                    </span>
                </li>
                <li><strong>🔢 Puerto:</strong> Deja 
                    <code style='
                        background: #3D3D42; 
                        padding: 2px 6px; 
                        border-radius: 3px; 
                        color: #6BA3D9;
                    '>587</code> (recomendado con TLS)
                </li>
                <li><strong>🔒 TLS:</strong> Mantén activado (recomendado para seguridad)</li>
            </ol>
            
            <div style='
                background-color: #1A4A2A; 
                border-left: 4px solid #4caf50; 
                padding: 10px; 
                margin: 15px 0; 
                border-radius: 4px;
            '>
                <h3 style='color: #6BCF8A; margin-top: 0;'>✅ Pasos Finales</h3>
                <ol style='font-size: 14px; line-height: 2.2; margin-bottom: 0; color: #DCDCE6;'>
                    <li><strong>💾 Guardar Configuración</strong> - Click en el botón verde</li>
                    <li><strong>📧 Probar Envío</strong> - Click en el botón azul</li>
                    <li><strong>📨 Verifica</strong> - Revisa tu correo (incluye carpeta SPAM)</li>
                    <li><strong>✅ ¡Listo!</strong> El sistema ya puede enviar correos automáticos</li>
                </ol>
            </div>
            
            <div style='
                background-color: #4A1A1A; 
                border-left: 4px solid #e53935; 
                padding: 10px; 
                margin: 15px 0; 
                border-radius: 4px;
            '>
                <h4 style='color: #E87070; margin-top: 0;'>
                    ⚠️ Solución de problemas comunes
                </h4>
                <ul style='font-size: 13px; line-height: 2; margin-bottom: 0; color: #DCDCE6;'>
                    <li><strong>"Authentication failed"</strong> → Genera una NUEVA contraseña de aplicación en Gmail</li>
                    <li><strong>No llega el correo</strong> → Revisa la carpeta SPAM o correo no deseado</li>
                    <li><strong>Error de conexión</strong> → Verifica tu conexión a internet</li>
                    <li><strong>Puerto bloqueado</strong> → Prueba con puerto 465 (SSL) y desactiva TLS</li>
                </ul>
            </div>
            
            <hr style='border: 1px solid #3D3D42; margin: 20px 0;'>
            
            <p style='text-align: center; color: #9696A5; font-size: 12px;'>
                📌 Si tienes problemas, contacta a soporte técnico
            </p>
            
        </body>
        </html>
        """;
    }
    
    // ============================================================
    // ========== PANEL DE CORREO ==================================
    // ============================================================
    
    /**
     * Crea el panel de configuración de correo con todos los campos
     * y botones necesarios para que el cliente configure su cuenta
     */
    private JPanel crearPanelCorreo() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(darkCard);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(50, 50, 55), 1),
            new TitledBorder(new EmptyBorder(15, 15, 15, 15), "Configuración de Correo",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 14), textLight)
        ));
        panel.setPreferredSize(new Dimension(500, 380));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        int row = 0;

        // ---------- Campo: Correo electrónico ----------
        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 1;
        JLabel lblCorreo = new JLabel("Correo:");
        lblCorreo.setForeground(textLight);
        lblCorreo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panel.add(lblCorreo, gbc);

        gbc.gridx = 1;
        txtCorreo = new JTextField(20);
        txtCorreo.setBackground(new Color(50, 50, 55));
        txtCorreo.setForeground(textLight);
        txtCorreo.setCaretColor(textLight);
        txtCorreo.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 65)));
        txtCorreo.setToolTipText("Ingresa el correo que usará para enviar los recordatorios");
        panel.add(txtCorreo, gbc);
        row++;

        // ---------- Campo: Contraseña de aplicación ----------
        gbc.gridx = 0; gbc.gridy = row;
        JLabel lblPass = new JLabel("Contraseña app:");
        lblPass.setForeground(textLight);
        lblPass.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panel.add(lblPass, gbc);

        gbc.gridx = 1;
        txtPasswordApp = new JPasswordField(20);
        txtPasswordApp.setBackground(new Color(50, 50, 55));
        txtPasswordApp.setForeground(textLight);
        txtPasswordApp.setCaretColor(textLight);
        txtPasswordApp.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 65)));
        txtPasswordApp.setToolTipText("Contraseña de aplicación (NO es tu contraseña de Gmail)");
        panel.add(txtPasswordApp, gbc);
        row++;

        // ---------- Campo: Servidor SMTP ----------
        gbc.gridx = 0; gbc.gridy = row;
        JLabel lblHost = new JLabel("Servidor SMTP:");
        lblHost.setForeground(textLight);
        lblHost.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panel.add(lblHost, gbc);

        gbc.gridx = 1;
        txtSmtpHost = new JTextField(20);
        txtSmtpHost.setText("smtp.gmail.com");
        txtSmtpHost.setBackground(new Color(50, 50, 55));
        txtSmtpHost.setForeground(textLight);
        txtSmtpHost.setCaretColor(textLight);
        txtSmtpHost.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 65)));
        txtSmtpHost.setToolTipText("Servidor SMTP (ej: smtp.gmail.com)");
        panel.add(txtSmtpHost, gbc);
        row++;

        // ---------- Campo: Puerto ----------
        gbc.gridx = 0; gbc.gridy = row;
        JLabel lblPort = new JLabel("Puerto:");
        lblPort.setForeground(textLight);
        lblPort.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panel.add(lblPort, gbc);

        gbc.gridx = 1;
        txtSmtpPort = new JTextField(20);
        txtSmtpPort.setText("587");
        txtSmtpPort.setBackground(new Color(50, 50, 55));
        txtSmtpPort.setForeground(textLight);
        txtSmtpPort.setCaretColor(textLight);
        txtSmtpPort.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 65)));
        txtSmtpPort.setToolTipText("Puerto SMTP (587 para TLS, 465 para SSL)");
        panel.add(txtSmtpPort, gbc);
        row++;

        // ---------- Checkbox: TLS ----------
        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 2;
        chkTLS = new JCheckBox("Usar TLS (recomendado)");
        chkTLS.setBackground(darkCard);
        chkTLS.setForeground(textLight);
        chkTLS.setSelected(true);
        chkTLS.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        chkTLS.setToolTipText("Marca esta opción para usar conexión segura TLS");
        panel.add(chkTLS, gbc);
        row++;

        // ---------- Botones: Guardar y Probar ----------
        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 2;
        JPanel botonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        botonPanel.setBackground(darkCard);

        btnGuardarCorreo = new JButton("Guardar Configuración");
        btnGuardarCorreo.setBackground(new Color(60, 180, 110));
        btnGuardarCorreo.setForeground(Color.WHITE);
        btnGuardarCorreo.setFocusPainted(false);
        btnGuardarCorreo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnGuardarCorreo.setToolTipText("Guarda la configuración en el archivo config.properties");
        btnGuardarCorreo.addActionListener(e -> guardarConfiguracionCorreo());

        btnProbarCorreo = new JButton("Probar Envío");
        btnProbarCorreo.setBackground(new Color(70, 130, 200));
        btnProbarCorreo.setForeground(Color.WHITE);
        btnProbarCorreo.setFocusPainted(false);
        btnProbarCorreo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnProbarCorreo.setToolTipText("Envía un correo de prueba para verificar la configuración");
        btnProbarCorreo.addActionListener(e -> probarEnvioCorreo());

        botonPanel.add(btnGuardarCorreo);
        botonPanel.add(btnProbarCorreo);
        panel.add(botonPanel, gbc);
        row++;

        // ---------- Label: Estado de la configuración ----------
        gbc.gridy = row;
        lblEstadoCorreo = new JLabel("Sin configurar");
        lblEstadoCorreo.setForeground(textGray);
        lblEstadoCorreo.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        panel.add(lblEstadoCorreo, gbc);
        row++;

        // ---------- Label: Información de ayuda ----------
        gbc.gridy = row;
        lblInfoCorreo = new JLabel("<html><small style='color: #888;'>Para Gmail: usa contraseña de aplicación (ver en ajustes de Google)</small></html>");
        lblInfoCorreo.setForeground(new Color(150, 150, 165));
        lblInfoCorreo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(lblInfoCorreo, gbc);

        return panel;
    }
    
    // ============================================================
    // ========== PANEL DE INFORMACIÓN (EXISTENTE) ================
    // ============================================================
    
    private JPanel crearPanelInformacion() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(darkCard);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(50, 50, 55), 1),
            new TitledBorder(new EmptyBorder(15, 15, 15, 15), "Información del Sistema", 
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 14), textLight)
        ));
        panel.setPreferredSize(new Dimension(500, 130));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 10, 3, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        lblVersion = new JLabel("Versión: 2.0.0");
        lblVersion.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblVersion.setForeground(textGray);
        
        lblPacientes = new JLabel("Total Pacientes: Cargando...");
        lblPacientes.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblPacientes.setForeground(textGray);
        
        lblCitas = new JLabel("Citas Hoy: Cargando...");
        lblCitas.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblCitas.setForeground(textGray);
        
        lblEstado = new JLabel("Estado: Activo");
        lblEstado.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblEstado.setForeground(new Color(60, 180, 110));
        
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(lblVersion, gbc);
        gbc.gridy = 1;
        panel.add(lblPacientes, gbc);
        gbc.gridy = 2;
        panel.add(lblCitas, gbc);
        gbc.gridy = 3;
        panel.add(lblEstado, gbc);
        
        return panel;
    }
    
    // ============================================================
    // ========== PANEL DE ACCIONES (EXISTENTE) ===================
    // ============================================================
    
    private JPanel crearPanelAcciones() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panel.setBackground(darkBg);
        
        JButton btnAplicar = createActionButton("Aplicar Configuración", new Color(150, 80, 200));
        btnAplicar.addActionListener(e -> aplicarConfiguracion());
        
        JButton btnRestaurar = createActionButton("Restaurar Predeterminados", accentBlue);
        btnRestaurar.addActionListener(e -> restaurarPredeterminados());
        
        panel.add(btnAplicar);
        panel.add(btnRestaurar);
        
        return panel;
    }
    
    private JButton createActionButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(200, 40));
        
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
    
    // ============================================================
    // ========== MÉTODOS DE CARGA =================================
    // ============================================================
    
    /**
     * Carga la configuración de correo desde ConfiguracionService
     */
    private void cargarConfiguracionCorreo() {
        try {
            // Verificar si hay credenciales configuradas
            if (configService.credencialesConfiguradas()) {
                // Cargar datos en los campos (la contraseña no se muestra)
                txtCorreo.setText(configService.getEmailUser());
                txtSmtpHost.setText(configService.getSmtpHost());
                txtSmtpPort.setText(configService.getSmtpPort());
                
                // Si hay configuración, actualizar estado
                lblEstadoCorreo.setText("Configurado");
                lblEstadoCorreo.setForeground(new Color(60, 180, 110));
            } else {
                lblEstadoCorreo.setText("Sin configurar");
                lblEstadoCorreo.setForeground(textGray);
            }
        } catch (Exception e) {
            lblEstadoCorreo.setText("Error al cargar");
            lblEstadoCorreo.setForeground(Color.ORANGE);
            System.err.println("Error cargando configuración de correo: " + e.getMessage());
        }
    }
    
    private void cargarInformacion() {
        try {
            PacienteController pacienteController = new PacienteController();
            int totalPacientes = pacienteController.contarPacientes();
            lblPacientes.setText("Total Pacientes: " + totalPacientes);
        } catch (Exception e) {
            System.err.println("Error al cargar información: " + e.getMessage());
        }
    }
    
    // ============================================================
    // ========== MÉTODOS DE ACCIÓN ================================
    // ============================================================
    
    private void aplicarConfiguracion() {
        try {
            if (parentFrame != null) {
                // Aquí puedes aplicar configuraciones adicionales si las hay
                // Por ahora solo notificamos
                
                JOptionPane.showMessageDialog(this,
                    "Configuración aplicada correctamente.\n" +
                    "Los cambios se han guardado en config.properties.",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al aplicar configuración: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void restaurarPredeterminados() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "¿Está seguro de restaurar la configuración predeterminada?\n" +
            "Tema: Oscuro\n" +
            "Tamaño de fuente: 14px\n\n" +
            "⚠️ Los cambios se aplicarán al reiniciar la aplicación.",
            "Restaurar predeterminados",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // Restaurar configuración de correo a valores por defecto
                txtCorreo.setText("");
                txtPasswordApp.setText("");
                txtSmtpHost.setText("smtp.gmail.com");
                txtSmtpPort.setText("587");
                chkTLS.setSelected(true);
                lblEstadoCorreo.setText("⚪ Sin configurar");
                lblEstadoCorreo.setForeground(textGray);
                
                JOptionPane.showMessageDialog(this,
                    "✅ Configuración restaurada a valores predeterminados.\n" +
                    "Guarda la configuración para aplicar los cambios.",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Error al restaurar configuración: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    // ============================================================
    // ========== MÉTODOS DE CONFIGURACIÓN DE CORREO ==============
    // ============================================================
    
    /**
     * Guarda la configuración de correo usando ConfiguracionService
     * Este método se ejecuta cuando el usuario hace clic en "Guardar Configuración"
     */
    private void guardarConfiguracionCorreo() {
        try {
            // Obtener valores de los campos
            String email = txtCorreo.getText().trim();
            String pass = new String(txtPasswordApp.getPassword()).trim();

            // Validar campos obligatorios
            if (email.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Correo y contraseña son obligatorios",
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Validar formato de email básico
            if (!email.contains("@") || !email.contains(".")) {
                JOptionPane.showMessageDialog(this,
                    "El correo electrónico no tiene un formato válido",
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Guardar usando ConfiguracionService
            configService.guardarConfiguracionCorreo(
                email,
                pass,
                txtSmtpHost.getText().trim(),
                txtSmtpPort.getText().trim(),
                chkTLS.isSelected()
            );

            // Actualizar estado visual
            lblEstadoCorreo.setText("Configuración guardada");
            lblEstadoCorreo.setForeground(new Color(60, 180, 110));

            // Limpiar campo de contraseña por seguridad
            txtPasswordApp.setText("");

            JOptionPane.showMessageDialog(this,
                "Configuración de correo guardada correctamente\n" +
                "El archivo config.properties ha sido actualizado.\n\n" +
                "Puedes usar el botón 'Probar Envío' para verificar.",
                "Éxito", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "❌ Error al guardar: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    /**
     * Prueba el envío de correo usando EmailService
     * Este método se ejecuta cuando el usuario hace clic en "Probar Envío"
     */
    private void probarEnvioCorreo() {
        try {
            // Verificar si hay configuración guardada
            if (!configService.credencialesConfiguradas()) {
                JOptionPane.showMessageDialog(this,
                    "Primero guarda la configuración de correo\n" +
                    "Haz clic en 'Guardar Configuración' primero.",
                    "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Pedir correo de destino
            String destino = JOptionPane.showInputDialog(this,
                "Ingresa un correo de prueba (destinatario):\n" +
                "Ejemplo: cliente@ejemplo.com",
                "Probar envío", JOptionPane.QUESTION_MESSAGE);

            if (destino == null || destino.trim().isEmpty()) {
                return; // Usuario canceló
            }

            // Validar formato del correo de destino
            if (!destino.trim().contains("@") || !destino.trim().contains(".")) {
                JOptionPane.showMessageDialog(this,
                    "El correo de destino no tiene un formato válido",
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Crear mensaje de prueba con HTML
            String asunto = "Prueba de configuración - Dental Clinic System";
            String mensaje = generarMensajePrueba();

            // Enviar usando el EmailService existente
            boolean enviado = emailService.enviarEmail(destino.trim(), asunto, mensaje);

            if (enviado) {
                JOptionPane.showMessageDialog(this,
                    "Correo de prueba enviado exitosamente a:\n" +
                    destino.trim() + "\n\n" +
                    "Si no lo recibes, verifica:\n" +
                    "• Que el correo existe\n" +
                    "• Revisa la carpeta de SPAM",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                    "❌ Error al enviar correo de prueba.\n\n" +
                    "Posibles causas:\n" +
                    "• Credenciales incorrectas\n" +
                    "• Contraseña de aplicación inválida\n" +
                    "• Sin conexión a internet\n" +
                    "• El servidor SMTP no responde\n\n" +
                    "Verifica tu configuración en config.properties",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "❌ Error al probar envío: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    /**
     * Genera el mensaje HTML para el correo de prueba
     * @return String con el HTML del mensaje
     */
    private String generarMensajePrueba() {
        String empresa = configService.getEmpresaNombre();
        String fecha = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
                      .format(new java.util.Date());
        
        return "<html>" +
               "<body style='font-family: Arial, sans-serif;'>" +
               "<div style='max-width: 600px; margin: auto; padding: 20px; background: #f8f9fa; border-radius: 10px;'>" +
               "<div style='background: #ffffff; padding: 25px; border-radius: 8px; box-shadow: 0 2px 5px rgba(0,0,0,0.1);'>" +
               "<h2 style='color: #2c3e50; text-align: center;'>✅ Configuración exitosa</h2>" +
               "<hr style='border: 1px solid #eee;'>" +
               "<p style='font-size: 16px;'>Estimado/a usuario,</p>" +
               "<p>Este es un correo de prueba enviado desde el <strong>" + empresa + "</strong>.</p>" +
               "<div style='background: #eaf2f8; padding: 15px; border-radius: 5px; margin: 15px 0;'>" +
               "<p><strong> Fecha:</strong> " + fecha + "</p>" +
               "<p><strong> Versión:</strong> 2.0.0</p>" +
               "<p><strong> Estado:</strong> <span style='color: #27ae60;'>Configuración correcta</span></p>" +
               "<p><strong> Remitente:</strong> " + configService.getEmailUser() + "</p>" +
               "</div>" +
               "<p style='color: #7f8c8d;'>Si recibes este mensaje, la configuración de correo está funcionando correctamente.</p>" +
               "<p style='color: #7f8c8d;'>El sistema ahora puede enviar recordatorios y facturas automáticamente.</p>" +
               "<hr style='border: 1px solid #eee;'>" +
               "<p style='color: #95a5a6; font-size: 12px; text-align: center;'>Saludos,<br><strong>" + empresa + "</strong></p>" +
               "<p style='color: #95a5a6; font-size: 10px; text-align: center;'>Este es un mensaje automático de prueba, por favor no responder.</p>" +
               "</div>" +
               "</div>" +
               "</body>" +
               "</html>";
    }
}