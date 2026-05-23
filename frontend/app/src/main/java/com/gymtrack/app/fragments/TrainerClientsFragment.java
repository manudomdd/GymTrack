package com.gymtrack.app.fragments;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.gymtrack.app.AIChatActivity;
import com.gymtrack.app.R;
import com.gymtrack.app.TrainerBiomarkersActivity;
import com.gymtrack.app.TrainerClientMetricsActivity;
import com.gymtrack.app.TrainerHomeActivity;
import com.gymtrack.app.network.AuthRepository;
import com.gymtrack.app.utils.AvatarHelper;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
 * Fragmento Android para mostrar y buscar la lista de clientes vinculados al entrenador.
 *
 * @author Manuel Dominguez
 * @version 1.0
 * @since 23/05/2026
 */
public class TrainerClientsFragment extends Fragment {

    // Atributo de tipo ClientAdapter para almacenar adapter.
    private ClientAdapter adapter;
    // Atributo de tipo LinearLayout para almacenar layoutEmpty.
    private LinearLayout layoutEmpty;
    // Listado interactivo (RecyclerView) para presentar clients.
    private RecyclerView rvClients;
    private final List<Map<String, Object>> allClients = new ArrayList<>();

    /**
     * Registra y persiste un nuevo registro en el sistema.
     *
     * @param inflater Objeto para inflar diseños XML en la interfaz.
     * @param container Contenedor padre donde se inserta la vista.
     * @param savedInstanceState Estado de instancia guardado previamente.
     * @return El resultado o estado devuelto tras procesar la petición.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_trainer_clients, container, false);
    }

    /**
     * Registra y persiste un nuevo registro en el sistema.
     *
     * @param view Vista raíz devuelta tras inflar el fragmento.
     * @param savedInstanceState Estado de instancia guardado previamente.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvClients = view.findViewById(R.id.rv_clients);
        layoutEmpty = view.findViewById(R.id.layout_empty);
        TextInputEditText etSearch = view.findViewById(R.id.et_search);

        adapter = new ClientAdapter(new ArrayList<>(), this::openClientDiary, this::openBiomarkers, this::openAiAssistant);
        rvClients.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvClients.setAdapter(adapter);

        fetchClients();

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override
            public void onTextChanged(CharSequence s, int st, int b, int c) {
                filterClients(s.toString().toLowerCase().trim());
            }
        });

        updateVisibility(allClients);
    }

    /**
     * Realiza una consulta para obtener los datos solicitados.
     *
     */
    private void fetchClients() {
        AuthRepository auth = new AuthRepository(requireContext());
        OkHttpClient client = new OkHttpClient();

        new Thread(() -> {
            try {
                Request request = new Request.Builder()
                        .url("http://10.0.2.2:8080/api/trainer/clients")
                        .addHeader("Authorization", "Bearer " + auth.getToken())
                        .get().build();

                try (Response response = client.newCall(request).execute()) {
                    if (response.isSuccessful() && response.body() != null) {
                        String json = response.body().string();
                        JsonArray array = JsonParser.parseString(json).getAsJsonArray();

                        allClients.clear();
                        for (JsonElement el : array) {
                            JsonObject obj = el.getAsJsonObject();
                            Map<String, Object> map = new HashMap<>();
                            map.put("id", obj.get("id").getAsLong());
                            map.put("nombre", obj.get("nombre").getAsString());
                            map.put("username", obj.has("username") ? obj.get("username").getAsString() : obj.has("email") ? obj.get("email").getAsString() : "");
                            map.put("peso", obj.get("peso").getAsDouble());
                            map.put("altura", obj.get("altura").getAsInt());
                            map.put("edad", obj.has("edad") && !obj.get("edad").isJsonNull() ? obj.get("edad").getAsInt() : 0);
                            map.put("ultimoGrupoMuscular", obj.has("ultimoGrupoMuscular") && !obj.get("ultimoGrupoMuscular").isJsonNull() ? obj.get("ultimoGrupoMuscular").getAsString() : "Ninguno");
                            map.put("avatar", obj.has("avatar") && !obj.get("avatar").isJsonNull() ? obj.get("avatar").getAsString() : null);
                            allClients.add(map);
                        }

                        if (getActivity() == null) return;
                        getActivity().runOnUiThread(() -> {
                            adapter.updateData(new ArrayList<>(allClients));
                            updateVisibility(allClients);
                        });
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * Procesa la operación correspondiente para filterClients.
     *
     * @param query Parámetro de entrada para la operación.
     */
    private void filterClients(String query) {
        List<Map<String, Object>> filtered = new ArrayList<>();
        for (Map<String, Object> client : allClients) {
            String nombre = ((String) client.get("nombre")).toLowerCase();
            String username = client.containsKey("username") ? ((String) client.get("username")).toLowerCase() : "";
            if (nombre.contains(query) || username.contains(query)) {
                filtered.add(client);
            }
        }
        adapter.updateData(filtered);
        updateVisibility(filtered);
    }

    /**
     * Actualiza los datos del registro en la base de datos.
     *
     * @param list Parámetro de entrada para la operación.
     */
    private void updateVisibility(List<Map<String, Object>> list) {
        boolean empty = list.isEmpty();
        rvClients.setVisibility(empty ? View.GONE : View.VISIBLE);
        layoutEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
    }

    /**
     * Procesa la operación correspondiente para openClientDiary.
     *
     * @param client Parámetro de entrada para la operación.
     */
    private void openClientDiary(Map<String, Object> client) {
        Object idObj = client.get("id");
        long clientId = idObj instanceof Number ? ((Number) idObj).longValue() : -1;
        if (clientId == -1) return;

        TrainingLogFragment fragment = new TrainingLogFragment();
        Bundle args = new Bundle();
        args.putLong("CLIENT_ID", clientId);
        fragment.setArguments(args);

        // Assumes the parent activity has a loadFragment method
        if (getActivity() instanceof TrainerHomeActivity) {
            ((TrainerHomeActivity) getActivity()).loadFragment(fragment);
        }
    }

    /**
     * Procesa la operación correspondiente para openBiomarkers.
     *
     * @param client Parámetro de entrada para la operación.
     */
    private void openBiomarkers(Map<String, Object> client) {
        Object idObj = client.get("id");
        long clientId = idObj instanceof Number ? ((Number) idObj).longValue() : -1;
        if (clientId == -1) return;

        Intent intent = new Intent(requireContext(),
                TrainerBiomarkersActivity.class);
        intent.putExtra("CLIENT_ID", clientId);
        intent.putExtra("CLIENT_NAME", (String) client.get("nombre"));
        startActivity(intent);
    }

    /**
     * Procesa la operación correspondiente para openAiAssistant.
     *
     * @param client Parámetro de entrada para la operación.
     */
    private void openAiAssistant(Map<String, Object> client) {
        Object idObj = client.get("id");
        long clientId = idObj instanceof Number ? ((Number) idObj).longValue() : -1;
        if (clientId == -1) return;

        Intent intent = new Intent(requireContext(),
                AIChatActivity.class);
        intent.putExtra("CLIENT_ID", clientId);
        intent.putExtra("CLIENT_NAME", (String) client.get("nombre"));
        startActivity(intent);
    }

    // ─── Interfaces ────────────────────────────────────────────────────────────

    interface OnClientClick {
        void onClick(Map<String, Object> client);
    }

    // ─── Adapter ───────────────────────────────────────────────────────────────

    private static class ClientAdapter extends RecyclerView.Adapter<ClientAdapter.VH> {

        private List<Map<String, Object>> data;
        private final OnClientClick diaryListener;
        private final OnClientClick biomarkerListener;
        private final OnClientClick aiListener;

        ClientAdapter(List<Map<String, Object>> data,
                OnClientClick diaryListener, OnClientClick biomarkerListener, OnClientClick aiListener) {
            this.data = data;
            this.diaryListener = diaryListener;
            this.biomarkerListener = biomarkerListener;
            this.aiListener = aiListener;
        }

        void updateData(List<Map<String, Object>> newData) {
            this.data = newData;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_client, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH h, int position) {
            Map<String, Object> c = data.get(position);
            String nombre = (String) c.get("nombre");
            String avatarStr = (String) c.get("avatar");

            h.tvName.setText(nombre);
            h.tvUsername.setText(c.containsKey("username") ? (String) c.get("username") : "");
            
            if (h.ivAvatar != null) {
                h.ivAvatar.setImageResource(AvatarHelper.getAvatarResource(avatarStr));
            }
            
            h.tvPeso.setText(c.get("peso") + " kg");
            h.tvAltura.setText(c.get("altura") + " cm");
            h.tvEdad.setText(c.get("edad") + " años");
            
            String lastWorkoutStr = "Ninguno";
            try {
                if (c.containsKey("ultimoGrupoMuscular") && c.get("ultimoGrupoMuscular") != null) {
                    lastWorkoutStr = (String) c.get("ultimoGrupoMuscular");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            h.tvLastWorkout.setText(lastWorkoutStr);

            h.btnDiary.setOnClickListener(v -> diaryListener.onClick(c));

            h.btnMetrics.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(),
                        TrainerClientMetricsActivity.class);
                Object idObj = c.get("id");
                long id = idObj instanceof Number ? ((Number) idObj).longValue() : -1L;
                intent.putExtra("CLIENT_ID", id);
                intent.putExtra("CLIENT_NAME", nombre);
                v.getContext().startActivity(intent);
            });

            h.btnBiomarkers.setOnClickListener(v -> biomarkerListener.onClick(c));
            
            h.btnAiAssistant.setOnClickListener(v -> aiListener.onClick(c));
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        private String formatLastWorkoutDate(String dateStr) {
            if (dateStr == null || dateStr.trim().isEmpty()) {
                return "Sin entrenamientos";
            }
            try {
                LocalDate date = LocalDate.parse(dateStr);
                LocalDate hoy = LocalDate.now();
                if (date.equals(hoy)) {
                    return "Hoy";
                } else if (date.equals(hoy.minusDays(1))) {
                    return "Ayer";
                } else {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    return date.format(formatter);
                }
            } catch (Exception e) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                    java.util.Date date = sdf.parse(dateStr);
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(date);
                    
                    Calendar today = Calendar.getInstance();
                    Calendar yesterday = Calendar.getInstance();
                    yesterday.add(Calendar.DAY_OF_YEAR, -1);
                    
                    if (cal.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                        cal.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)) {
                        return "Hoy";
                    } else if (cal.get(Calendar.YEAR) == yesterday.get(Calendar.YEAR) &&
                               cal.get(Calendar.DAY_OF_YEAR) == yesterday.get(Calendar.DAY_OF_YEAR)) {
                        return "Ayer";
                    } else {
                        SimpleDateFormat outFmt = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
                        return outFmt.format(date);
                    }
                } catch (Exception ex) {
                    return dateStr;
                }
            }
        }

        static class VH extends RecyclerView.ViewHolder {
            TextView tvName, tvUsername,
                    tvLastWorkout, tvPeso, tvAltura, tvEdad;
            ImageView ivAvatar;
            Button btnDiary, btnMetrics, btnBiomarkers, btnAiAssistant;

            VH(@NonNull View v) {
                super(v);
                tvName = v.findViewById(R.id.tv_client_name);
                tvUsername = v.findViewById(R.id.tv_client_username);
                ivAvatar = v.findViewById(R.id.iv_client_avatar);
                tvLastWorkout = v.findViewById(R.id.tv_last_workout);
                tvPeso = v.findViewById(R.id.tv_peso);
                tvAltura = v.findViewById(R.id.tv_altura);
                tvEdad = v.findViewById(R.id.tv_edad);
                btnDiary = v.findViewById(R.id.btn_diary);
                btnMetrics = v.findViewById(R.id.btn_metrics);
                btnBiomarkers = v.findViewById(R.id.btn_biomarkers);
                btnAiAssistant = v.findViewById(R.id.btn_ai_assistant);
            }
        }
    }
}
