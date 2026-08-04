package com.example.proyecto_integrador;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

// Aquí está la línea mágica que conecta tus IDs
import com.example.proyecto_integrador.R;




public class RegistroActivity extends AppCompatActivity {

    private EditText etNombre, etUsuario, etPassword;
    private Button btnRegistrar;
    private TextView tvVolverLogin;

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        // Inicializamos la base de datos
        dbHelper = new DatabaseHelper(this);

        // Enlazamos las variables
        etNombre = findViewById(R.id.etNombreRegistro);
        etUsuario = findViewById(R.id.etUsuarioRegistro);
        etPassword = findViewById(R.id.etPasswordRegistro);
        btnRegistrar = findViewById(R.id.btnRegistrar);
        tvVolverLogin = findViewById(R.id.tvVolverLogin);

        // Acción al hacer clic en registrar
        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrarPaciente();
            }
        });

        // Acción para volver al Login
        tvVolverLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // finish() destruye la pantalla actual y te regresa automáticamente a la anterior (Login)
                finish();
            }
        });
    }

    private void registrarPaciente() {
        String nombre = etNombre.getText().toString().trim();
        String usuario = etUsuario.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        // Por defecto al registrarse, asignamos el rol paciente y alergias en Ninguna
        String rol = "paciente";
        String alergias = "Ninguna";

        // Validamos que no haya campos vacíos
        if (nombre.isEmpty() || usuario.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor llena todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Insertamos el usuario con los 5 argumentos que pide DatabaseHelper
        boolean insertado = dbHelper.insertarUsuario(nombre, usuario, password, rol, alergias);

        if (insertado) {
            Toast.makeText(this, "Registro exitoso. Ya puedes iniciar sesión.", Toast.LENGTH_LONG).show();
            finish(); // Cierra el registro y vuelve al Login
        } else {
            Toast.makeText(this, "Error al registrar. El usuario podría ya existir.", Toast.LENGTH_SHORT).show();
        }
    }
}