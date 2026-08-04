package com.example.proyecto_integrador;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class PacienteActivity extends AppCompatActivity {

    private Button btnAgendarCita, btnVerCitas, btnCerrarSesion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paciente);

        btnAgendarCita = findViewById(R.id.btnAgendarCitaPaciente);
        btnVerCitas = findViewById(R.id.btnVerCitasPaciente);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesionPaciente);

        // Acción: Agendar Cita
        btnAgendarCita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PacienteActivity.this, AgendarCitaActivity.class);
                startActivity(intent);
            }
        });

        // Acción: Ver Citas
        btnVerCitas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PacienteActivity.this, HistorialActivity.class);
                startActivity(intent);
            }
        });

        // Acción: Cerrar Sesión
        btnCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PacienteActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }
}