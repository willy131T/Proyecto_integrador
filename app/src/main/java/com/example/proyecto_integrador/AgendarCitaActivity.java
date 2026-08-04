package com.example.proyecto_integrador;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;

public class AgendarCitaActivity extends AppCompatActivity {

    private EditText etFecha, etHora, etMotivoOtro;
    private Spinner spinnerMotivo;
    private Button btnConfirmar;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agendar_cita);

        dbHelper = new DatabaseHelper(this);

        etFecha = findViewById(R.id.etFechaCita);
        etHora = findViewById(R.id.etHoraCita);
        spinnerMotivo = findViewById(R.id.spinnerMotivo);
        etMotivoOtro = findViewById(R.id.etMotivoOtro);
        btnConfirmar = findViewById(R.id.btnConfirmarCita);

        // 1. Selector de fecha automático
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

        // 2. Spinner de motivos
        final String[] motivos = {
                "Limpieza dental (Profilaxis)",
                "Ortodoncia (Ajuste / Revision)",
                "Extracción dental",
                "Dolor agudo / Urgencia",
                "Revisión general",
                "Otro..."
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, motivos);
        spinnerMotivo.setAdapter(adapter);

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

        // 3. Botón confirmar
        btnConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarCita();
            }
        });
    }

    private void guardarCita() {
        String fecha = etFecha.getText().toString().trim();
        String hora = etHora.getText().toString().trim();
        String motivoSeleccionado = spinnerMotivo.getSelectedItem().toString();
        String motivoFinal = motivoSeleccionado.equals("Otro...") ? etMotivoOtro.getText().toString().trim() : motivoSeleccionado;

        if (fecha.isEmpty() || hora.isEmpty() || motivoFinal.isEmpty()) {
            Toast.makeText(this, "Por favor completa la fecha, hora y motivo", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean guardado = dbHelper.insertarCita(fecha, hora, motivoFinal);

        if (guardado) {
            Toast.makeText(this, "¡Cita agendada con éxito!", Toast.LENGTH_LONG).show();
            finish();
        } else {
            Toast.makeText(this, "Error al agendar la cita", Toast.LENGTH_SHORT).show();
        }
    }
}