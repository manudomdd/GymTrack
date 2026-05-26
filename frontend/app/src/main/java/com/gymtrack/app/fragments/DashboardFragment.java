package com.gymtrack.app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.gymtrack.app.HomeActivity;
import com.gymtrack.app.R;
import com.gymtrack.app.network.ClientRepository;
import com.gymtrack.app.network.dto.DashboardClientDTO;

/**
 * Fragmento que representa el panel principal (Dashboard) del usuario con rol CLIENTE.
 * <p>
 * Muestra las estadísticas resumidas del día actual mediante cuatro métricas
 * principales (entrenamientos completados, pasos dados, calorías estimadas 
 * quemadas y horas de sueño de la noche anterior) para proporcionar una visión 
 * rápida del rendimiento y adherencia del cliente.
 * </p>
 *
 * @author Manuel Dominguez
 * @version 1.0
 * @since 26/05/2025
 */
public class DashboardFragment extends Fragment {


    private TextView tvEntrenamientos;
    private TextView tvPasos;
    private TextView tvCalorias;
    private TextView tvSueno;
    private ClientRepository clientRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvEntrenamientos = view.findViewById(R.id.tv_entrenamientos);
        tvPasos = view.findViewById(R.id.tv_pasos);
        tvCalorias = view.findViewById(R.id.tv_calorias);
        tvSueno = view.findViewById(R.id.tv_sueno);

        clientRepository = new ClientRepository(requireContext());
        fetchDashboardData();
    }
    
    private void fetchDashboardData() {
        clientRepository.getClientDashboard(new ClientRepository.ClientDashboardCallback() {
            @Override
            public void onSuccess(DashboardClientDTO dashboard) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        tvEntrenamientos.setText(String.valueOf(dashboard.getEntrenamientos()));
                        tvPasos.setText(String.valueOf(dashboard.getPasosHoy()));
                        tvCalorias.setText(String.valueOf(dashboard.getCalorias()));
                        tvSueno.setText(String.format("%.1f h", dashboard.getHorasSueno()));
                    });
                }
            }

            @Override
            public void onError(String message) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> 
                        Toast.makeText(getContext(), "Error: " + message, Toast.LENGTH_SHORT).show()
                    );
                }
            }
        });
    }
}
