package com.example.proyecto_integrador;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class InventarioActivity extends AppCompatActivity {

    private EditText etNombre, etCantidad, etCategoria;
    private Button btnGuardar;
    private ListView lvInventario;
    private DatabaseHelper dbHelper;

    private ArrayList<String> listaMaterialesVisibles; // Lo que ve el usuario en la lista
    // Listas ocultas para recordar los datos exactos al hacer clic
    private ArrayList<Integer> listaIdsOcultos;
    private ArrayList<String> listaNombresOcultos;
    private ArrayList<Integer> listaCantidadesOcultas;
    private ArrayList<String> listaCategoriasOcultas;

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

        listaMaterialesVisibles = new ArrayList<>();
        listaIdsOcultos = new ArrayList<>();
        listaNombresOcultos = new ArrayList<>();
        listaCantidadesOcultas = new ArrayList<>();
        listaCategoriasOcultas = new ArrayList<>();

        cargarInventario();

        // Botón para guardar un material NUEVO
        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarMaterial();
            }
        });

        // ⬇️ NUEVO: Acción al tocar un elemento del inventario
        lvInventario.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Si la lista está vacía (y muestra el mensaje de "No hay materiales"), no hacemos nada
                if (listaIdsOcultos.get(position) == -1) return;

                // Obtenemos los datos exactos del elemento tocado
                int idMaterial = listaIdsOcultos.get(position);
                String nombreSeleccionado = listaNombresOcultos.get(position);
                int cantidadSeleccionada = listaCantidadesOcultas.get(position);
                String categoriaSeleccionada = listaCategoriasOcultas.get(position);

                mostrarMenuOpciones(idMaterial, nombreSeleccionado, cantidadSeleccionada, categoriaSeleccionada);
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
            cargarInventario();
        } else {
            Toast.makeText(this, "Error al registrar el material", Toast.LENGTH_SHORT).show();
        }
    }

    private void cargarInventario() {
        listaMaterialesVisibles.clear();
        listaIdsOcultos.clear();
        listaNombresOcultos.clear();
        listaCantidadesOcultas.clear();
        listaCategoriasOcultas.clear();

        Cursor cursor = dbHelper.obtenerInventario();

        if (cursor.getCount() == 0) {
            listaMaterialesVisibles.add("No hay materiales en el inventario.");
            listaIdsOcultos.add(-1); // ID falso para evitar errores si tocan el mensaje
        } else {
            while (cursor.moveToNext()) {
                int idReal = cursor.getInt(0); // ID de la base de datos
                String nombre = cursor.getString(1);
                int cantidad = cursor.getInt(2);
                String categoria = cursor.getString(3);

                // Guardamos los datos ocultos para cuando el doctor haga clic
                listaIdsOcultos.add(idReal);
                listaNombresOcultos.add(nombre);
                listaCantidadesOcultas.add(cantidad);
                listaCategoriasOcultas.add(categoria);

                // Guardamos el texto bonito que verá el doctor en la pantalla
                String item = "📦 " + nombre + "\n🔢 Cantidad: " + cantidad + "\n🏷️ Categoría: " + categoria;
                listaMaterialesVisibles.add(item);
            }
        }

        adaptador = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listaMaterialesVisibles);
        lvInventario.setAdapter(adaptador);
    }

    // ==========================================
    // MENÚ FLOTANTE PARA EDITAR / ELIMINAR
    // ==========================================
    private void mostrarMenuOpciones(final int idMaterial, final String nombreActual, final int cantidadActual, final String categoriaActual) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Opciones: " + nombreActual);

        String[] opciones = {"✏️ Editar Material", "🗑️ Eliminar Material"};
        builder.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    mostrarDialogoEditar(idMaterial, nombreActual, cantidadActual, categoriaActual);
                } else if (which == 1) {
                    eliminarMaterialConfirmacion(idMaterial, nombreActual);
                }
            }
        });
        builder.show();
    }

    // LÓGICA PARA EDITAR
    private void mostrarDialogoEditar(final int idMaterial, String nombreActual, int cantidadActual, String categoriaActual) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Editar: " + nombreActual);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 20, 50, 20);

        final EditText inputNombre = new EditText(this);
        inputNombre.setText(nombreActual);
        layout.addView(inputNombre);

        final EditText inputCantidad = new EditText(this);
        inputCantidad.setText(String.valueOf(cantidadActual));
        inputCantidad.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        layout.addView(inputCantidad);

        final EditText inputCategoria = new EditText(this);
        inputCategoria.setText(categoriaActual);
        layout.addView(inputCategoria);

        builder.setView(layout);

        builder.setPositiveButton("Actualizar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String nuevoNombre = inputNombre.getText().toString().trim();
                String nuevaCantStr = inputCantidad.getText().toString().trim();
                String nuevaCat = inputCategoria.getText().toString().trim();

                if (!nuevoNombre.isEmpty() && !nuevaCantStr.isEmpty()) {
                    int nuevaCantidad = Integer.parseInt(nuevaCantStr);
                    boolean actualizado = dbHelper.actualizarMaterial(idMaterial, nuevoNombre, nuevaCantidad, nuevaCat);
                    if (actualizado) {
                        Toast.makeText(InventarioActivity.this, "Material actualizado", Toast.LENGTH_SHORT).show();
                        cargarInventario(); // Recargamos la lista
                    }
                }
            }
        });
        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    // LÓGICA PARA ELIMINAR
    private void eliminarMaterialConfirmacion(final int idMaterial, String nombreActual) {
        new AlertDialog.Builder(this)
                .setTitle("¿Eliminar " + nombreActual + "?")
                .setMessage("Esta acción no se puede deshacer.")
                .setPositiveButton("Sí, eliminar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (dbHelper.eliminarMaterial(idMaterial)) {
                            Toast.makeText(InventarioActivity.this, "Material eliminado", Toast.LENGTH_SHORT).show();
                            cargarInventario(); // Recargamos la lista
                        }
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}