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

import com.gymtrack.app.R;
import com.gymtrack.app.TrainerHomeActivity;
import com.gymtrack.app.network.AuthRepository;
import com.gymtrack.app.network.dto.DashboardTrainerDTO;

/**
 * Fragment del Dashboard principal del entrenador.
 * Equivale al _buildDashboard() de TrainerHomeScreen.dart.
 */
public class TrainerDashboardFragment extends Fragment {

    private TextView tvClientesTotales;
    private TextView tvActivosHoy;
    private TextView tvEntrenamientos;
    private TextView tvEstaSemana;
    private AuthRepository authRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_trainer_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        tvClientesTotales = view.findViewById(R.id.tv_clientes_totales);
        tvActivosHoy = view.findViewById(R.id.tv_activos_hoy);
        tvEntrenamientos = view.findViewById(R.id.tv_entrenamientos);
        tvEstaSemana = view.findViewById(R.id.tv_esta_semana);
        
        authRepository = new AuthRepository(requireContext());
        fetchDashboardData();
    }
    
    private void fetchDashboardData() {
        authRepository.getTrainerDashboard(new AuthRepository.TrainerDashboardCallback() {
            @Override
            public void onSuccess(DashboardTrainerDTO dashboard) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        tvClientesTotales.setText(String.valueOf(dashboard.getClientesTotales()));
                        tvActivosHoy.setText(String.valueOf(dashboard.getActivosHoy()));
                        tvEntrenamientos.setText(String.valueOf(dashboard.getEntrenamientos()));
                        tvEstaSemana.setText(String.valueOf(dashboard.getEstaSemana()));
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
