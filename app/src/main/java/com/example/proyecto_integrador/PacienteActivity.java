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

        // Recibimos el nombre o usuario que inició sesión
        nombreUsuarioLogueado = getIntent().getStringExtra("nombre_usuario");
        if (nombreUsuarioLogueado == null) {
            nombreUsuarioLogueado = "Paciente";
        }

        tvBienvenida.setText("¡Hola, " + nombreUsuarioLogueado + "!");

        cargarDatosPaciente();
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
        // Refrescamos los datos por si agendó una cita o el doctor le actualizó el tratamiento
        cargarDatosPaciente();
        cargarProximaCita();
    }

    private void cargarDatosPaciente() {
        Cursor cursor = dbHelper.obtenerPacientes();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"));
                // Verificamos si coincide el nombre (o parte de él)
                if (nombre.equalsIgnoreCase(nombreUsuarioLogueado) || nombreUsuarioLogueado.contains(nombre)) {
                    int edad = cursor.getInt(cursor.getColumnIndexOrThrow("edad"));
                    String alergias = cursor.getString(cursor.getColumnIndexOrThrow("alergias"));
                    String tratamiento = cursor.getString(cursor.getColumnIndexOrThrow("tratamiento"));

                    if (alergias == null || alergias.isEmpty()) alergias = "Ninguna";
                    if (tratamiento == null || tratamiento.isEmpty()) tratamiento = "Sin tratamientos activos recientes.";

                    tvDatosInfo.setText("🎂 Edad: " + edad + " años\n⚠️ Alergias: " + alergias);
                    tvTratamientoInfo.setText(tratamiento);
                    break;
                }
            }
            cursor.close();
        }
    }

    private void cargarProximaCita() {
        Cursor cursor = dbHelper.obtenerCitas();
        if (cursor != null && cursor.getCount() > 0) {
            // Tomamos la última cita registrada como referencia para la tarjeta
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