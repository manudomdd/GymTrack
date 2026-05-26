package com.gymtrack.app.network.dto;

/**
 * Data Transfer Object (DTO) que mapea la respuesta del backend para el panel principal del cliente.
 * <p>
 * Encapsula de forma estricta las métricas del día actual: total de series registradas, 
 * contador de pasos, calorías quemadas y horas de sueño. Su uso garantiza una capa de 
 * aislamiento entre la serialización de la API y la vista de la aplicación.
 * </p>
 *
 * @author Manuel Dominguez
 * @version 1.0
 * @since 26/05/2025
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
