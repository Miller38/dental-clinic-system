package com.dentalclinicsystem.service;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Properties;

public class ConfiguracionService {
    private static ConfiguracionService instancia;
    private Properties props;
    private boolean configuracionCargada = false;
    
    // ================================================================
    // 🔥 EMAIL DEL ADMINISTRADOR (DESTINO DE ENCUESTAS) - FIJO Y OCULTO
    // ================================================================
    // Este email es donde el desarrollador/administrador recibe las encuestas
    // NO se muestra en la interfaz y NO puede ser modificado por el cliente
    private static final String EMAIL_ADMIN_DESTINO = "millergutierrez38@gmail.com";
    
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
        
        // Cargar desde classpath
        try {
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
        
        // Cargar desde variables de entorno
        cargarDesdeVariablesEntorno();
        
        if (!configuracionCargada) {
            System.err.println("❌ ERROR CRÍTICO: No se pudo cargar la configuración");
            configuracionCargada = false;
        }
    }
    
    private void cargarDesdeVariablesEntorno() {
        String emailUser = System.getenv("EMAIL_USER");
        if (emailUser != null && !emailUser.isEmpty()) {
            props.setProperty("email.user", emailUser);
            configuracionCargada = true;
        }
        
        String emailPassword = System.getenv("EMAIL_PASSWORD");
        if (emailPassword != null && !emailPassword.isEmpty()) {
            props.setProperty("email.password", emailPassword);
            configuracionCargada = true;
        }
    }
    
    private void imprimirConfiguracion() {
        System.out.println("📧 EMAIL_USER: " + props.getProperty("email.user"));
        System.out.println("📧 EMAIL_PASSWORD: " + 
            (props.getProperty("email.password") != null ? "✅ Cargada" : "❌ No cargada"));
        System.out.println("📧 EMAIL_ADMIN_DESTINO: " + getEmailAdminDestino());
        System.out.println("🏢 EMPRESA: " + props.getProperty("empresa.nombre"));
    }
    
    // ================================================================
    // ========== MÉTODOS GET =========================================
    // ================================================================
    
    public String getEmailUser() {
        return props.getProperty("email.user");
    }
    
    public String getEmailPassword() {
        return props.getProperty("email.password");
    }
    
    /**
     * 🔥 RETORNA EL EMAIL DEL ADMINISTRADOR (DESTINO DE ENCUESTAS)
     * Este email es FIJO y NO se muestra en la interfaz
     * El cliente NO puede modificarlo
     */
    public String getEmailAdminDestino() {
        // Intentar obtener de properties (por si se configuró manualmente)
        String email = props.getProperty("email.admin.destino");
        if (email != null && !email.isEmpty()) {
            return email;
        }
        // Si no está en properties, usar el valor por defecto (FIJO)
        return EMAIL_ADMIN_DESTINO;
    }
    
    /**
     * 🔥 MÉTODO DEPRECADO - Mantenido por compatibilidad
     * Ahora retorna el email del administrador (mismo que getEmailAdminDestino)
     */
    @Deprecated
    public String getEmailDestino() {
        return getEmailAdminDestino();
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
        return configuracionCargada && credencialesConfiguradas();
    }
    
    // ================================================================
    // ========== GUARDAR CONFIGURACIÓN ===============================
    // ================================================================
    
    /**
     * Guarda la configuración de correo del REMITENTE (cliente)
     * 🔥 NO MODIFICA el email del administrador (destino)
     */
    public void guardarConfiguracionCorreo(String email, String password, 
                                           String smtpHost, String smtpPort, 
                                           boolean usarTLS) throws Exception {
        
        // Guardar email del remitente (lo configura el cliente)
        props.setProperty("email.user", email);
        props.setProperty("email.password", password);
        props.setProperty("smtp.host", smtpHost);
        props.setProperty("smtp.port", smtpPort);
        props.setProperty("smtp.tls", String.valueOf(usarTLS));
        
        // 🔥 NUNCA MODIFICAR email.admin.destino - es FIJO
        // Si no existe, establecerlo (solo la primera vez)
        if (props.getProperty("email.admin.destino") == null) {
            props.setProperty("email.admin.destino", EMAIL_ADMIN_DESTINO);
        }
        
        // Guardar en archivo
        String[] rutasGuardado = {
            System.getProperty("user.dir") + "\\src\\config.properties",
            System.getProperty("user.dir") + "\\config.properties",
            System.getProperty("user.home") + "\\.dentalclinic\\config.properties"
        };
        
        boolean guardado = false;
        for (String ruta : rutasGuardado) {
            try {
                java.io.File file = new java.io.File(ruta);
                if (file.getParentFile() != null) {
                    file.getParentFile().mkdirs();
                }
                
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
    
    public boolean tieneConfiguracionCorreo() {
        return credencialesConfiguradas();
    }
    
    public java.util.Map<String, String> getConfiguracionCorreo() {
        java.util.Map<String, String> config = new java.util.HashMap<>();
        config.put("email", getEmailUser());
        config.put("password", getEmailPassword());
        config.put("smtp.host", getSmtpHost());
        config.put("smtp.port", getSmtpPort());
        config.put("smtp.tls", props.getProperty("smtp.tls", "true"));
        config.put("empresa.nombre", getEmpresaNombre());
        // 🔥 NO incluir email.admin.destino en la configuración visible
        return config;
    }
}