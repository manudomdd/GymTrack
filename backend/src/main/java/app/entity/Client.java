package app.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.FetchType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import jakarta.persistence.Transient;

/**
 * Entidad que representa a un usuario con el rol de Cliente en la aplicación.
 *
 * @author Manuel Dominguez
 * @version 1.0
 * @since 23/05/2026
 */
@Entity
@Table(name = "clients")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Client extends User {

    // Fecha de nacimiento del usuario cliente.
    @jakarta.persistence.Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento; 
    
    // Peso corporal actual del cliente en kilogramos.
    private double peso; 
    // Altura del cliente en centímetros.
    private int altura;
    // Nivel de actividad física diaria no asociada al ejercicio (NEAT).
    private int neat; 

    // Entrenador personal asociado al cliente.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trainer_id")
    private Trainer trainer;

    /**
     * Constructor de la clase Client con inyección de dependencias.
     *
     */
    public Client() {
        super();
    }

    /**
     * Recupera el valor actual de fechanacimiento.
     *
     * @return El resultado o estado devuelto tras procesar la petición.
     */
    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    /**
     * Establece el valor de fechanacimiento.
     *
     * @param fechaNacimiento Parámetro de entrada para la operación.
     */
    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    /**
     * Recupera el valor actual de edad.
     *
     * @return El resultado o estado devuelto tras procesar la petición.
     */
    @Transient
    public int getEdad() {
        if (fechaNacimiento != null) {
            return Period.between(fechaNacimiento, LocalDate.now()).getYears();
        }
        return 0;
    }

    /**
     * Establece el valor de edad.
     *
     * @param edad Parámetro de entrada para la operación.
     */
    public void setEdad(int edad) {
        // Setter vacío porque edad es calculada dinámicamente
    }

    /**
     * Recupera el valor actual de peso.
     *
     * @return El resultado o estado devuelto tras procesar la petición.
     */
    public double getPeso() {
        return peso;
    }

    /**
     * Establece el valor de peso.
     *
     * @param peso Parámetro de entrada para la operación.
     */
    public void setPeso(double peso) {
        this.peso = peso;
    }

    /**
     * Recupera el valor actual de altura.
     *
     * @return El resultado o estado devuelto tras procesar la petición.
     */
    public int getAltura() {
        return altura;
    }

    /**
     * Establece el valor de altura.
     *
     * @param altura Parámetro de entrada para la operación.
     */
    public void setAltura(int altura) {
        this.altura = altura;
    }

    /**
     * Recupera el valor actual de neat.
     *
     * @return El resultado o estado devuelto tras procesar la petición.
     */
    public int getNeat() {
        return neat;
    }

    /**
     * Establece el valor de neat.
     *
     * @param neat Parámetro de entrada para la operación.
     */
    public void setNeat(int neat) {
        this.neat = neat;
    }

    /**
     * Recupera el valor actual de trainer.
     *
     * @return El resultado o estado devuelto tras procesar la petición.
     */
    public Trainer getTrainer() {
        return trainer;
    }

    /**
     * Establece el valor de trainer.
     *
     * @param trainer Parámetro de entrada para la operación.
     */
    public void setTrainer(Trainer trainer) {
        this.trainer = trainer;
    }
}
