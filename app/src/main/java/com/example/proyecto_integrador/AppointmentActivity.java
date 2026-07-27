package com.example.proyecto_integrador;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AppointmentActivity extends AppCompatActivity {

    private EditText etNombre, etFecha, etHora;
    private Button btnConfirmarCita;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment);

        etNombre = findViewById(R.id.etNombre);
        etFecha = findViewById(R.id.etFecha);
        etHora = findViewById(R.id.etHora);
        btnConfirmarCita = findViewById(R.id.btnConfirmarCita);

        btnConfirmarCita.setOnClickListener(v -> {
            String nombre = etNombre.getText().toString().trim();
            String fecha = etFecha.getText().toString().trim();
            String hora = etHora.getText().toString().trim();

            if (nombre.isEmpty() || fecha.isEmpty() || hora.isEmpty()) {
                Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Cita agendada para " + nombre, Toast.LENGTH_LONG).show();
                finish(); // Regresa a la pantalla anterior
            }
        });
    }
}