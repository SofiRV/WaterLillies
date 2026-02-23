package com.sofirv.waterlilies.duckgame;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.sofirv.waterlilies.R;

public class GameActivity extends AppCompatActivity {

    private GameView gameView;
    private ImageButton restartButton;
    private ImageButton nextLevelButton;
    private ImageButton homeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the activity to full screen mode (hide title and status bar)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.duck_activity_game);

        gameView = findViewById(R.id.game_view);
        restartButton = findViewById(R.id.btn_restart);
        nextLevelButton = findViewById(R.id.btn_next);
        homeButton = findViewById(R.id.btn_home);

        // Restart the current level
        restartButton.setOnClickListener(v -> gameView.restartLevel());
        // Go to the next level
        nextLevelButton.setOnClickListener(v -> gameView.goToNextLevel());
        // Return to the main menu
        homeButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, MenuActivity.class);
            startActivity(intent);
            finish();
        });

        // Hide the system UI for an immersive experience
        hideSystemUI();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    /**
     * Hides the system UI elements for immersive full screen mode.
     */
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
}