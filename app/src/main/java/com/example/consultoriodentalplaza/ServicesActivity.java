package com.example.consultoriodentalplaza;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ServicesActivity extends AppCompatActivity {

    private Button btnServicio1, btnServicio2, btnServicio3, btnServicio4, btnServicio5, btnIrAgendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_services);

        btnServicio1 = findViewById(R.id.btnServicio1);
        btnServicio2 = findViewById(R.id.btnServicio2);
        btnServicio3 = findViewById(R.id.btnServicio3);
        btnServicio4 = findViewById(R.id.btnServicio4);
        btnServicio5 = findViewById(R.id.btnServicio5);
        btnIrAgendar = findViewById(R.id.btnIrAgendar);

        // Listener común para la navegación de servicios
        View.OnClickListener servicioClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button b = (Button) v;
                String servicioSeleccionado = b.getText().toString();

                Intent intent = new Intent(ServicesActivity.this, AppointmentActivity.class);
                intent.putExtra("SERVICIO_NOMBRE", servicioSeleccionado);
                startActivity(intent);
            }
        };

        btnServicio1.setOnClickListener(servicioClickListener);
        btnServicio2.setOnClickListener(servicioClickListener);
        btnServicio3.setOnClickListener(servicioClickListener);
        btnServicio4.setOnClickListener(servicioClickListener);
        btnServicio5.setOnClickListener(servicioClickListener);

        btnIrAgendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ServicesActivity.this, AppointmentActivity.class);
                startActivity(intent);
            }
        });
    }
}