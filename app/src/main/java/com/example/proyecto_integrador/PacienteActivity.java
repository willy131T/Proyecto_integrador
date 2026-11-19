package com.example.proyecto_integrador;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PacienteActivity extends AppCompatActivity {

    private TextView tvBienvenida, tvDatosPaciente, tvTratamiento, tvProximaCita;
    private Button btnAgendar, btnCerrarSesion;
    private DatabaseHelper dbHelper;
    private String nombreUsuarioLogueado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paciente);

        dbHelper = new DatabaseHelper(this);

        tvBienvenida = findViewById(R.id.tvBienvenidaPaciente);
        tvDatosPaciente = findViewById(R.id.tvDatosPacienteInfo);
        tvTratamiento = findViewById(R.id.tvTratamientoPacienteInfo);
        tvProximaCita = findViewById(R.id.tvProximaCitaPaciente);
        btnAgendar = findViewById(R.id.btnAgendarCitaPaciente);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesionPaciente);

        // Recibimos el usuario logueado
        nombreUsuarioLogueado = getIntent().getStringExtra("nombre_usuario");
        if (nombreUsuarioLogueado == null) {
            nombreUsuarioLogueado = "william_p";
        }

        cargarDatosUsuario();
        cargarHistorialClinicoPaciente();
        cargarProximaCita();

        btnAgendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PacienteActivity.this, AgendarCitaActivity.class);
                intent.putExtra("paciente_seleccionado", nombreUsuarioLogueado);
                startActivity(intent);
            }
        });

        btnCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PacienteActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarProximaCita();
        cargarHistorialClinicoPaciente();
    }

    private void cargarDatosUsuario() {
        Cursor cursor = dbHelper.obtenerUsuarioPorUsername(nombreUsuarioLogueado);
        if (cursor != null && cursor.moveToFirst()) {
            String nombreReal = cursor.getString(cursor.getColumnIndexOrThrow("nombre"));
            int edad = cursor.getInt(cursor.getColumnIndexOrThrow("edad"));
            String alergias = cursor.getString(cursor.getColumnIndexOrThrow("alergias"));

            tvBienvenida.setText("¡Hola, " + nombreReal + "!");
            tvDatosPaciente.setText("🎂 Edad: " + edad + " años\n⚠️ Alergias: " + (alergias != null ? alergias : "Ninguna"));

            // Actualizamos al nombre real para que coincida exactamente con los registros de la BD
            nombreUsuarioLogueado = nombreReal;
            cursor.close();
        } else {
            if (cursor != null) cursor.close();
            tvBienvenida.setText("¡Hola, Paciente!");
            tvDatosPaciente.setText("No se encontraron datos médicos.");
        }
    }

    private void cargarHistorialClinicoPaciente() {
        // 1. Intentar cargar diagnósticos desde la API central
        RetrofitClient.getApiService().obtenerDiagnosticos(nombreUsuarioLogueado).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Map<String, Object>> lista = (List<Map<String, Object>>) response.body().get("diagnosticos");
                    if (lista != null && !lista.isEmpty()) {
                        StringBuilder sb = new StringBuilder();
                        for (Map<String, Object> d : lista) {
                            String fecha = (String) d.get("fecha");
                            String citaInfo = (String) d.get("cita_info");
                            String procedimiento = (String) d.get("procedimiento");
                            String medicamentos = (String) d.get("medicamentos");

                            sb.append("🗓️ Fecha: ").append(fecha).append("\n");
                            sb.append("📌 ").append(citaInfo).append("\n");
                            sb.append("🦷 Procedimiento: ").append(procedimiento).append("\n");
                            if (medicamentos != null && !medicamentos.isEmpty() && !medicamentos.equals("Ninguno")) {
                                sb.append("💊 Receta: ").append(medicamentos).append("\n");
                            }
                            sb.append("----------------------------------\n\n");
                        }
                        tvTratamiento.setText(sb.toString().trim());
                        return;
                    }
                }
                cargarHistorialLocal();
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                cargarHistorialLocal();
            }
        });
    }

    private void cargarHistorialLocal() {
        Cursor cursor = dbHelper.obtenerDiagnosticosParaPaciente(nombreUsuarioLogueado);
        StringBuilder sb = new StringBuilder();

        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String citaInfo = cursor.getString(cursor.getColumnIndexOrThrow("cita_info"));
                String procedimiento = cursor.getString(cursor.getColumnIndexOrThrow("procedimiento"));
                String medicamentos = cursor.getString(cursor.getColumnIndexOrThrow("medicamentos"));
                String fecha = cursor.getString(cursor.getColumnIndexOrThrow("fecha"));

                sb.append("🗓️ Fecha: ").append(fecha).append("\n");
                sb.append("📌 ").append(citaInfo).append("\n");
                sb.append("🦷 Procedimiento: ").append(procedimiento).append("\n");
                if (medicamentos != null && !medicamentos.isEmpty()) {
                    sb.append("💊 Receta: ").append(medicamentos).append("\n");
                }
                sb.append("----------------------------------\n\n");
            }
            cursor.close();
            tvTratamiento.setText(sb.toString().trim());
        } else {
            tvTratamiento.setText("No tienes tratamientos o diagnósticos registrados todavía.");
        }
    }

    private void cargarProximaCita() {
        // 1. Intentar cargar próxima cita desde la API central
        RetrofitClient.getApiService().obtenerProximaCita(nombreUsuarioLogueado).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Object> proxima = (Map<String, Object>) response.body().get("proxima_cita");
                    if (proxima != null) {
                        String fecha = (String) proxima.get("fecha");
                        String hora = (String) proxima.get("hora");
                        String motivo = (String) proxima.get("motivo");
                        String doctor = (String) proxima.get("doctor");

                        tvProximaCita.setText("📅 Fecha: " + fecha + "\n⏰ Hora: " + hora + "\n👨‍⚕️ Doctor: " + doctor + "\n🦷 Motivo: " + motivo + "\n\n👉 Toca aquí para ver todas las citas");
                        tvProximaCita.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mostrarTodasLasCitasDelPaciente();
                            }
                        });
                        return;
                    }
                }
                cargarProximaCitaLocal();
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                cargarProximaCitaLocal();
            }
        });
    }

    private void cargarProximaCitaLocal() {
        Cursor cursor = dbHelper.obtenerCitasPorPaciente(nombreUsuarioLogueado);

        if (cursor != null && cursor.getCount() > 0) {
            if (cursor.moveToLast()) {
                String fecha = cursor.getString(cursor.getColumnIndexOrThrow("fecha"));
                String hora = cursor.getString(cursor.getColumnIndexOrThrow("hora"));
                String motivo = cursor.getString(cursor.getColumnIndexOrThrow("motivo"));

                tvProximaCita.setText("📅 Fecha: " + fecha + "\n⏰ Hora: " + hora + "\n🦷 Motivo: " + motivo + "\n\n👉 Toca aquí para ver todas las citas");
            }
        } else {
            tvProximaCita.setText("No tienes citas agendadas actualmente.");
        }

        tvProximaCita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarTodasLasCitasDelPaciente();
            }
        });

        if (cursor != null) cursor.close();
    }

    private void mostrarTodasLasCitasDelPaciente() {
        Cursor cursor = dbHelper.obtenerCitasPorPaciente(nombreUsuarioLogueado);
        StringBuilder sb = new StringBuilder();

        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String fecha = cursor.getString(cursor.getColumnIndexOrThrow("fecha"));
                String hora = cursor.getString(cursor.getColumnIndexOrThrow("hora"));
                String motivo = cursor.getString(cursor.getColumnIndexOrThrow("motivo"));
                String doctor = cursor.getString(cursor.getColumnIndexOrThrow("doctor"));

                sb.append("📅 ").append(fecha).append(" a las ").append(hora).append("\n");
                sb.append("👨‍⚕️ ").append(doctor).append("\n");
                sb.append("🦷 Motivo: ").append(motivo).append("\n");
                sb.append("----------------------------\n");
            }
            cursor.close();
        } else {
            sb.append("No hay citas registradas.");
        }

        new AlertDialog.Builder(this)
                .setTitle("📅 Todas las Citas Agendadas")
                .setMessage(sb.toString())
                .setPositiveButton("Cerrar", null)
                .show();
    }
}