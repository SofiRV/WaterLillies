package com.sofirv.waterlilies.main_app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

import com.sofirv.waterlilies.R;

public class HomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ImageButton btnScores = findViewById(R.id.button_scores);
        ImageButton btn2048   = findViewById(R.id.button_2048);
        ImageButton btnDuck   = findViewById(R.id.button_duckgame);

        btnScores.setOnClickListener(view ->
                startActivity(new Intent(this, ScoresActivity.class))
        );
        btn2048.setOnClickListener(view ->
                startActivity(new Intent(this, com.sofirv.waterlilies.game2048.MainActivity2048.class))
        );
        btnDuck.setOnClickListener(view ->
                startActivity(new Intent(this, com.sofirv.waterlilies.duckgame.SplashActivity.class))
        );
    }
}