package com.example.proyecto_integrador;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class HistorialActivity extends AppCompatActivity {

    private TextView tvHistorial;
    private DatabaseHelper dbHelper;
    private String nombrePaciente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial);

        tvHistorial = findViewById(R.id.tvHistorial);
        dbHelper = new DatabaseHelper(this);

        // Recibimos el nombre del paciente desde DetallePacienteActivity
        nombrePaciente = getIntent().getStringExtra("nombre_paciente");
        if (nombrePaciente == null) nombrePaciente = "Desconocido";

        cargarHistorialClinico();
    }

    private void cargarHistorialClinico() {
        Cursor cursor = dbHelper.obtenerDiagnosticosParaPaciente(nombrePaciente);
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
                sb.append("----------------------------------\n\n");
            }
            cursor.close();
            tvHistorial.setText(sb.toString().trim());
        } else {
            tvHistorial.setText("El paciente " + nombrePaciente + " aún no tiene diagnósticos o historial clínico registrado.");
        }
    }
}