package com.gymtrack.app.network;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Repositorio para gestionar las llamadas HTTP del panel de administrador.
 */
public class AdminRepository {

    private static final String BASE_URL = "https://gymtrack-production-5934.up.railway.app/api/admin";
    private final OkHttpClient client;
    private final Gson gson;
    private final AuthRepository authRepository;

    public AdminRepository(Context context) {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();
        this.gson = new Gson();
        this.authRepository = new AuthRepository(context);
    }

    public interface UsersCallback {
        void onSuccess(List<Map<String, Object>> users);
        void onError(String message);
    }

    public interface ActionCallback {
        void onSuccess();
        void onError(String message);
    }

    public void getAllUsers(UsersCallback callback) {
        new Thread(() -> {
            try {
                Request request = new Request.Builder()
                        .url(BASE_URL + "/users")
                        .addHeader("Authorization", "Bearer " + authRepository.getToken())
                        .get()
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (response.isSuccessful() && response.body() != null) {
                        Type type = new TypeToken<List<Map<String, Object>>>(){}.getType();
                        List<Map<String, Object>> users = gson.fromJson(response.body().string(), type);
                        callback.onSuccess(users);
                    } else {
                        callback.onError("Error al obtener usuarios: " + response.code());
                    }
                }
            } catch (IOException e) {
                callback.onError("Error de conexión: " + e.getMessage());
            }
        }).start();
    }

    public void deleteTrainer(Long id, ActionCallback callback) {
        new Thread(() -> {
            try {
                Request request = new Request.Builder()
                        .url(BASE_URL + "/users/trainer/" + id)
                        .addHeader("Authorization", "Bearer " + authRepository.getToken())
                        .delete()
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (response.isSuccessful()) {
                        callback.onSuccess();
                    } else {
                        callback.onError("Error al eliminar entrenador: " + response.code());
                    }
                }
            } catch (IOException e) {
                callback.onError("Error de conexión: " + e.getMessage());
            }
        }).start();
    }

    public void deleteClient(Long id, ActionCallback callback) {
        new Thread(() -> {
            try {
                Request request = new Request.Builder()
                        .url(BASE_URL + "/users/client/" + id)
                        .addHeader("Authorization", "Bearer " + authRepository.getToken())
                        .delete()
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (response.isSuccessful()) {
                        callback.onSuccess();
                    } else {
                        callback.onError("Error al eliminar cliente: " + response.code());
                    }
                }
            } catch (IOException e) {
                callback.onError("Error de conexión: " + e.getMessage());
            }
        }).start();
    }
}
