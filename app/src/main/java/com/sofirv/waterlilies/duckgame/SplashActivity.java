package com.sofirv.waterlilies.duckgame;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.sofirv.waterlilies.R;

/**
 * Activity that displays a splash screen for the Duck Game.
 * Automatically navigates to the main menu after a short delay.
 */
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.duck_activity_splash);

        hideSystemUI();

        // Navigate to MenuActivity after a 2-second delay
        new Handler().postDelayed(() -> {
            startActivity(new Intent(SplashActivity.this, MenuActivity.class));
            finish();
        }, 2000);
    }

    /**
     * Enables immersive fullscreen mode by hiding system UI elements.
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