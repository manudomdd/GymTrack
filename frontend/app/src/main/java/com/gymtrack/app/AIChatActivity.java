package com.gymtrack.app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.gymtrack.app.network.AuthRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Actividad Android de chat interactivo con la IA enfocada en el análisis detallado del cliente.
 *
 * @author Manuel Dominguez
 * @version 1.0
 * @since 23/05/2026
 */
public class AIChatActivity extends AppCompatActivity {

    // Atributo de tipo long para almacenar clientId.
    private long clientId;
    // Atributo de tipo String para almacenar clientName.
    private String clientName;

    // Listado interactivo (RecyclerView) para presentar chat.
    private RecyclerView rvChat;
    // Campo de entrada de texto (EditText) para ingresar el/la message.
    private EditText etMessage;
    // Botón interactivo (Button) para send.
    private Button btnSend;
    // Atributo de tipo ProgressBar para almacenar pbLoading.
    private ProgressBar pbLoading;

    // Atributo de tipo ChatAdapter para almacenar adapter.
    private ChatAdapter adapter;
    // Atributo de tipo List<ChatMessage> para almacenar messages.
    private final List<ChatMessage> messages = new ArrayList<>();

    // Repositorio para operaciones de persistencia de la entidad Auth.
    private AuthRepository authRepository;
    // Atributo de tipo OkHttpClient para almacenar httpClient.
    private OkHttpClient httpClient;

    /**
     * Registra y persiste un nuevo registro en el sistema.
     *
     * @param savedInstanceState Estado de instancia guardado previamente.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_chat);

        clientId = getIntent().getLongExtra("CLIENT_ID", -1);
        clientName = getIntent().getStringExtra("CLIENT_NAME");

        if (clientId == -1) {
            Toast.makeText(this, "Error: Cliente no especificado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        authRepository = new AuthRepository(this);
        httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        TextView tvName = findViewById(R.id.tv_client_name);
        tvName.setText(clientName != null ? clientName : "Cliente");

        Button btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> finish());

        rvChat = findViewById(R.id.rv_chat);
        etMessage = findViewById(R.id.et_message);
        btnSend = findViewById(R.id.btn_send);
        pbLoading = findViewById(R.id.pb_loading);

        adapter = new ChatAdapter(messages);
        rvChat.setLayoutManager(new LinearLayoutManager(this));
        rvChat.setAdapter(adapter);

        btnSend.setOnClickListener(v -> {
            String text = etMessage.getText().toString().trim();
            if (!text.isEmpty()) {
                sendMessage(text);
            }
        });

        // Mensaje inicial de bienvenida de la IA
        messages.add(new ChatMessage("ai", "¡Hola! Estoy listo para ayudarte a analizar el rendimiento de " +
                (clientName != null ? clientName : "este cliente") + ". " +
                "Puedes preguntarme sobre su progreso en pesos, series, RIR, hábitos de sueño o cantidad de pasos. ¿En qué te gustaría enfocarte?"));
        adapter.notifyDataSetChanged();
    }

    /**
     * Procesa la operación correspondiente para sendMessage.
     *
     * @param msgText Parámetro de entrada para la operación.
     */
    private void sendMessage(String msgText) {
        // 1. Añadir mensaje localmente
        messages.add(new ChatMessage("trainer", msgText));
        adapter.notifyItemInserted(messages.size() - 1);
        rvChat.scrollToPosition(messages.size() - 1);
        etMessage.setText("");

        // 2. Activar indicador de carga y deshabilitar botón
        pbLoading.setVisibility(View.VISIBLE);
        btnSend.setEnabled(false);

        // 3. Petición en segundo plano
        new Thread(() -> {
            try {
                JsonObject jsonReq = new JsonObject();
                jsonReq.addProperty("message", msgText);

                RequestBody body = RequestBody.create(jsonReq.toString(), MediaType.get("application/json; charset=utf-8"));
                Request request = new Request.Builder()
                        .url("http://10.0.2.2:8080/api/trainer/client/" + clientId + "/ai-chat")
                        .addHeader("Authorization", "Bearer " + authRepository.getToken())
                        .post(body)
                        .build();

                try (Response response = httpClient.newCall(request).execute()) {
                    if (response.isSuccessful() && response.body() != null) {
                        String respStr = response.body().string();
                        JsonObject jsonResp = new Gson().fromJson(respStr, JsonObject.class);
                        String aiResponse = jsonResp.get("response").getAsString();

                        runOnUiThread(() -> {
                            pbLoading.setVisibility(View.GONE);
                            btnSend.setEnabled(true);
                            messages.add(new ChatMessage("ai", aiResponse));
                            adapter.notifyItemInserted(messages.size() - 1);
                            rvChat.scrollToPosition(messages.size() - 1);
                        });
                    } else {
                        runOnUiThread(() -> {
                            pbLoading.setVisibility(View.GONE);
                            btnSend.setEnabled(true);
                            Toast.makeText(AIChatActivity.this, "Error del servidor: " + response.code(), Toast.LENGTH_SHORT).show();
                        });
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    pbLoading.setVisibility(View.GONE);
                    btnSend.setEnabled(true);
                    Toast.makeText(AIChatActivity.this, "Error de red: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    // ─── Modelos y Adapters Internos ───────────────────────────────────────────

    static class ChatMessage {
        String sender; // "trainer" o "ai"
        String text;

        ChatMessage(String sender, String text) {
            this.sender = sender;
            this.text = text;
        }
    }

    static class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.VH> {
        private final List<ChatMessage> messages;

        ChatAdapter(List<ChatMessage> messages) {
            this.messages = messages;
        }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_message, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH h, int position) {
            ChatMessage msg = messages.get(position);
            if ("trainer".equals(msg.sender)) {
                h.layoutTrainer.setVisibility(View.VISIBLE);
                h.layoutAi.setVisibility(View.GONE);
                h.tvMessageTrainer.setText(msg.text);
            } else {
                h.layoutTrainer.setVisibility(View.GONE);
                h.layoutAi.setVisibility(View.VISIBLE);
                h.tvMessageAi.setText(msg.text);
            }
        }

        @Override
        public int getItemCount() {
            return messages.size();
        }

        static class VH extends RecyclerView.ViewHolder {
            View layoutTrainer, layoutAi;
            TextView tvMessageTrainer, tvMessageAi;

            VH(@NonNull View v) {
                super(v);
                layoutTrainer = v.findViewById(R.id.layout_trainer);
                layoutAi = v.findViewById(R.id.layout_ai);
                tvMessageTrainer = v.findViewById(R.id.tv_message_trainer);
                tvMessageAi = v.findViewById(R.id.tv_message_ai);
            }
        }
    }
}
