package app.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * Entidad que representa una serie de ejercicio dentro de una sesión de entrenamiento.
 *
 * @author Manuel Dominguez
 * @version 1.0
 * @since 23/05/2026
 */
@Entity
@Table(name = "workout_sessions")
public class WorkoutSession {

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
    // Nombre específico del ejercicio realizado.
    private String exercise;
    // Grupo muscular principal asociado al ejercicio.
    private String muscleGroup; // "Pecho", "Espalda", etc.

    /**
     * Número ordinal de la serie dentro de la sesión del día.
     * Columna mantenida como 'sets' en BD para no romper el esquema existente.
     * Ej: Serie 1, Serie 2, Serie 3...
     */
    @Column(name = "sets")
    private int seriesNumber;

    // Número de repeticiones completadas con éxito en la serie.
    private int reps;
    // Repeticiones en reserva (RIR) estimadas por el cliente.
    private int rir;
    // Carga total levantada en kilogramos para la serie.
    @Column(name = "peso_total")
    private Double pesoTotal; // Carga utilizada
    // Comentario opcional provisto por el cliente sobre la serie.
    private String comment;

    // Comentario de feedback técnico provisto por el entrenador.
    @Column(name = "feedback_entrenador")
    private String feedbackEntrenador;

    /**
     * Constructor de la clase WorkoutSession con inyección de dependencias.
     *
     */
    public WorkoutSession() {
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
     * Recupera el valor actual de exercise.
     *
     * @return El resultado o estado devuelto tras procesar la petición.
     */
    public String getExercise() {
        return exercise;
    }

    /**
     * Establece el valor de exercise.
     *
     * @param exercise Parámetro de entrada para la operación.
     */
    public void setExercise(String exercise) {
        this.exercise = exercise;
    }

    /**
     * Recupera el valor actual de musclegroup.
     *
     * @return El resultado o estado devuelto tras procesar la petición.
     */
    public String getMuscleGroup() {
        return muscleGroup;
    }

    /**
     * Establece el valor de musclegroup.
     *
     * @param muscleGroup Parámetro de entrada para la operación.
     */
    public void setMuscleGroup(String muscleGroup) {
        this.muscleGroup = muscleGroup;
    }

    /**
     * Recupera el valor actual de seriesnumber.
     *
     * @return El resultado o estado devuelto tras procesar la petición.
     */
    public int getSeriesNumber() {
        return seriesNumber;
    }

    /**
     * Establece el valor de seriesnumber.
     *
     * @param seriesNumber Parámetro de entrada para la operación.
     */
    public void setSeriesNumber(int seriesNumber) {
        this.seriesNumber = seriesNumber;
    }

    /**
     * Recupera el valor actual de reps.
     *
     * @return El resultado o estado devuelto tras procesar la petición.
     */
    public int getReps() {
        return reps;
    }

    /**
     * Establece el valor de reps.
     *
     * @param reps Parámetro de entrada para la operación.
     */
    public void setReps(int reps) {
        this.reps = reps;
    }

    /**
     * Recupera el valor actual de rir.
     *
     * @return El resultado o estado devuelto tras procesar la petición.
     */
    public int getRir() {
        return rir;
    }

    /**
     * Establece el valor de rir.
     *
     * @param rir Parámetro de entrada para la operación.
     */
    public void setRir(int rir) {
        this.rir = rir;
    }

    /**
     * Recupera el valor actual de pesototal.
     *
     * @return El resultado o estado devuelto tras procesar la petición.
     */
    public Double getPesoTotal() {
        return pesoTotal;
    }

    /**
     * Establece el valor de pesototal.
     *
     * @param pesoTotal Parámetro de entrada para la operación.
     */
    public void setPesoTotal(Double pesoTotal) {
        this.pesoTotal = pesoTotal;
    }

    /**
     * Recupera el valor actual de comment.
     *
     * @return El resultado o estado devuelto tras procesar la petición.
     */
    public String getComment() {
        return comment;
    }

    /**
     * Establece el valor de comment.
     *
     * @param comment Parámetro de entrada para la operación.
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * Recupera el valor actual de feedbackentrenador.
     *
     * @return El resultado o estado devuelto tras procesar la petición.
     */
    public String getFeedbackEntrenador() {
        return feedbackEntrenador;
    }

    /**
     * Establece el valor de feedbackentrenador.
     *
     * @param feedbackEntrenador Parámetro de entrada para la operación.
     */
    public void setFeedbackEntrenador(String feedbackEntrenador) {
        this.feedbackEntrenador = feedbackEntrenador;
    }

}
