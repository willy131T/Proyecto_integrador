package com.example.proyecto_integrador;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class RegistroActivity extends AppCompatActivity {

    private EditText etNombre, etUsuario, etPassword, etEdad, etAlergias;
    private Button btnRegistrar;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        dbHelper = new DatabaseHelper(this);

        etNombre = findViewById(R.id.etRegNombre);
        etUsuario = findViewById(R.id.etRegUsuario);
        etPassword = findViewById(R.id.etRegPassword);
        etEdad = findViewById(R.id.etRegEdad);
        etAlergias = findViewById(R.id.etRegAlergias);
        btnRegistrar = findViewById(R.id.btnRegistrarseFinal);

        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrarPaciente();
            }
        });
    }

    private void registrarPaciente() {
        String nombre = etNombre.getText().toString().trim();
        String usuario = etUsuario.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String edadStr = etEdad.getText().toString().trim();
        String alergias = etAlergias.getText().toString().trim();

        if (nombre.isEmpty() || usuario.isEmpty() || password.isEmpty() || edadStr.isEmpty()) {
            Toast.makeText(this, "Por favor completa los campos obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        int edad = Integer.parseInt(edadStr);
        if (alergias.isEmpty()) alergias = "Ninguna";

        // Insertamos en la base de datos con rol paciente
        boolean insertado = dbHelper.insertarUsuarioCompleto(nombre, usuario, password, "paciente", edad, alergias, "Sin tratamiento");

        if (insertado) {
            Toast.makeText(this, "¡Registro exitoso! Ya puedes iniciar sesión", Toast.LENGTH_LONG).show();
            finish();
        } else {
            Toast.makeText(this, "Error: El usuario ya existe o faltaron datos", Toast.LENGTH_SHORT).show();
        }
    }
}