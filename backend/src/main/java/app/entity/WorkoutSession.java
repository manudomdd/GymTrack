package app.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * Entidad JPA que representa una sesión de entrenamiento individual de un cliente.
 * <p>
 * Cada registro corresponde a una serie específica de un ejercicio, almacenando 
 * datos como repeticiones, RIR (Repeticiones en Reserva) y carga utilizada (peso total). 
 * Contiene además un campo para el feedback opcional del entrenador.
 * Se mapea en la base de datos a través de la tabla {@code workout_sessions}.
 * </p>
 *
 * @author Manuel Dominguez
 * @version 1.0
 * @since 26/05/2025
 */
@Entity
@Table(name = "workout_sessions")
public class WorkoutSession {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Client client;

    private LocalDate date;
    private String exercise;
    private String muscleGroup; // "Pecho", "Espalda", etc.

    /**
     * Número ordinal de la serie dentro de la sesión del día.
     * Columna mantenida como 'sets' en BD para no romper el esquema existente.
     * Ej: Serie 1, Serie 2, Serie 3...
     */
    @Column(name = "sets")
    private int seriesNumber;

    private int reps;
    private int rir;
    @Column(name = "peso_total")
    private Double pesoTotal; // Carga utilizada
    private String comment;

    @Column(name = "feedback_entrenador")
    private String feedbackEntrenador;

    public WorkoutSession() {
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

    public String getExercise() {
        return exercise;
    }

    public void setExercise(String exercise) {
        this.exercise = exercise;
    }

    public String getMuscleGroup() {
        return muscleGroup;
    }

    public void setMuscleGroup(String muscleGroup) {
        this.muscleGroup = muscleGroup;
    }

    public int getSeriesNumber() {
        return seriesNumber;
    }

    public void setSeriesNumber(int seriesNumber) {
        this.seriesNumber = seriesNumber;
    }

    public int getReps() {
        return reps;
    }

    public void setReps(int reps) {
        this.reps = reps;
    }

    public int getRir() {
        return rir;
    }

    public void setRir(int rir) {
        this.rir = rir;
    }

    public Double getPesoTotal() {
        return pesoTotal;
    }

    public void setPesoTotal(Double pesoTotal) {
        this.pesoTotal = pesoTotal;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getFeedbackEntrenador() {
        return feedbackEntrenador;
    }

    public void setFeedbackEntrenador(String feedbackEntrenador) {
        this.feedbackEntrenador = feedbackEntrenador;
    }

}
