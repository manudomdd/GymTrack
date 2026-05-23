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
 * Fragmento Android del dashboard principal que resume la actividad diaria del cliente.
 *
 * @author Manuel Dominguez
 * @version 1.0
 * @since 23/05/2026
 */
public class DashboardFragment extends Fragment {

    // Vista de texto (TextView) para mostrar el/la entrenamientos.
    private TextView tvEntrenamientos;
    // Vista de texto (TextView) para mostrar el/la pasos.
    private TextView tvPasos;
    // Vista de texto (TextView) para mostrar el/la calorias.
    private TextView tvCalorias;
    // Vista de texto (TextView) para mostrar el/la sueno.
    private TextView tvSueno;
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
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
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

        tvEntrenamientos = view.findViewById(R.id.tv_entrenamientos);
        tvPasos = view.findViewById(R.id.tv_pasos);
        tvCalorias = view.findViewById(R.id.tv_calorias);
        tvSueno = view.findViewById(R.id.tv_sueno);

        clientRepository = new ClientRepository(requireContext());
        fetchDashboardData();
    }
    
    /**
     * Realiza una consulta para obtener los datos solicitados.
     *
     */
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
