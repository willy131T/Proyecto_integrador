package com.example.proyecto_integrador;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class RegistrarDiagnosticoActivity extends AppCompatActivity {

    private Spinner spinnerCitas;
    private EditText etProcedimiento, etMedicamentos;
    private Button btnGuardar;
    private DatabaseHelper dbHelper;
    private ArrayList<String> listaCitasStr;
    private String nombrePaciente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_diagnostico);

        dbHelper = new DatabaseHelper(this);
        nombrePaciente = getIntent().getStringExtra("nombre_paciente");
        if (nombrePaciente == null) {
            nombrePaciente = "Paciente General";
        }

        spinnerCitas = findViewById(R.id.spinnerCitasPaciente);
        etProcedimiento = findViewById(R.id.etProcedimientoClinico);
        etMedicamentos = findViewById(R.id.etMedicamentosClinico);
        btnGuardar = findViewById(R.id.btnGuardarDiagnosticoFinal);

        cargarCitasEnSpinner();

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarDiagnostico();
            }
        });
    }

    private void cargarCitasEnSpinner() {
        listaCitasStr = new ArrayList<>();
        Cursor cursor = dbHelper.obtenerCitas();

        if (cursor == null || cursor.getCount() == 0) {
            listaCitasStr.add("Sin citas previas registradas");
        } else {
            while (cursor.moveToNext()) {
                String fecha = cursor.getString(cursor.getColumnIndexOrThrow("fecha"));
                String hora = cursor.getString(cursor.getColumnIndexOrThrow("hora"));
                String motivo = cursor.getString(cursor.getColumnIndexOrThrow("motivo"));
                listaCitasStr.add("Cita: " + fecha + " [" + hora + "] - " + motivo);
            }
        }
        if (cursor != null) {
            cursor.close();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, listaCitasStr);
        spinnerCitas.setAdapter(adapter);
    }

    private void guardarDiagnostico() {
        String procedimiento = etProcedimiento.getText().toString().trim();
        String medicamentos = etMedicamentos.getText().toString().trim();
        String citaSeleccionada = spinnerCitas.getSelectedItem() != null ? spinnerCitas.getSelectedItem().toString() : "Sin cita";

        if (procedimiento.isEmpty()) {
            Toast.makeText(this, "El procedimiento realizado es obligatorio", Toast.LENGTH_SHORT).show();
            return;
        }

        String fechaActual = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());

        // 1. Guardar en la nueva tabla de diagnósticos independientes
        boolean guardadoTabla = dbHelper.insertarDiagnostico(nombrePaciente, citaSeleccionada, procedimiento, medicamentos, fechaActual);

        // 2. Actualizar también el tratamiento activo para que el paciente lo vea reflejado de inmediato
        String tratamientoCompleto = "📅 " + citaSeleccionada + "\n🦷 Proc: " + procedimiento +
                (medicamentos.isEmpty() ? "" : "\n💊 Receta: " + medicamentos);
        dbHelper.actualizarTratamientoPaciente(nombrePaciente, tratamientoCompleto);

        if (guardadoTabla) {
            Toast.makeText(this, "¡Diagnóstico guardado en el historial clínico!", Toast.LENGTH_LONG).show();
            finish();
        } else {
            Toast.makeText(this, "Error al registrar el diagnóstico", Toast.LENGTH_SHORT).show();
        }
    }
}