package com.example.proyecto_integrador;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    // Declara la variable arriba con los demás botones
    private Button btnAgendarCita, btnVerHistorial, btnHistorialClinico, btnVerPacientes, btnCerrarSesion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Enlazamos las variables con los botones del XML
        btnAgendarCita = findViewById(R.id.btnAgendarCita);
        btnVerHistorial = findViewById(R.id.btnVerHistorial);
        btnHistorialClinico = findViewById(R.id.btnHistorialClinico); // Botón para el expediente clínico
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);
        // Dentro deonCreate():
        btnVerPacientes = findViewById(R.id.btnVerPacientes);

        btnVerPacientes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, VerHistorialClinicoActivity.class);
                startActivity(intent);
            }
        });

        // Acción: Botón Agendar Cita
        btnAgendarCita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AgendarCitaActivity.class);
                startActivity(intent);
            }
        });

        // Acción: Botón Ver Historial de Citas
        btnVerHistorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, HistorialActivity.class);
                startActivity(intent);
            }
        });

        // Acción: Botón Historial Clínico (Pacientes)
        btnHistorialClinico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, HistorialClinicoActivity.class);
                startActivity(intent);
            }
        });

        // Acción: Botón Cerrar Sesión
        btnCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }
}