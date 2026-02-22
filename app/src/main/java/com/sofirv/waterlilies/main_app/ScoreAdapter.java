package com.sofirv.waterlilies.main_app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sofirv.waterlilies.R;

import java.util.List;

public class ScoreAdapter extends RecyclerView.Adapter<ScoreAdapter.ViewHolder> {

    private final List<Score> scores;

    public ScoreAdapter(List<Score> scores) {
        this.scores = scores;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_score, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Score score = scores.get(position);
        holder.tvPlayer.setText("Player: " + score.player);
        holder.tvScore.setText("Score: " + score.score);
        holder.tvGame.setText("Game: " + score.game);
        holder.tvDate.setText("Date: " + score.date);
    }

    @Override
    public int getItemCount() {
        return scores.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvPlayer, tvScore, tvGame, tvDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPlayer = itemView.findViewById(R.id.tvPlayer);
            tvScore = itemView.findViewById(R.id.tvScore);
            tvGame = itemView.findViewById(R.id.tvGame);
            tvDate = itemView.findViewById(R.id.tvDate);
        }
    }
}