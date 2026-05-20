package com.gymtrack.app.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.gymtrack.app.R;

public class StepCounterService extends Service implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor stepSensor;
    private int currentSteps = 0;
    private int initialSteps = -1;
    private static final String CHANNEL_ID = "StepCounterChannel";
    public static final String ACTION_STEPS_UPDATED = "com.gymtrack.app.STEPS_UPDATED";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        }
        
        // Iniciar la escucha de notificaciones SSE en segundo plano de manera persistente
        startNotificationListener();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateNotification();
        if (stepSensor != null) {
            sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI);
        }
        return START_STICKY;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            int totalSteps = (int) event.values[0];
            if (initialSteps == -1) {
                initialSteps = totalSteps;
            }
            currentSteps = totalSteps - initialSteps;
            
            // Guardar localmente
            SharedPreferences prefs = getSharedPreferences("gymtrack_prefs", Context.MODE_PRIVATE);
            prefs.edit().putInt("daily_steps", currentSteps).apply();

            // Emitir un broadcast para actualizar la UI si la app está abierta
            Intent broadcast = new Intent(ACTION_STEPS_UPDATED);
            broadcast.putExtra("steps", currentSteps);
            sendBroadcast(broadcast);

            updateNotification();
        }
    }

    private void updateNotification() {
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("GymTrack")
                .setContentText("Pasos dados hoy: " + currentSteps)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setSilent(true)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();
        
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(1, notification, android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_HEALTH);
        } else {
            startForeground(1, notification);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    @Nullable
    @Override
    public IBinder onBind(Intent intent) { return null; }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, "Contador de Pasos",
                    NotificationManager.IMPORTANCE_LOW);
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    private void startNotificationListener() {
        new Thread(() -> {
            java.io.BufferedReader reader = null;
            java.net.HttpURLConnection conn = null;
            try {
                com.gymtrack.app.network.AuthRepository authRepository = new com.gymtrack.app.network.AuthRepository(this);
                String token = authRepository.getToken();
                if (token == null) {
                    // Si no hay token, esperar 10 segundos y reintentar
                    try { Thread.sleep(10000); } catch (InterruptedException ignored) {}
                    startNotificationListener();
                    return;
                }

                java.net.URL url = new java.net.URL("http://10.0.2.2:8080/api/client/notifications/sse");
                conn = (java.net.HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Authorization", "Bearer " + token);
                conn.setRequestProperty("Accept", "text/event-stream");
                conn.setReadTimeout(0); // Timeout infinito para canal de persistencia

                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {
                    reader = new java.io.BufferedReader(new java.io.InputStreamReader(conn.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.startsWith("data:")) {
                            String data = line.substring(5).trim();
                            // Ignorar el mensaje de conexión inicial
                            if (!data.contains("Conexión de notificaciones establecida con éxito")) {
                                showSystemNotification("GymTrack", data);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (reader != null) reader.close();
                } catch (Exception ignored) {}
                if (conn != null) conn.disconnect();

                // Reintentar conexión tras 5 segundos ante caídas
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ignored) {}
                startNotificationListener();
            }
        }).start();
    }

    private void showSystemNotification(String title, String message) {
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "gymtrack_coach_notifications";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Notificaciones de Entrenador",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Notificaciones sobre comentarios y feedback de tu entrenador");
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        if (manager != null) {
            manager.notify((int) System.currentTimeMillis(), builder.build());
        }
    }
}
