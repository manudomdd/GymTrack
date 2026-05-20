package com.gymtrack.app.fragments;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.gymtrack.app.R;
import com.gymtrack.app.network.AuthRepository;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Fragment para el registro de entrenamientos.
 *
 * Flujo de registro (cliente):
 * 1. Pulsar "+ Añadir Entrenamiento".
 * 2. Introducir nombre del ejercicio y grupo muscular (cabecera común a todas
 * las series).
 * 3. Pulsar "+ Añadir Serie" para añadir filas individuales (Peso, Reps, RIR,
 * Comentario).
 * 4. Al guardar, se empaquetan todas las series en un JsonArray y se envían a
 * POST /api/client/workouts/batch.
 *
 * Vista del entrenador: modo solo lectura (btnAdd oculto).
 * Lee de GET /api/trainer/client/{clientId}/workouts y muestra cada serie como
 * card.
 */
public class TrainingLogFragment extends Fragment {

    private Calendar selectedDate = Calendar.getInstance();
    private TextView tvMonthYear;
    private GridLayout gridDays;
    private RecyclerView rvWorkouts;
    private LinearLayout layoutEmpty;
    private WorkoutAdapter adapter;

    // Lista completa de series recibidas del backend (modelo plano)
    private final List<Map<String, Object>> workoutSessions = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_training_log, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvMonthYear = view.findViewById(R.id.tv_month_year);
        gridDays = view.findViewById(R.id.grid_days);
        rvWorkouts = view.findViewById(R.id.rv_workouts);
        layoutEmpty = view.findViewById(R.id.layout_empty);
        Button btnPrev = view.findViewById(R.id.btn_prev_month);
        Button btnNext = view.findViewById(R.id.btn_next_month);
        Button btnAdd = view.findViewById(R.id.btn_add_workout);

        // En modo entrenador (se recibe CLIENT_ID como argumento) ocultamos el botón de
        // añadir
        long clientId = getArguments() != null ? getArguments().getLong("CLIENT_ID", -1) : -1;
        if (clientId != -1) {
            btnAdd.setVisibility(View.GONE);
        }

        adapter = new WorkoutAdapter(new ArrayList<>(), clientId != -1, sessionId -> {
            deleteWorkoutSeries(sessionId);
        });
        rvWorkouts.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvWorkouts.setAdapter(adapter);

        btnPrev.setOnClickListener(v -> {
            selectedDate.add(Calendar.MONTH, -1);
            refreshCalendar();
        });
        btnNext.setOnClickListener(v -> {
            selectedDate.add(Calendar.MONTH, 1);
            refreshCalendar();
        });
        btnAdd.setOnClickListener(v -> showAddWorkoutDialog());

        Button btnSaveFeedback = view.findViewById(R.id.btn_save_feedback);
        TextInputEditText etTrainerFeedback = view.findViewById(R.id.et_trainer_feedback);
        btnSaveFeedback.setOnClickListener(v -> {
            String feedback = etTrainerFeedback.getText() != null ? etTrainerFeedback.getText().toString().trim() : "";
            saveFeedbackToBackend(clientId, feedback);
        });

