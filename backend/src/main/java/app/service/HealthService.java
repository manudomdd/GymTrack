package app.service;

import app.entity.Client;
import app.entity.SleepLog;
import app.entity.StepLog;
import app.repository.SleepLogRepository;
import app.repository.StepLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Servicio para gestionar los registros de salud (sueño y pasos) de los usuarios.
 *
 * @author Manuel Dominguez
 * @version 1.0
 * @since 23/05/2026
 */
@Service
public class HealthService {

    // Repositorio para operaciones de persistencia de la entidad Sleep.
    @Autowired
    private SleepLogRepository sleepRepo;

    // Repositorio para operaciones de persistencia de la entidad Step.
    @Autowired
    private StepLogRepository stepRepo;

    /**
     * Registra y persiste un nuevo registro en el sistema.
     *
     * @param log Registro de salud o biomarcador.
     * @return El resultado o estado devuelto tras procesar la petición.
     */
    public SleepLog saveSleepLog(SleepLog log) {
        Optional<SleepLog> existing = sleepRepo.findByClientIdAndDate(log.getClient().getId(), log.getDate());
        if (existing.isPresent()) {
            SleepLog e = existing.get();
            e.setHoursSlept(log.getHoursSlept());
            e.setScore(log.getScore());
            return sleepRepo.save(e);
        }
        return sleepRepo.save(log);
    }

    /**
     * Registra y persiste un nuevo registro en el sistema.
     *
     * @param log Registro de salud o biomarcador.
     * @return El resultado o estado devuelto tras procesar la petición.
     */
    public StepLog saveStepLog(StepLog log) {
        Optional<StepLog> existing = stepRepo.findByClientIdAndDate(log.getClient().getId(), log.getDate());
        if (existing.isPresent()) {
            StepLog e = existing.get();
            e.setSteps(log.getSteps()); // Se sobreescribe con el total del día calculado en el cliente
            return stepRepo.save(e);
        }
        return stepRepo.save(log);
    }

    /**
     * Recupera el valor actual de sleeplogs.
     *
     * @param userId Identificador único del usuario o cliente asociado.
     * @return El resultado o estado devuelto tras procesar la petición.
     */
    public List<SleepLog> getSleepLogs(Long userId) {
        return sleepRepo.findByClientId(userId);
    }

    /**
     * Recupera el valor actual de steplogs.
     *
     * @param userId Identificador único del usuario o cliente asociado.
     * @return El resultado o estado devuelto tras procesar la petición.
     */
    public List<StepLog> getStepLogs(Long userId) {
        return stepRepo.findByClientId(userId);
    }
}
