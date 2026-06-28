package com.dentalclinicsystem.util;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.opencsv.CSVWriter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ExportUtil {

    private static final DateTimeFormatter FORMATTER = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
    
    // ================================================================
    // ========== NUEVA CARPETA DE EXPORTACIÓN ========================
    // ================================================================
    
    /**
     * Obtiene la carpeta de exportación (raíz del proyecto /exportaciones)
     * Si no existe, la crea automáticamente
     */
    private static File getExportFolder() {
        // Obtener la ruta raíz del proyecto
        String userDir = System.getProperty("user.dir");
        File exportFolder = new File(userDir, "exportaciones");
        
        // Crear la carpeta si no existe
        if (!exportFolder.exists()) {
            boolean creado = exportFolder.mkdirs();
            if (creado) {
                System.out.println("📁 Carpeta de exportaciones creada: " + exportFolder.getAbsolutePath());
            } else {
                System.err.println("❌ No se pudo crear la carpeta de exportaciones");
            }
        }
        
        return exportFolder;
    }
    
    /**
     * Genera la ruta completa del archivo dentro de la carpeta exportaciones
     */
    private static String getExportPath(String nombreArchivo) {
        File exportFolder = getExportFolder();
        File archivo = new File(exportFolder, nombreArchivo);
        return archivo.getAbsolutePath();
    }

    /**
     * Exporta un reporte a Excel (.xlsx) - SIN DIÁLOGO
     */
    public static boolean exportToExcel(DefaultTableModel model, String titulo, Component parent) {
        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(parent, 
                "No hay datos para exportar a Excel", 
                "Sin Datos", 
                JOptionPane.WARNING_MESSAGE);
            return false;
        }

        String nombreArchivo = generarNombreArchivo(titulo, "xlsx");
        String rutaCompleta = getExportPath(nombreArchivo);
        
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            String sheetName = titulo.length() > 31 ? titulo.substring(0, 31) : titulo;
            Sheet sheet = workbook.createSheet(sheetName);
            
            // ===== CREAR ESTILOS =====
            org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerFont.setFontHeightInPoints((short) 11);
            
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);
            
            org.apache.poi.ss.usermodel.Font dataFont = workbook.createFont();
            dataFont.setFontHeightInPoints((short) 10);
            
            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setFont(dataFont);
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderTop(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);
            dataStyle.setAlignment(HorizontalAlignment.CENTER);
            dataStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            
            // ===== ENCABEZADOS =====
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < model.getColumnCount(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(model.getColumnName(i));
                cell.setCellStyle(headerStyle);
                sheet.autoSizeColumn(i);
                if (sheet.getColumnWidth(i) < 3000) {
                    sheet.setColumnWidth(i, 3000);
                }
            }
            
            // ===== DATOS =====
            for (int i = 0; i < model.getRowCount(); i++) {
                Row row = sheet.createRow(i + 1);
                for (int j = 0; j < model.getColumnCount(); j++) {
                    Cell cell = row.createCell(j);
                    Object value = model.getValueAt(i, j);
                    cell.setCellValue(value != null ? value.toString() : "");
                    cell.setCellStyle(dataStyle);
                }
            }
            
            // ===== GUARDAR =====
            try (FileOutputStream fileOut = new FileOutputStream(rutaCompleta)) {
                workbook.write(fileOut);
            }
            
            JOptionPane.showMessageDialog(parent, 
                "✅ Reporte exportado exitosamente a Excel\n" +
                "📂 Carpeta: exportaciones/\n" +
                "📄 Archivo: " + nombreArchivo + "\n" +
                "📊 Registros: " + model.getRowCount(), 
                "Exportación Exitosa", 
                JOptionPane.INFORMATION_MESSAGE);
            
            abrirArchivo(rutaCompleta);
            return true;
            
        } catch (IOException e) {
            JOptionPane.showMessageDialog(parent, 
                "❌ Error al exportar a Excel: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }

    /**
     * Exporta un reporte a PDF - SIN DIÁLOGO
     */
    public static boolean exportToPDF(DefaultTableModel model, String titulo, Component parent) {
        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(parent, 
                "No hay datos para exportar a PDF", 
                "Sin Datos", 
                JOptionPane.WARNING_MESSAGE);
            return false;
        }

        String nombreArchivo = generarNombreArchivo(titulo, "pdf");
        String rutaCompleta = getExportPath(nombreArchivo);
        
        try {
            Document document = new Document(PageSize.A4.rotate());
            document.setMargins(20, 20, 20, 20);
            
            PdfWriter.getInstance(document, new FileOutputStream(rutaCompleta));
            document.open();
            
            // ===== TÍTULO =====
            com.itextpdf.text.Font titleFont = new com.itextpdf.text.Font(
                com.itextpdf.text.Font.FontFamily.HELVETICA, 
                18, 
                com.itextpdf.text.Font.BOLD
            );
            Paragraph title = new Paragraph(titulo, titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);
            
            // ===== FECHA =====
            com.itextpdf.text.Font dateFont = new com.itextpdf.text.Font(
                com.itextpdf.text.Font.FontFamily.HELVETICA, 
                10, 
                com.itextpdf.text.Font.NORMAL
            );
            String fechaActual = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            Paragraph date = new Paragraph("Generado: " + fechaActual, dateFont);
            date.setAlignment(Element.ALIGN_RIGHT);
            date.setSpacingAfter(15);
            document.add(date);
            
            // ===== TABLA =====
            int numColumns = model.getColumnCount();
            if (numColumns == 0) {
                document.close();
                JOptionPane.showMessageDialog(parent, 
                    "No hay columnas para exportar", 
                    "Sin Datos", 
                    JOptionPane.WARNING_MESSAGE);
                return false;
            }
            
            PdfPTable table = new PdfPTable(numColumns);
            table.setWidthPercentage(100);
            if (numColumns > 8) {
                table.setWidthPercentage(90);
            }
            
            // ===== ENCABEZADOS =====
            com.itextpdf.text.Font headerFont = new com.itextpdf.text.Font(
                com.itextpdf.text.Font.FontFamily.HELVETICA, 
                10, 
                com.itextpdf.text.Font.BOLD
            );
            BaseColor headerBg = new BaseColor(200, 200, 210);
            
            for (int i = 0; i < numColumns; i++) {
                String columnName = model.getColumnName(i);
                if (columnName == null) columnName = "Columna " + (i + 1);
                
                PdfPCell cell = new PdfPCell(new Phrase(columnName, headerFont));
                cell.setBackgroundColor(headerBg);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setPadding(6);
                cell.setBorderWidth(1);
                table.addCell(cell);
            }
            
            // ===== DATOS =====
            com.itextpdf.text.Font dataFont = new com.itextpdf.text.Font(
                com.itextpdf.text.Font.FontFamily.HELVETICA, 
                9, 
                com.itextpdf.text.Font.NORMAL
            );
            
            int rowCount = model.getRowCount();
            int maxRows = Math.min(rowCount, 1000);
            
            for (int i = 0; i < maxRows; i++) {
                for (int j = 0; j < numColumns; j++) {
                    Object value = model.getValueAt(i, j);
                    String text = (value != null) ? value.toString() : "";
                    if (text.length() > 100) {
                        text = text.substring(0, 97) + "...";
                    }
                    
                    PdfPCell cell = new PdfPCell(new Phrase(text, dataFont));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cell.setPadding(4);
                    cell.setBorderWidth(0.5f);
                    table.addCell(cell);
                }
            }
            
            document.add(table);
            
            // ===== PIE DE PÁGINA =====
            com.itextpdf.text.Font footerFont = new com.itextpdf.text.Font(
                com.itextpdf.text.Font.FontFamily.HELVETICA, 
                8, 
                com.itextpdf.text.Font.ITALIC
            );
            Paragraph footer = new Paragraph(
                "Total de registros: " + rowCount + " | Generado por Sistema Dental Clinic", 
                footerFont
            );
            footer.setAlignment(Element.ALIGN_CENTER);
            footer.setSpacingBefore(15);
            document.add(footer);
            
            document.close();
            
            JOptionPane.showMessageDialog(parent, 
                "✅ Reporte exportado exitosamente a PDF\n" +
                "📂 Carpeta: exportaciones/\n" +
                "📄 Archivo: " + nombreArchivo + "\n" +
                "📊 Registros: " + rowCount, 
                "Exportación Exitosa", 
                JOptionPane.INFORMATION_MESSAGE);
            
            abrirArchivo(rutaCompleta);
            return true;
            
        } catch (DocumentException | IOException e) {
            JOptionPane.showMessageDialog(parent, 
                "❌ Error al exportar a PDF: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(parent, 
                "❌ Error inesperado: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }

    /**
     * Exporta un reporte a CSV - SIN DIÁLOGO
     */
    public static boolean exportToCSV(DefaultTableModel model, String titulo, Component parent) {
        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(parent, 
                "No hay datos para exportar a CSV", 
                "Sin Datos", 
                JOptionPane.WARNING_MESSAGE);
            return false;
        }

        String nombreArchivo = generarNombreArchivo(titulo, "csv");
        String rutaCompleta = getExportPath(nombreArchivo);
        
        try (CSVWriter writer = new CSVWriter(
                new FileWriter(rutaCompleta),
                CSVWriter.DEFAULT_SEPARATOR,
                CSVWriter.NO_QUOTE_CHARACTER,
                CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                CSVWriter.DEFAULT_LINE_END
            )) {
                
            // ===== ENCABEZADOS =====
            String[] headers = new String[model.getColumnCount()];
            for (int i = 0; i < model.getColumnCount(); i++) {
                String columnName = model.getColumnName(i);
                headers[i] = (columnName != null) ? columnName : "Columna" + (i + 1);
            }
            writer.writeNext(headers);
            
            // ===== DATOS =====
            for (int i = 0; i < model.getRowCount(); i++) {
                String[] row = new String[model.getColumnCount()];
                for (int j = 0; j < model.getColumnCount(); j++) {
                    Object value = model.getValueAt(i, j);
                    row[j] = (value != null) ? value.toString() : "";
                }
                writer.writeNext(row);
            }
            
            JOptionPane.showMessageDialog(parent, 
                "✅ Reporte exportado exitosamente a CSV\n" +
                "📂 Carpeta: exportaciones/\n" +
                "📄 Archivo: " + nombreArchivo + "\n" +
                "📊 Registros: " + model.getRowCount(), 
                "Exportación Exitosa", 
                JOptionPane.INFORMATION_MESSAGE);
            
            abrirArchivo(rutaCompleta);
            return true;
            
        } catch (IOException e) {
            JOptionPane.showMessageDialog(parent, 
                "❌ Error al exportar a CSV: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }

    /**
     * Exporta con diálogo de selección de formato
     */
    public static void exportarConDialogo(DefaultTableModel model, String titulo, Component parent) {
        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(parent, 
                "No hay datos para exportar", 
                "Sin Datos", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        String[] opciones = {"Excel (.xlsx)", "PDF (.pdf)", "CSV (.csv)"};
        String formato = (String) JOptionPane.showInputDialog(
            parent,
            "Seleccione el formato de exportación:",
            "Exportar Reporte",
            JOptionPane.QUESTION_MESSAGE,
            null,
            opciones,
            opciones[0]
        );
        
        if (formato == null) return;
        
        boolean exito = false;
        if (formato.equals(opciones[0])) {
            exito = exportToExcel(model, titulo, parent);
        } else if (formato.equals(opciones[1])) {
            exito = exportToPDF(model, titulo, parent);
        } else if (formato.equals(opciones[2])) {
            exito = exportToCSV(model, titulo, parent);
        }
        
        if (!exito) {
            JOptionPane.showMessageDialog(parent, 
                "❌ No se pudo completar la exportación", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    // ================================================================
    // ============== MÉTODOS AUXILIARES ==============================
    // ================================================================

    private static String generarNombreArchivo(String titulo, String extension) {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        String nombreBase = titulo.replaceAll("\\s+", "_")
                                  .replaceAll("[^a-zA-Z0-9_]", "");
        if (nombreBase.isEmpty()) {
            nombreBase = "Reporte";
        }
        return nombreBase + "_" + timestamp + "." + extension;
    }

    /**
     * Abre el archivo con la aplicación predeterminada del sistema
     */
    private static void abrirArchivo(String ruta) {
        try {
            File archivo = new File(ruta);
            if (archivo.exists() && Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                if (desktop.isSupported(Desktop.Action.OPEN)) {
                    desktop.open(archivo);
                }
            }
        } catch (IOException e) {
            System.out.println("Archivo guardado en: " + ruta);
        }
    }
}