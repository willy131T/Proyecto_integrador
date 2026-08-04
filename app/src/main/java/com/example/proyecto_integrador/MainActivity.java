package com.example.proyecto_integrador;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Button btnAgendarCita, btnVerHistorial, btnCerrarSesion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Enlazamos las variables con los botones del XML
        btnAgendarCita = findViewById(R.id.btnAgendarCita);
        btnVerHistorial = findViewById(R.id.btnVerHistorial);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);

        // Acción: Botón Agendar Cita
        btnAgendarCita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Aquí pondremos el Intent para ir a AgendarCitaActivity más adelante
                Toast.makeText(MainActivity.this, "Abriendo calendario para agendar...", Toast.LENGTH_SHORT).show();
            }
        });

        // Acción: Botón Ver Historial
        btnVerHistorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Aquí pondremos el Intent para ir a HistorialActivity más adelante
                Toast.makeText(MainActivity.this, "Cargando tu historial clínico...", Toast.LENGTH_SHORT).show();
            }
        });

        // Acción: Botón Cerrar Sesión
        btnCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Regresamos a la pantalla de Login
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                // Estas "flags" limpian el historial de pantallas para que no puedan regresar al panel presionando "atrás"
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }
}