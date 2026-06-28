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
        if (date == null) return "";
        LocalDate localDate = date.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
        return localDate.format(FORMATTER_DATE);
    }
    
    public static java.util.Date parseToDate(String dateStr) {
        LocalDate date = parseDate(dateStr);
        if (date == null) return null;
        return java.util.Date.from(date.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant());
    }
    
    // ================================================================
    // ============== VALIDACIONES ====================================
    // ================================================================
    
    public static String validarFecha(String dateStr, String campo) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return campo + " no puede estar vacío";
        }
        
        String trimmed = dateStr.trim();
        if (!trimmed.matches("\\d{4}-\\d{2}-\\d{2}")) {
            return "Formato de " + campo + " inválido. Use YYYY-MM-DD";
        }
        
        LocalDate date = parseDate(trimmed);
        if (date == null) {
            return "Formato de " + campo + " inválido. Use YYYY-MM-DD";
        }
        
        int year = date.getYear();
        if (year < 1900 || year > 2100) {
            return campo + " debe estar entre 1900 y 2100";
        }
        
        return null;
    }
    
    public static String validarRangoFechas(String inicio, String fin) {
        if (inicio == null || inicio.trim().isEmpty()) {
            return "Fecha inicio no puede estar vacía";
        }
        if (fin == null || fin.trim().isEmpty()) {
            return "Fecha fin no puede estar vacía";
        }
        
        String errorInicio = validarFecha(inicio, "Fecha inicio");
        if (errorInicio != null) return errorInicio;
        
        String errorFin = validarFecha(fin, "Fecha fin");
        if (errorFin != null) return errorFin;
        
        LocalDate dateInicio = parseDate(inicio.trim());
        LocalDate dateFin = parseDate(fin.trim());
        
        if (dateInicio == null || dateFin == null) {
            return "Formato de fecha inválido";
        }
        
        if (dateInicio.isAfter(dateFin)) {
            return "La fecha de inicio no puede ser mayor que la fecha fin";
        }
        
        long daysBetween = dateInicio.until(dateFin).getDays();
        if (daysBetween > 365) {
            return "El rango de fechas no puede superar 1 año";
        }
        
        return null;
    }
    
    public static boolean mostrarErrorSiInvalido(String error, JComponent parent) {
        if (error != null) {
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
     * @param valor String con formato de moneda (ej: "$1,234.56")
     * @return double valor numérico
     */
    public static double parseMoneda(String valor) {
        if (valor == null || valor.isEmpty()) return 0.0;
        try {
            // Eliminar $, comas, espacios y cualquier otro caracter no numérico excepto el punto
            String limpio = valor.replace("$", "")
                                 .replace(",", "")
                                 .replace(" ", "")
                                 .replace("¢", "")
                                 .replace("€", "")
                                 .replace("£", "")
                                 .trim();
            if (limpio.isEmpty()) return 0.0;
            return Double.parseDouble(limpio);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}