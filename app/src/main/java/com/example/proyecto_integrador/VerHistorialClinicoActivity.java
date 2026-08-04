package com.example.proyecto_integrador;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
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

        // Evento para detectar cuando el doctor selecciona un paciente de la lista
        lvPacientes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Obtenemos el texto completo del elemento seleccionado
                String pacienteSeleccionado = listaPacientes.get(position);

                // Preparamos el Intent para abrir la pantalla de detalles del paciente
                Intent intent = new Intent(VerHistorialClinicoActivity.this, DetallePacienteActivity.class);

                // Extraemos el nombre limpio y pasamos la información completa
                intent.putExtra("nombre_paciente", pacienteSeleccionado.split("\n")[0].replace("👤 Nombre: ", ""));
                intent.putExtra("info_paciente", pacienteSeleccionado);
                startActivity(intent);
            }
        });
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