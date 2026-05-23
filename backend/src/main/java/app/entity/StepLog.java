package app.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * Entidad de persistencia para el registro diario de pasos del cliente.
 *
 * @author Manuel Dominguez
 * @version 1.0
 * @since 23/05/2026
 */
@Entity
@Table(name = "step_logs")
public class StepLog {

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
    // Número total de pasos caminados en el día.
    private int steps;

    /**
     * Constructor de la clase StepLog con inyección de dependencias.
     *
     */
    public StepLog() {
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
     * Recupera el valor actual de steps.
     *
     * @return El resultado o estado devuelto tras procesar la petición.
     */
    public int getSteps() {
        return steps;
    }

    /**
     * Establece el valor de steps.
     *
     * @param steps Parámetro de entrada para la operación.
     */
    public void setSteps(int steps) {
        this.steps = steps;
    }
}
