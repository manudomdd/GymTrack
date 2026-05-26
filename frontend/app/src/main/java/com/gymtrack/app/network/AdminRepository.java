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
 * Repositorio que centraliza las llamadas HTTP del panel de administración.
 * <p>
 * Se comunica con el backend desplegado en Railway a través de los endpoints
 * protegidos bajo {@code /api/admin}. Todas las peticiones incluyen el token JWT
 * almacenado en {@link AuthRepository} para que el servidor pueda validar el rol
 * del usuario antes de procesar la solicitud.
 * </p>
 * <p>
 * Las operaciones de red se ejecutan en hilos secundarios para no bloquear el
 * hilo principal de la interfaz de usuario. Los resultados se notifican mediante
 * las interfaces de callback definidas en esta clase.
 * </p>
 *
 * @author Manuel Dominguez
 * @version 1.0
 * @since 26/05/2025
 */
public class AdminRepository {

    private static final String BASE_URL = "https://gymtrack-production-5934.up.railway.app/api/admin";

    private final OkHttpClient client;
    private final Gson gson;
    private final AuthRepository authRepository;

    /**
     * Crea una nueva instancia del repositorio.
     *
     * @param context contexto de Android necesario para acceder a las SharedPreferences
     *                donde se almacena el token JWT
     */
    public AdminRepository(Context context) {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();
        this.gson = new Gson();
        this.authRepository = new AuthRepository(context);
    }

    /**
     * Interfaz de callback para operaciones que devuelven una lista de usuarios.
     */
    public interface UsersCallback {
        /**
         * Se invoca cuando el servidor responde con éxito y la lista de usuarios
         * ha sido deserializada correctamente.
         *
         * @param users lista de mapas con los datos de cada usuario
         */
        void onSuccess(List<Map<String, Object>> users);

        /**
         * Se invoca cuando la petición falla por error de red o de servidor.
         *
         * @param message descripción del error producido
         */
        void onError(String message);
    }

    /**
     * Interfaz de callback para operaciones de escritura sin valor de retorno.
     */
    public interface ActionCallback {
        /** Se invoca cuando la operación finaliza con éxito en el servidor. */
        void onSuccess();

        /**
         * Se invoca cuando la petición falla por error de red o de servidor.
         *
         * @param message descripción del error producido
         */
        void onError(String message);
    }

    /**
     * Obtiene del servidor el listado completo de usuarios registrados,
     * excluyendo las cuentas de administrador.
     * <p>
     * Endpoint: {@code GET /api/admin/users}
     * </p>
     *
     * @param callback receptor del resultado de la operación
     */
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

    /**
     * Elimina un entrenador del sistema por su identificador.
     * Los clientes vinculados a ese entrenador permanecen activos pero desvinculados.
     * <p>
     * Endpoint: {@code DELETE /api/admin/users/trainer/{id}}
     * </p>
     *
     * @param id       identificador del entrenador a eliminar
     * @param callback receptor del resultado de la operación
     */
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

    /**
     * Elimina un cliente del sistema junto con todos sus registros de actividad.
     * <p>
     * Endpoint: {@code DELETE /api/admin/users/client/{id}}
     * </p>
     *
     * @param id       identificador del cliente a eliminar
     * @param callback receptor del resultado de la operación
     */
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
