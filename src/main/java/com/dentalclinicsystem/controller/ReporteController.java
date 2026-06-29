package com.dentalclinicsystem.controller;

import com.dentalclinicsystem.dao.ReporteDAO;
import com.dentalclinicsystem.model.Cita;
import com.dentalclinicsystem.model.Paciente;
import com.dentalclinicsystem.model.Venta;
import com.dentalclinicsystem.util.FechaUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * CONTROLLER - SOLO lógica de negocio y validaciones
 * NO consulta la base de datos directamente
 */
public class ReporteController {

    private ReporteDAO dao;

    public ReporteController() {
        this.dao = new ReporteDAO();
    }

    // ================================================================
    // ============== PACIENTES ========================================
    // ================================================================
    
    public List<Paciente> getReportePacientes() {
        return dao.getPacientes();
    }
    
    public List<Paciente> getReportePacientesPorFechas(String fechaInicio, String fechaFin) {
        // Validar fechas
        if (fechaInicio == null || fechaInicio.trim().isEmpty()) {
            fechaInicio = FechaUtil.primerDiaDelMes();
        }
        if (fechaFin == null || fechaFin.trim().isEmpty()) {
            fechaFin = FechaUtil.hoy();
        }
        
        return dao.getPacientesPorFechas(fechaInicio, fechaFin);
    }
    
    public int[] getEstadisticasPacientes() {
        return dao.getEstadisticasPacientes();
    }
    
    public int[] getPacientesPorMes() {
        return dao.getPacientesPorMes();
    }

    // ================================================================
    // ============== CITAS ============================================
    // ================================================================
    
    public List<Cita> getReporteCitas(String fechaInicio, String fechaFin) {
        if (fechaInicio == null || fechaInicio.trim().isEmpty()) {
            fechaInicio = FechaUtil.primerDiaDelMes();
        }
        if (fechaFin == null || fechaFin.trim().isEmpty()) {
            fechaFin = FechaUtil.hoy();
        }
        
        return dao.getCitas(fechaInicio, fechaFin);
    }
    
    public int[] getEstadisticasCitas() {
        return dao.getEstadisticasCitas();
    }
    
    public List<Object[]> getCitasPorOdontologo(String fechaInicio, String fechaFin) {
        if (fechaInicio == null || fechaInicio.trim().isEmpty()) {
            fechaInicio = FechaUtil.primerDiaDelMes();
        }
        if (fechaFin == null || fechaFin.trim().isEmpty()) {
            fechaFin = FechaUtil.hoy();
        }
        
        List<Object[]> datos = dao.getCitasPorOdontologo(fechaInicio, fechaFin);
        
        // Filtrar administradores (lógica de negocio)
        List<Object[]> filtrados = new ArrayList<>();
        List<String> excluir = new ArrayList<>();
        excluir.add("Miller");
        excluir.add("admin");
        excluir.add("Administrador");
        
        for (Object[] row : datos) {
            String nombre = row[0].toString();
            boolean esAdmin = false;
            for (String admin : excluir) {
                if (nombre.equalsIgnoreCase(admin)) {
                    esAdmin = true;
                    break;
                }
            }
            if (!esAdmin) {
                filtrados.add(row);
            }
        }
        
        return filtrados;
    }

    // ================================================================
    // ============== VENTAS ===========================================
    // ================================================================
    
    public List<Venta> getReporteVentas(String fechaInicio, String fechaFin) {
        if (fechaInicio == null || fechaInicio.trim().isEmpty()) {
            fechaInicio = FechaUtil.primerDiaDelMes();
        }
        if (fechaFin == null || fechaFin.trim().isEmpty()) {
            fechaFin = FechaUtil.hoy();
        }
        
        return dao.getVentas(fechaInicio, fechaFin);
    }
    
    public List<Object[]> getIngresosPorDia(String fechaInicio, String fechaFin) {
        if (fechaInicio == null || fechaInicio.trim().isEmpty()) {
            fechaInicio = FechaUtil.primerDiaDelMes();
        }
        if (fechaFin == null || fechaFin.trim().isEmpty()) {
            fechaFin = FechaUtil.hoy();
        }
        
        return dao.getIngresosPorDia(fechaInicio, fechaFin);
    }
    
    public List<Object[]> getIngresosPorMetodoPago(String fechaInicio, String fechaFin) {
        if (fechaInicio == null || fechaInicio.trim().isEmpty()) {
            fechaInicio = FechaUtil.primerDiaDelMes();
        }
        if (fechaFin == null || fechaFin.trim().isEmpty()) {
            fechaFin = FechaUtil.hoy();
        }
        
        return dao.getIngresosPorMetodoPago(fechaInicio, fechaFin);
    }

    // ================================================================
    // ============== SERVICIOS ========================================
    // ================================================================
    
    public List<Object[]> getServiciosMasSolicitados(String fechaInicio, String fechaFin) {
        if (fechaInicio == null || fechaInicio.trim().isEmpty()) {
            fechaInicio = FechaUtil.primerDiaDelMes();
        }
        if (fechaFin == null || fechaFin.trim().isEmpty()) {
            fechaFin = FechaUtil.hoy();
        }
        
        return dao.getServiciosMasSolicitados(fechaInicio, fechaFin);
    }

    // ================================================================
    // ============== INVENTARIO =======================================
    // ================================================================
    
    public List<Object[]> getInsumosStockBajo() {
        return dao.getInsumosStockBajo();
    }

    // ================================================================
    // ============== RESÚMEN GENERAL =================================
    // ================================================================
    
    public String[] getResumenGeneral() {
        return dao.getResumenGeneral();
    }
}