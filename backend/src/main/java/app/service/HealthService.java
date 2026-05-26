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
 * Servicio encargado de gestionar los registros diarios de salud y bienestar.
 * <p>
 * Proporciona métodos transaccionales para guardar, actualizar y consultar
 * las métricas de sueño (horas dormidas, calidad) y actividad física (pasos) 
 * de los clientes, asegurando que solo exista un registro por fecha y usuario.
 * </p>
 *
 * @author Manuel Dominguez
 * @version 1.0
 * @since 26/05/2025
 */
@Service
public class HealthService {


    @Autowired
    private SleepLogRepository sleepRepo;

    @Autowired
    private StepLogRepository stepRepo;

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

    public StepLog saveStepLog(StepLog log) {
        Optional<StepLog> existing = stepRepo.findByClientIdAndDate(log.getClient().getId(), log.getDate());
        if (existing.isPresent()) {
            StepLog e = existing.get();
            e.setSteps(log.getSteps()); // Se sobreescribe con el total del día calculado en el cliente
            return stepRepo.save(e);
        }
        return stepRepo.save(log);
    }

    public List<SleepLog> getSleepLogs(Long userId) {
        return sleepRepo.findByClientId(userId);
    }

    public List<StepLog> getStepLogs(Long userId) {
        return stepRepo.findByClientId(userId);
    }
}
