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

    // 1. Variables para nuestros elementos del XML
    private EditText etUsuario, etPassword;
    private Button btnLogin;
    private TextView tvRegistrarse;

    // 2. Variable para conectar con nuestra base de datos
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login); // Vincula con el XML que acabamos de crear

        // Inicializamos la base de datos
        dbHelper = new DatabaseHelper(this);

        // Enlazamos las variables de Java con los IDs del XML
        etUsuario = findViewById(R.id.etUsuario);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegistrarse = findViewById(R.id.tvRegistrarse);

        // Acción al hacer clic en el botón de Iniciar Sesión
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniciarSesion();
            }
        });

        // Acción al hacer clic en el texto de Registro
        tvRegistrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent para saltar a la pantalla de Registro (Siguiente plantilla)
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
            return; // Detiene la ejecución para no consultar la base de datos en vano
        }

        // Validación 2: Consultamos a la base de datos qué rol tiene este usuario
        String rol = dbHelper.validarLogin(usuario, password);

        if (rol != null) {
            // Si el rol NO es nulo, significa que el usuario y la contraseña son correctos
            Toast.makeText(this, "Bienvenido, entrando como: " + rol, Toast.LENGTH_SHORT).show();

            // Lógica de ruteo dependiendo si es el Doctor o el Paciente
            if (rol.equals("dentista")) {
                // Lo mandamos al panel del doctor (MainActivity por ahora)
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish(); // Destruimos la pantalla de login para que no pueda volver con la flecha de "atrás"
            } else if (rol.equals("paciente")) {
                // Lo mandamos al panel del paciente (Usaremos MainActivity por ahora hasta crear el panel del paciente)
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        } else {
            // Si el rol es nulo, los datos son incorrectos
            Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
        }
    }
}