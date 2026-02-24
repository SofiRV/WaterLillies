package com.sofirv.waterlilies.main_app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

import com.sofirv.waterlilies.R;

/**
 * Main home screen activity. Allows navigation to high scores, 2048 game, or Duck Game.
 */
public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the layout for this activity
        setContentView(R.layout.activity_home);

        // Reference the image buttons for navigation
        ImageButton scoresButton = findViewById(R.id.button_scores);
        ImageButton game2048Button = findViewById(R.id.button_2048);
        ImageButton duckGameButton = findViewById(R.id.button_duckgame);

        // Go to the high scores activity
        scoresButton.setOnClickListener(view ->
                startActivity(new Intent(this, ScoresActivity.class))
        );
        // Go to the 2048 game activity
        game2048Button.setOnClickListener(view ->
                startActivity(new Intent(this, com.sofirv.waterlilies.game2048.MainActivity2048.class))
        );
        // Go to the Duck Game splash activity
        duckGameButton.setOnClickListener(view ->
                startActivity(new Intent(this, com.sofirv.waterlilies.duckgame.SplashActivity.class))
        );
    }
}