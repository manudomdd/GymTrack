package app.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * Entidad JPA que representa el registro diario de sueño de un cliente.
 * <p>
 * Almacena información sobre las horas dormidas y una puntuación (score) 
 * que indica la calidad subjetiva del descanso en una fecha específica.
 * Se mapea a la tabla {@code sleep_logs} en la base de datos.
 * </p>
 *
 * @author Manuel Dominguez
 * @version 1.0
 * @since 26/05/2025
 */
@Entity
@Table(name = "sleep_logs")
public class SleepLog {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Client client;

    private LocalDate date;
    private int hoursSlept;
    private int score;

    public SleepLog() {
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

    public int getHoursSlept() {
        return hoursSlept;
    }

    public void setHoursSlept(int hoursSlept) {
        this.hoursSlept = hoursSlept;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
