package com.dentalclinicsystem.config;

import javax.swing.*;
import java.io.*;
import java.net.*;
import java.nio.file.*;

public class ActualizadorApp {

    // ================================================================
    // 🔥 NUEVO: Iniciar verificación automática al cargar la app
    // ================================================================
    private static boolean verificacionIniciada = false;

    /**
     * Inicia la verificación automática de actualizaciones
     * Debe llamarse UNA SOLA VEZ al iniciar la aplicación
     * 
     * @param parent El JFrame padre (puede ser null)
     */
    public static void iniciarVerificacionAutomatica(JFrame parent) {
        if (verificacionIniciada) {
            return; // Ya se inició
        }
        verificacionIniciada = true;
        
        System.out.println("🔄 Iniciando sistema de actualizaciones automáticas...");
        
        // Verificar inmediatamente al iniciar
        verificarActualizacion(parent);
        
        // Programar verificaciones periódicas (cada 24 horas)
        java.util.Timer timer = new java.util.Timer();
        timer.schedule(new java.util.TimerTask() {
            @Override
            public void run() {
                System.out.println("🔄 Verificando actualizaciones automáticas (programado)...");
                verificarActualizacion(null); // No necesitamos el parent para verificaciones programadas
            }
        }, 24 * 60 * 60 * 1000, 24 * 60 * 60 * 1000); // Cada 24 horas
        
        System.out.println("✅ Sistema de actualizaciones iniciado");
        System.out.println("   📅 Verificación inmediata: completada");
        System.out.println("   ⏰ Próxima verificación: en 24 horas");
    }

