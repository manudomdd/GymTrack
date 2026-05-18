package app.dto;

public class DashboardTrainerDTO {
    private int clientesTotales;
    private int activosHoy;
    private int entrenamientos;
    private int estaSemana;

    public DashboardTrainerDTO() {}

    public DashboardTrainerDTO(int clientesTotales, int activosHoy, int entrenamientos, int estaSemana) {
        this.clientesTotales = clientesTotales;
        this.activosHoy = activosHoy;
        this.entrenamientos = entrenamientos;
        this.estaSemana = estaSemana;
    }

    public int getClientesTotales() {
        return clientesTotales;
    }

    public void setClientesTotales(int clientesTotales) {
        this.clientesTotales = clientesTotales;
    }

    public int getActivosHoy() {
        return activosHoy;
    }

    public void setActivosHoy(int activosHoy) {
        this.activosHoy = activosHoy;
    }

    public int getEntrenamientos() {
        return entrenamientos;
    }

    public void setEntrenamientos(int entrenamientos) {
        this.entrenamientos = entrenamientos;
    }

    public int getEstaSemana() {
        return estaSemana;
    }

    public void setEstaSemana(int estaSemana) {
        this.estaSemana = estaSemana;
    }
}
