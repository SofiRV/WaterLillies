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

public class ScoreSearchActivity extends AppCompatActivity {

    private ScoreDBHelper dbHelper;
    private RecyclerView recyclerView;
    private ScoreAdapter adapter;
    private EditText searchQuery;
    private Button searchBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_search);

        dbHelper = new ScoreDBHelper(this);
        recyclerView = findViewById(R.id.search_results);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchQuery = findViewById(R.id.search_query);
        searchBtn = findViewById(R.id.search_button);

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String q = searchQuery.getText().toString().trim();
                List<Score> results = dbHelper.searchScores(q);
                adapter = new ScoreAdapter(results);
                recyclerView.setAdapter(adapter);
            }
        });
    }
}