        refreshCalendar();
        fetchWorkoutsFromBackend();
    }

    // ─── Calendario ──────────────────────────────────────────────────────────

    /** Reconstruye el calendario para el mes/año de selectedDate */
    private void refreshCalendar() {
        SimpleDateFormat fmt = new SimpleDateFormat("MMMM yyyy", new Locale("es", "ES"));
        String cap = fmt.format(selectedDate.getTime());
        tvMonthYear.setText(cap.substring(0, 1).toUpperCase() + cap.substring(1));

        gridDays.removeAllViews();

        Calendar firstDayCal = (Calendar) selectedDate.clone();
        firstDayCal.set(Calendar.DAY_OF_MONTH, 1);
        int dayOfWeek = firstDayCal.get(Calendar.DAY_OF_WEEK);
        // Calendar.SUNDAY = 1, MONDAY = 2... ajustamos para que Lunes sea columna 0
        int emptySlots = dayOfWeek == Calendar.SUNDAY ? 6 : dayOfWeek - 2;

        for (int i = 0; i < emptySlots; i++) {
            TextView empty = new TextView(requireContext());
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1, 1f);
            empty.setLayoutParams(params);
            gridDays.addView(empty);
        }

        int daysInMonth = selectedDate.getActualMaximum(Calendar.DAY_OF_MONTH);

        for (int day = 1; day <= daysInMonth; day++) {
            final int d = day;
            boolean isSelected = isSameDay(selectedDate, day);

            TextView tvDay = new TextView(requireContext());
            tvDay.setText(String.valueOf(day));
            tvDay.setTextColor(isSelected ? 0xFF0F0014 : 0xFFFFFFFF);
            tvDay.setTextSize(14);
            tvDay.setGravity(Gravity.CENTER);
            tvDay.setPadding(4, 8, 4, 8);

            if (isSelected)
                tvDay.setBackgroundResource(R.drawable.bg_avatar_magenta);

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1, 1f);
            params.setMargins(2, 2, 2, 2);
            tvDay.setLayoutParams(params);

            tvDay.setOnClickListener(v -> {
                selectedDate.set(Calendar.DAY_OF_MONTH, d);
                refreshCalendar();
                refreshWorkoutList();
            });

            gridDays.addView(tvDay);
        }

        refreshWorkoutList();
    }

    private boolean isSameDay(Calendar cal, int day) {
        return cal.get(Calendar.DAY_OF_MONTH) == day;
    }

    // ─── Lista de Series ──────────────────────────────────────────────────────

    /** Filtra y muestra las series para el día seleccionado */
    private void refreshWorkoutList() {
        List<Map<String, Object>> filtered = new ArrayList<>();
        String currentFeedback = null;
        for (Map<String, Object> w : workoutSessions) {
            Calendar wDate = (Calendar) w.get("date");
            if (wDate != null
                    && wDate.get(Calendar.YEAR) == selectedDate.get(Calendar.YEAR)
                    && wDate.get(Calendar.MONTH) == selectedDate.get(Calendar.MONTH)
                    && wDate.get(Calendar.DAY_OF_MONTH) == selectedDate.get(Calendar.DAY_OF_MONTH)) {
                filtered.add(w);
                if (w.containsKey("feedbackEntrenador") && w.get("feedbackEntrenador") != null) {
                    currentFeedback = (String) w.get("feedbackEntrenador");
                }
            }
        }
        adapter.updateData(filtered);
        rvWorkouts.setVisibility(filtered.isEmpty() ? View.GONE : View.VISIBLE);
        layoutEmpty.setVisibility(filtered.isEmpty() ? View.VISIBLE : View.GONE);

        long clientId = getArguments() != null ? getArguments().getLong("CLIENT_ID", -1) : -1;
        boolean isTrainerMode = (clientId != -1);

        if (getView() != null) {
            CardView cardTrainer = getView().findViewById(R.id.card_trainer_feedback);
            CardView cardClient = getView().findViewById(R.id.card_client_feedback);

            if (isTrainerMode) {
                cardClient.setVisibility(View.GONE);
                if (!filtered.isEmpty()) {
                    cardTrainer.setVisibility(View.VISIBLE);
                    TextInputEditText etTrainerFeedback = getView().findViewById(R.id.et_trainer_feedback);
                    if (etTrainerFeedback != null) {
                        etTrainerFeedback.setText(currentFeedback != null ? currentFeedback : "");
                    }
                } else {
                    cardTrainer.setVisibility(View.GONE);
                }
            } else {
                cardTrainer.setVisibility(View.GONE);
                if (currentFeedback != null && !currentFeedback.trim().isEmpty()) {
                    cardClient.setVisibility(View.VISIBLE);
                    TextView tvClientFeedback = getView().findViewById(R.id.tv_client_feedback);
                    if (tvClientFeedback != null) {
                        tvClientFeedback.setText(currentFeedback);
                    }
                } else {
                    cardClient.setVisibility(View.GONE);
                }
            }
        }
    }

    // ─── Diálogo dinámico de añadir entrenamiento ─────────────────────────────

    /**
     * Diálogo con dos partes:
     * - Cabecera fija: Nombre ejercicio + Grupo muscular
     * - Contenedor dinámico: filas de series añadidas con "+ Añadir Serie"
     *
     * Cada fila contiene: Serie N (auto), Peso, Reps, RIR, Comentario.
     */
    private void showAddWorkoutDialog() {
        // Inflamos el diálogo en un ScrollView para poder hacer scroll cuando hay
        // muchas series
        ScrollView scrollWrapper = new ScrollView(requireContext());
        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_add_workout, null);
        scrollWrapper.addView(dialogView);

        AutoCompleteTextView spinnerExercise = dialogView.findViewById(R.id.spinner_exercise);
        AutoCompleteTextView spinnerMuscleGroup = dialogView.findViewById(R.id.spinner_muscle_group);
        LinearLayout containerSeries = dialogView.findViewById(R.id.container_series);
        Button btnAddSeries = dialogView.findViewById(R.id.btn_add_series);

        String[] groups = { "Pecho", "Espalda", "Hombros", "Cuadriceps", "Femoral", "Biceps", "Triceps" };
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                requireContext(), R.layout.item_dropdown, groups);
        spinnerMuscleGroup.setAdapter(spinnerAdapter);

        Map<String, String[]> exerciseCatalog = new HashMap<>();
        exerciseCatalog.put("Pecho", new String[]{"Press banca", "Press inclinado barra", "Press inclinado mancuernas", "Peck deck", "Aperturas en poleas"});
        exerciseCatalog.put("Espalda", new String[]{"Peso muerto", "Remo con barra", "Remo con mancuerna", "Jalon al pecho", "Remo gironda"});
        exerciseCatalog.put("Hombros", new String[]{"Press militar", "Elevaciones laterales"});
        exerciseCatalog.put("Cuadriceps", new String[]{"Sentadilla", "Prensa", "Extensiones de cuadriceps", "Hack squat"});
        exerciseCatalog.put("Femoral", new String[]{"Curl femoral sentado", "Curl femoral tumbado"});
        exerciseCatalog.put("Biceps", new String[]{"Curl con barra", "Curl con mancuernas", "Curl banco scott"});
        exerciseCatalog.put("Triceps", new String[]{"Extension triceps", "Press frances", "Overhead extension"});

        spinnerMuscleGroup.setOnItemClickListener((parent, view, position, id) -> {
            String selectedGroup = groups[position];
            String[] exercises = exerciseCatalog.get(selectedGroup);
            if (exercises == null) exercises = new String[0];
            ArrayAdapter<String> exerciseAdapter = new ArrayAdapter<>(requireContext(), R.layout.item_dropdown, exercises);
            spinnerExercise.setAdapter(exerciseAdapter);
            spinnerExercise.setText("", false);
        });

        // Lista que rastrea las vistas de cada serie añadida
        final List<View> seriesViews = new ArrayList<>();

        // Añadir la primera serie automáticamente para no dejar el diálogo vacío
        addSeriesRow(containerSeries, seriesViews);

        btnAddSeries.setOnClickListener(v -> addSeriesRow(containerSeries, seriesViews));

        new AlertDialog.Builder(requireContext())
                .setTitle("Registrar Entrenamiento")
                .setView(scrollWrapper)
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    String exercise = spinnerExercise.getText().toString();
                    if (exercise.isEmpty()) {
                        Toast.makeText(requireContext(),
                                "Selecciona un ejercicio", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (seriesViews.isEmpty()) {
                        Toast.makeText(requireContext(),
                                "Añade al menos una serie", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String muscleGroup = spinnerMuscleGroup.getText().toString();
                    String dateStr = new SimpleDateFormat("yyyy-MM-dd", Locale.US)
                            .format(selectedDate.getTime());

                    // Construir el JsonArray con una entrada por serie
                    JsonArray seriesArray = new JsonArray();
                    for (int i = 0; i < seriesViews.size(); i++) {
                        View row = seriesViews.get(i);
                        TextInputEditText etPeso = row.findViewById(R.id.et_serie_peso);
                        TextInputEditText etReps = row.findViewById(R.id.et_serie_reps);
                        TextInputEditText etRir = row.findViewById(R.id.et_serie_rir);
                        TextInputEditText etComment = row.findViewById(R.id.et_serie_comment);

                        JsonObject serie = new JsonObject();
                        serie.addProperty("exercise", exercise);
                        serie.addProperty("muscleGroup", muscleGroup);
                        serie.addProperty("seriesNumber", i + 1); // 1-indexed
                        serie.addProperty("pesoTotal", parseDoubleOrZero(etPeso));
                        serie.addProperty("reps", parseOrZero(etReps));
                        serie.addProperty("rir", parseOrZero(etRir));
                        serie.addProperty("comment",
                                etComment.getText() != null ? etComment.getText().toString() : "");
                        serie.addProperty("date", dateStr);
                        seriesArray.add(serie);
                    }

                    saveWorkoutBatchToBackend(seriesArray);
                })
                .show();
    }

    /**
     * Infla una nueva fila (item_series_row.xml), actualiza el número de serie
     * en la etiqueta y la añade al contenedor dinámico del diálogo.
     */
    private void addSeriesRow(LinearLayout container, List<View> seriesViews) {
        View row = LayoutInflater.from(requireContext())
                .inflate(R.layout.item_series_row, container, false);
        int seriesNum = seriesViews.size() + 1;
        TextView tvLabel = row.findViewById(R.id.tv_series_label);
        tvLabel.setText("Serie " + seriesNum);
        container.addView(row);
        seriesViews.add(row);
    }

    // ─── Comunicación con el backend ─────────────────────────────────────────

    /**
     * POST /api/client/workouts/batch
     * Envía el JsonArray completo de series y refresca la lista al recibir 200.
     * Guard: si el token es null (sesión expirada), muestra error y aborta.
     */
    private void saveWorkoutBatchToBackend(JsonArray seriesArray) {
        AuthRepository auth = new AuthRepository(requireContext());

        String token = auth.getToken();
        if (token == null) {
            Toast.makeText(requireContext(),
                    "Sesión expirada. Por favor, vuelve a iniciar sesión.",
                    Toast.LENGTH_LONG).show();
            return;
        }

        OkHttpClient client = new OkHttpClient();

        new Thread(() -> {
            try {
                RequestBody body = RequestBody.create(
                        seriesArray.toString(),
                        MediaType.get("application/json; charset=utf-8"));
                Request request = new Request.Builder()
                        .url("http://10.0.2.2:8080/api/client/workouts/batch")
                        .addHeader("Authorization", "Bearer " + token)
                        .post(body).build();

                try (Response response = client.newCall(request).execute()) {
                    if (getActivity() == null)
                        return;
                    getActivity().runOnUiThread(() -> {
                        if (response.isSuccessful()) {
                            Toast.makeText(getContext(),
                                    "Entrenamiento guardado", Toast.LENGTH_SHORT).show();
                            fetchWorkoutsFromBackend();
                        } else if (response.code() == 403 || response.code() == 401) {
                            Toast.makeText(getContext(),
                                    "Sesión expirada. Por favor, vuelve a iniciar sesión.",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getContext(),
                                    "Error al guardar (" + response.code() + ")",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } catch (IOException e) {
                if (getActivity() == null)
                    return;
                getActivity().runOnUiThread(
                        () -> Toast.makeText(getContext(),
                                "Error de conexión", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    /**
     * DELETE /api/client/workouts/{id}
     * Elimina la serie individual especificada y refresca la lista.
     */
    private void deleteWorkoutSeries(long sessionId) {
        AuthRepository auth = new AuthRepository(requireContext());
        String token = auth.getToken();
        if (token == null) return;

        OkHttpClient client = new OkHttpClient();

        new Thread(() -> {
            try {
                Request request = new Request.Builder()
                        .url("http://10.0.2.2:8080/api/client/workouts/" + sessionId)
                        .addHeader("Authorization", "Bearer " + token)
                        .delete().build();

                try (Response response = client.newCall(request).execute()) {
                    if (getActivity() == null) return;
                    getActivity().runOnUiThread(() -> {
                        if (response.isSuccessful()) {
                            Toast.makeText(getContext(), "Serie eliminada", Toast.LENGTH_SHORT).show();
                            fetchWorkoutsFromBackend();
                        } else {
                            Toast.makeText(getContext(), "Error al eliminar (" + response.code() + ")", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } catch (IOException e) {
                if (getActivity() == null) return;
                getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    /**
     * PUT /api/trainer/client/{clientId}/feedback?date=yyyy-MM-dd
     * Guarda el feedback del entrenador para este cliente y fecha.
     */
    private void saveFeedbackToBackend(long clientId, String feedback) {
        AuthRepository auth = new AuthRepository(requireContext());
        String token = auth.getToken();
        if (token == null) return;

        OkHttpClient client = new OkHttpClient();
        String dateStr = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(selectedDate.getTime());

        new Thread(() -> {
            try {
                JsonObject bodyJson = new JsonObject();
                bodyJson.addProperty("feedback", feedback);

                RequestBody body = RequestBody.create(
                        bodyJson.toString(),
                        MediaType.get("application/json; charset=utf-8"));

                Request request = new Request.Builder()
                        .url("http://10.0.2.2:8080/api/trainer/client/" + clientId + "/feedback?date=" + dateStr)
                        .addHeader("Authorization", "Bearer " + token)
                        .put(body).build();

                try (Response response = client.newCall(request).execute()) {
                    if (getActivity() == null) return;
                    getActivity().runOnUiThread(() -> {
                        if (response.isSuccessful()) {
                            Toast.makeText(getContext(), "Feedback guardado correctamente", Toast.LENGTH_SHORT).show();
                            fetchWorkoutsFromBackend();
                        } else {
                            Toast.makeText(getContext(), "Error al guardar feedback (" + response.code() + ")", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } catch (IOException e) {
                if (getActivity() == null) return;
                getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    /**
     * GET /api/client/workouts (cliente)
     * GET /api/trainer/client/{id}/workouts (entrenador en modo lectura)
     *
     * Parseo robusto: todos los campos opcionales se comprueban con isJsonNull()
     * para evitar NullPointerException si el cliente dejó algún campo vacío.
     */
    private void fetchWorkoutsFromBackend() {
        AuthRepository auth = new AuthRepository(requireContext());
        OkHttpClient client = new OkHttpClient();

        new Thread(() -> {
            try {
                long clientId = getArguments() != null
                        ? getArguments().getLong("CLIENT_ID", -1)
                        : -1;
                String url = clientId != -1
                        ? "http://10.0.2.2:8080/api/trainer/client/" + clientId + "/workouts"
                        : "http://10.0.2.2:8080/api/client/workouts";

                Request request = new Request.Builder()
                        .url(url)
                        .addHeader("Authorization", "Bearer " + auth.getToken())
                        .get().build();

                try (Response response = client.newCall(request).execute()) {
                    if (response.isSuccessful() && response.body() != null) {
                        String json = response.body().string();
                        JsonArray array = JsonParser.parseString(json).getAsJsonArray();

                        workoutSessions.clear();
                        for (JsonElement el : array) {
                            JsonObject obj = el.getAsJsonObject();
                            Map<String, Object> map = new HashMap<>();

                            // Campos obligatorios
                            map.put("id", safeGetLong(obj, "id", -1L));
                            map.put("exercise", safeGetString(obj, "exercise", "—"));
                            map.put("muscleGroup", safeGetString(obj, "muscleGroup", "—"));

                            // seriesNumber: número ordinal de la serie (campo "seriesNumber" en JSON)
                            map.put("seriesNumber", safeGetInt(obj, "seriesNumber", 1));

                            map.put("pesoTotal", safeGetDouble(obj, "pesoTotal", 0.0));
                            map.put("reps", safeGetInt(obj, "reps", 0));
                            map.put("rir", safeGetInt(obj, "rir", 0));

                            // Comentario puede ser null si el usuario lo dejó vacío
                            map.put("comment", safeGetString(obj, "comment", ""));

                            // Feedback del entrenador
                            map.put("feedbackEntrenador", safeGetString(obj, "feedbackEntrenador", null));

                            // Fecha
                            String dateStr = safeGetString(obj, "date", null);
                            if (dateStr != null) {
                                Calendar cal = Calendar.getInstance();
                                cal.setTime(new SimpleDateFormat("yyyy-MM-dd", Locale.US)
                                        .parse(dateStr));
                                map.put("date", cal);
                            }

                            workoutSessions.add(map);
                        }

                        if (getActivity() == null)
                            return;
                        getActivity().runOnUiThread(this::refreshWorkoutList);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    // ─── Helpers de parseo robusto ───────────────────────────────────────────

    private String safeGetString(JsonObject obj, String key, String fallback) {
        if (!obj.has(key) || obj.get(key).isJsonNull())
            return fallback;
        JsonElement el = obj.get(key);
        if (!el.isJsonPrimitive())
            return fallback;
        return el.getAsString();
    }

    private int safeGetInt(JsonObject obj, String key, int fallback) {
        try {
            if (!obj.has(key) || obj.get(key).isJsonNull())
                return fallback;
            return obj.get(key).getAsInt();
        } catch (Exception e) {
            return fallback;
        }
    }

    private double safeGetDouble(JsonObject obj, String key, double fallback) {
        try {
            if (!obj.has(key) || obj.get(key).isJsonNull())
                return fallback;
            return obj.get(key).getAsDouble();
        } catch (Exception e) {
            return fallback;
        }
    }

    private long safeGetLong(JsonObject obj, String key, long fallback) {
        try {
            if (!obj.has(key) || obj.get(key).isJsonNull())
                return fallback;
            return obj.get(key).getAsLong();
        } catch (Exception e) {
            return fallback;
        }
    }

    private int parseOrZero(TextInputEditText et) {
        try {
            return et.getText() != null ? Integer.parseInt(et.getText().toString()) : 0;
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private double parseDoubleOrZero(TextInputEditText et) {
        try {
            return et.getText() != null ? Double.parseDouble(et.getText().toString()) : 0.0;
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    // ─── Adapter ──────────────────────────────────────────────────────────────

    /**
     * Adaptador para mostrar cada serie individual en el RecyclerView.
     * Funciona tanto para el cliente (modo escritura) como para el entrenador (modo
     * lectura).
     */
    public interface OnWorkoutDeleteListener {
        void onDelete(long sessionId);
    }

    private static class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.VH> {

        private List<Map<String, Object>> data;
        private final boolean isTrainerMode;
        private final OnWorkoutDeleteListener deleteListener;

        WorkoutAdapter(List<Map<String, Object>> data, boolean isTrainerMode, OnWorkoutDeleteListener deleteListener) {
            this.data = data;
            this.isTrainerMode = isTrainerMode;
            this.deleteListener = deleteListener;
        }

        void updateData(List<Map<String, Object>> newData) {
            this.data = newData;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_workout, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            Map<String, Object> w = data.get(position);

            // Grupo muscular (subtítulo en mayúsculas)
            String muscleGroup = (String) w.get("muscleGroup");
            holder.tvMuscleGroup.setText(muscleGroup != null ? muscleGroup.toUpperCase() : "");

            // Nombre del ejercicio
            holder.tvExercise.setText((String) w.get("exercise"));

            // Número de serie ordinal
            Object seriesNum = w.get("seriesNumber");
            holder.tvSets.setText(seriesNum != null ? String.valueOf(seriesNum) : "—");

            // Peso
            Object peso = w.get("pesoTotal");
            holder.tvPeso.setText(peso != null ? String.valueOf(peso) : "—");

            // Repeticiones
            Object reps = w.get("reps");
            holder.tvReps.setText(reps != null ? String.valueOf(reps) : "—");

            // RIR
            Object rir = w.get("rir");
            holder.tvRir.setText(rir != null ? String.valueOf(rir) : "—");

            // Comentario: visible solo si no está vacío
            String comment = (String) w.get("comment");
            if (comment != null && !comment.trim().isEmpty()) {
                holder.tvComment.setVisibility(View.VISIBLE);
                holder.tvComment.setText("💬 " + comment);
            } else {
                holder.tvComment.setVisibility(View.GONE);
            }

            // Botón editar oculto; eliminar disponible para el cliente, oculto para el entrenador
            holder.btnEdit.setVisibility(View.GONE);
            if (isTrainerMode) {
                holder.btnDelete.setVisibility(View.GONE);
            } else {
                holder.btnDelete.setVisibility(View.VISIBLE);
                holder.btnDelete.setOnClickListener(v -> {
                    Object idObj = w.get("id");
                    long sessionId = idObj instanceof Number ? ((Number) idObj).longValue() : -1;
                    if (sessionId == -1) return;

                    new AlertDialog.Builder(v.getContext())
                            .setTitle("Eliminar serie")
                            .setMessage("¿Estás seguro de que deseas eliminar esta serie?")
                            .setPositiveButton("Eliminar", (dialog, which) -> deleteListener.onDelete(sessionId))
                            .setNegativeButton("Cancelar", null)
                            .show();
                });
            }
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        static class VH extends RecyclerView.ViewHolder {
            TextView tvMuscleGroup, tvExercise, tvSets, tvPeso, tvReps, tvRir, tvComment;
            View btnEdit, btnDelete;

            VH(@NonNull View itemView) {
                super(itemView);
                tvMuscleGroup = itemView.findViewById(R.id.tv_muscle_group);
                tvExercise = itemView.findViewById(R.id.tv_exercise_name);
                tvSets = itemView.findViewById(R.id.tv_sets);
                tvPeso = itemView.findViewById(R.id.tv_peso);
                tvReps = itemView.findViewById(R.id.tv_reps);
                tvRir = itemView.findViewById(R.id.tv_rir);
                tvComment = itemView.findViewById(R.id.tv_comment);
                btnEdit = itemView.findViewById(R.id.btn_edit);
                btnDelete = itemView.findViewById(R.id.btn_delete);
            }
        }
    }
}
