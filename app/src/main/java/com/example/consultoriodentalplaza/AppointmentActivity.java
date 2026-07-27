package com.example.consultoriodentalplaza;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AppointmentActivity extends AppCompatActivity {

    private TextView tvServicioSeleccionado;
    private EditText etNombrePaciente, etTelefono;
    private Button btnConfirmarCita;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment);

        tvServicioSeleccionado = findViewById(R.id.tvServicioSeleccionado);
        etNombrePaciente = findViewById(R.id.etNombrePaciente);
        etTelefono = findViewById(R.id.etTelefono);
        btnConfirmarCita = findViewById(R.id.btnConfirmarCita);

        // Recibir el servicio seleccionado desde ServicesActivity
        if (getIntent().hasExtra("SERVICIO_NOMBRE")) {
            String servicio = getIntent().getStringExtra("SERVICIO_NOMBRE");
            tvServicioSeleccionado.setText("Servicio: " + servicio);
        }

        btnConfirmarCita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nombre = etNombrePaciente.getText().toString();
                if (nombre.isEmpty()) {
                    Toast.makeText(AppointmentActivity.this, "Por favor ingresa tu nombre", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AppointmentActivity.this, "¡Cita agendada con éxito para " + nombre + "!", Toast.LENGTH_LONG).show();
                    finish(); // Regresa a la pantalla anterior sin cerrar la app inesperadamente
                }
            }
        });
    }
}