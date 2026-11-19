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
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.HashMap;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AgendarCitaActivity extends AppCompatActivity {

    // 1. Declaración de variables para los elementos de la interfaz
    private EditText etFecha, etHora, etMotivoOtro;
    private Spinner spinnerMotivo, spinnerDoctor;
    private Button btnConfirmar;
    private DatabaseHelper dbHelper;
    private String nombrePacienteFinal; // Variable global corregida para evitar errores de alcance

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agendar_cita);

        // Recibimos el nombre del paciente enviado por el Intent de manera segura
        String pacienteActual = getIntent().getStringExtra("paciente_seleccionado");
        nombrePacienteFinal = (pacienteActual != null && !pacienteActual.isEmpty()) ? pacienteActual : "Paciente General";

        // Inicializamos la base de datos
        dbHelper = new DatabaseHelper(this);

        // 2. Enlazamos las variables con los IDs del diseño XML
        spinnerDoctor = findViewById(R.id.spinnerDoctor);
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
                        etHora.setText(String.format("%02d:%02d", selectedHour, selectedMinute));
                    }
                }, hour, minute, true);
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

        spinnerMotivo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (motivos[position].equals("Otro...")) {
                    etMotivoOtro.setVisibility(View.VISIBLE);
                } else {
                    etMotivoOtro.setVisibility(View.GONE);
                    etMotivoOtro.setText("");
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
        final String doctorSeleccionado = spinnerDoctor.getSelectedItem().toString();
        final String fecha = etFecha.getText().toString().trim();
        final String hora = etHora.getText().toString().trim();
        String motivoSeleccionado = spinnerMotivo.getSelectedItem().toString();
        final String motivoFinal = motivoSeleccionado.equals("Otro...") ? etMotivoOtro.getText().toString().trim() : motivoSeleccionado;

        if (fecha.isEmpty() || hora.isEmpty() || motivoFinal.isEmpty()) {
            Toast.makeText(this, "Por favor completa la fecha, hora y motivo", Toast.LENGTH_SHORT).show();
            return;
        }

        btnConfirmar.setEnabled(false);

        // 1. Enviar cita a la API REST central (FastAPI + PostgreSQL)
        Map<String, Object> citaData = new HashMap<>();
        citaData.put("nombre_paciente", nombrePacienteFinal);
        citaData.put("doctor", doctorSeleccionado);
        citaData.put("fecha", fecha);
        citaData.put("hora", hora);
        citaData.put("motivo", motivoFinal);

        RetrofitClient.getApiService().agendarCita(citaData).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                btnConfirmar.setEnabled(true);
                if (response.isSuccessful()) {
                    // Guardar también copia en base local
                    dbHelper.insertarCita(nombrePacienteFinal, doctorSeleccionado, fecha, hora, motivoFinal);
                    Toast.makeText(AgendarCitaActivity.this, "✅ ¡Cita sincronizada y agendada con éxito!", Toast.LENGTH_LONG).show();
                    programarRecordatorio(fecha, hora, doctorSeleccionado);
                    finish();
                } else if (response.code() == 409) {
                    Toast.makeText(AgendarCitaActivity.this, "❌ El " + doctorSeleccionado + " ya tiene una cita agendada en esa fecha y hora. Por favor, elige otra.", Toast.LENGTH_LONG).show();
                } else {
                    guardarCitaLocal(doctorSeleccionado, fecha, hora, motivoFinal);
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                btnConfirmar.setEnabled(true);
                // Si la API no responde, guardar en la base local
                guardarCitaLocal(doctorSeleccionado, fecha, hora, motivoFinal);
            }
        });
    }

    private void guardarCitaLocal(String doctorSeleccionado, String fecha, String hora, String motivoFinal) {
        if (dbHelper.verificarDisponibilidadHorario(fecha, hora, doctorSeleccionado)) {
            Toast.makeText(this, "❌ El " + doctorSeleccionado + " ya tiene una cita agendada en esa fecha y hora. Por favor, elige otra.", Toast.LENGTH_LONG).show();
            return;
        }

        boolean guardado = dbHelper.insertarCita(nombrePacienteFinal, doctorSeleccionado, fecha, hora, motivoFinal);
        if (guardado) {
            Toast.makeText(this, "✅ ¡Cita guardada en base local!", Toast.LENGTH_LONG).show();
            programarRecordatorio(fecha, hora, doctorSeleccionado);
            finish();
        } else {
            Toast.makeText(this, "Error al agendar la cita", Toast.LENGTH_SHORT).show();
        }
    }

    private void programarRecordatorio(String fecha, String hora, String doctor) {
        try {
            String fechaHoraCita = fecha + " " + hora;
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            Date date = sdf.parse(fechaHoraCita);

            if (date != null) {
                long tiempoCitaEnMilisegundos = date.getTime();
                long tiempoRecordatorio = System.currentTimeMillis() + 10000;

                Intent intent = new Intent(this, RecordatorioReceiver.class);
                intent.putExtra("fecha", fecha);
                intent.putExtra("hora", hora);
                intent.putExtra("doctor", doctor);

                PendingIntent pendingIntent = PendingIntent.getBroadcast(
                        this,
                        0,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
                );

                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

                if (alarmManager != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
                        Toast.makeText(this, "La app requiere permiso para programar alarmas exactas.", Toast.LENGTH_LONG).show();
                        return;
                    }
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, tiempoRecordatorio, pendingIntent);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Aviso: No se pudo programar la notificación automática.", Toast.LENGTH_SHORT).show();
        }
    }
}