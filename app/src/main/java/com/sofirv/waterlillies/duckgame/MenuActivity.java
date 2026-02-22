package com.sofirv.waterlillies.duckgame;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;

import com.sofirv.waterlillies.R;

public class MenuActivity extends AppCompatActivity {

    private ProgressManager progressManager;
    private LevelManager levelManager;

    private ImageButton btnPlay;
    private ImageButton btnContinue;
    private ImageButton btnLevels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Pantalla completa
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.duck_activity_menu);

        // Inicializar managers
        progressManager = new ProgressManager(this);
        levelManager = new LevelManager(this);

        // Referencias a los ImageButtons
        btnPlay = findViewById(R.id.btnPlay);
        btnContinue = findViewById(R.id.btnContinue);
        btnLevels = findViewById(R.id.btnLevels);

        // Configurar clicks
        btnPlay.setOnClickListener(v -> {
            levelManager.resetToLevel1();
            startGame();
        });

        btnContinue.setOnClickListener(v -> startGame());

        btnLevels.setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, LevelSelectActivity.class);
            startActivity(intent);
        });

        // Actualizar visibilidad de continuar según progreso
        updateContinueButton();

        // Modo inmersivo
        hideSystemUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateContinueButton();
    }

    private void updateContinueButton() {
        int maxUnlocked = progressManager.getMaxLevelUnlocked();
        if (maxUnlocked > 1 || progressManager.isLevelCompleted(1)) {
            btnContinue.setVisibility(View.VISIBLE);
        } else {
            btnContinue.setVisibility(View.GONE);
        }
    }

    private void startGame() {
        Intent intent = new Intent(MenuActivity.this, GameActivity.class);
        startActivity(intent);
    }

    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
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