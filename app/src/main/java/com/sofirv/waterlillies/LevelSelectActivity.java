package com.sofirv.waterlillies;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android. widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class LevelSelectActivity extends AppCompatActivity {

    private ProgressManager progressManager;
    private LevelManager levelManager;
    private TextView tvTotalStars;
    private GridLayout gridLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_select);

        progressManager = new ProgressManager(this);
        levelManager = new LevelManager(this);

        tvTotalStars = findViewById(R.id.tv_total_stars);
        gridLayout = findViewById(R.id.grid_layout);

        updateTotalStars();
        createLevelButtons();

        // Botón de reiniciar progreso
        Button btnReset = findViewById(R.id. btn_reset_progress);
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showResetDialog();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateTotalStars();
        updateLevelButtons();
    }

    private void updateTotalStars() {
        int total = progressManager.getTotalStars();
        tvTotalStars.setText("⭐ Estrellas: " + total + "/15");
    }

    private void createLevelButtons() {
        int totalLevels = levelManager.getTotalLevels();

        for (int i = 1; i <= totalLevels; i++) {
            final int levelNumber = i;

            Button levelButton = new Button(this);
            levelButton.setId(View.generateViewId());
            levelButton.setText(String.valueOf(levelNumber));
            levelButton. setTextSize(24);

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 200;
            params.height = 200;
            params.setMargins(20, 20, 20, 20);
            levelButton.setLayoutParams(params);

            updateButtonAppearance(levelButton, levelNumber);

            levelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (progressManager.isLevelUnlocked(levelNumber)) {
                        startLevel(levelNumber);
                    }
                }
            });

            gridLayout.addView(levelButton);
        }
    }

    private void updateLevelButtons() {
        int totalLevels = levelManager.getTotalLevels();

        for (int i = 0; i < gridLayout.getChildCount(); i++) {
            View child = gridLayout.getChildAt(i);
            if (child instanceof Button) {
                Button button = (Button) child;
                int levelNumber = i + 1;
                updateButtonAppearance(button, levelNumber);
            }
        }
    }

    private void updateButtonAppearance(Button button, int levelNumber) {
        boolean unlocked = progressManager.isLevelUnlocked(levelNumber);
        boolean completed = progressManager.isLevelCompleted(levelNumber);
        int stars = progressManager.getLevelStars(levelNumber);

        if (! unlocked) {
            // Nivel bloqueado
            button.setBackgroundColor(Color.GRAY);
            button.setTextColor(Color. DKGRAY);
            button.setText("🔒");
            button.setEnabled(false);
        } else if (completed) {
            // Nivel completado
            button.setBackgroundColor(Color.parseColor("#4CAF50"));
            button.setTextColor(Color.WHITE);

            String starsText = "";
            for (int i = 0; i < stars; i++) {
                starsText += "⭐";
            }
            button. setText(levelNumber + "\n" + starsText);
        } else {
            // Nivel desbloqueado pero no completado
            button.setBackgroundColor(Color.parseColor("#2196F3"));
            button. setTextColor(Color.WHITE);
            button.setText(String.valueOf(levelNumber));
            button.setEnabled(true);
        }
    }

    private void startLevel(int levelNumber) {
        levelManager.setCurrentLevel(levelNumber);
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }

    private void showResetDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Reiniciar Progreso")
                .setMessage("¿Estás seguro de que quieres reiniciar todo tu progreso?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    progressManager.resetProgress();
                    recreate(); // Recargar la actividad
                })
                .setNegativeButton("No", null)
                .show();
    }
}