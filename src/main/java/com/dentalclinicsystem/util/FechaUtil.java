package com.dentalclinicsystem.util;

import javax.swing.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

public class FechaUtil {
    
    // Formatos
    public static final String FORMATO_DATE = "yyyy-MM-dd";
    public static final String FORMATO_DATETIME = "yyyy-MM-dd HH:mm:ss";
    
    // FORMATTER CON RESOLVER STRICT PARA VALIDACIÓN CORRECTA
    private static final DateTimeFormatter FORMATTER_DATE = 
        DateTimeFormatter.ofPattern(FORMATO_DATE)
            .withResolverStyle(ResolverStyle.STRICT);
    
    private static final DateTimeFormatter FORMATTER_DATETIME = 
        DateTimeFormatter.ofPattern(FORMATO_DATETIME)
            .withResolverStyle(ResolverStyle.STRICT);
    
    // ================================================================
    // ============== MÉTODOS BÁSICOS ================================
    // ================================================================
    
    public static String ahora() {
        return LocalDateTime.now().format(FORMATTER_DATETIME);
    }
    
    public static String hoy() {
        return LocalDate.now().format(FORMATTER_DATE);
    }
    
    public static String primerDiaDelMes() {
        return LocalDate.now().withDayOfMonth(1).format(FORMATTER_DATE);
    }
    
    public static String ultimoDiaDelMes() {
        return LocalDate.now().withDayOfMonth(
            LocalDate.now().lengthOfMonth()
        ).format(FORMATTER_DATE);
    }
    
    // ================================================================
    // ============== CONVERSIÓN ======================================
    // ================================================================
    
    public static LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) return null;
        try {
            String trimmed = dateStr.trim();
            if (!trimmed.matches("\\d{4}-\\d{2}-\\d{2}")) {
                return null;
            }
            return LocalDate.parse(trimmed, FORMATTER_DATE);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
    
    public static String formatDate(LocalDate date) {
        return date != null ? date.format(FORMATTER_DATE) : "";
    }
    
    public static String dateToString(java.util.Date date) {
        if (date == null) return hoy();
        try {
            LocalDate localDate = date.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
            return localDate.format(FORMATTER_DATE);
        } catch (Exception e) {
            return hoy();
        }
    }
    
    public static java.util.Date parseToDate(String dateStr) {
        LocalDate date = parseDate(dateStr);
        if (date == null) return null;
        return java.util.Date.from(date.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant());
    }
    
    // ================================================================
    // ============== VALIDACIONES (SILENCIOSAS) ======================
    // ================================================================
    
    /**
     * Valida formato de fecha - NUNCA retorna mensajes de error
     * @return true si es válida, false si no
     */
    public static boolean esFechaValida(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return false;
        }
        String trimmed = dateStr.trim();
        if (!trimmed.matches("\\d{4}-\\d{2}-\\d{2}")) {
            return false;
        }
        try {
            LocalDate.parse(trimmed, FORMATTER_DATE);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
    
    /**
     * Normaliza una fecha - SIEMPRE retorna una fecha válida
     */
    public static String normalizarFecha(String fecha) {
        if (fecha == null || fecha.trim().isEmpty()) {
            return hoy();
        }
        
        String limpio = fecha.trim();
        
        // Si ya está en formato válido, devolverlo
        if (limpio.matches("\\d{4}-\\d{2}-\\d{2}")) {
            try {
                LocalDate.parse(limpio, FORMATTER_DATE);
                return limpio;
            } catch (DateTimeParseException e) {
                return hoy();
            }
        }
        
        // Intentar convertir otros formatos comunes
        try {
            // DD/MM/YYYY -> YYYY-MM-DD
            if (limpio.matches("\\d{2}/\\d{2}/\\d{4}")) {
                String[] partes = limpio.split("/");
                return partes[2] + "-" + partes[1] + "-" + partes[0];
            }
            // DD-MM-YYYY -> YYYY-MM-DD
            if (limpio.matches("\\d{2}-\\d{2}-\\d{4}")) {
                String[] partes = limpio.split("-");
                return partes[2] + "-" + partes[1] + "-" + partes[0];
            }
            // DD.MM.YYYY -> YYYY-MM-DD
            if (limpio.matches("\\d{2}\\.\\d{2}\\.\\d{4}")) {
                String[] partes = limpio.split("\\.");
                return partes[2] + "-" + partes[1] + "-" + partes[0];
            }
        } catch (Exception e) {
            return hoy();
        }
        
        return hoy();
    }
    
    /**
     * Valida y normaliza un rango de fechas - SIEMPRE retorna fechas válidas
     */
    public static String[] validarYNormalizarRango(String fechaInicio, String fechaFin) {
        String inicio = normalizarFecha(fechaInicio);
        String fin = normalizarFecha(fechaFin);
        
        // Asegurar que inicio <= fin
        if (inicio.compareTo(fin) > 0) {
            String temp = inicio;
            inicio = fin;
            fin = temp;
        }
        
        return new String[]{inicio, fin};
    }
    
    /**
     * Valida que una fecha no sea futura
     */
    public static boolean esFechaPasada(String dateStr) {
        if (!esFechaValida(dateStr)) return false;
        LocalDate fecha = parseDate(dateStr);
        return fecha != null && !fecha.isAfter(LocalDate.now());
    }
    
    /**
     * Valida que una fecha esté en el rango permitido (1900-2100)
     */
    public static boolean esFechaEnRango(String dateStr) {
        if (!esFechaValida(dateStr)) return false;
        LocalDate fecha = parseDate(dateStr);
        if (fecha == null) return false;
        int year = fecha.getYear();
        return year >= 1900 && year <= 2100;
    }
    
    /**
     * Muestra un error si existe - SOLO para UI
     */
    public static boolean mostrarErrorSiInvalido(String error, JComponent parent) {
        if (error != null && !error.isEmpty()) {
            JOptionPane.showMessageDialog(parent, 
                error, 
                "Error de Validación", 
                JOptionPane.ERROR_MESSAGE);
            return true;
        }
        return false;
    }
    
    // ================================================================
    // ============== PARSEO DE MONEDA ================================
    // ================================================================
    
    /**
     * Limpia un string eliminando caracteres de formato de moneda
     */
    public static double parseMoneda(String valor) {
        if (valor == null || valor.isEmpty()) return 0.0;
        try {
            String limpio = valor.replace("$", "")
                                 .replace(",", "")
                                 .replace(" ", "")
                                 .replace("¢", "")
                                 .replace("€", "")
                                 .replace("£", "")
                                 .replace("US$", "")
                                 .replace("USD", "")
                                 .trim();
            if (limpio.isEmpty()) return 0.0;
            return Double.parseDouble(limpio);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
    
    /**
     * Formatea un número como moneda
     */
    public static String formatMoneda(double valor) {
        return String.format("$%.2f", valor);
    }
}