    public static void verificarActualizacion(JFrame parent) {
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                try {
                    String repo = ActualizacionConfig.getRepo();
                    String versionActual = ActualizacionConfig.getVersion();

                    System.out.println("🔍 Buscando actualizaciones...");
                    System.out.println("   📦 Repo: " + repo);
                    System.out.println("   📌 Version actual: " + versionActual);

                    // Validar que el repo no esté vacío
                    if (repo == null || repo.isEmpty() || repo.equals("null")) {
                        System.out.println("⚠️ Repositorio no configurado. Saltando verificación.");
                        return null;
                    }

                    String apiUrl = "https://api.github.com/repos/" + repo + "/releases/latest";
                    URL url = URI.create(apiUrl).toURL();
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestProperty("Accept", "application/vnd.github.v3+json");
                    conn.setConnectTimeout(5000);
                    conn.setReadTimeout(5000);

                    int responseCode = conn.getResponseCode();
                    if (responseCode != 200) {
                        System.out.println("⚠️ Error al consultar GitHub: HTTP " + responseCode);
                        conn.disconnect();
                        return null;
                    }

                    String json = new String(conn.getInputStream().readAllBytes());
                    conn.disconnect();

                    String versionRemota = extractJsonValue(json, "tag_name");
                    String downloadUrl = extractDownloadUrl(json);
                    String nombreArchivoRemoto = extractFileNameFromUrl(downloadUrl);

                    if (versionRemota == null || downloadUrl == null) {
                        System.out.println("⚠️ No se pudo obtener la información de versión");
                        return null;
                    }
                    
                    System.out.println("   📌 Version remota: " + versionRemota);
                    System.out.println("   📁 Archivo remoto: " + nombreArchivoRemoto);
                    
                    if (versionRemota.startsWith("v")) {
                        versionRemota = versionRemota.substring(1);
                    }

                    if (esVersionNueva(versionActual, versionRemota)) {
                        System.out.println("🆕 ¡Nueva versión disponible! " + versionActual + " → " + versionRemota);
                        mostrarDialogoActualizacion(parent, versionActual, versionRemota, downloadUrl, nombreArchivoRemoto);
                    } else {
                        System.out.println("✅ La aplicación está actualizada (v" + versionActual + ")");
                    }

                } catch (MalformedURLException e) {
                    System.err.println("❌ URL inválida: " + e.getMessage());
                } catch (Exception e) {
                    System.err.println("❌ Error al verificar actualizaciones:");
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

    private static String extractJsonValue(String json, String key) {
        String searchKey = "\"" + key + "\":\"";
        int idx = json.indexOf(searchKey);
        if (idx == -1) {
            return null;
        }
        idx += searchKey.length();
        int end = json.indexOf("\"", idx);
        if (end == -1) {
            return null;
        }
        return json.substring(idx, end);
    }

    private static String extractDownloadUrl(String json) {
        try {
            String searchPattern = "\"browser_download_url\":\"";
            int lastIndex = 0;
            
            while (true) {
                int urlStart = json.indexOf(searchPattern, lastIndex);
                if (urlStart == -1) break;
                
                urlStart += searchPattern.length();
                int urlEnd = json.indexOf("\"", urlStart);
                if (urlEnd == -1) break;
                
                String url = json.substring(urlStart, urlEnd);
                if (url.endsWith(".jar") || url.endsWith(".exe")) {
                    System.out.println("   🔗 URL descarga: " + url);
                    return url;
                }
                
                lastIndex = urlEnd;
            }
            
            System.err.println("⚠️ No se encontró ningún archivo .jar o .exe en los assets");
            return null;
        } catch (Exception e) {
            System.err.println("❌ Error al extraer URL de descarga: " + e.getMessage());
            return null;
        }
    }
    
    private static String extractFileNameFromUrl(String url) {
        if (url == null) return "DentalClinicSystem.jar";
        String[] parts = url.split("/");
        return parts[parts.length - 1];
    }

    private static boolean esVersionNueva(String actual, String remota) {
        try {
            String[] actualParts = actual.split("\\.");
            String[] remotaParts = remota.split("\\.");

            for (int i = 0; i < Math.max(actualParts.length, remotaParts.length); i++) {
                int act = i < actualParts.length ? Integer.parseInt(actualParts[i].replaceAll("[^0-9]", "")) : 0;
                int rem = i < remotaParts.length ? Integer.parseInt(remotaParts[i].replaceAll("[^0-9]", "")) : 0;
                if (rem > act) {
                    return true;
                }
                if (rem < act) {
                    return false;
                }
            }
            return false;
        } catch (NumberFormatException e) {
            System.err.println("❌ Error al comparar versiones: " + e.getMessage());
            return false;
        }
    }

    private static void mostrarDialogoActualizacion(JFrame parent, String versionActual,
            String versionRemota, String downloadUrl, String nombreArchivo) {
        
        // Si parent es null, usar un JFrame temporal para el diálogo
        JFrame dialogParent = parent != null ? parent : new JFrame();
        
        SwingUtilities.invokeLater(() -> {
            int respuesta = JOptionPane.showConfirmDialog(dialogParent,
                    "<html><body style='width: 350px;'>"
                    + "<h3>🆕 ¡Nueva versión disponible!</h3>"
                    + "<p>Versión actual: <b>" + versionActual + "</b><br>"
                    + "Nueva versión: <b>" + versionRemota + "</b><br>"
                    + "Archivo: <b>" + nombreArchivo + "</b></p>"
                    + "<p style='color: #666; font-size: 12px;'>"
                    + "La actualización se descargará e instalará automáticamente.</p>"
                    + "<p>¿Desea actualizar ahora?</p>"
                    + "</body></html>",
                    "Actualización disponible",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.INFORMATION_MESSAGE);

            if (respuesta == JOptionPane.YES_OPTION) {
                descargarYActualizar(dialogParent, downloadUrl, versionRemota, nombreArchivo);
            }
        });
    }

    private static void descargarYActualizar(JFrame parent, String downloadUrl, 
                                             String nuevaVersion, String nombreArchivoRemoto) {
        JDialog progressDialog = new JDialog(parent, "Actualizando", true);
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressDialog.add(progressBar);
        progressDialog.setSize(300, 80);
        progressDialog.setLocationRelativeTo(parent);

        new Thread(() -> {
            try {
                String tempDir = System.getProperty("java.io.tmpdir");
                Path nuevoJarPath = Paths.get(tempDir, nombreArchivoRemoto);

                System.out.println("📥 Descargando desde: " + downloadUrl);
                System.out.println("📁 Guardando como: " + nuevoJarPath);
                
                URL url = URI.create(downloadUrl).toURL();
                Files.copy(url.openStream(), nuevoJarPath, StandardCopyOption.REPLACE_EXISTING);

                System.out.println("✅ Descarga completada en: " + nuevoJarPath);

                crearScriptActualizacion(nuevoJarPath.toAbsolutePath().toString(), 
                                        nuevaVersion, nombreArchivoRemoto);

                SwingUtilities.invokeLater(() -> {
                    progressDialog.dispose();
                    JOptionPane.showMessageDialog(parent,
                            "<html><body>"
                            + "<p>✅ La actualización se completará al reiniciar la aplicación.</p>"
                            + "<p>🔄 La aplicación se cerrará ahora.</p>"
                            + "</body></html>",
                            "Actualización lista",
                            JOptionPane.INFORMATION_MESSAGE);

                    System.exit(0);
                });

            } catch (Exception e) {
                System.err.println("❌ Error en descarga: " + e.getMessage());
                e.printStackTrace();
                SwingUtilities.invokeLater(() -> {
                    progressDialog.dispose();
                    JOptionPane.showMessageDialog(parent,
                            "❌ Error al descargar la actualización:\n" + e.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                });
            }
        }).start();

        progressDialog.setVisible(true);
    }

    private static void crearScriptActualizacion(String nuevoJarPath, String nuevaVersion, 
                                                  String nombreArchivoRemoto) {
        try {
            String currentDir = System.getProperty("user.dir");
            String scriptPath;
            String comando;
            String repo = ActualizacionConfig.getRepo();
            
            String jarActual = getCurrentJarName();

            if (System.getProperty("os.name").toLowerCase().contains("win")) {
                scriptPath = currentDir + File.separator + "update.bat";
                comando = "@echo off\n"
                        + "echo ========================================\n"
                        + "echo  ACTUALIZANDO DENTAL CLINIC SYSTEM\n"
                        + "echo ========================================\n"
                        + "timeout /t 2 /nobreak > nul\n"
                        + "echo 📦 Copiando nuevo archivo...\n"
                        + "copy /Y \"" + nuevoJarPath + "\" \"" + currentDir + "\\" + jarActual + "\"\n"
                        + "echo 📝 Actualizando version...\n"
                        + "echo app.version=" + nuevaVersion + " > \"" + currentDir + "\\app.properties\"\n"
                        + "echo app.repo=" + repo + " >> \"" + currentDir + "\\app.properties\"\n"
                        + "echo 🧹 Limpiando archivos temporales...\n"
                        + "del \"" + nuevoJarPath + "\" 2>nul\n"
                        + "echo ========================================\n"
                        + "echo  ✅ ACTUALIZACION COMPLETADA\n"
                        + "echo ========================================\n"
                        + "echo 🚀 Iniciando aplicacion...\n"
                        + "start javaw -jar \"" + currentDir + "\\" + jarActual + "\"\n"
                        + "del \"%~f0\"\n";
            } else {
                scriptPath = currentDir + File.separator + "update.sh";
                comando = "#!/bin/bash\n"
                        + "echo '========================================'\n"
                        + "echo ' ACTUALIZANDO DENTAL CLINIC SYSTEM'\n"
                        + "echo '========================================'\n"
                        + "sleep 2\n"
                        + "echo '📦 Copiando nuevo archivo...'\n"
                        + "cp \"" + nuevoJarPath + "\" \"" + currentDir + "/" + jarActual + "\"\n"
                        + "echo '📝 Actualizando version...'\n"
                        + "echo \"app.version=" + nuevaVersion + "\" > \"" + currentDir + "/app.properties\"\n"
                        + "echo \"app.repo=" + repo + "\" >> \"" + currentDir + "/app.properties\"\n"
                        + "echo '🧹 Limpiando archivos temporales...'\n"
                        + "rm \"" + nuevoJarPath + "\"\n"
                        + "echo '========================================'\n"
                        + "echo ' ✅ ACTUALIZACION COMPLETADA'\n"
                        + "echo '========================================'\n"
                        + "echo '🚀 Iniciando aplicacion...'\n"
                        + "java -jar \"" + currentDir + "/" + jarActual + "\" &\n"
                        + "rm -- \"$0\"\n";
            }

            Files.writeString(Paths.get(scriptPath), comando);
            
            if (!System.getProperty("os.name").toLowerCase().contains("win")) {
                new ProcessBuilder("chmod", "+x", scriptPath).start();
            }
            
            System.out.println("📝 Script de actualización creado: " + scriptPath);
            Runtime.getRuntime().exec(scriptPath);
            
        } catch (IOException e) {
            System.err.println("❌ Error al crear script: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static String getCurrentJarName() {
        try {
            String path = ActualizadorApp.class.getProtectionDomain()
                            .getCodeSource()
                            .getLocation()
                            .toURI()
                            .getPath();
            return new File(path).getName();
        } catch (Exception e) {
            System.err.println("⚠️ Error obteniendo nombre del JAR actual: " + e.getMessage());
            return "DentalClinicSystem.jar";
        }
    }
}