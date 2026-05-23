package com.gymtrack.app.network.dto;

/**
 * Objeto DTO en el frontend que mapea la respuesta del dashboard del cliente.
 *
 * @author Manuel Dominguez
 * @version 1.0
 * @since 23/05/2026
 */
public class DashboardClientDTO {
    private int entrenamientos;
    private int pasosHoy;
    private int calorias;
    private double horasSueno;

    public int getEntrenamientos() { return entrenamientos; }
    public void setEntrenamientos(int entrenamientos) { this.entrenamientos = entrenamientos; }

    public int getPasosHoy() { return pasosHoy; }
    public void setPasosHoy(int pasosHoy) { this.pasosHoy = pasosHoy; }

    public int getCalorias() { return calorias; }
    public void setCalorias(int calorias) { this.calorias = calorias; }

    public double getHorasSueno() { return horasSueno; }
    public void setHorasSueno(double horasSueno) { this.horasSueno = horasSueno; }
}
