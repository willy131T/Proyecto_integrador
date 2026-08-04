package com.example.proyecto_integrador;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

        // 2. Botón: Registrar nuevo diagnóstico / tratamiento en tiempo real
        btnNuevoDiagnostico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDialogoDiagnostico();
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

    // Ventana emergente para actualizar el tratamiento o diagnóstico del paciente actual
    private void mostrarDialogoDiagnostico() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Nuevo Diagnóstico / Tratamiento");

        final EditText inputTratamiento = new EditText(this);
        inputTratamiento.setHint("Ej. Resina en molar, Profilaxis, etc.");
        inputTratamiento.setPadding(40, 30, 40, 30);
        builder.setView(inputTratamiento);

        builder.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String nuevoTratamiento = inputTratamiento.getText().toString().trim();

                if (nuevoTratamiento.isEmpty()) {
                    Toast.makeText(DetallePacienteActivity.this, "El campo no puede estar vacío", Toast.LENGTH_SHORT).show();
                    return;
                }

                boolean actualizado = dbHelper.actualizarTratamientoPaciente(nombrePaciente, nuevoTratamiento);

                if (actualizado) {
                    Toast.makeText(DetallePacienteActivity.this, "¡Tratamiento actualizado con éxito!", Toast.LENGTH_LONG).show();
                    // Refrescamos el texto en pantalla
                    tvInfo.setText(infoPaciente.replaceAll("🦷 Tratamiento: .*", "🦷 Tratamiento: " + nuevoTratamiento));
                    infoPaciente = tvInfo.getText().toString();
                } else {
                    Toast.makeText(DetallePacienteActivity.this, "Error al actualizar el tratamiento", Toast.LENGTH_SHORT).show();
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