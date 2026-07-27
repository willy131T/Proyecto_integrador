package com.example.proyecto_integrador;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class ServicesActivity extends AppCompatActivity {

    private Button btnAgendarCita;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_services);

        btnAgendarCita = findViewById(R.id.btnAgendarCita);

        btnAgendarCita.setOnClickListener(v -> {
            Intent intent = new Intent(ServicesActivity.this, AppointmentActivity.class);
            startActivity(intent);
        });
    }
}