package app.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * Entidad de persistencia para el registro diario de las horas y calidad de sueño.
 *
 * @author Manuel Dominguez
 * @version 1.0
 * @since 23/05/2026
 */
@Entity
@Table(name = "sleep_logs")
public class SleepLog {

    // Identificador único autoincremental de la entidad.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Cliente asociado a este registro.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Client client;

    // Fecha en la que se realiza el registro diario.
    private LocalDate date;
    // Cantidad de horas totales dormidas por el cliente.
    private int hoursSlept;
    // Calificación de la calidad del sueño (escala del 1 al 10).
    private int score;

    /**
     * Constructor de la clase SleepLog con inyección de dependencias.
     *
     */
    public SleepLog() {
    }

    /**
     * Recupera el valor actual de id.
     *
     * @return El resultado o estado devuelto tras procesar la petición.
     */
    public Long getId() {
        return id;
    }

    /**
     * Establece el valor de id.
     *
     * @param id Parámetro de entrada para la operación.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Recupera el valor actual de client.
     *
     * @return El resultado o estado devuelto tras procesar la petición.
     */
    public Client getClient() {
        return client;
    }

    /**
     * Establece el valor de client.
     *
     * @param client Parámetro de entrada para la operación.
     */
    public void setClient(Client client) {
        this.client = client;
    }

    /**
     * Recupera el valor actual de date.
     *
     * @return El resultado o estado devuelto tras procesar la petición.
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Establece el valor de date.
     *
     * @param date Parámetro de entrada para la operación.
     */
    public void setDate(LocalDate date) {
        this.date = date;
    }

    /**
     * Recupera el valor actual de hoursslept.
     *
     * @return El resultado o estado devuelto tras procesar la petición.
     */
    public int getHoursSlept() {
        return hoursSlept;
    }

    /**
     * Establece el valor de hoursslept.
     *
     * @param hoursSlept Parámetro de entrada para la operación.
     */
    public void setHoursSlept(int hoursSlept) {
        this.hoursSlept = hoursSlept;
    }

    /**
     * Recupera el valor actual de score.
     *
     * @return El resultado o estado devuelto tras procesar la petición.
     */
    public int getScore() {
        return score;
    }

    /**
     * Establece el valor de score.
     *
     * @param score Parámetro de entrada para la operación.
     */
    public void setScore(int score) {
        this.score = score;
    }
}
