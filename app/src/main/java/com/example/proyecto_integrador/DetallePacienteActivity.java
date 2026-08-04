package com.example.proyecto_integrador;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
                startActivity(intent);
            }
        });

        // 2. Botón: Registrar diagnóstico y tratamiento con procedimiento y medicamentos
        // Botón: Abrir pantalla completa para registrar diagnóstico y tratamiento
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

    private void mostrarDialogoDiagnostico() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Registrar Diagnóstico y Tratamiento");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 20, 40, 20);

        final EditText inputProcedimiento = new EditText(this);
        inputProcedimiento.setHint("Procedimiento realizado (Ej. Resina, Limpieza)");
        inputProcedimiento.setPadding(0, 10, 0, 20);
        layout.addView(inputProcedimiento);

        final EditText inputMedicamentos = new EditText(this);
        inputMedicamentos.setHint("Medicamentos recetados (Ej. Amoxicilina 500mg)");
        inputMedicamentos.setPadding(0, 10, 0, 10);
        layout.addView(inputMedicamentos);

        builder.setView(layout);

        builder.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String procedimiento = inputProcedimiento.getText().toString().trim();
                String medicamentos = inputMedicamentos.getText().toString().trim();

                if (procedimiento.isEmpty()) {
                    Toast.makeText(DetallePacienteActivity.this, "El procedimiento es obligatorio", Toast.LENGTH_SHORT).show();
                    return;
                }

                String tratamientoCompleto = procedimiento + (medicamentos.isEmpty() ? "" : " | Receta: " + medicamentos);

                boolean actualizado = dbHelper.actualizarTratamientoPaciente(nombrePaciente, tratamientoCompleto);

                if (actualizado) {
                    Toast.makeText(DetallePacienteActivity.this, "¡Diagnóstico guardado con éxito!", Toast.LENGTH_LONG).show();
                    tvInfo.setText(infoPaciente.replaceAll("🦷 Tratamiento: .*", "🦷 Tratamiento: " + tratamientoCompleto));
                    infoPaciente = tvInfo.getText().toString();
                } else {
                    Toast.makeText(DetallePacienteActivity.this, "Error al guardar el diagnóstico", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
}