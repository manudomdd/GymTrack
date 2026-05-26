package com.gymtrack.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.gymtrack.app.adapters.UserAdminAdapter;
import com.gymtrack.app.network.AdminRepository;
import com.gymtrack.app.network.AuthRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.text.Editable;
import android.text.TextWatcher;
import com.google.android.material.textfield.TextInputEditText;

/**
 * Actividad que implementa el panel de control del administrador del sistema.
 * <p>
 * Muestra un listado completo de todos los usuarios registrados (clientes y
 * entrenadores) con la posibilidad de buscarlos por nombre o username y de
 * eliminarlos de forma permanente previa confirmación mediante un diálogo.
 * </p>
 * <p>
 * El acceso a esta pantalla está restringido por la lógica de navegación en
 * {@link LoginActivity}: solo los usuarios con rol {@code ADMIN} son redirigidos
 * aquí tras iniciar sesión. Adicionalmente, el backend protege todos los endpoints
 * de administración con {@code @PreAuthorize("hasRole('ADMIN')")}.
 * </p>
 *
 * @author Manuel Dominguez
 * @version 1.0
 * @since 26/05/2025
 */
public class AdminDashboardActivity extends AppCompatActivity {

    private RecyclerView recyclerUsers;
    private ProgressBar progressBar;
    private TextInputEditText etSearch;
    private AdminRepository adminRepository;
    private AuthRepository authRepository;
    private UserAdminAdapter adapter;

    /** Lista completa de usuarios obtenida del servidor, sin filtrar. */
    private List<Map<String, Object>> fullUserList = new ArrayList<>();

    /** Subconjunto de fullUserList que se muestra según el filtro activo. */
    private List<Map<String, Object>> filteredUserList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        Toolbar toolbar = findViewById(R.id.toolbar_admin);
        setSupportActionBar(toolbar);

        // El ícono de navegación actúa como botón de cierre de sesión.
        toolbar.setNavigationIcon(android.R.drawable.ic_menu_revert);
        toolbar.setNavigationOnClickListener(v -> logout());

        recyclerUsers = findViewById(R.id.recycler_users);
        progressBar   = findViewById(R.id.progress_admin);
        etSearch      = findViewById(R.id.et_admin_search);

        adminRepository = new AdminRepository(this);
        authRepository  = new AuthRepository(this);

        recyclerUsers.setLayoutManager(new LinearLayoutManager(this));
        adapter = new UserAdminAdapter(filteredUserList, this::showDeleteConfirmationDialog);
        recyclerUsers.setAdapter(adapter);

        // Filtrar la lista en tiempo real conforme el administrador escribe en el buscador.
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterUsers(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        loadUsers();
    }

    /**
     * Filtra la lista de usuarios según la cadena de búsqueda proporcionada,
     * comparando sin distinción de mayúsculas contra el nombre y el username.
     *
     * @param query texto introducido por el administrador en el campo de búsqueda
     */
    private void filterUsers(String query) {
        filteredUserList.clear();
        if (query.trim().isEmpty()) {
            filteredUserList.addAll(fullUserList);
        } else {
            String lowerQuery = query.toLowerCase();
            for (Map<String, Object> user : fullUserList) {
                String name     = (String) user.get("nombre");
                String username = (String) user.get("username");
                if ((name != null && name.toLowerCase().contains(lowerQuery)) ||
                    (username != null && username.toLowerCase().contains(lowerQuery))) {
                    filteredUserList.add(user);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    /**
     * Solicita al servidor el listado completo de usuarios y actualiza el RecyclerView.
     * Muestra el indicador de progreso durante la carga y lo oculta al finalizar.
     */
    private void loadUsers() {
        progressBar.setVisibility(View.VISIBLE);
        adminRepository.getAllUsers(new AdminRepository.UsersCallback() {
            @Override
            public void onSuccess(List<Map<String, Object>> users) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    fullUserList.clear();
                    fullUserList.addAll(users);
                    filterUsers(etSearch.getText() != null ? etSearch.getText().toString() : "");
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(AdminDashboardActivity.this, message, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    /**
     * Muestra un diálogo de confirmación antes de proceder con la eliminación de un usuario.
     * La acción destructiva solo se ejecuta si el administrador confirma explícitamente.
     *
     * @param user mapa con los datos del usuario sobre el que se ha pulsado el botón de eliminar
     */
    private void showDeleteConfirmationDialog(Map<String, Object> user) {
        String name     = (String) user.get("nombre");
        String type     = (String) user.get("tipoUsuario");
        Double idDouble = (Double) user.get("id");
        Long   id       = idDouble.longValue();

        new MaterialAlertDialogBuilder(this)
                .setTitle("Eliminar " + type)
                .setMessage("¿Estás seguro de que deseas eliminar permanentemente a " + name + "?")
                .setPositiveButton("Eliminar", (dialog, which) -> deleteUser(id, type))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    /**
     * Ejecuta la eliminación del usuario en el servidor según su tipo.
     * Recarga la lista completa una vez que la operación finaliza con éxito.
     *
     * @param id   identificador del usuario a eliminar
     * @param type rol del usuario ({@code "ENTRENADOR"} o {@code "CLIENTE"})
     */
    private void deleteUser(Long id, String type) {
        progressBar.setVisibility(View.VISIBLE);

        AdminRepository.ActionCallback callback = new AdminRepository.ActionCallback() {
            @Override
            public void onSuccess() {
                runOnUiThread(() -> {
                    Toast.makeText(AdminDashboardActivity.this, "Usuario eliminado", Toast.LENGTH_SHORT).show();
                    loadUsers();
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(AdminDashboardActivity.this, message, Toast.LENGTH_SHORT).show();
                });
            }
        };

        if ("ENTRENADOR".equals(type)) {
            adminRepository.deleteTrainer(id, callback);
        } else if ("CLIENTE".equals(type)) {
            adminRepository.deleteClient(id, callback);
        }
    }

    /**
     * Cierra la sesión del administrador, limpia el token almacenado
     * y redirige a la pantalla de inicio de sesión sin posibilidad de
     * volver atrás con el botón de retroceso.
     */
    private void logout() {
        authRepository.clearToken();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
