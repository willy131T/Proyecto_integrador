package com.example.proyecto_integrador;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class HistorialClinicoActivity extends AppCompatActivity {

    private EditText etNombre, etEdad, etAlergias, etTratamiento;
    private Button btnGuardar;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial_clinico);

        dbHelper = new DatabaseHelper(this);

        // Enlazamos las variables con la vista XML
        etNombre = findViewById(R.id.etNombrePaciente);
        etEdad = findViewById(R.id.etEdadPaciente);
        etAlergias = findViewById(R.id.etAlergias);
        etTratamiento = findViewById(R.id.etTratamiento);
        btnGuardar = findViewById(R.id.btnGuardarHistorial);

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarDatos();
            }
        });
    }

    private void guardarDatos() {
        String nombre = etNombre.getText().toString().trim();
        String edadStr = etEdad.getText().toString().trim();
        String alergias = etAlergias.getText().toString().trim();
        String tratamiento = etTratamiento.getText().toString().trim();

        // Validamos que no dejen campos vitales en blanco
        if (nombre.isEmpty() || edadStr.isEmpty() || tratamiento.isEmpty()) {
            Toast.makeText(this, "Por favor llena los campos obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        int edad = Integer.parseInt(edadStr);

        // Mandamos a guardar a SQLite
        boolean insertado = dbHelper.agregarHistorial(nombre, edad, alergias, tratamiento);

        if (insertado) {
            Toast.makeText(this, "Historial guardado exitosamente", Toast.LENGTH_SHORT).show();
            finish(); // Cierra esta pantalla y regresa al menú principal
        } else {
            Toast.makeText(this, "Error al guardar el historial", Toast.LENGTH_SHORT).show();
        }
    }
}