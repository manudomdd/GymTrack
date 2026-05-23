package app.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.OneToMany;
import jakarta.persistence.FetchType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Entidad que representa a un usuario con el rol de Entrenador en la aplicación.
 *
 * @author Manuel Dominguez
 * @version 1.0
 * @since 23/05/2026
 */
@Entity
@Table(name = "trainers")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Trainer extends User {

    // Código de vinculación único generado para el entrenador.
    @jakarta.persistence.Column(unique = true)
    private String trainerCode;

    // Listado de clientes vinculados a este entrenador.
    @OneToMany(mappedBy = "trainer", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Client> clients;

    /**
     * Constructor de la clase Trainer con inyección de dependencias.
     *
     */
    public Trainer() {
        super();
    }

    /**
     * Recupera el valor actual de trainercode.
     *
     * @return El resultado o estado devuelto tras procesar la petición.
     */
    public String getTrainerCode() {
        return trainerCode;
    }

    /**
     * Establece el valor de trainercode.
     *
     * @param trainerCode Parámetro de entrada para la operación.
     */
    public void setTrainerCode(String trainerCode) {
        this.trainerCode = trainerCode;
    }

    /**
     * Recupera el valor actual de clients.
     *
     * @return El resultado o estado devuelto tras procesar la petición.
     */
    public List<Client> getClients() {
        return clients;
    }

    /**
     * Establece el valor de clients.
     *
     * @param clients Parámetro de entrada para la operación.
     */
    public void setClients(List<Client> clients) {
        this.clients = clients;
    }
}
