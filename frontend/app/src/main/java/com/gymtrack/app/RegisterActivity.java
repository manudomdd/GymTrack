package com.gymtrack.app;

import android.app.DatePickerDialog;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import java.util.Calendar;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.slider.Slider;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.gymtrack.app.network.AuthRepository;

import java.util.HashMap;
import java.util.Map;

/**
 * Pantalla de Registro de GymTrack.
 * Equivale a RegisterScreen.dart de Flutter.
 *
 * Campos: nombre, email, password, peso, altura, NEAT (slider 1-5).
 * En caso de éxito vuelve al Login.
 */
public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText etNombre, etUsername, etPassword, etPeso, etAltura, etFechaNacimiento;
    private Slider sliderNeat;
    private TextView tvNeatLabel;
    private Button btnRegister, btnGoLogin;
    private ProgressBar progressBar;
    private View tilTrainerCode;
    private TextInputEditText etTrainerCode;
    private AuthRepository authRepository;

    private int neatValue = 3;

    private static final String[] NEAT_LABELS = {
            "", "Muy Sedentario", "Sedentario", "Moderado", "Activo", "Muy Activo"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        authRepository = new AuthRepository(this);

        etNombre = findViewById(R.id.et_nombre);
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        etPeso = findViewById(R.id.et_peso);
        etAltura = findViewById(R.id.et_altura);
        etFechaNacimiento = findViewById(R.id.et_fecha_nacimiento);
        sliderNeat = findViewById(R.id.slider_neat);
        tvNeatLabel = findViewById(R.id.tv_neat_label);
        btnRegister = findViewById(R.id.btn_register);
        btnGoLogin = findViewById(R.id.btn_go_login);
        progressBar = findViewById(R.id.progress_bar);
        tilTrainerCode = findViewById(R.id.til_trainer_code);
        etTrainerCode = findViewById(R.id.et_trainer_code);

        // Lógica para mostrar/ocultar código de entrenador
        RadioGroup rgRole = findViewById(R.id.rg_role);
        rgRole.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_entrenador) {
                tilTrainerCode.setVisibility(View.GONE);
            } else {
                tilTrainerCode.setVisibility(View.VISIBLE);
            }
        });

        // Listener del slider NEAT
        sliderNeat.addOnChangeListener((slider, value, fromUser) -> {
            neatValue = (int) value;
            tvNeatLabel.setText("NIVEL DE ACTIVIDAD FISICA DIARIA: " + NEAT_LABELS[neatValue]);
        });

        // Date Picker para Fecha de Nacimiento
        etFechaNacimiento.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.YEAR, -18); // 18 años por defecto
            long defaultSelection = calendar.getTimeInMillis();

            Calendar maxDate = Calendar.getInstance();
            maxDate.add(Calendar.YEAR, -10); // Opcional: no permitir fechas futuras (min 10 años)
            long maxSelection = maxDate.getTimeInMillis();

            CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder()
                    .setValidator(DateValidatorPointBackward.before(maxSelection));

            MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText("SELECCIONA TU FECHA DE NACIMIENTO")
                    .setSelection(defaultSelection)
                    .setCalendarConstraints(constraintsBuilder.build())
                    .build();

            datePicker.addOnPositiveButtonClickListener(selection -> {
                Calendar selectedCal = Calendar.getInstance();
                selectedCal.setTimeInMillis(selection);
                String date = String.format("%04d-%02d-%02d",
                        selectedCal.get(Calendar.YEAR),
                        selectedCal.get(Calendar.MONTH) + 1,
                        selectedCal.get(Calendar.DAY_OF_MONTH));
                etFechaNacimiento.setText(date);
            });

            datePicker.show(getSupportFragmentManager(), "DATE_PICKER");
        });

        btnRegister.setOnClickListener(v -> handleRegister());
        btnGoLogin.setOnClickListener(v -> finish());
    }

    /** Gestiona el registro del nuevo usuario */
    private void handleRegister() {
        String nombre = getText(etNombre);
        String username = getText(etUsername);
        String password = getText(etPassword);
        String fechaNacimiento = getText(etFechaNacimiento);
        String pesoStr = getText(etPeso);
        String alturaStr = getText(etAltura);

        if (nombre.isEmpty() || username.isEmpty() || password.isEmpty()
                || fechaNacimiento.isEmpty() || pesoStr.isEmpty() || alturaStr.isEmpty()) {
            showSnackbar("Por favor, rellena todos los campos", false);
            return;
        }

        double peso;
        int altura;
        try {
            peso = Double.parseDouble(pesoStr);
            altura = Integer.parseInt(alturaStr);
        } catch (NumberFormatException e) {
            showSnackbar("Peso y altura deben ser números válidos", false);
            return;
        }

        setLoading(true);

        String tipoUsuario = "CLIENTE";
        RadioGroup rgRole = findViewById(R.id.rg_role);
        if (rgRole.getCheckedRadioButtonId() == R.id.rb_entrenador) {
            tipoUsuario = "ENTRENADOR";
        }

        Map<String, Object> userData = new HashMap<>();
        userData.put("nombre", nombre);
        userData.put("username", username);
        userData.put("password", password);
        userData.put("fechaNacimiento", fechaNacimiento);
        userData.put("peso", peso);
        userData.put("altura", altura);
        userData.put("neat", neatValue);
        userData.put("tipoUsuario", tipoUsuario);

        if (tipoUsuario.equals("CLIENTE")) {
            String trainerCode = etTrainerCode.getText().toString().trim();
            if (!trainerCode.isEmpty()) {
                userData.put("trainerCode", trainerCode);
            }
        }

        authRepository.register(userData, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess() {
                runOnUiThread(() -> {
                    setLoading(false);
                    showSnackbar("¡Cuenta creada con éxito! Inicia sesión.", true);
                    // Volver al Login tras un breve delay
                    btnRegister.postDelayed(() -> finish(), 1500);
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> {
                    setLoading(false);
                    showSnackbar("Error: El username ya existe o hay un problema", false);
                });
            }
        });
    }

    private String getText(TextInputEditText et) {
        return et.getText() != null ? et.getText().toString().trim() : "";
    }

    private void setLoading(boolean loading) {
        btnRegister.setVisibility(loading ? View.GONE : View.VISIBLE);
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
