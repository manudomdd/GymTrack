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
 * Fragmento Android del dashboard principal del entrenador.
 *
 * @author Manuel Dominguez
 * @version 1.0
 * @since 23/05/2026
 */
public class TrainerDashboardFragment extends Fragment {

    // Vista de texto (TextView) para mostrar el/la clientestotales.
    private TextView tvClientesTotales;
    // Vista de texto (TextView) para mostrar el/la activoshoy.
    private TextView tvActivosHoy;
    // Vista de texto (TextView) para mostrar el/la entrenamientos.
    private TextView tvEntrenamientos;
    // Vista de texto (TextView) para mostrar el/la estasemana.
    private TextView tvEstaSemana;
    // Repositorio para operaciones de persistencia de la entidad Auth.
    private AuthRepository authRepository;

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
        return inflater.inflate(R.layout.fragment_trainer_dashboard, container, false);
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
        
        tvClientesTotales = view.findViewById(R.id.tv_clientes_totales);
        tvActivosHoy = view.findViewById(R.id.tv_activos_hoy);
        tvEntrenamientos = view.findViewById(R.id.tv_entrenamientos);
        tvEstaSemana = view.findViewById(R.id.tv_esta_semana);
        
        authRepository = new AuthRepository(requireContext());
        fetchDashboardData();
    }
    
    /**
     * Realiza una consulta para obtener los datos solicitados.
     *
     */
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
