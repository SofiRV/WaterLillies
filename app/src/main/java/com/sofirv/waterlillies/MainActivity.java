package com.sofirv.waterlillies;

import androidx.appcompat.app. AppCompatActivity;
import android. content.Intent;
import android. os.Bundle;
import android. os.Handler;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Esperar 2 segundos y luego ir al menú
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(MainActivity.this, MenuActivity.class);
                startActivity(intent);
                finish(); // Cerrar splash para que no puedas volver
            }
        }, 2000); // 2000ms = 2 segundos
    }
}