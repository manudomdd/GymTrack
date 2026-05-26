package com.gymtrack.app.network.dto;

/**
 * Data Transfer Object (DTO) que mapea la respuesta del backend para el panel gerencial del entrenador.
 * <p>
 * Encapsula los indicadores clave de rendimiento (KPIs) de la cartera de clientes, 
 * tales como el volumen total de usuarios asignados, clientes con actividad física 
 * en el día actual y el flujo de entrenamientos de la semana.
 * </p>
 *
 * @author Manuel Dominguez
 * @version 1.0
 * @since 26/05/2025
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
