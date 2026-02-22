package com.sofirv.waterlilies.main_app;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sofirv.waterlilies.R;


import java.util.List;

public class ScoresActivity extends AppCompatActivity {

    private ScoresAdapter adapter;
    private ScoreDBHelper dbHelper;
    private RecyclerView recyclerView;
    private String actualGameFilter = "Todos";
    private boolean bestFirst = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scores);

        dbHelper = new ScoreDBHelper(this);
        recyclerView = findViewById(R.id.recycler_scores);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Muestra todos los scores por default, de mejor a peor
        List<ScoreDBHelper.Score> scores = dbHelper.getScoresByGameOrdered(actualGameFilter, bestFirst);
        adapter = new ScoresAdapter(scores);
        recyclerView.setAdapter(adapter);
    }

    private void updateScores(String game, boolean bestFirst) {
        actualGameFilter = game;
        List<ScoreDBHelper.Score> scores = dbHelper.getScoresByGameOrdered(game, bestFirst);
        adapter = new ScoresAdapter(scores);
        recyclerView.setAdapter(adapter);
    }

    // Menú de opciones (tres puntitos)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_scores, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_filter_duck:
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}