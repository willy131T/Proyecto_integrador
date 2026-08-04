package com.example.proyecto_integrador;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class PacienteActivity extends AppCompatActivity {

    private TextView tvBienvenida, tvDatosInfo, tvTratamientoInfo, tvProximaCita;
    private Button btnAgendar, btnCerrarSesion;
    private DatabaseHelper dbHelper;
    private String nombreUsuarioLogueado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paciente);

        dbHelper = new DatabaseHelper(this);

        tvBienvenida = findViewById(R.id.tvBienvenidaPaciente);
        tvDatosInfo = findViewById(R.id.tvDatosPacienteInfo);
        tvTratamientoInfo = findViewById(R.id.tvTratamientoPacienteInfo);
        tvProximaCita = findViewById(R.id.tvProximaCitaPaciente);
        btnAgendar = findViewById(R.id.btnAgendarCitaPaciente);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesionPaciente);

        // Recibimos el nombre del usuario que inició sesión
        nombreUsuarioLogueado = getIntent().getStringExtra("nombre_usuario");
        if (nombreUsuarioLogueado == null) {
            nombreUsuarioLogueado = "Paciente";
        }

        tvBienvenida.setText("¡Hola, " + nombreUsuarioLogueado + "!");

        cargarDatosPaciente();
        cargarDiagnosticosYTratamientos();
        cargarProximaCita();

        // Botón para agendar cita
        btnAgendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PacienteActivity.this, AgendarCitaActivity.class);
                intent.putExtra("paciente_seleccionado", nombreUsuarioLogueado);
                startActivity(intent);
            }
        });

        // Botón cerrar sesión
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

    @Override
    protected void onResume() {
        super.onResume();
        // Refrescamos toda la información al volver a la pantalla
        cargarDatosPaciente();
        cargarDiagnosticosYTratamientos();
        cargarProximaCita();
    }

    private void cargarDatosPaciente() {
        // Consultamos directo en la tabla de usuarios usando el username con el que inició sesión
        Cursor cursor = dbHelper.obtenerUsuarioPorUsername(nombreUsuarioLogueado);
        boolean encontrado = false;

        if (cursor != null && cursor.moveToFirst()) {
            // Verificamos si la tabla tiene columnas de edad y alergias
            try {
                int edad = cursor.getInt(cursor.getColumnIndexOrThrow("edad"));
                String alergias = cursor.getString(cursor.getColumnIndexOrThrow("alergias"));
                String nombreReal = cursor.getString(cursor.getColumnIndexOrThrow("nombre"));

                if (alergias == null || alergias.isEmpty()) alergias = "Ninguna";

                // Si quieres actualizar el saludo con su nombre real de registro:
                if (nombreReal != null && !nombreReal.isEmpty()) {
                    tvBienvenida.setText("¡Hola, " + nombreReal + "!");
                }

                tvDatosInfo.setText("🎂 Edad: " + edad + " años\n⚠️ Alergias: " + alergias);
                encontrado = true;
            } catch (Exception e) {
                // Por si acaso la columna de edad viene vacía o nula
                tvDatosInfo.setText("🎂 Edad: No registrada\n⚠️ Alergias: Ninguna");
            }
            cursor.close();
        }

        if (!encontrado) {
            tvDatosInfo.setText("🎂 Edad: No registrada\n⚠️ Alergias: Ninguna");
        }
    }

    private void cargarDiagnosticosYTratamientos() {
        // Consultamos la nueva tabla de diagnósticos independiente para este paciente
        Cursor cursor = dbHelper.obtenerDiagnosticosPorPaciente(nombreUsuarioLogueado);
        StringBuilder sb = new StringBuilder();

        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String citaInfo = cursor.getString(cursor.getColumnIndexOrThrow("cita_info"));
                String procedimiento = cursor.getString(cursor.getColumnIndexOrThrow("procedimiento"));
                String medicamentos = cursor.getString(cursor.getColumnIndexOrThrow("medicamentos"));
                String fecha = cursor.getString(cursor.getColumnIndexOrThrow("fecha"));

                sb.append("🗓️ Fecha: ").append(fecha).append("\n");
                sb.append("📌 ").append(citaInfo).append("\n");
                sb.append("🦷 Procedimiento: ").append(procedimiento).append("\n");
                if (medicamentos != null && !medicamentos.isEmpty()) {
                    sb.append("💊 Receta: ").append(medicamentos).append("\n");
                }
                sb.append("----------------------------------\n");
            }
            cursor.close();
            tvTratamientoInfo.setText(sb.toString().trim());
        } else {
            tvTratamientoInfo.setText("No tienes tratamientos o diagnósticos registrados todavía.");
        }
    }

    private void cargarProximaCita() {
        Cursor cursor = dbHelper.obtenerCitas();
        if (cursor != null && cursor.getCount() > 0) {
            if (cursor.moveToLast()) {
                String fecha = cursor.getString(cursor.getColumnIndexOrThrow("fecha"));
                String hora = cursor.getString(cursor.getColumnIndexOrThrow("hora"));
                String motivo = cursor.getString(cursor.getColumnIndexOrThrow("motivo"));

                tvProximaCita.setText("📅 Fecha: " + fecha + "\n⏰ Hora: " + hora + "\n🦷 Motivo: " + motivo);
            }
        } else {
            tvProximaCita.setText("No tienes citas agendadas actualmente.");
        }
        if (cursor != null) {
            cursor.close();
        }
    }
}