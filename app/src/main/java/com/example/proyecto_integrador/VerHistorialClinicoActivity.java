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
        listaPacientes.clear();
        Cursor cursor = dbHelper.obtenerPacientes();

        if (cursor.getCount() == 0) {
            Toast.makeText(this, "No hay expedientes clínicos registrados", Toast.LENGTH_SHORT).show();
            return;
        }

        while (cursor.moveToNext()) {
            // Usamos nombres de columnas exactos para evitar cruce de datos
            String nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"));
            int edad = cursor.getInt(cursor.getColumnIndexOrThrow("edad"));
            String alergias = cursor.getString(cursor.getColumnIndexOrThrow("alergias"));
            String tratamiento = cursor.getString(cursor.getColumnIndexOrThrow("tratamiento"));

            // Manejo por si vienen vacíos o nulos
            if (alergias == null || alergias.isEmpty()) alergias = "Ninguna";
            if (tratamiento == null || tratamiento.isEmpty()) tratamiento = "Pendiente";

            String pacienteFormateado = "👤 Nombre: " + nombre +
                    "\n🎂 Edad: " + edad + " años" +
                    "\n⚠️ Alergias: " + alergias +
                    "\n🦷 Tratamiento: " + tratamiento;
            listaPacientes.add(pacienteFormateado);
        }

        adaptador = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listaPacientes);
        lvPacientes.setAdapter(adaptador);
        cursor.close();
    }
}