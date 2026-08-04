package com.example.proyecto_integrador;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class InventarioActivity extends AppCompatActivity {

    private EditText etNombre, etCantidad, etCategoria;
    private Button btnGuardar;
    private ListView lvInventario;
    private DatabaseHelper dbHelper;
    private ArrayList<String> listaMateriales;
    private ArrayAdapter<String> adaptador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventario);

        dbHelper = new DatabaseHelper(this);

        etNombre = findViewById(R.id.etNombreMaterial);
        etCantidad = findViewById(R.id.etCantidadMaterial);
        etCategoria = findViewById(R.id.etCategoriaMaterial);
        btnGuardar = findViewById(R.id.btnGuardarMaterial);
        lvInventario = findViewById(R.id.lvInventario);

        listaMateriales = new ArrayList<>();
        cargarInventario();

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarMaterial();
            }
        });
    }

    private void guardarMaterial() {
        String nombre = etNombre.getText().toString().trim();
        String cantidadStr = etCantidad.getText().toString().trim();
        String categoria = etCategoria.getText().toString().trim();

        if (nombre.isEmpty() || cantidadStr.isEmpty() || categoria.isEmpty()) {
            Toast.makeText(this, "Por favor llena todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        int cantidad = Integer.parseInt(cantidadStr);

        boolean insertado = dbHelper.insertarMaterial(nombre, cantidad, categoria);

        if (insertado) {
            Toast.makeText(this, "Material registrado con éxito", Toast.LENGTH_SHORT).show();
            etNombre.setText("");
            etCantidad.setText("");
            etCategoria.setText("");
            cargarInventario(); // Recargamos la lista automáticamente
        } else {
            Toast.makeText(this, "Error al registrar el material", Toast.LENGTH_SHORT).show();
        }
    }

    private void cargarInventario() {
        listaMateriales.clear();
        Cursor cursor = dbHelper.obtenerInventario();

        if (cursor.getCount() == 0) {
            listaMateriales.add("No hay materiales en el inventario.");
        } else {
            while (cursor.moveToNext()) {
                String nombre = cursor.getString(1);
                int cantidad = cursor.getInt(2);
                String categoria = cursor.getString(3);

                String item = "📦 " + nombre + "\n🔢 Cantidad: " + cantidad + "\n🏷️ Categoría: " + categoria;
                listaMateriales.add(item);
            }
        }

        adaptador = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listaMateriales);
        lvInventario.setAdapter(adaptador);
    }
}