package com.sofirv.waterlilies.main_app;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sofirv.waterlilies.R;

import java.util.List;

public class ScoresActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ScoreAdapter adapter;
    private ScoreDBHelper dbHelper;

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
    }
}