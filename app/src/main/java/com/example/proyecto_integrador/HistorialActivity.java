package com.example.proyecto_integrador;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class HistorialActivity extends AppCompatActivity {

    private ListView lvCitas;
    private DatabaseHelper dbHelper;
    private ArrayList<String> listaCitas;
    private ArrayList<Integer> listaIds; // Nueva lista para guardar los IDs ocultos
    private ArrayAdapter<String> adaptador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial);

        lvCitas = findViewById(R.id.lvCitas);
        dbHelper = new DatabaseHelper(this);
        listaCitas = new ArrayList<>();
        listaIds = new ArrayList<>(); // Inicializamos la lista de IDs

        cargarCitas();

        // Detectar toque prolongado en un elemento de la lista
        lvCitas.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                mostrarDialogoEliminar(position);
                return true;
            }
        });
    }

    private void cargarCitas() {
        // Limpiamos las listas por si recargamos los datos
        listaCitas.clear();
        listaIds.clear();

        Cursor cursor = dbHelper.obtenerCitas();

        if (cursor.getCount() == 0) {
            Toast.makeText(this, "No hay citas registradas", Toast.LENGTH_SHORT).show();
            return;
        }

        while (cursor.moveToNext()) {
            // Asumimos que la columna 0 es el ID_CITA
            int idCita = cursor.getInt(0);
            String fecha = cursor.getString(1);
            String hora = cursor.getString(2);
            String motivo = cursor.getString(3);

            // Guardamos el ID en nuestra lista paralela
            listaIds.add(idCita);

            String citaFormateada = "📅 Fecha: " + fecha + "\n⏰ Hora: " + hora + "\n🩺 Motivo: " + motivo;
            listaCitas.add(citaFormateada);
        }

        adaptador = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listaCitas);
        lvCitas.setAdapter(adaptador);
    }

    // Método para mostrar la alerta de confirmación
    private void mostrarDialogoEliminar(final int posicion) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Cancelar Cita");
        builder.setMessage("¿Estás seguro de que deseas cancelar y eliminar esta cita?");

        builder.setPositiveButton("Sí, eliminar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Sacamos el ID correspondiente a la posición tocada
                int idParaBorrar = listaIds.get(posicion);

                boolean eliminado = dbHelper.eliminarCita(idParaBorrar);
                if (eliminado) {
                    Toast.makeText(HistorialActivity.this, "Cita eliminada", Toast.LENGTH_SHORT).show();
                    // Quitamos los datos de las listas y actualizamos la vista
                    listaCitas.remove(posicion);
                    listaIds.remove(posicion);
                    adaptador.notifyDataSetChanged();
                } else {
                    Toast.makeText(HistorialActivity.this, "Error al eliminar", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }
}