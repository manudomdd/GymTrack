package app.service;

import app.entity.SleepLog;
import app.entity.StepLog;
import app.entity.User;
import app.entity.WorkoutSession;
import app.repository.UserRepository;
import app.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Servicio de integración con la IA para recomendaciones personalizadas de entrenamiento y bienestar.
 *
 * @author Manuel Dominguez
 * @version 1.0
 * @since 23/05/2026
 */
@Service
public class IAService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private WorkoutService workoutService;

    @Autowired
    private HealthService healthService;

    private final RestTemplate restTemplate = new RestTemplate();

    public String generateAIResponse(Long clientId, String trainerQuery) {
        Optional<app.entity.Client> clientOpt = clientRepository.findById(clientId);
        if (!clientOpt.isPresent()) {
            return "Error: Cliente no encontrado.";
        }
        app.entity.Client client = clientOpt.get();

        // 1. Recopilar datos básicos
        String infoBasica = String.format("Nombre: %s, Edad: %d años, Peso: %.1f kg, Altura: %d cm, NEAT objetivo: %d kcal.",
                client.getNombre(), client.getEdad(), client.getPeso(), client.getAltura(), client.getNeat());

        // 2. Recopilar entrenamientos recientes (últimas 40 series)
        List<WorkoutSession> sessions = workoutService.getSessionsByUser(clientId);
        List<WorkoutSession> recentSessions = sessions.stream()
                .filter(s -> s.getDate() != null)
                .sorted((s1, s2) -> s2.getDate().compareTo(s1.getDate()))
                .limit(40)
                .sorted(Comparator.comparing(WorkoutSession::getDate)) // Volver a ordenar cronológicamente para el prompt
                .collect(Collectors.toList());

        StringBuilder entrenamientosStr = new StringBuilder();
        if (recentSessions.isEmpty()) {
            entrenamientosStr.append("No hay entrenamientos registrados recientemente.\n");
        } else {
            for (WorkoutSession s : recentSessions) {
                entrenamientosStr.append(String.format("- %s | %s (%s): Serie %d -> %.1f kg x %d reps (RIR: %d). Comentario: %s\n",
                        s.getDate(), s.getExercise(), s.getMuscleGroup(), s.getSeriesNumber(),
                        s.getPesoTotal() != null ? s.getPesoTotal() : 0.0, s.getReps(), s.getRir(), s.getComment() != null ? s.getComment() : "Ninguno"));
            }
        }

        // 3. Recopilar sueño (últimos 7 días)
        List<SleepLog> sleepLogs = healthService.getSleepLogs(clientId);
        List<SleepLog> recentSleep = sleepLogs.stream()
                .filter(s -> s.getDate() != null)
                .sorted((s1, s2) -> s2.getDate().compareTo(s1.getDate()))
                .limit(7)
                .sorted(Comparator.comparing(SleepLog::getDate))
                .collect(Collectors.toList());

        StringBuilder sleepStr = new StringBuilder();
        if (recentSleep.isEmpty()) {
            sleepStr.append("No hay registros de sueño recientes.\n");
        } else {
            for (SleepLog s : recentSleep) {
                sleepStr.append(String.format("- %s: %d horas, Score de calidad: %d/100\n",
                        s.getDate(), s.getHoursSlept(), s.getScore()));
            }
        }

        // 4. Recopilar pasos (últimos 7 días)
        List<StepLog> stepLogs = healthService.getStepLogs(clientId);
        List<StepLog> recentSteps = stepLogs.stream()
                .filter(s -> s.getDate() != null)
                .sorted((s1, s2) -> s2.getDate().compareTo(s1.getDate()))
                .limit(7)
                .sorted(Comparator.comparing(StepLog::getDate))
                .collect(Collectors.toList());

        StringBuilder stepsStr = new StringBuilder();
        if (recentSteps.isEmpty()) {
            stepsStr.append("No hay registros de pasos recientes.\n");
        } else {
            for (StepLog s : recentSteps) {
                stepsStr.append(String.format("- %s: %d pasos\n",
                        s.getDate(), s.getSteps()));
            }
        }

        // 5. Construir Prompt de Sistema
        String systemPrompt = String.format(
                "Eres un Asistente de IA experto en entrenamiento personal, biomecánica y salud para GymTrack.\n" +
                "Estás analizando la información de un cliente para ayudar a su entrenador personal a tomar mejores decisiones.\n\n" +
                "CONTEXTO DEL CLIENTE:\n" +
                "- Datos Físicos: %s\n\n" +
                "- Historial de Entrenamientos Recientes (Series individuales con carga y RIR):\n%s\n" +
                "- Registros de Sueño Recientes:\n%s\n" +
                "- Registros de Pasos Recientes:\n%s\n" +
                "INSTRUCCIONES:\n" +
                "1. Analiza con precisión la relación entre sus entrenamientos, RIR, volumen, fatiga acumulada, horas de sueño y nivel de pasos diarios.\n" +
                "2. Sé extremadamente conciso y directo. Evita introducciones innecesarias.\n" +
                "3. Responde de manera profesional y clara en español, dando consejos prácticos y objetivos basados en la evidencia científica.",
                infoBasica, entrenamientosStr.toString(), sleepStr.toString(), stepsStr.toString()
        );

        // 6. Enviar a Groq API
        String apiKey = System.getenv("GROQ_API_KEY");
        if (apiKey == null || apiKey.trim().isEmpty()) {
            // Mock de respuesta para facilitar pruebas si no hay API key configurada
            return "[Simulación IA - Sin API Key] Hola. Analizando los datos del cliente " + client.getNombre() + ": " +
                    "veo que tiene un sueño promedio de " +
                    (recentSleep.isEmpty() ? "N/D" : String.format("%.1f", recentSleep.stream().mapToInt(SleepLog::getHoursSlept).average().orElse(0.0))) + " horas. " +
                    "Sus pasos diarios promedian " +
                    (recentSteps.isEmpty() ? "N/D" : String.format("%.0f", recentSteps.stream().mapToInt(StepLog::getSteps).average().orElse(0.0))) + " pasos. " +
                    "Respecto a su consulta: '" + trainerQuery + "', le recomiendo ajustar la intensidad (RIR) de acuerdo con su calidad de sueño.";
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "llama-3.1-8b-instant");

            List<Map<String, String>> messages = new ArrayList<>();
            Map<String, String> systemMessage = new HashMap<>();
            systemMessage.put("role", "system");
            systemMessage.put("content", systemPrompt);
            messages.add(systemMessage);

            Map<String, String> userMessage = new HashMap<>();
            userMessage.put("role", "user");
            userMessage.put("content", trainerQuery);
            messages.add(userMessage);

            requestBody.put("messages", messages);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(
                    "https://api.groq.com/openai/v1/chat/completions",
                    entity,
                    Map.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
                if (choices != null && !choices.isEmpty()) {
                    Map<String, Object> choice = choices.get(0);
                    Map<String, String> message = (Map<String, String>) choice.get("message");
                    if (message != null) {
                        return message.get("content");
                    }
                }
            }
            return "Error al procesar la respuesta de la IA (status " + response.getStatusCode() + ").";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error en la conexión con la API de Groq: " + e.getMessage();
        }
    }

    public String generateGeneralAIResponse(String trainerQuery) {
        String systemPrompt = "Eres un asistente experto en ciencias del deporte y la salud, diseñado para ayudar a entrenadores personales con sus dudas de entrenamiento, nutrición, fatiga o cualquier otra consulta técnica.";

        String apiKey = System.getenv("GROQ_API_KEY");
        if (apiKey == null || apiKey.trim().isEmpty()) {
            return "[Simulación IA General - Sin API Key] Hola. Respecto a tu consulta general: '" + trainerQuery + "', como experto en ciencias del deporte, te recomiendo planificar la sobrecarga progresiva y asegurar una adecuada ingesta de macronutrientes adaptada al objetivo del deportista.";
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "llama-3.1-8b-instant");

            List<Map<String, String>> messages = new ArrayList<>();
            Map<String, String> systemMessage = new HashMap<>();
            systemMessage.put("role", "system");
            systemMessage.put("content", systemPrompt);
            messages.add(systemMessage);

            Map<String, String> userMessage = new HashMap<>();
            userMessage.put("role", "user");
            userMessage.put("content", trainerQuery);
            messages.add(userMessage);

            requestBody.put("messages", messages);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(
                    "https://api.groq.com/openai/v1/chat/completions",
                    entity,
                    Map.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
                if (choices != null && !choices.isEmpty()) {
                    Map<String, Object> choice = choices.get(0);
                    Map<String, String> message = (Map<String, String>) choice.get("message");
                    if (message != null) {
                        return message.get("content");
                    }
                }
            }
            return "Error al procesar la respuesta de la IA (status " + response.getStatusCode() + ").";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error en la conexión con la API de Groq: " + e.getMessage();
        }
    }
}
