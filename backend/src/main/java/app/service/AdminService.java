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

    @Transactional
    public void deleteTrainer(Long id) {
        Trainer trainer = trainerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Entrenador no encontrado"));
        
        // Desvincular clientes asociados
        List<Client> clients = trainer.getClients();
        if (clients != null) {
            for (Client client : clients) {
                client.setTrainer(null);
                clientRepository.save(client);
            }
        }
        
        // Eliminar entrenador
        trainerRepository.delete(trainer);
    }

    @Transactional
    public void deleteClient(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));

        // Eliminar registros asociados para evitar violación de claves foráneas
        workoutSessionRepository.deleteAllByClientId(id);
        sleepLogRepository.deleteAllByClientId(id);
        stepLogRepository.deleteAllByClientId(id);
        
        // Eliminar cliente
        clientRepository.delete(client);
    }
}
