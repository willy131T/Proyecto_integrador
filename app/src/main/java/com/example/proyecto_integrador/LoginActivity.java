package com.example.proyecto_integrador;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    // 1. Variables para nuestros elementos del XML (Actualizadas con los nombres correctos)
    private EditText etUsuario, etPassword;
    private Button btnIngresar;
    private TextView tvRegistro;

    // 2. Variable para conectar con nuestra base de datos
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Inicializamos la base de datos
        dbHelper = new DatabaseHelper(this);

        // Enlazamos las variables de Java con los IDs exactos del nuevo diseño XML
        etUsuario = findViewById(R.id.etUsuario);
        etPassword = findViewById(R.id.etPassword);
        btnIngresar = findViewById(R.id.btnIngresar); // Corregido aquí
        tvRegistro = findViewById(R.id.tvRegistro);   // Corregido aquí

        // Acción al hacer clic en el botón de Iniciar Sesión
        btnIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniciarSesion();
            }
        });

        // Acción al hacer clic en el texto de Registro
        tvRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegistroActivity.class);
                startActivity(intent);
            }
        });
    }

    // Método encargado de validar las credenciales
    private void iniciarSesion() {
        // Obtenemos el texto que escribió el usuario y le quitamos los espacios en blanco
        String usuario = etUsuario.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validación 1: Que no dejen los campos vacíos
        if (usuario.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor, llena todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validación 2: Consultamos a la base de datos qué rol tiene este usuario
        String rol = dbHelper.validarLogin(usuario, password);

        // Validación 3: Protegemos contra nulos para que no crashee la app si el usuario no existe
        if (rol != null) {
            if (rol.equals("dentista")) {
                // Panel completo para el Administrador / Doctor
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else if (rol.equals("paciente")) {
                // Panel limitado y específico para el Paciente
                Intent intent = new Intent(LoginActivity.this, PacienteActivity.class);
                intent.putExtra("nombre_usuario", usuario);
                startActivity(intent);
                finish();
            }
        } else {
            // Si el rol es nulo, los datos son incorrectos o el usuario no existe
            Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
        }
    }
}