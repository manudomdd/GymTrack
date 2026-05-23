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
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.JsonObject;
import com.gymtrack.app.R;
import com.gymtrack.app.network.ClientRepository;
import com.gymtrack.app.utils.AvatarHelper;

/**
 * Fragmento Android que gestiona el perfil del cliente y la vinculación con el entrenador.
 *
 * @author Manuel Dominguez
 * @version 1.0
 * @since 23/05/2026
 */
public class ClientProfileFragment extends Fragment {

    private TextInputEditText etNombre, etEdad, etPeso, etAltura, etTrainerCode;
    // Vista de texto (TextView) para mostrar el/la trainerstatus.
    private TextView tvTrainerStatus;
    private Button btnUpdate, btnLink;
    // Vista de imagen (ImageView) para visualizar el/la avatar.
    private ImageView ivAvatar;
    // Repositorio para operaciones de persistencia de la entidad Client.
    private ClientRepository clientRepository;

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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_client_profile, container, false);
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

        clientRepository = new ClientRepository(requireContext());

        etNombre = view.findViewById(R.id.et_nombre);
        etEdad = view.findViewById(R.id.et_edad);
        etEdad.setEnabled(false);
        etEdad.setFocusable(false);
        etPeso = view.findViewById(R.id.et_peso);
        etAltura = view.findViewById(R.id.et_altura);
        etTrainerCode = view.findViewById(R.id.et_trainer_code);
        tvTrainerStatus = view.findViewById(R.id.tv_trainer_status);
        btnUpdate = view.findViewById(R.id.btn_update_profile);
        btnLink = view.findViewById(R.id.btn_link_trainer);
        ivAvatar = view.findViewById(R.id.iv_client_profile_avatar);

        loadProfileData();

        btnUpdate.setOnClickListener(v -> handleUpdateProfile());
        btnLink.setOnClickListener(v -> handleLinkTrainer());
    }

    /**
     * Procesa la operación correspondiente para loadProfileData.
     *
     */
    private void loadProfileData() {
        clientRepository.getProfile(new ClientRepository.Callback<JsonObject>() {
            @Override
            public void onSuccess(JsonObject result) {
                if (getActivity() == null)
                    return;
                getActivity().runOnUiThread(() -> {
                    etNombre.setText(result.get("nombre").getAsString());
                    etEdad.setText(result.get("edad").getAsString());
                    etPeso.setText(result.get("peso").getAsString());
                    etAltura.setText(result.get("altura").getAsString());
                    
                    if (ivAvatar != null && result.has("avatar") && !result.get("avatar").isJsonNull()) {
                        String avatarStr = result.get("avatar").getAsString();
                        ivAvatar.setImageResource(AvatarHelper.getAvatarResource(avatarStr));
                    }

                    if (result.has("trainer") && !result.get("trainer").isJsonNull()) {
                        JsonObject trainer = result.getAsJsonObject("trainer");
                        tvTrainerStatus.setText("Vinculado con: " + trainer.get("nombre").getAsString());
                        tvTrainerStatus.setTextColor(getResources().getColor(R.color.magenta));
                        btnLink.setEnabled(false);
                        etTrainerCode.setEnabled(false);
                    }
                });
            }

            @Override
            public void onError(String message) {
                if (getActivity() == null)
                    return;
                getActivity().runOnUiThread(
                        () -> Toast.makeText(getContext(), "Error: " + message, Toast.LENGTH_SHORT).show());
            }
        });
    }

    /**
     * Actualiza los datos del registro en la base de datos.
     *
     */
    private void handleUpdateProfile() {
        String nombre = etNombre.getText().toString().trim();
        String pesoStr = etPeso.getText().toString().trim();
        String alturaStr = etAltura.getText().toString().trim();

        if (nombre.isEmpty() || pesoStr.isEmpty() || alturaStr.isEmpty()) {
            Toast.makeText(getContext(), "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObject data = new JsonObject();
        data.addProperty("nombre", nombre);
        data.addProperty("peso", Double.parseDouble(pesoStr));
        data.addProperty("altura", Integer.parseInt(alturaStr));

        clientRepository.updateProfile(data, new ClientRepository.Callback<Void>() {
            @Override
            public void onSuccess(Void result) {
                if (getActivity() == null)
                    return;
                getActivity().runOnUiThread(
                        () -> Toast.makeText(getContext(), "Perfil actualizado", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onError(String message) {
                if (getActivity() == null)
                    return;
                getActivity().runOnUiThread(() -> Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show());
            }
        });
    }

    /**
     * Procesa la operación correspondiente para handleLinkTrainer.
     *
     */
    private void handleLinkTrainer() {
        String code = etTrainerCode.getText().toString().trim().toUpperCase();
        if (code.isEmpty())
            return;

        clientRepository.linkTrainer(code, new ClientRepository.Callback<String>() {
            @Override
            public void onSuccess(String result) {
                if (getActivity() == null)
                    return;
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), result, Toast.LENGTH_SHORT).show();
                    loadProfileData();
                });
            }

            @Override
            public void onError(String message) {
                if (getActivity() == null)
                    return;
                getActivity().runOnUiThread(() -> Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show());
            }
        });
    }
}
