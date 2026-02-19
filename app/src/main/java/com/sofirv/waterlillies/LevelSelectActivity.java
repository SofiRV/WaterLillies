package com.sofirv.waterlillies;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class LevelSelectActivity extends AppCompatActivity {

    private ProgressManager progressManager;
    private LevelManager levelManager;
    private RecyclerView rvLevels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_select);

        progressManager = new ProgressManager(this);
        levelManager = new LevelManager(this);

        rvLevels = findViewById(R.id.rvLevels);

        ImageButton btnBackMenu = findViewById(R.id.btnBackMenu);
        btnBackMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Volver al menú
                Intent intent = new Intent(LevelSelectActivity.this, MenuActivity.class);
                startActivity(intent);
                finish(); // cerrar LevelSelectActivity
            }
        });

        // Grid de 3 columnas
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        rvLevels.setLayoutManager(gridLayoutManager);

        LevelAdapter adapter = new LevelAdapter(this, levelManager.getTotalLevels(), progressManager, levelNumber -> {
            // Al hacer clic en un nivel desbloqueado
            levelManager.setCurrentLevel(levelNumber);
            Intent intent = new Intent(LevelSelectActivity.this, GameActivity.class);
            startActivity(intent);
        });

        rvLevels.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideSystemUI();
    }

    // Método para modo inmersivo
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