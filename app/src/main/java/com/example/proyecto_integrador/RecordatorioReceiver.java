package com.example.proyecto_integrador;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;

public class RecordatorioReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "canal_citas";

    @Override
    public void onReceive(Context context, Intent intent) {
        // Obtenemos los datos que le mandamos desde AgendarCitaActivity
        String fecha = intent.getStringExtra("fecha");
        String hora = intent.getStringExtra("hora");
        String doctor = intent.getStringExtra("doctor");

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // En Android 8.0 o superior, es obligatorio crear un "Canal de Notificación"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Recordatorios de Citas",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Canal para notificar a los pacientes");
            notificationManager.createNotificationChannel(channel);
        }

        // Si tocan la notificación, abrimos el Login de la app
        Intent i = new Intent(context, LoginActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, i, PendingIntent.FLAG_IMMUTABLE);

        // Construimos el diseño visual de la notificación
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_popup_reminder) // Icono de relojito nativo
                .setContentTitle("🦷 ¡Recordatorio de tu Cita Dental!")
                .setContentText("Mañana tienes una cita con el " + doctor + " a las " + hora + " hrs.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        // Lanzamos la notificación (el ID 1 es para que se sobreescriba si hay varias juntas)
        notificationManager.notify(1, builder.build());
    }
}