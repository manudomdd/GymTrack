package com.gymtrack.app;

import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
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

    private TextInputEditText etEmail, etPassword;
    private Button btnLogin, btnGoRegister;
    private ProgressBar progressBar;
    private AuthRepository authRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        authRepository = new AuthRepository(this);

        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        btnGoRegister = findViewById(R.id.btn_go_register);
        progressBar = findViewById(R.id.progress_bar);

        btnLogin.setOnClickListener(v -> handleLogin());
        btnGoRegister.setOnClickListener(v -> startActivity(new Intent(this, RegisterActivity.class)));
    }

    /** Gestiona el intento de login del usuario */
    private void handleLogin() {
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString() : "";

        if (email.isEmpty() || password.isEmpty()) {
            showSnackbar("Por favor, rellena todos los campos", false);
            return;
        }

        setLoading(true);

        authRepository.login(email, password, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess() {
                runOnUiThread(() -> {
                    setLoading(false);
                    showSnackbar("¡Bienvenido!", true);

                    // Redirigir según el rol
                    Class<?> targetActivity = HomeActivity.class;
                    if ("ENTRENADOR".equals(authRepository.getRole())) {
                        targetActivity = TrainerHomeActivity.class;
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

        GradientDrawable gd = new GradientDrawable();
        gd.setColor(0xEE1A0026);
        gd.setCornerRadius(cornerRadius);
        int strokeColor = success ? 0xFF00BF80 : 0xFFFF00BF;
        gd.setStroke(strokeWidth, strokeColor);

        View snackbarView = snackbar.getView();
        snackbarView.setBackground(gd);
        snackbarView.setElevation(8 * density);

        if (snackbarView.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) snackbarView.getLayoutParams();
            params.setMargins(margin, 0, margin, margin);
            snackbarView.setLayoutParams(params);
        }

        TextView textView = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
        if (textView != null) {
            textView.setTextColor(0xFFFFFFFF);
            textView.setTextSize(14);
            textView.setGravity(Gravity.CENTER_VERTICAL);

            int iconRes = success ? android.R.drawable.ic_dialog_info : android.R.drawable.ic_dialog_alert;
            textView.setCompoundDrawablesWithIntrinsicBounds(iconRes, 0, 0, 0);
            textView.setCompoundDrawablePadding((int) (8 * density));

            textView.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        }

        snackbar.show();
    }
}
