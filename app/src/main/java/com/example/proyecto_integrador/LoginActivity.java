package com.example.proyecto_integrador;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.HashMap;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    // 1. Variables para nuestros elementos del XML (Actualizadas con los nombres correctos)
    private EditText etUsuario, etPassword;
    private Button btnIngresar;
    private TextView tvRegistro;

    // 2. Variable para conectar con nuestra base de datos local (fallback)
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Inicializamos la base de datos local
        dbHelper = new DatabaseHelper(this);

        // Enlazamos las variables de Java con los IDs exactos del nuevo diseño XML
        etUsuario = findViewById(R.id.etUsuario);
        etPassword = findViewById(R.id.etPassword);
        btnIngresar = findViewById(R.id.btnIngresar);
        tvRegistro = findViewById(R.id.tvRegistro);

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
        final String usuario = etUsuario.getText().toString().trim();
        final String password = etPassword.getText().toString().trim();

        if (usuario.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor, llena todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        btnIngresar.setEnabled(false);

        // 1. Intentar autenticación centralizada contra la API REST (FastAPI / PostgreSQL)
        Map<String, String> credenciales = new HashMap<>();
        credenciales.put("usuario", usuario);
        credenciales.put("password", password);

        RetrofitClient.getApiService().login(credenciales).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                btnIngresar.setEnabled(true);
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Object> body = response.body();
                    Map<String, Object> usuarioObj = (Map<String, Object>) body.get("usuario");
                    String rol = usuarioObj != null ? (String) usuarioObj.get("rol") : null;

                    navegarSegunRol(usuario, rol);
                } else {
                    // Si el servidor respondió 401 (credenciales incorrectas)
                    loginFallbackLocal(usuario, password);
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                btnIngresar.setEnabled(true);
                // Si el servidor API está apagado o sin red, usar respaldo local
                loginFallbackLocal(usuario, password);
            }
        });
    }

    private void loginFallbackLocal(String usuario, String password) {
        String rol = dbHelper.validarLogin(usuario, password);
        if (rol != null) {
            navegarSegunRol(usuario, rol);
        } else {
            Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
        }
    }

    private void navegarSegunRol(String usuario, String rol) {
        if ("dentista".equalsIgnoreCase(rol)) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else if ("paciente".equalsIgnoreCase(rol)) {
            Intent intent = new Intent(LoginActivity.this, PacienteActivity.class);
            intent.putExtra("nombre_usuario", usuario);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Rol desconocido: " + rol, Toast.LENGTH_SHORT).show();
        }
    }
}