package com.gymtrack.app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.gymtrack.app.R;
import com.gymtrack.app.network.AuthRepository;
import com.gymtrack.app.utils.AvatarHelper;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Fragmento que muestra el perfil profesional del entrenador.
 * <p>
 * Muestra la información personal del usuario (nombre, nombre de usuario y avatar) 
 * obtenida del servidor. Adicionalmente, expone el Código de Entrenador 
 * (Trainer Code) asignado, un dato vital que los clientes necesitan introducir 
 * durante el registro en la aplicación para vincular sus cuentas automáticamente.
 * </p>
 *
 * @author Manuel Dominguez
 * @version 1.0
 * @since 26/05/2025
 */
public class TrainerProfileFragment extends Fragment {


    private TextView tvName, tvUsername, tvCode;
    private ImageView ivAvatar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_trainer_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvName = view.findViewById(R.id.tv_trainer_name);
        tvUsername = view.findViewById(R.id.tv_trainer_username);
        tvCode = view.findViewById(R.id.tv_trainer_code);
        ivAvatar = view.findViewById(R.id.iv_trainer_avatar); // Necesita id en XML
        
        fetchTrainerProfile();
    }

    private void fetchTrainerProfile() {
        AuthRepository auth = new AuthRepository(requireContext());
        OkHttpClient client = new OkHttpClient();

        new Thread(() -> {
            try {
                Request request = new Request.Builder()
                        .url("https://gymtrack-production-5934.up.railway.app/api/client/profile") // Same profile endpoint
                        .addHeader("Authorization", "Bearer " + auth.getToken())
                        .get()
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (response.isSuccessful() && response.body() != null) {
                        JsonObject obj = JsonParser.parseString(response.body().string()).getAsJsonObject();
                        
                        String nombre = obj.has("nombre") ? obj.get("nombre").getAsString() : "Sin nombre";
                        String username = obj.has("username") ? obj.get("username").getAsString() : "Sin username";
                        String code = obj.has("trainerCode") && !obj.get("trainerCode").isJsonNull() 
                                ? obj.get("trainerCode").getAsString() : "N/A";
                        String avatar = obj.has("avatar") && !obj.get("avatar").isJsonNull() 
                                ? obj.get("avatar").getAsString() : null;
                        
                        if (getActivity() == null) return;
                        getActivity().runOnUiThread(() -> {
                            tvName.setText(nombre);
                            tvUsername.setText(username);
                            tvCode.setText(code);
                            if (ivAvatar != null) {
                                ivAvatar.setImageResource(AvatarHelper.getAvatarResource(avatar));
                            }
                        });
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
