package com.gymtrack.app.network;

import android.content.Context;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import com.gymtrack.app.network.dto.DashboardClientDTO;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Repositorio en el frontend para gestionar las peticiones de red relativas a los clientes.
 *
 * @author Manuel Dominguez
 * @version 1.0
 * @since 23/05/2026
 */
public class ClientRepository {
    // Atributo de tipo String para almacenar BASE_URL.
    private static final String BASE_URL = "http://10.0.2.2:8080/api/client";
    // Atributo de tipo MediaType para almacenar JSON.
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    // Cliente asociado a este registro.
    private final OkHttpClient client;
    // Atributo de tipo Gson para almacenar gson.
    private final Gson gson;
    // Repositorio para operaciones de persistencia de la entidad Auth.
    private final AuthRepository authRepository;

    /**
     * Constructor de la clase ClientRepository con inyección de dependencias.
     *
     * @param context Parámetro de entrada para la operación.
     */
    public ClientRepository(Context context) {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .build();
        this.gson = new Gson();
        this.authRepository = new AuthRepository(context);
    }

    public interface Callback<T> {
        void onSuccess(T result);
        void onError(String message);
    }

    /**
     * Recupera el valor actual de profile.
     *
     * @param callback Parámetro de entrada para la operación.
     */
    public void getProfile(Callback<JsonObject> callback) {
        new Thread(() -> {
            try {
                Request request = new Request.Builder()
                        .url(BASE_URL + "/profile")
                        .addHeader("Authorization", "Bearer " + authRepository.getToken())
                        .get()
                        .build();
                try (Response response = client.newCall(request).execute()) {
                    if (response.isSuccessful() && response.body() != null) {
                        JsonObject json = gson.fromJson(response.body().string(), JsonObject.class);
                        callback.onSuccess(json);
                    } else {
                        callback.onError("Error al obtener perfil");
                    }
                }
            } catch (IOException e) {
                callback.onError(e.getMessage());
            }
        }).start();
    }

    /**
     * Actualiza los datos del registro en la base de datos.
     *
     * @param data Parámetro de entrada para la operación.
     * @param callback Parámetro de entrada para la operación.
     */
    public void updateProfile(JsonObject data, Callback<Void> callback) {
        new Thread(() -> {
            try {
                RequestBody body = RequestBody.create(data.toString(), JSON);
                Request request = new Request.Builder()
                        .url(BASE_URL + "/profile")
                        .addHeader("Authorization", "Bearer " + authRepository.getToken())
                        .put(body)
                        .build();
                try (Response response = client.newCall(request).execute()) {
                    if (response.isSuccessful()) {
                        callback.onSuccess(null);
                    } else {
                        callback.onError("Error al actualizar perfil");
                    }
                }
            } catch (IOException e) {
                callback.onError(e.getMessage());
            }
        }).start();
    }

    /**
     * Procesa la operación correspondiente para linkTrainer.
     *
     * @param code Código único de vinculación del entrenador.
     * @param callback Parámetro de entrada para la operación.
     */
    public void linkTrainer(String code, Callback<String> callback) {
        new Thread(() -> {
            try {
                RequestBody emptyBody = RequestBody.create("", null);
                Request request = new Request.Builder()
                        .url(BASE_URL + "/link-trainer/" + code)
                        .addHeader("Authorization", "Bearer " + authRepository.getToken())
                        .post(emptyBody)
                        .build();
                try (Response response = client.newCall(request).execute()) {
                    String respBody = response.body() != null ? response.body().string() : "";
                    if (response.isSuccessful()) {
                        callback.onSuccess(respBody);
                    } else {
                        callback.onError(respBody.isEmpty() ? "Código inválido" : respBody);
                    }
                }
            } catch (IOException e) {
                callback.onError(e.getMessage());
            }
        }).start();
    }

    public interface ClientDashboardCallback {
        void onSuccess(DashboardClientDTO dashboard);
        void onError(String message);
    }

    /**
     * Recupera el valor actual de clientdashboard.
     *
     * @param callback Parámetro de entrada para la operación.
     */
    public void getClientDashboard(ClientDashboardCallback callback) {
        new Thread(() -> {
            try {
                Request request = new Request.Builder()
                        .url(BASE_URL + "/dashboard")
                        .addHeader("Authorization", "Bearer " + authRepository.getToken())
                        .get()
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (response.isSuccessful() && response.body() != null) {
                        DashboardClientDTO dashboard = gson.fromJson(response.body().string(), DashboardClientDTO.class);
                        callback.onSuccess(dashboard);
                    } else {
                        callback.onError("Error al obtener dashboard: " + response.code());
                    }
                }
            } catch (IOException e) {
                callback.onError(e.getMessage());
            }
        }).start();
    }
}
