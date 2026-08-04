package com.example.proyecto_integrador;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class VerHistorialClinicoActivity extends AppCompatActivity {

    private ListView lvPacientes;
    private DatabaseHelper dbHelper;
    private ArrayList<String> listaPacientes;
    private ArrayAdapter<String> adaptador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_historial_clinico);

        lvPacientes = findViewById(R.id.lvPacientes);
        dbHelper = new DatabaseHelper(this);
        listaPacientes = new ArrayList<>();

        cargarPacientes();
    }

    private void cargarPacientes() {
        Cursor cursor = dbHelper.obtenerPacientes();

        if (cursor.getCount() == 0) {
            Toast.makeText(this, "No hay expedientes clínicos registrados", Toast.LENGTH_SHORT).show();
            return;
        }

        while (cursor.moveToNext()) {
            // Asumiendo el orden de columnas: 0:ID, 1:NOMBRE, 2:EDAD, 3:ALERGIAS, 4:TRATAMIENTO
            String nombre = cursor.getString(1);
            int edad = cursor.getInt(2);
            String alergias = cursor.getString(3);
            String tratamiento = cursor.getString(4);

            String pacienteFormateado = "👤 Nombre: " + nombre +
                    "\n🎂 Edad: " + edad + " años" +
                    "\n⚠️ Alergias: " + alergias +
                    "\n🦷 Tratamiento: " + tratamiento;
            listaPacientes.add(pacienteFormateado);
        }

        adaptador = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listaPacientes);
        lvPacientes.setAdapter(adaptador);
    }
}