package com.gymtrack.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.gymtrack.app.network.AuthRepository;

/**
 * Pantalla de Login de GymTrack.
 * Equivale a LoginScreen.dart de Flutter.
 *
 * Permite al usuario iniciar sesión con email y contraseña.
 * En caso de éxito navega a HomeActivity.
 * Ofrece navegación a RegisterActivity para crear cuenta nueva.
 */
public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etUsername, etPassword;
    private Button btnLogin, btnGoRegister;
    private ProgressBar progressBar;
    private AuthRepository authRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        authRepository = new AuthRepository(this);

        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        btnGoRegister = findViewById(R.id.btn_go_register);
        progressBar = findViewById(R.id.progress_bar);

        btnLogin.setOnClickListener(v -> handleLogin());
        btnGoRegister.setOnClickListener(v -> startActivity(new Intent(this, RegisterActivity.class)));
    }

    /** Gestiona el intento de login del usuario */
    private void handleLogin() {
        String username = etUsername.getText() != null ? etUsername.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString() : "";

        if (username.isEmpty() || password.isEmpty()) {
            showSnackbar("Por favor, rellena todos los campos", false);
            return;
        }

        setLoading(true);

        authRepository.login(username, password, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess() {
                runOnUiThread(() -> {
                    setLoading(false);
                    showSnackbar("¡Bienvenido!", true);

                    // Redirigir según el rol
                    Class<?> targetActivity = HomeActivity.class;
                    if ("ENTRENADOR".equals(authRepository.getRole())) {
                        targetActivity = TrainerHomeActivity.class;
                    } else if ("ADMIN".equals(authRepository.getRole())) {
                        targetActivity = AdminDashboardActivity.class;
                    }

                    Intent intent = new Intent(LoginActivity.this, targetActivity);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> {
                    setLoading(false);
                    showSnackbar("Error: Credenciales inválidas", false);
                });
            }
        });
    }

    private void setLoading(boolean loading) {
        btnLogin.setVisibility(loading ? View.GONE : View.VISIBLE);
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
    }

    private void showSnackbar(String message, boolean success) {
        View rootView = findViewById(android.R.id.content);
        Snackbar snackbar = Snackbar.make(rootView, message, Snackbar.LENGTH_LONG);

        float density = getResources().getDisplayMetrics().density;
        int cornerRadius = (int) (12 * density);
        int strokeWidth = (int) (1.5f * density);
        int margin = (int) (16 * density);

        // Drawable personalizado programático para efecto vidrio/neón
        android.graphics.drawable.GradientDrawable gd = new android.graphics.drawable.GradientDrawable();
        gd.setColor(0xEE1A0026); // Fondo violeta oscuro semi-transparente
        gd.setCornerRadius(cornerRadius);
        int strokeColor = success ? 0xFF00BF80 : 0xFFFF00BF; // Verde éxito vs Magenta neón para error
        gd.setStroke(strokeWidth, strokeColor);

        View snackbarView = snackbar.getView();
        snackbarView.setBackground(gd);
        snackbarView.setElevation(8 * density);

        // Ajustar layout para convertirlo en flotante
        if (snackbarView.getLayoutParams() instanceof android.view.ViewGroup.MarginLayoutParams) {
            android.view.ViewGroup.MarginLayoutParams params = (android.view.ViewGroup.MarginLayoutParams) snackbarView
                    .getLayoutParams();
            params.setMargins(margin, 0, margin, margin);
            snackbarView.setLayoutParams(params);
        }

        // Personalizar el texto y añadir icono moderno
        android.widget.TextView textView = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
        if (textView != null) {
            textView.setTextColor(0xFFFFFFFF);
            textView.setTextSize(14);
            textView.setGravity(android.view.Gravity.CENTER_VERTICAL);

            // Icono nativo adaptado
            int iconRes = success ? android.R.drawable.ic_dialog_info : android.R.drawable.ic_dialog_alert;
            textView.setCompoundDrawablesWithIntrinsicBounds(iconRes, 0, 0, 0);
            textView.setCompoundDrawablePadding((int) (8 * density));

            // Tipografía moderna
            textView.setTypeface(
                    android.graphics.Typeface.create("sans-serif-medium", android.graphics.Typeface.NORMAL));
        }

        snackbar.show();
    }
}
