package com.gymtrack.app.network;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import com.gymtrack.app.network.dto.DashboardTrainerDTO;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Repositorio central de autenticación y red.
 * <p>
 * Gestiona el flujo crítico de sesión (Login, Registro y Manejo de JWT) utilizando 
 * {@link android.content.SharedPreferences} para la persistencia local del token y el rol de usuario.
 * Además, actúa de forma temporal como proveedor de llamadas HTTP generalistas para 
 * los módulos de entrenador y progreso, ejecutando las peticiones de red asíncronamente 
 * mediante OkHttp3 para no bloquear el hilo principal (UI Thread).
 * </p>
 *
 * @author Manuel Dominguez
 * @version 1.0
 * @since 26/05/2025
 */
public class AuthRepository {


    private static final String BASE_URL = "https://gymtrack-production-5934.up.railway.app/api/auth";
    private static final String PREFS_NAME = "gymtrack_prefs";
    private static final String KEY_TOKEN = "token";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private final OkHttpClient client;
    private final Gson gson;
    private final SharedPreferences prefs;

    public AuthRepository(Context context) {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .build();
        this.gson = new Gson();
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    /** Interfaz de callback para operaciones asíncronas */
    public interface AuthCallback {
        void onSuccess();
        void onError(String message);
    }

    /**
     * Permite al usuario iniciar sesión con username y contraseña.
     * Si tiene éxito, guarda el JWT en SharedPreferences.
     * Se ejecuta en un hilo separado; el callback corre en el hilo llamante.
     */
    public void login(String username, String password, AuthCallback callback) {
        new Thread(() -> {
            try {
                JsonObject body = new JsonObject();
                body.addProperty("username", username);
                body.addProperty("password", password);

                RequestBody requestBody = RequestBody.create(body.toString(), JSON);
                Request request = new Request.Builder()
                        .url(BASE_URL + "/login")
                        .post(requestBody)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (response.isSuccessful() && response.body() != null) {
                        String responseBody = response.body().string();
                        JsonObject jsonResponse = gson.fromJson(responseBody, JsonObject.class);
                        if (jsonResponse.has("token")) {
                            String token = jsonResponse.get("token").getAsString();
                            prefs.edit().putString(KEY_TOKEN, token).apply();
                        }
                        if (jsonResponse.has("tipoUsuario") && !jsonResponse.get("tipoUsuario").isJsonNull()) {
                            String role = jsonResponse.get("tipoUsuario").getAsString();
                            prefs.edit().putString("role", role).apply();
                        } else {
                            prefs.edit().putString("role", "CLIENTE").apply();
                        }
                        callback.onSuccess();
                    } else {
                        callback.onError("Credenciales inválidas");
                    }
                }
            } catch (IOException e) {
                callback.onError("Error de conexión: " + e.getMessage());
            }
        }).start();
    }

    /**
     * Registra un nuevo usuario con los datos proporcionados.
     * userData debe contener: nombre, username, password, peso, altura, neat, tipoUsuario
     */
    public void register(Map<String, Object> userData, AuthCallback callback) {
        new Thread(() -> {
            try {
                String bodyJson = gson.toJson(userData);
                RequestBody requestBody = RequestBody.create(bodyJson, JSON);
                Request request = new Request.Builder()
                        .url(BASE_URL + "/register")
                        .post(requestBody)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (response.isSuccessful()) {
                        callback.onSuccess();
                    } else {
                        callback.onError("Error en el registro. El username puede que ya exista.");
                    }
                }
            } catch (IOException e) {
                callback.onError("Error de conexión: " + e.getMessage());
            }
        }).start();
    }

    /** Elimina el token guardado (logout local) */
    public void clearToken() {
        prefs.edit().remove(KEY_TOKEN).apply();
    }

    /** Devuelve el token JWT almacenado, o null si no existe */
    public String getToken() {
        return prefs.getString(KEY_TOKEN, null);
    }

    /** Devuelve el rol del usuario (CLIENTE o ENTRENADOR) */
    public String getRole() {
        return prefs.getString("role", "CLIENTE");
    }

    public interface ProgressCallback {
        void onSuccess(Map<String, Double> metrics);
        void onError(String message);
    }

    public void getClientProgress(long clientId, ProgressCallback callback) {
        new Thread(() -> {
            try {
                Request request = new Request.Builder()
                        .url("https://gymtrack-production-5934.up.railway.app/api/trainer/client/" + clientId + "/progress")
                        .addHeader("Authorization", "Bearer " + getToken())
                        .get()
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (response.isSuccessful() && response.body() != null) {
                        Type type = new com.google.gson.reflect.TypeToken<Map<String, Double>>(){}.getType();
                        Map<String, Double> metrics = gson.fromJson(response.body().string(), type);
                        callback.onSuccess(metrics);
                    } else {
                        callback.onError("Error al obtener métricas");
                    }
                }
            } catch (IOException e) {
                callback.onError("Error de conexión: " + e.getMessage());
            }
        }).start();
    }

    /** Callback para obtener los datos de salud de un cliente */
    public interface HealthCallback {
        void onSuccess(JsonObject health);
        void onError(String message);
    }

    /**
     * Obtiene los registros de salud (sueño y pasos) de un cliente vinculado.
     * Endpoint: GET /api/trainer/client/{clientId}/health
     */
    public void getClientHealth(long clientId, HealthCallback callback) {
        new Thread(() -> {
            try {
                Request request = new Request.Builder()
                        .url("https://gymtrack-production-5934.up.railway.app/api/trainer/client/" + clientId + "/health")
                        .addHeader("Authorization", "Bearer " + getToken())
                        .get()
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (response.isSuccessful() && response.body() != null) {
                        JsonObject health = gson.fromJson(response.body().string(), JsonObject.class);
                        callback.onSuccess(health);
                    } else {
                        callback.onError("Error al obtener datos de salud: " + response.code());
                    }
                }
            } catch (IOException e) {
                callback.onError("Error de conexión: " + e.getMessage());
            }
        }).start();
    }

    public interface TrainerDashboardCallback {
        void onSuccess(DashboardTrainerDTO dashboard);
        void onError(String message);
    }

    public void getTrainerDashboard(TrainerDashboardCallback callback) {
        new Thread(() -> {
            try {
                Request request = new Request.Builder()
                        .url("https://gymtrack-production-5934.up.railway.app/api/trainer/dashboard")
                        .addHeader("Authorization", "Bearer " + getToken())
                        .get()
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (response.isSuccessful() && response.body() != null) {
                        DashboardTrainerDTO dashboard = gson.fromJson(response.body().string(), DashboardTrainerDTO.class);
                        callback.onSuccess(dashboard);
                    } else {
                        callback.onError("Error al obtener dashboard: " + response.code());
                    }
                }
            } catch (IOException e) {
                callback.onError("Error de conexión: " + e.getMessage());
            }
        }).start();
    }
}

