package app.service;

import app.dto.UserAdminDTO;
import app.entity.Client;
import app.entity.Trainer;
import app.entity.User;
import app.repository.ClientRepository;
import app.repository.SleepLogRepository;
import app.repository.StepLogRepository;
import app.repository.TrainerRepository;
import app.repository.UserRepository;
import app.repository.WorkoutSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio que centraliza la lógica de negocio del panel de administración.
 * <p>
 * Expone operaciones para consultar y eliminar usuarios del sistema.
 * Las operaciones de borrado están envueltas en transacciones para garantizar
 * la integridad referencial: antes de eliminar cualquier entidad se limpian
 * los registros dependientes que de otro modo generarían violaciones de
 * clave foránea en base de datos.
 * </p>
 *
 * @author Manuel Dominguez
 * @version 1.0
 * @since 26/05/2025
 */
@Service
public class AdminService {

    private final UserRepository userRepository;
    private final TrainerRepository trainerRepository;
    private final ClientRepository clientRepository;
    private final WorkoutSessionRepository workoutSessionRepository;
    private final SleepLogRepository sleepLogRepository;
    private final StepLogRepository stepLogRepository;

    @Autowired
    public AdminService(UserRepository userRepository,
                        TrainerRepository trainerRepository,
                        ClientRepository clientRepository,
                        WorkoutSessionRepository workoutSessionRepository,
                        SleepLogRepository sleepLogRepository,
                        StepLogRepository stepLogRepository) {
        this.userRepository = userRepository;
        this.trainerRepository = trainerRepository;
        this.clientRepository = clientRepository;
        this.workoutSessionRepository = workoutSessionRepository;
        this.sleepLogRepository = sleepLogRepository;
        this.stepLogRepository = stepLogRepository;
    }

    /**
     * Devuelve la lista de todos los usuarios registrados en el sistema,
     * excluyendo las cuentas con rol {@code ADMIN} para evitar que el
     * administrador pueda eliminarse a sí mismo desde el panel.
     *
     * @return lista de {@link UserAdminDTO} con los datos básicos de cada usuario
     */
    public List<UserAdminDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .filter(user -> !user.getTipoUsuario().name().equals("ADMIN"))
                .map(user -> new UserAdminDTO(
                        user.getId(),
                        user.getNombre(),
                        user.getUsername(),
                        user.getTipoUsuario()
                ))
                .collect(Collectors.toList());
    }

    /**
     * Elimina un entrenador del sistema de forma segura.
     * <p>
     * Antes de borrar el entrenador, desvincula a todos sus clientes
     * (establece su referencia al entrenador a {@code null}) para mantener
     * la coherencia de los datos sin eliminar las cuentas de cliente.
     * </p>
     *
     * @param id identificador del entrenador a eliminar
     * @throws IllegalArgumentException si no existe ningún entrenador con ese id
     */
    @Transactional
    public void deleteTrainer(Long id) {
        Trainer trainer = trainerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Entrenador no encontrado"));

        // Antes de borrar, se desvinculan los clientes para evitar
        // que queden con una referencia rota a un entrenador eliminado.
        List<Client> clients = trainer.getClients();
        if (clients != null) {
            for (Client client : clients) {
                client.setTrainer(null);
                clientRepository.save(client);
            }
        }

        trainerRepository.delete(trainer);
    }

    /**
     * Elimina un cliente del sistema junto con todos sus registros de actividad.
     * <p>
     * La eliminación se realiza en cascada manual para respetar las restricciones
     * de clave foránea: primero se borran las sesiones de entrenamiento, los
     * registros de sueño y los registros de pasos; después se elimina la cuenta.
     * </p>
     *
     * @param id identificador del cliente a eliminar
     * @throws IllegalArgumentException si no existe ningún cliente con ese id
     */
    @Transactional
    public void deleteClient(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));

        // Eliminar en orden los registros dependientes para respetar
        // las restricciones de integridad referencial de la base de datos.
        workoutSessionRepository.deleteAllByClientId(id);
        sleepLogRepository.deleteAllByClientId(id);
        stepLogRepository.deleteAllByClientId(id);

        clientRepository.delete(client);
    }
}
