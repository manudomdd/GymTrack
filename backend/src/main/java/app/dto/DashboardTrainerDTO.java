package app.dto;

/**
 * Objeto de transferencia de datos para el dashboard del entrenador.
 *
 * @author Manuel Dominguez
 * @version 1.0
 * @since 23/05/2026
 */
public class DashboardTrainerDTO {
    // Atributo de tipo int para almacenar clientesTotales.
    private int clientesTotales;
    // Vista de imagen (ImageView) para visualizar el/la actoshoy.
    private int activosHoy;
    // Atributo de tipo int para almacenar entrenamientos.
    private int entrenamientos;
    // Atributo de tipo int para almacenar estaSemana.
    private int estaSemana;

    /**
     * Constructor de la clase DashboardTrainerDTO con inyección de dependencias.
     *
     */
    public DashboardTrainerDTO() {}

    /**
     * Constructor de la clase DashboardTrainerDTO con inyección de dependencias.
     *
     * @param clientesTotales Parámetro de entrada para la operación.
     * @param activosHoy Parámetro de entrada para la operación.
     * @param entrenamientos Parámetro de entrada para la operación.
     * @param estaSemana Parámetro de entrada para la operación.
     */
    public DashboardTrainerDTO(int clientesTotales, int activosHoy, int entrenamientos, int estaSemana) {
        this.clientesTotales = clientesTotales;
        this.activosHoy = activosHoy;
        this.entrenamientos = entrenamientos;
        this.estaSemana = estaSemana;
    }

    /**
     * Recupera el valor actual de clientestotales.
     *
     * @return El resultado o estado devuelto tras procesar la petición.
     */
    public int getClientesTotales() {
        return clientesTotales;
    }

    /**
     * Establece el valor de clientestotales.
     *
     * @param clientesTotales Parámetro de entrada para la operación.
     */
    public void setClientesTotales(int clientesTotales) {
        this.clientesTotales = clientesTotales;
    }

    /**
     * Recupera el valor actual de activoshoy.
     *
     * @return El resultado o estado devuelto tras procesar la petición.
     */
    public int getActivosHoy() {
        return activosHoy;
    }

    /**
     * Establece el valor de activoshoy.
     *
     * @param activosHoy Parámetro de entrada para la operación.
     */
    public void setActivosHoy(int activosHoy) {
        this.activosHoy = activosHoy;
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
     * Recupera el valor actual de estasemana.
     *
     * @return El resultado o estado devuelto tras procesar la petición.
     */
    public int getEstaSemana() {
        return estaSemana;
    }

    /**
     * Establece el valor de estasemana.
     *
     * @param estaSemana Parámetro de entrada para la operación.
     */
    public void setEstaSemana(int estaSemana) {
        this.estaSemana = estaSemana;
    }
}
