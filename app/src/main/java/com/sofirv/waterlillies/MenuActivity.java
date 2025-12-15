package com.sofirv.waterlillies;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget. Button;
import android.widget.TextView;

public class MenuActivity extends AppCompatActivity {

    private ProgressManager progressManager;
    private LevelManager levelManager;
    private Button btnPlay;
    private Button btnContinue;
    private Button btnLevels;
    private TextView tvProgress;
    private TextView tvLevelProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Pantalla completa
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
                WindowManager.LayoutParams. FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R. layout.activity_menu);

        // Inicializar managers
        progressManager = new ProgressManager(this);
        levelManager = new LevelManager(this);

        // Referencias
        btnPlay = findViewById(R. id.btn_play);
        btnContinue = findViewById(R.id.btn_continue);
        btnLevels = findViewById(R.id.btn_levels);
        tvProgress = findViewById(R.id.tv_progress);
        tvLevelProgress = findViewById(R.id.tv_level_progress);

        // Actualizar UI
        updateProgressDisplay();

        // Botón Jugar (siempre empieza desde nivel 1)
        btnPlay.setOnClickListener(new View. OnClickListener() {
            @Override
            public void onClick(View v) {
                levelManager.resetToLevel1();
                startGame();
            }
        });

        // Botón Continuar (continúa desde el progreso guardado)
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Ya está configurado en levelManager
                startGame();
            }
        });

        // Botón Niveles
        btnLevels.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, LevelSelectActivity.class);
                startActivity(intent);
            }
        });

        // Modo inmersivo
        hideSystemUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateProgressDisplay();
    }

    private void updateProgressDisplay() {
        // Actualizar estrellas totales
        int totalStars = progressManager.getTotalStars();
        tvProgress.setText("⭐ " + totalStars + "/15");

        // Actualizar nivel actual
        int currentLevel = progressManager.getCurrentLevel();
        int totalLevels = levelManager.getTotalLevels();
        tvLevelProgress.setText("Nivel " + currentLevel + "/" + totalLevels);

        // Mostrar botón "Continuar" solo si hay progreso
        int maxUnlocked = progressManager.getMaxLevelUnlocked();
        if (maxUnlocked > 1 || progressManager.isLevelCompleted(1)) {
            btnContinue.setVisibility(View. VISIBLE);
        } else {
            btnContinue.setVisibility(View.GONE);
        }
    }

    private void startGame() {
        Intent intent = new Intent(MenuActivity. this, GameActivity.class);
        startActivity(intent);
    }

    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView. setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View. SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
        );
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }
}