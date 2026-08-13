package com.example.proyecto_integrador;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class DetallePacienteActivity extends AppCompatActivity {

    private TextView tvNombre, tvInfo;
    private Button btnVerCitas, btnNuevoDiagnostico, btnAgendarCita;
    private String nombrePaciente, infoPaciente;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_paciente);

        dbHelper = new DatabaseHelper(this);

        tvNombre = findViewById(R.id.tvNombreDetalle);
        tvInfo = findViewById(R.id.tvInfoDetalle);
        btnVerCitas = findViewById(R.id.btnVerCitasPacienteSeleccionado);
        btnNuevoDiagnostico = findViewById(R.id.btnNuevoDiagnostico);
        btnAgendarCita = findViewById(R.id.btnAgendarCitaParaEl);

        // Recibimos los datos enviados desde la lista de pacientes
        nombrePaciente = getIntent().getStringExtra("nombre_paciente");
        infoPaciente = getIntent().getStringExtra("info_paciente");

        tvNombre.setText(nombrePaciente);
        tvInfo.setText(infoPaciente);

        // 1. Botón: Ver citas e historial de este paciente
        btnVerCitas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetallePacienteActivity.this, HistorialActivity.class);
                intent.putExtra("nombre_paciente", nombrePaciente); // ⬅️ AGREGA ESTA LÍNEA
                startActivity(intent);
            }
        });

        // 2. Botón: Abrir pantalla completa para registrar diagnóstico y tratamiento
        btnNuevoDiagnostico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetallePacienteActivity.this, RegistrarDiagnosticoActivity.class);
                intent.putExtra("nombre_paciente", nombrePaciente);
                startActivity(intent);
            }
        });

        // 3. Botón: Agendar cita para este paciente
        btnAgendarCita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetallePacienteActivity.this, AgendarCitaActivity.class);
                intent.putExtra("paciente_seleccionado", nombrePaciente);
                startActivity(intent);
            }
        });
    }

    // ⬇️ AQUÍ ESTÁ EL ONRESUME UBICADO CORRECTAMENTE ABAJO DE ONCREATE
    @Override
    protected void onResume() {
        super.onResume();
        // Recarga automáticamente los datos y el tratamiento actualizado al volver de registrar el diagnóstico
        cargarDatosPacienteEnDetalle();
    }

    private void cargarDatosPacienteEnDetalle() {
        Cursor cursor = dbHelper.obtenerPacientes();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"));
                if (nombre.equalsIgnoreCase(nombrePaciente)) {
                    int edad = cursor.getInt(cursor.getColumnIndexOrThrow("edad"));
                    String alergias = cursor.getString(cursor.getColumnIndexOrThrow("alergias"));
                    String tratamiento = cursor.getString(cursor.getColumnIndexOrThrow("tratamiento"));

                    if (alergias == null || alergias.isEmpty()) alergias = "Ninguna";
                    if (tratamiento == null || tratamiento.isEmpty()) tratamiento = "Sin tratamiento registrado";

                    // Reconstruimos el texto informativo con los datos más frescos de la BD
                    infoPaciente = "🎂 Edad: " + edad + " años\n" +
                            "⚠️ Alergias: " + alergias + "\n" +
                            "🦷 Tratamiento: " + tratamiento;

                    tvInfo.setText(infoPaciente);
                    break;
                }
            }
            cursor.close();
        }
    }
}