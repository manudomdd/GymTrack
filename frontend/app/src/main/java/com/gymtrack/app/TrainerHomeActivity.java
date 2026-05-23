package com.gymtrack.app;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.JsonObject;
import com.gymtrack.app.fragments.TrainerClientsFragment;
import com.gymtrack.app.fragments.TrainerDashboardFragment;
import com.gymtrack.app.fragments.TrainerProfileFragment;
import com.gymtrack.app.network.AuthRepository;
import com.gymtrack.app.network.ClientRepository;
import com.gymtrack.app.utils.AvatarHelper;

/**
 * Actividad principal para el rol de entrenador, conteniendo la navegación y paneles de control.
 *
 * @author Manuel Dominguez
 * @version 1.0
 * @since 23/05/2026
 */
public class TrainerHomeActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navView;
    private AuthRepository authRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trainer_home);

        authRepository = new AuthRepository(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        navView = findViewById(R.id.nav_view);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.nav_dashboard, R.string.nav_dashboard);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navView.setNavigationItemSelectedListener(item -> {
            drawerLayout.closeDrawer(GravityCompat.START);
            int id = item.getItemId();
            if (id == R.id.nav_trainer_dashboard) {
                loadFragment(new TrainerDashboardFragment());
            } else if (id == R.id.nav_clients) {
                loadFragment(new TrainerClientsFragment());
            } else if (id == R.id.nav_general_assistant) {
                Intent intent = new Intent(this, AIGeneralChatActivity.class);
                startActivity(intent);
            } else if (id == R.id.nav_profile) {
                loadFragment(new TrainerProfileFragment());
            } else if (id == R.id.nav_logout) {
                showLogoutDialog();
            }
            return true;
        });

        if (savedInstanceState == null) {
            loadFragment(new TrainerDashboardFragment());
            navView.setCheckedItem(R.id.nav_trainer_dashboard);
        }
        
        loadNavHeaderData();
    }
    
    private void loadNavHeaderData() {
        ClientRepository clientRepository = new ClientRepository(this);
        clientRepository.getProfile(new ClientRepository.Callback<JsonObject>() {
            @Override
            public void onSuccess(JsonObject result) {
                runOnUiThread(() -> {
                    android.view.View headerView = navView.getHeaderView(0);
                    if (headerView != null) {
                        ImageView ivAvatar = headerView.findViewById(R.id.iv_nav_avatar);
                        if (ivAvatar != null && result.has("avatar") && !result.get("avatar").isJsonNull()) {
                            String avatarStr = result.get("avatar").getAsString();
                            ivAvatar.setImageResource(AvatarHelper.getAvatarResource(avatarStr));
                        }
                    }
                });
            }

            @Override
            public void onError(String message) {
                // Ignore
            }
        });
    }

    public void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    private void showLogoutDialog() {
        new MaterialAlertDialogBuilder(this, R.style.Theme_GymTrack_Dialog)
                .setTitle("Cerrar Sesión")
                .setMessage("¿Estás seguro de que quieres cerrar sesión?")
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Cerrar Sesión", (dialog, which) -> {
                    authRepository.clearToken();
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .show();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
