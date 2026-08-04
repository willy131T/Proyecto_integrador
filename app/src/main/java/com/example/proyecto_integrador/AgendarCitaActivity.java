package com.example.proyecto_integrador;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AgendarCitaActivity extends AppCompatActivity {

    private EditText etFecha, etHora, etMotivo;
    private Button btnGuardar;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agendar_cita);

        // 1. Inicializamos la base de datos
        dbHelper = new DatabaseHelper(this);

        // 2. Enlazamos las variables con los IDs del XML
        etFecha = findViewById(R.id.etFechaCita);
        etHora = findViewById(R.id.etHoraCita);
        etMotivo = findViewById(R.id.etMotivoCita);
        btnGuardar = findViewById(R.id.btnGuardarCita);

        // 3. Acción al hacer clic en el botón de guardar
        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Capturamos el texto de los campos
                String fecha = etFecha.getText().toString().trim();
                String hora = etHora.getText().toString().trim();
                String motivo = etMotivo.getText().toString().trim();

                // Validación simple: que no estén vacíos
                if (fecha.isEmpty() || hora.isEmpty() || motivo.isEmpty()) {
                    Toast.makeText(AgendarCitaActivity.this, "Por favor, llena todos los campos", Toast.LENGTH_SHORT).show();
                    return; // Detiene la ejecución aquí si faltan datos
                }

                // Inserción en la base de datos
                boolean insertado = dbHelper.insertarCita(fecha, hora, motivo);

                if (insertado) {
                    Toast.makeText(AgendarCitaActivity.this, "Cita agendada correctamente", Toast.LENGTH_SHORT).show();
                    finish(); // Cierra esta pantalla y regresa automáticamente al MainActivity
                } else {
                    Toast.makeText(AgendarCitaActivity.this, "Error al guardar la cita", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}