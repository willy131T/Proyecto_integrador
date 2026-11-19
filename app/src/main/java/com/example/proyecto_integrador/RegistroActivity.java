package com.example.proyecto_integrador;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.HashMap;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
        final String nombre = etNombre.getText().toString().trim();
        final String usuario = etUsuario.getText().toString().trim();
        final String password = etPassword.getText().toString().trim();
        String edadStr = etEdad.getText().toString().trim();
        final String alergias = etAlergias.getText().toString().trim().isEmpty() ? "Ninguna" : etAlergias.getText().toString().trim();

        if (nombre.isEmpty() || usuario.isEmpty() || password.isEmpty() || edadStr.isEmpty()) {
            Toast.makeText(this, "Por favor completa los campos obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        final int edad;
        try {
            edad = Integer.parseInt(edadStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Ingresa una edad válida", Toast.LENGTH_SHORT).show();
            return;
        }

        btnRegistrar.setEnabled(false);

        // 1. Enviar registro a la API central
        Map<String, Object> data = new HashMap<>();
        data.put("nombre", nombre);
        data.put("usuario", usuario);
        data.put("password", password);
        data.put("edad", edad);
        data.put("alergias", alergias);

        RetrofitClient.getApiService().registrarPaciente(data).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                btnRegistrar.setEnabled(true);
                if (response.isSuccessful()) {
                    // Guardar copia local
                    dbHelper.insertarUsuarioCompleto(nombre, usuario, password, "paciente", edad, alergias, "Sin tratamiento");
                    Toast.makeText(RegistroActivity.this, "¡Registro sincronizado exitosamente!", Toast.LENGTH_LONG).show();
                    finish();
                } else if (response.code() == 400) {
                    Toast.makeText(RegistroActivity.this, "El nombre de usuario ya existe en el servidor.", Toast.LENGTH_SHORT).show();
                } else {
                    registrarLocal(nombre, usuario, password, edad, alergias);
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                btnRegistrar.setEnabled(true);
                registrarLocal(nombre, usuario, password, edad, alergias);
            }
        });
    }

    private void registrarLocal(String nombre, String usuario, String password, int edad, String alergias) {
        boolean insertado = dbHelper.insertarUsuarioCompleto(nombre, usuario, password, "paciente", edad, alergias, "Sin tratamiento");
        if (insertado) {
            Toast.makeText(this, "¡Registro guardado en modo local!", Toast.LENGTH_LONG).show();
            finish();
        } else {
            Toast.makeText(this, "Error: El usuario ya existe localmente", Toast.LENGTH_SHORT).show();
        }
    }
}