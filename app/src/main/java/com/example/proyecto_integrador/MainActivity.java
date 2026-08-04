package com.example.proyecto_integrador;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Button btnVerPacientes, btnInventario, btnRegistrarDoctor, btnCerrarSesion;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);

        btnVerPacientes = findViewById(R.id.btnVerPacientes);
        btnInventario = findViewById(R.id.btnInventario);
        btnRegistrarDoctor = findViewById(R.id.btnRegistrarDoctor);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);

        // 1. Ir a la lista de pacientes (Desde aquí se selecciona al paciente a atender)
        btnVerPacientes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, VerHistorialClinicoActivity.class);
                startActivity(intent);
            }
        });

        // 2. Control de Inventario
        btnInventario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, InventarioActivity.class);
                startActivity(intent);
            }
        });

        // 3. Registrar Nuevo Doctor (Exclusivo Administrador)
        btnRegistrarDoctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDialogoRegistrarDoctor();
            }
        });

        // 4. Cerrar Sesión
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

    private void mostrarDialogoRegistrarDoctor() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Registrar Nuevo Doctor");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 20, 40, 20);

        final EditText inputNombre = new EditText(this);
        inputNombre.setHint("Nombre completo");
        layout.addView(inputNombre);

        final EditText inputUsuario = new EditText(this);
        inputUsuario.setHint("Nombre de usuario");
        layout.addView(inputUsuario);

        final EditText inputPassword = new EditText(this);
        inputPassword.setHint("Contraseña");
        inputPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        layout.addView(inputPassword);

        builder.setView(layout);

        builder.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String nombre = inputNombre.getText().toString().trim();
                String usuario = inputUsuario.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();

                if (nombre.isEmpty() || usuario.isEmpty() || password.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
                    return;
                }

                boolean insertado = dbHelper.insertarUsuario(nombre, usuario, password, "dentista", "Ninguna");

                if (insertado) {
                    Toast.makeText(MainActivity.this, "Doctor registrado con éxito", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Error: El usuario ya existe", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
}