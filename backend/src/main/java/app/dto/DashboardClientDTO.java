package app.dto;

/**
 * Objeto de transferencia de datos para el dashboard del cliente.
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

    public DashboardClientDTO() {}

    public DashboardClientDTO(int entrenamientos, int pasosHoy, int calorias, double horasSueno) {
        this.entrenamientos = entrenamientos;
        this.pasosHoy = pasosHoy;
        this.calorias = calorias;
        this.horasSueno = horasSueno;
    }

    public int getEntrenamientos() {
        return entrenamientos;
    }

    public void setEntrenamientos(int entrenamientos) {
        this.entrenamientos = entrenamientos;
    }

    public int getPasosHoy() {
        return pasosHoy;
    }

    public void setPasosHoy(int pasosHoy) {
        this.pasosHoy = pasosHoy;
    }

    public int getCalorias() {
        return calorias;
    }

    public void setCalorias(int calorias) {
        this.calorias = calorias;
    }

    public double getHorasSueno() {
        return horasSueno;
    }

    public void setHorasSueno(double horasSueno) {
        this.horasSueno = horasSueno;
    }
}
