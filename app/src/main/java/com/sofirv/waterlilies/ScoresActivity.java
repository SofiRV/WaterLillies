package com.sofirv.waterlilies.duckgame;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;

import com.sofirv.waterlilies.R;
import com.sofirv.waterlilies.ScoreDBHelper;

import java.util.List;

public class ScoresActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scores);

        ListView listView = findViewById(R.id.scores_list_view);
        ScoreDBHelper dbHelper = new ScoreDBHelper(this);
        List<String> scores = dbHelper.getAllScoresAsStrings();  // Implementa este método en tu helper
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, scores);
        listView.setAdapter(adapter);
    }
}