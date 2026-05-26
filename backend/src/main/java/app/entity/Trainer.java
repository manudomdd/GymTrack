package app.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.OneToMany;
import jakarta.persistence.FetchType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Entidad JPA que representa a un entrenador personal en la aplicación.
 * <p>
 * Hereda de la entidad {@link User} y sus datos específicos se almacenan en 
 * la tabla {@code trainers}. Un entrenador se identifica unívocamente por su 
 * código ({@code trainerCode}), el cual los clientes utilizan durante el 
 * proceso de registro para vincularse a su perfil.
 * </p>
 *
 * @author Manuel Dominguez
 * @version 1.0
 * @since 26/05/2025
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
