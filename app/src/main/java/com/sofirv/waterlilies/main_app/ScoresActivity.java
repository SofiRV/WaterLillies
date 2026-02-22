package com.sofirv.waterlilies.main_app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sofirv.waterlilies.R;

import java.util.List;

public class ScoresActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ScoreAdapter adapter;
    private ScoreDBHelper dbHelper;
    private Button searchBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scores);

        recyclerView = findViewById(R.id.recycler_scores);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dbHelper = new ScoreDBHelper(this);
        List<Score> scoreList = dbHelper.getAllScores();

        adapter = new ScoreAdapter(scoreList);
        recyclerView.setAdapter(adapter);

        // Botón de buscar integrado
        searchBtn = findViewById(R.id.button_search_scores);
        searchBtn.setOnClickListener(v -> {
            Intent intent = new Intent(ScoresActivity.this, ScoreSearchActivity.class);
            startActivity(intent);
        });
    }
}