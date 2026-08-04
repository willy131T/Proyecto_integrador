package com.example.proyecto_integrador;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class HistorialActivity extends AppCompatActivity {

    private ListView lvCitas;
    private DatabaseHelper dbHelper;
    private ArrayList<String> listaCitas;
    private ArrayAdapter<String> adaptador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial);

        lvCitas = findViewById(R.id.lvCitas);
        dbHelper = new DatabaseHelper(this);
        listaCitas = new ArrayList<>();

        cargarCitas();
    }

    private void cargarCitas() {
        Cursor cursor = dbHelper.obtenerCitas();

        // Verificamos si hay datos
        if (cursor.getCount() == 0) {
            Toast.makeText(this, "No hay citas registradas", Toast.LENGTH_SHORT).show();
            return;
        }

        // Recorremos el cursor fila por fila
        while (cursor.moveToNext()) {
            // NOTA: Ajusta los índices (0, 1, 2, 3...) dependiendo de cómo creaste tu tabla CITAS.
            // Si la columna 0 es el ID, la 1 es la Fecha, la 2 la Hora y la 3 el Motivo:
            String fecha = cursor.getString(1);
            String hora = cursor.getString(2);
            String motivo = cursor.getString(3);

            // Damos formato a cómo se verá cada fila en la pantalla
            String citaFormateada = "📅 Fecha: " + fecha + "\n⏰ Hora: " + hora + "\n🩺 Motivo: " + motivo;
            listaCitas.add(citaFormateada);
        }

        // Conectamos los datos con la vista
        adaptador = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listaCitas);
        lvCitas.setAdapter(adaptador);
    }
}