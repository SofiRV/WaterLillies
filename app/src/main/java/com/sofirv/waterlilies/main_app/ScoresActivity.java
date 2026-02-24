package com.sofirv.waterlilies.main_app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sofirv.waterlilies.R;

import java.util.List;

/**
 * Activity to display the list of high scores.
 */
public class ScoresActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ScoreAdapter scoreAdapter;
    private ScoreDBHelper dbHelper;
    private Button searchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the layout for the scores activity
        setContentView(R.layout.activity_scores);

        // Set up the RecyclerView with linear layout
        recyclerView = findViewById(R.id.recycler_scores);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the database helper and fetch all scores
        dbHelper = new ScoreDBHelper(this);
        List<Score> allScores = dbHelper.getAllScores();

        // Set up the adapter for the RecyclerView
        scoreAdapter = new ScoreAdapter(allScores);
        recyclerView.setAdapter(scoreAdapter);

        // Button to access the score search screen
        searchButton = findViewById(R.id.button_search_scores);
        searchButton.setOnClickListener(v -> {
            Intent intent = new Intent(ScoresActivity.this, ScoreSearchActivity.class);
            startActivity(intent);
        });
    }
}