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
 * Actividad que presenta el análisis de progreso de entrenamiento de un cliente específico.
 * <p>
 * Consulta al backend el resultado del cálculo de regresión lineal sobre el historial
 * de cargas levantadas. Muestra una interfaz categorizada por grupos musculares y 
 * evalúa matemáticamente si el cliente se encuentra en fase de progresión, 
 * regresión o estancamiento para cada ejercicio.
 * </p>
 *
 * @author Manuel Dominguez
 * @version 1.0
 * @since 26/05/2025
 */
public class TrainerClientMetricsActivity extends AppCompatActivity {


    private LinearLayout metricsContainer;
    private LinearLayout healthContainer;
    private AuthRepository authRepository;
    private Map<String, Double> allMetrics;

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

    private void addInfoRow(LinearLayout parent, String text, int color) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTextColor(color);
        tv.setTextSize(15);
        tv.setPadding(8, 6, 0, 24);
        parent.addView(tv);
    }
}
