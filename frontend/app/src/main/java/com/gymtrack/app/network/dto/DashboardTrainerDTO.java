package com.gymtrack.app.network.dto;

/**
 * Objeto DTO en el frontend que mapea la respuesta del dashboard del entrenador.
 *
 * @author Manuel Dominguez
 * @version 1.0
 * @since 23/05/2026
 */
public class DashboardTrainerDTO {
    private int clientesTotales;
    private int activosHoy;
    private int entrenamientos;
    private int estaSemana;

    public int getClientesTotales() { return clientesTotales; }
    public void setClientesTotales(int clientesTotales) { this.clientesTotales = clientesTotales; }

    public int getActivosHoy() { return activosHoy; }
    public void setActivosHoy(int activosHoy) { this.activosHoy = activosHoy; }

    public int getEntrenamientos() { return entrenamientos; }
    public void setEntrenamientos(int entrenamientos) { this.entrenamientos = entrenamientos; }

    public int getEstaSemana() { return estaSemana; }
    public void setEstaSemana(int estaSemana) { this.estaSemana = estaSemana; }
}
