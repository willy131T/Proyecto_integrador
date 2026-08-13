package com.example.proyecto_integrador;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;

public class AgendarCitaActivity extends AppCompatActivity {

    // 1. Declaración de variables para los elementos de la interfaz
    private EditText etFecha, etHora, etMotivoOtro;
    private Spinner spinnerMotivo, spinnerDoctor;
    private Button btnConfirmar;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agendar_cita);

        // Inicializamos la base de datos
        dbHelper = new DatabaseHelper(this);

        // 2. Enlazamos las variables con los IDs del diseño XML
        spinnerDoctor = findViewById(R.id.spinnerDoctor); // NUEVO: Selector de doctor
        etFecha = findViewById(R.id.etFechaCita);
        etHora = findViewById(R.id.etHoraCita);
        spinnerMotivo = findViewById(R.id.spinnerMotivo);
        etMotivoOtro = findViewById(R.id.etMotivoOtro);
        btnConfirmar = findViewById(R.id.btnConfirmarCita);

        // ==========================================
        // CONFIGURACIÓN DE ELEMENTOS VISUALES
        // ==========================================

        // A) Llenar el Spinner de Doctores
        String[] doctores = {"Dr. Admin", "Dra. López", "Dr. Martínez"};
        ArrayAdapter<String> adapterDocs = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, doctores);
        spinnerDoctor.setAdapter(adapterDocs);

        // B) Selector de fecha automático (Despliega el Calendario)
        etFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int anio = calendar.get(Calendar.YEAR);
                int mes = calendar.get(Calendar.MONTH);
                int dia = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(AgendarCitaActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                // Formateamos para que siempre tenga 2 dígitos (ej. 05/08/2026)
                                String fechaFormateada = String.format("%02d/%02d/%d", dayOfMonth, (month + 1), year);
                                etFecha.setText(fechaFormateada);
                            }
                        }, anio, mes, dia);
                datePickerDialog.show();
            }
        });

        // C) Selector de hora automático (Despliega el Reloj)
        etHora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);

                TimePickerDialog mTimePicker = new TimePickerDialog(AgendarCitaActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        // Formateamos para que siempre tenga 2 dígitos (ej. 09:05)
                        etHora.setText(String.format("%02d:%02d", selectedHour, selectedMinute));
                    }
                }, hour, minute, true); // 'true' es para usar formato de 24 horas
                mTimePicker.show();
            }
        });

        // D) Configurar Spinner de motivos y la caja de texto extra ("Otro...")
        final String[] motivos = {
                "Limpieza dental (Profilaxis)",
                "Ortodoncia (Ajuste / Revision)",
                "Extracción dental",
                "Dolor agudo / Urgencia",
                "Revisión general",
                "Otro..."
        };

        ArrayAdapter<String> adapterMotivos = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, motivos);
        spinnerMotivo.setAdapter(adapterMotivos);

        // Mostrar/Ocultar campo extra dinámicamente si eligen la opción "Otro..."
        spinnerMotivo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (motivos[position].equals("Otro...")) {
                    etMotivoOtro.setVisibility(View.VISIBLE);
                } else {
                    etMotivoOtro.setVisibility(View.GONE);
                    etMotivoOtro.setText(""); // Limpiamos el texto si cambian a un motivo predefinido
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // ==========================================
        // ACCIÓN DEL BOTÓN GUARDAR
        // ==========================================
        btnConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarCita();
            }
        });
    }

    // Método independiente que agrupa la lógica de validación y guardado en la BD
    private void guardarCita() {
        String doctorSeleccionado = spinnerDoctor.getSelectedItem().toString();
        String fecha = etFecha.getText().toString().trim();
        String hora = etHora.getText().toString().trim();
        String motivoSeleccionado = spinnerMotivo.getSelectedItem().toString();

        // Si eligió "Otro...", tomamos el texto escrito a mano, sino tomamos la opción del spinner
        String motivoFinal = motivoSeleccionado.equals("Otro...") ? etMotivoOtro.getText().toString().trim() : motivoSeleccionado;

        // Validación 1: Evitar que dejen campos en blanco
        if (fecha.isEmpty() || hora.isEmpty() || motivoFinal.isEmpty()) {
            Toast.makeText(this, "Por favor completa la fecha, hora y motivo", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validación 2 (LÓGICA INTELIGENTE): ¿El doctor está ocupado a esa hora y día?
        if (dbHelper.verificarDisponibilidadHorario(fecha, hora, doctorSeleccionado)) {
            Toast.makeText(this, "❌ El " + doctorSeleccionado + " ya tiene una cita agendada en esa fecha y hora. Por favor, elige otra.", Toast.LENGTH_LONG).show();
            return; // Detenemos el guardado para que el usuario intente con otro horario
        }

        // Si pasó todas las validaciones, procedemos a guardar (enviamos también el doctor)
        boolean guardado = dbHelper.insertarCita(doctorSeleccionado, fecha, hora, motivoFinal);

        if (guardado) {
            Toast.makeText(this, "✅ ¡Cita agendada con éxito!", Toast.LENGTH_LONG).show();

            // TODO: Aquí configuraremos más adelante el AlarmManager para el sistema de notificaciones automáticas.

            finish(); // Cerramos la pantalla actual y volvemos a la anterior
        } else {
            Toast.makeText(this, "Error al agendar la cita", Toast.LENGTH_SHORT).show();
        }
    }
}