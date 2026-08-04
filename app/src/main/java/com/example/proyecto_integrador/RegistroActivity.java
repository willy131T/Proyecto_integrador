package com.example.proyecto_integrador;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class RegistroActivity extends AppCompatActivity {

    private EditText etNombre, etUsuario, etPassword;
    private Button btnRegistrar;
    private TextView tvVolverLogin;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        dbHelper = new DatabaseHelper(this);

        etNombre = findViewById(R.id.etNombreRegistro);
        etUsuario = findViewById(R.id.etUsuarioRegistro);
        etPassword = findViewById(R.id.etPasswordRegistro);
        btnRegistrar = findViewById(R.id.btnRegistrar);
        tvVolverLogin = findViewById(R.id.tvVolverLogin);

        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrarPaciente();
            }
        });

        tvVolverLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void registrarPaciente() {
        String nombre = etNombre.getText().toString().trim();
        String usuario = etUsuario.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String rol = "paciente"; // Fijo para pacientes
        String alergias = "Ninguna";

        if (nombre.isEmpty() || usuario.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor llena todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean insertado = dbHelper.insertarUsuario(nombre, usuario, password, rol, alergias);

        if (insertado) {
            Toast.makeText(this, "¡Registro exitoso! Ya puedes iniciar sesión.", Toast.LENGTH_LONG).show();
            finish();
        } else {
            Toast.makeText(this, "Error al registrar. El usuario podría ya existir.", Toast.LENGTH_SHORT).show();
        }
    }
}