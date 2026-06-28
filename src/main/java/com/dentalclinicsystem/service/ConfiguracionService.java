package com.dentalclinicsystem.service;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Properties;

public class ConfiguracionService {
    private static ConfiguracionService instancia;
    private Properties props;
    private boolean configuracionCargada = false;
    
    private ConfiguracionService() {
        props = new Properties();
        cargarConfiguracion();
    }
    
    public static ConfiguracionService getInstance() {
        if (instancia == null) {
            instancia = new ConfiguracionService();
        }
        return instancia;
    }
    
    private void cargarConfiguracion() {
        System.out.println("🔍 Buscando config.properties...");
        
        // ============================================
        // OPCIÓN 1: Cargar desde ruta ABSOLUTA
        // ============================================
        String[] rutasPosibles = {
            "D:\\DentalClinicSystem\\src\\config.properties",
            "C:\\DentalClinicSystem\\src\\config.properties",
            System.getProperty("user.dir") + "\\src\\config.properties",
            System.getProperty("user.dir") + "\\config.properties",
            System.getProperty("user.home") + "\\.dentalclinic\\config.properties"
        };
        
        for (String ruta : rutasPosibles) {
            try {
                System.out.println("📁 Intentando cargar desde: " + ruta);
                try (FileInputStream input = new FileInputStream(ruta)) {
                    props.load(input);
                    configuracionCargada = true;
                    System.out.println("✅ Configuración cargada desde: " + ruta);
                    imprimirConfiguracion();
                    return;
                }
            } catch (Exception e) {
                // Continuar con la siguiente ruta
            }
        }
        
        // ============================================
        // OPCIÓN 2: Cargar desde CLASSPATH
        // ============================================
        try {
            java.net.URL url = getClass().getClassLoader().getResource("config.properties");
            System.out.println("📁 Buscando en classpath: " + url);
            
            try (InputStream input = getClass().getClassLoader()
                    .getResourceAsStream("config.properties")) {
                
                if (input != null) {
                    props.load(input);
                    configuracionCargada = true;
                    System.out.println("✅ Configuración cargada desde classpath");
                    imprimirConfiguracion();
                    return;
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Error cargando desde classpath: " + e.getMessage());
        }
        
        // ============================================
        // OPCIÓN 3: Cargar desde VARIABLES DE ENTORNO
        // ============================================
        cargarDesdeVariablesEntorno();
        
        // ============================================
        // OPCIÓN 4: Mostrar ERROR si no hay configuración
        // ============================================
        if (!configuracionCargada) {
            System.err.println("❌ ERROR CRÍTICO: No se pudo cargar la configuración");
            System.err.println("📁 Crea el archivo config.properties en:");
            System.err.println("   D:\\DentalClinicSystem\\src\\config.properties");
            System.err.println("");
            System.err.println("📝 Contenido del archivo:");
            System.err.println("   email.user=tu_correo@gmail.com");
            System.err.println("   email.password=tu_contraseña_app");
            System.err.println("   email.destino=destino@gmail.com");
            System.err.println("   smtp.host=smtp.gmail.com");
            System.err.println("   smtp.port=587");
            System.err.println("   smtp.tls=true");
            System.err.println("   iva.porcentaje=19");
            System.err.println("   empresa.nombre=Mi Clinica");
            System.err.println("");
            System.err.println("⚠️ O configura variables de entorno:");
            System.err.println("   EMAIL_USER");
            System.err.println("   EMAIL_PASSWORD");
            System.err.println("   EMAIL_DESTINO");
            
            configuracionCargada = false;
        }
    }
    
    private void cargarDesdeVariablesEntorno() {
        String emailUser = System.getenv("EMAIL_USER");
        if (emailUser != null && !emailUser.isEmpty()) {
            props.setProperty("email.user", emailUser);
            configuracionCargada = true;
            System.out.println("✅ EMAIL_USER cargado desde variable de entorno");
        }
        
        String emailPassword = System.getenv("EMAIL_PASSWORD");
        if (emailPassword != null && !emailPassword.isEmpty()) {
            props.setProperty("email.password", emailPassword);
            configuracionCargada = true;
            System.out.println("✅ EMAIL_PASSWORD cargado desde variable de entorno");
        }
        
        String emailDestino = System.getenv("EMAIL_DESTINO");
        if (emailDestino != null && !emailDestino.isEmpty()) {
            props.setProperty("email.destino", emailDestino);
        }
        
        String smtpHost = System.getenv("SMTP_HOST");
        if (smtpHost != null && !smtpHost.isEmpty()) {
            props.setProperty("smtp.host", smtpHost);
        }
        
        String smtpPort = System.getenv("SMTP_PORT");
        if (smtpPort != null && !smtpPort.isEmpty()) {
            props.setProperty("smtp.port", smtpPort);
        }
        
        String smtpTLS = System.getenv("SMTP_TLS");
        if (smtpTLS != null && !smtpTLS.isEmpty()) {
            props.setProperty("smtp.tls", smtpTLS);
        }
    }
    
    private void imprimirConfiguracion() {
        System.out.println("📧 EMAIL_USER: " + props.getProperty("email.user"));
        System.out.println("📧 EMAIL_PASSWORD: " + 
            (props.getProperty("email.password") != null ? "✅ Cargada" : "❌ No cargada"));
        System.out.println("📧 EMAIL_DESTINO: " + props.getProperty("email.destino"));
        System.out.println("🏢 EMPRESA: " + props.getProperty("empresa.nombre"));
        System.out.println("📊 IVA: " + props.getProperty("iva.porcentaje") + "%");
    }
    
    // ========== MÉTODOS GET EXISTENTES ==========
    
    public String getEmailUser() {
        return props.getProperty("email.user");
    }
    
    public String getEmailPassword() {
        return props.getProperty("email.password");
    }
    
    public String getEmailDestino() {
        return props.getProperty("email.destino", "admin@dentalclinic.com");
    }
    
    public String getEmailInfo() {
        return props.getProperty("email.info", "info@dentalclinic.com");
    }
    
    public String getEmpresaNombre() {
        return props.getProperty("empresa.nombre", "Dental Clinic System");
    }
    
    public String getEmpresaTelefono() {
        return props.getProperty("empresa.telefono", "300-000-0000");
    }
    
    public String getEncuestasDirectorio() {
        return props.getProperty("encuestas.directorio", "encuestas");
    }
    
    public String getFacturasDirectorio() {
        return props.getProperty("facturas.directorio", "facturas");
    }
    
    public String getSmtpHost() {
        return props.getProperty("smtp.host", "smtp.gmail.com");
    }
    
    public String getSmtpPort() {
        return props.getProperty("smtp.port", "587");
    }
    
    public double getIvaPorcentaje() {
        try {
            String valor = props.getProperty("iva.porcentaje", "19");
            return Double.parseDouble(valor) / 100.0;
        } catch (NumberFormatException e) {
            return 0.19;
        }
    }
    
    public double getIvaPorcentajeNumerico() {
        try {
            String valor = props.getProperty("iva.porcentaje", "19");
            return Double.parseDouble(valor);
        } catch (NumberFormatException e) {
            return 19.0;
        }
    }
    
    public boolean isConfiguracionCargada() {
        return configuracionCargada;
    }
    
    public boolean credencialesConfiguradas() {
        String user = getEmailUser();
        String pass = getEmailPassword();
        return user != null && !user.isEmpty() && 
               pass != null && !pass.isEmpty() &&
               !user.equals("tu_correo@gmail.com");
    }
    
    public boolean esConfiguracionValida() {
        if (!configuracionCargada) {
            return false;
        }
        String user = getEmailUser();
        return user != null && !user.isEmpty() && 
               !user.equals("tu_correo@gmail.com") &&
               credencialesConfiguradas();
    }
    
    // =========================================================
    // ========== NUEVOS MÉTODOS PARA GUARDAR CONFIGURACIÓN =====
    // =========================================================
    
    /**
     * Guarda la configuración de correo en el archivo properties
     * Este método es llamado desde el PanelConfiguracion cuando el usuario
     * hace clic en "Guardar Configuración"
     * 
     * @param email Correo electrónico del remitente
     * @param password Contraseña de aplicación (no la de Gmail)
     * @param smtpHost Servidor SMTP (ej: smtp.gmail.com)
     * @param smtpPort Puerto SMTP (587 para TLS, 465 para SSL)
     * @param usarTLS true para usar TLS, false para SSL
     * @throws Exception Si hay error al guardar el archivo
     */
    public void guardarConfiguracionCorreo(String email, String password, 
                                           String smtpHost, String smtpPort, 
                                           boolean usarTLS) throws Exception {
        
        // Actualizar propiedades en memoria
        props.setProperty("email.user", email);
        props.setProperty("email.password", password);
        props.setProperty("smtp.host", smtpHost);
        props.setProperty("smtp.port", smtpPort);
        props.setProperty("smtp.tls", String.valueOf(usarTLS));
        
        // Intentar guardar en diferentes ubicaciones
        String[] rutasGuardado = {
            System.getProperty("user.dir") + "\\src\\config.properties",
            System.getProperty("user.dir") + "\\config.properties",
            System.getProperty("user.home") + "\\.dentalclinic\\config.properties"
        };
        
        boolean guardado = false;
        for (String ruta : rutasGuardado) {
            try {
                // Crear directorio si no existe
                java.io.File file = new java.io.File(ruta);
                if (file.getParentFile() != null) {
                    file.getParentFile().mkdirs();
                }
                
                // Guardar archivo
                try (FileOutputStream fos = new FileOutputStream(ruta)) {
                    props.store(fos, "Configuración actualizada - Dental Clinic System");
                    System.out.println("✅ Configuración guardada en: " + ruta);
                    guardado = true;
                    configuracionCargada = true;
                    break;
                }
            } catch (Exception e) {
                System.err.println("⚠️ No se pudo guardar en: " + ruta);
            }
        }
        
        if (!guardado) {
            throw new Exception("No se pudo guardar la configuración en ninguna ubicación");
        }
    }
    
    /**
     * Verifica si hay configuración de correo guardada
     * @return true si las credenciales están configuradas correctamente
     */
    public boolean tieneConfiguracionCorreo() {
        return credencialesConfiguradas();
    }
    
    /**
     * Obtiene la configuración completa de correo como un Map
     * @return Map con todas las propiedades de correo
     */
    public java.util.Map<String, String> getConfiguracionCorreo() {
        java.util.Map<String, String> config = new java.util.HashMap<>();
        config.put("email", getEmailUser());
        config.put("password", getEmailPassword());
        config.put("smtp.host", getSmtpHost());
        config.put("smtp.port", getSmtpPort());
        config.put("smtp.tls", props.getProperty("smtp.tls", "true"));
        config.put("empresa.nombre", getEmpresaNombre());
        return config;
    }
}