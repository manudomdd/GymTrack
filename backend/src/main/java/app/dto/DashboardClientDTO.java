package app.dto;

/**
 * Objeto de transferencia de datos para el dashboard del cliente.
 *
 * @author Manuel Dominguez
 * @version 1.0
 * @since 23/05/2026
 */
public class DashboardClientDTO {
    // Atributo de tipo int para almacenar entrenamientos.
    private int entrenamientos;
    // Atributo de tipo int para almacenar pasosHoy.
    private int pasosHoy;
    // Atributo de tipo int para almacenar calorias.
    private int calorias;
    // Atributo de tipo double para almacenar horasSueno.
    private double horasSueno;

    /**
     * Constructor de la clase DashboardClientDTO con inyección de dependencias.
     *
     */
    public DashboardClientDTO() {}

    /**
     * Constructor de la clase DashboardClientDTO con inyección de dependencias.
     *
     * @param entrenamientos Parámetro de entrada para la operación.
     * @param pasosHoy Parámetro de entrada para la operación.
     * @param calorias Parámetro de entrada para la operación.
     * @param horasSueno Parámetro de entrada para la operación.
     */
    public DashboardClientDTO(int entrenamientos, int pasosHoy, int calorias, double horasSueno) {
        this.entrenamientos = entrenamientos;
        this.pasosHoy = pasosHoy;
        this.calorias = calorias;
        this.horasSueno = horasSueno;
    }

    /**
     * Recupera el valor actual de entrenamientos.
     *
     * @return El resultado o estado devuelto tras procesar la petición.
     */
    public int getEntrenamientos() {
        return entrenamientos;
    }

    /**
     * Establece el valor de entrenamientos.
     *
     * @param entrenamientos Parámetro de entrada para la operación.
     */
    public void setEntrenamientos(int entrenamientos) {
        this.entrenamientos = entrenamientos;
    }

    /**
     * Recupera el valor actual de pasoshoy.
     *
     * @return El resultado o estado devuelto tras procesar la petición.
     */
    public int getPasosHoy() {
        return pasosHoy;
    }

    /**
     * Establece el valor de pasoshoy.
     *
     * @param pasosHoy Parámetro de entrada para la operación.
     */
    public void setPasosHoy(int pasosHoy) {
        this.pasosHoy = pasosHoy;
    }

    /**
     * Recupera el valor actual de calorias.
     *
     * @return El resultado o estado devuelto tras procesar la petición.
     */
    public int getCalorias() {
        return calorias;
    }

    /**
     * Establece el valor de calorias.
     *
     * @param calorias Parámetro de entrada para la operación.
     */
    public void setCalorias(int calorias) {
        this.calorias = calorias;
    }

    /**
     * Recupera el valor actual de horassueno.
     *
     * @return El resultado o estado devuelto tras procesar la petición.
     */
    public double getHorasSueno() {
        return horasSueno;
    }

    /**
     * Establece el valor de horassueno.
     *
     * @param horasSueno Parámetro de entrada para la operación.
     */
    public void setHorasSueno(double horasSueno) {
        this.horasSueno = horasSueno;
    }
}
