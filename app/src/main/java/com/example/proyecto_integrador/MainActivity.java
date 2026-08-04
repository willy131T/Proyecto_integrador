package com.example.proyecto_integrador;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    // Declara la variable arriba con los demás botones
    private Button btnAgendarCita, btnVerHistorial, btnHistorialClinico, btnVerPacientes, btnInventario, btnRegistrarDoctor, btnCerrarSesion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Enlazamos las variables con los botones del XML
        btnAgendarCita = findViewById(R.id.btnAgendarCita);
        btnVerHistorial = findViewById(R.id.btnVerHistorial);
        btnHistorialClinico = findViewById(R.id.btnHistorialClinico); // Botón para el expediente clínico
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);

        btnRegistrarDoctor = findViewById(R.id.btnRegistrarDoctor);

        btnRegistrarDoctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDialogoRegistrarDoctor();
            }
        });
        btnInventario = findViewById(R.id.btnInventario);
        btnInventario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, InventarioActivity.class);
                startActivity(intent);
            }
        });
        btnVerPacientes = findViewById(R.id.btnVerPacientes);



        btnVerPacientes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, VerHistorialClinicoActivity.class);
                startActivity(intent);
            }
        });

        // Acción: Botón Agendar Cita
        btnAgendarCita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AgendarCitaActivity.class);
                startActivity(intent);
            }
        });

        // Acción: Botón Ver Historial de Citas
        btnVerHistorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, HistorialActivity.class);
                startActivity(intent);
            }
        });

        // Acción: Botón Historial Clínico (Pacientes)
        btnHistorialClinico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, HistorialClinicoActivity.class);
                startActivity(intent);
            }
        });

        // Acción: Botón Cerrar Sesión
        btnCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    // Método para mostrar una ventana flotante de registro de doctores
    private void mostrarDialogoRegistrarDoctor() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Registrar Nuevo Doctor");

        // Creamos un diseño interno con campos de texto para el diálogo
        android.widget.LinearLayout layout = new android.widget.LinearLayout(this);
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        layout.setPadding(40, 20, 40, 20);

        final android.widget.EditText inputNombre = new android.widget.EditText(this);
        inputNombre.setHint("Nombre completo");
        layout.addView(inputNombre);

        final android.widget.EditText inputUsuario = new android.widget.EditText(this);
        inputUsuario.setHint("Nombre de usuario");
        layout.addView(inputUsuario);

        final android.widget.EditText inputPassword = new android.widget.EditText(this);
        inputPassword.setHint("Contraseña");
        inputPassword.setInputType(android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        layout.addView(inputPassword);

        builder.setView(layout);

        // Botones de acción del diálogo
        builder.setPositiveButton("Guardar", new android.content.DialogInterface.OnClickListener() {
            @Override
            public void onClick(android.content.DialogInterface dialog, int which) {
                String nombre = inputNombre.getText().toString().trim();
                String usuario = inputUsuario.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();

                if (nombre.isEmpty() || usuario.isEmpty() || password.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
                    return;
                }

                DatabaseHelper dbHelper = new DatabaseHelper(MainActivity.this);
                boolean insertado = dbHelper.insertarUsuario(nombre, usuario, password, "dentista", "Ninguna");

                if (insertado) {
                    Toast.makeText(MainActivity.this, "Doctor registrado con éxito", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Error: El usuario ya existe", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancelar", new android.content.DialogInterface.OnClickListener() {
            @Override
            public void onClick(android.content.DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
}