package com.sofirv.waterlilies.main_app;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sofirv.waterlilies.R;

import java.util.List;

/**
 * Activity to search and display scores based on a user query.
 */
public class ScoreSearchActivity extends AppCompatActivity {

    private ScoreDBHelper dbHelper;
    private RecyclerView recyclerView;
    private ScoreAdapter scoreAdapter;
    private EditText searchEditText;
    private Button searchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the layout for score searching
        setContentView(R.layout.activity_score_search);

        // Initialize database helper, RecyclerView, and UI components
        dbHelper = new ScoreDBHelper(this);
        recyclerView = findViewById(R.id.search_results);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchEditText = findViewById(R.id.search_query);
        searchButton = findViewById(R.id.search_button);

        // Handle search button click to filter score list
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = searchEditText.getText().toString().trim();
                List<Score> results = dbHelper.searchScores(query);
                scoreAdapter = new ScoreAdapter(results);
                recyclerView.setAdapter(scoreAdapter);
            }
        });
    }
}