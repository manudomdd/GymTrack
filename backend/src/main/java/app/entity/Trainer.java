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

    @jakarta.persistence.Column(unique = true)
    private String trainerCode;

    @OneToMany(mappedBy = "trainer", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Client> clients;

    public Trainer() {
        super();
    }

    public String getTrainerCode() {
        return trainerCode;
    }

    public void setTrainerCode(String trainerCode) {
        this.trainerCode = trainerCode;
    }

    public List<Client> getClients() {
        return clients;
    }

    public void setClients(List<Client> clients) {
        this.clients = clients;
    }
}
