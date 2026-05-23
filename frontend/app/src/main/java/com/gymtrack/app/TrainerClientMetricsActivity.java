package com.gymtrack.app;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.gymtrack.app.network.AuthRepository;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Actividad Android para mostrar y analizar las métricas de progreso de un cliente.
 *
 * @author Manuel Dominguez
 * @version 1.0
 * @since 23/05/2026
 */
public class TrainerClientMetricsActivity extends AppCompatActivity {

    // Campo de entrada de texto (EditText) para ingresar el/la mricscontainer.
    private LinearLayout metricsContainer;
    // Atributo de tipo LinearLayout para almacenar healthContainer.
    private LinearLayout healthContainer;
    // Repositorio para operaciones de persistencia de la entidad Auth.
    private AuthRepository authRepository;
    private Map<String, Double> allMetrics;

    /**
     * Registra y persiste un nuevo registro en el sistema.
     *
     * @param savedInstanceState Estado de instancia guardado previamente.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_metrics);

        authRepository = new AuthRepository(this);

        TextView tvName = findViewById(R.id.tv_client_name);
        metricsContainer = findViewById(R.id.ll_metrics_container);
        Button btnBack = findViewById(R.id.btn_back);

        long clientId = getIntent().getLongExtra("CLIENT_ID", -1);
        String clientName = getIntent().getStringExtra("CLIENT_NAME");

        if (clientName != null) {
            tvName.setText("Métricas de " + clientName);
        }

        btnBack.setOnClickListener(v -> finish());

        if (clientId != -1) {
            fetchMetrics(clientId);
        } else {
            Toast.makeText(this, "ID de cliente inválido", Toast.LENGTH_SHORT).show();
        }
    }

    // ── Métricas de progreso de carga ──────────────────────────────────────────

    private void fetchMetrics(long clientId) {
        authRepository.getClientProgress(clientId, new AuthRepository.ProgressCallback() {
            @Override
            public void onSuccess(Map<String, Double> metrics) {
                runOnUiThread(() -> {
                    allMetrics = metrics;
                    showMuscleGroups();
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> Toast.makeText(
                        TrainerClientMetricsActivity.this, error, Toast.LENGTH_SHORT).show());
            }
        });
    }

    /**
     * Procesa la operación correspondiente para showMuscleGroups.
     *
     */
    private void showMuscleGroups() {
        metricsContainer.removeAllViews();
        addSectionHeader(metricsContainer, "Selecciona un Grupo Muscular");

        if (allMetrics == null) {
            // Wait for metrics to load or fail gracefully
            return;
        }

        String[] allGroups = {"Pecho", "Espalda", "Hombros", "Cuadriceps", "Femoral", "Biceps", "Triceps"};
        
        int heightPx = (int) (64 * getResources().getDisplayMetrics().density);
        int marginPx = (int) (16 * getResources().getDisplayMetrics().density);

        for (String group : allGroups) {
            Button btnGroup = new Button(this);
            btnGroup.setText(group.toUpperCase());
            btnGroup.setBackgroundResource(R.drawable.bg_input_field);
            btnGroup.setTextColor(getResources().getColor(R.color.white));
            btnGroup.setTextSize(16);
            btnGroup.setTypeface(null, Typeface.BOLD);
            
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    heightPx
            );
            params.setMargins(0, 0, 0, marginPx);
            btnGroup.setLayoutParams(params);
            
            btnGroup.setOnClickListener(v -> showExercisesForGroup(group));
            
            metricsContainer.addView(btnGroup);
        }
    }

    /**
     * Procesa la operación correspondiente para showExercisesForGroup.
     *
     * @param group Parámetro de entrada para la operación.
     */
    private void showExercisesForGroup(String group) {
        metricsContainer.removeAllViews();
        
        Button btnBackGroup = new Button(this);
        btnBackGroup.setText("Volver a Grupos Musculares");
        btnBackGroup.setBackgroundResource(R.drawable.bg_input_field);
        btnBackGroup.setTextColor(getResources().getColor(R.color.magenta));
        btnBackGroup.setTypeface(null, Typeface.BOLD);
        
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, 24);
        btnBackGroup.setLayoutParams(params);
        btnBackGroup.setOnClickListener(v -> showMuscleGroups());
        
        metricsContainer.addView(btnBackGroup);
        
        addSectionHeader(metricsContainer, "Evolución: " + group.toUpperCase());
        
        boolean hasExercises = false;
        for (Map.Entry<String, Double> entry : allMetrics.entrySet()) {
            String key = entry.getKey();
            if (key.startsWith(group + " - ")) {
                String exerciseName = key.substring(group.length() + 3);
                addMetricView(exerciseName, entry.getValue());
                hasExercises = true;
            }
        }
        
        if (!hasExercises) {
            addInfoRow(metricsContainer, "Aún no hay registros de evolución de cargas suficientes para el grupo: " + group, 0xFFAAAAAA);
        }
    }

    /**
     * Registra y persiste un nuevo registro en el sistema.
     *
     * @param exerciseName Parámetro de entrada para la operación.
     * @param slope Parámetro de entrada para la operación.
     */
    private void addMetricView(String exerciseName, double slope) {
        View card = LayoutInflater.from(this).inflate(R.layout.item_metric_card, metricsContainer, false);
        
        TextView tvMuscle = card.findViewById(R.id.tv_muscle_group);
        TextView tvSlope = card.findViewById(R.id.tv_slope_value);
        TextView tvEval = card.findViewById(R.id.tv_evaluation);
        
        tvMuscle.setText(exerciseName.toUpperCase());
        
        String evaluation;
        int color;
        if (slope > 0.05) {
            evaluation = "Progresión";
            color = 0xFF00BF80; // success_green
        } else if (slope < -0.05) {
            evaluation = "Regresión";
            color = 0xFFFF3333; // error_red
        } else {
            evaluation = "Estancamiento";
            color = 0xFFFFA726; // orange
        }
        
        tvSlope.setText(String.format("Evolución: %+.2f kg/día", slope));
        tvEval.setText(evaluation);
        tvEval.setBackgroundTintList(ColorStateList.valueOf(color));
        
        metricsContainer.addView(card);
    }

    // ── Helpers de UI ──────────────────────────────────────────────────────────

    private void addSectionHeader(LinearLayout parent, String title) {
        TextView tv = new TextView(this);
        tv.setText(title);
        tv.setTextColor(getResources().getColor(R.color.magenta));
        tv.setTextSize(18);
        tv.setTypeface(null, Typeface.BOLD);
        tv.setPadding(0, 24, 0, 16);
        parent.addView(tv);
    }

    /**
     * Registra y persiste un nuevo registro en el sistema.
     *
     * @param parent Parámetro de entrada para la operación.
     * @param text Parámetro de entrada para la operación.
     * @param color Parámetro de entrada para la operación.
     */
    private void addInfoRow(LinearLayout parent, String text, int color) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTextColor(color);
        tv.setTextSize(15);
        tv.setPadding(8, 6, 0, 24);
        parent.addView(tv);
    }
}
