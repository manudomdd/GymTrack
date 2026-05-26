package app.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * Entidad JPA que representa el registro diario de actividad física (pasos) de un cliente.
 * <p>
 * Almacena el número total de pasos (steps) registrados durante una fecha determinada.
 * Esta entidad permite generar gráficas y estadísticas de movimiento diario.
 * Se mapea a la tabla {@code step_logs} en la base de datos.
 * </p>
 *
 * @author Manuel Dominguez
 * @version 1.0
 * @since 26/05/2025
 */
@Entity
@Table(name = "step_logs")
public class StepLog {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Client client;

    private LocalDate date;
    private int steps;

    public StepLog() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }
}
