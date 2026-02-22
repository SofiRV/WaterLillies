package com.sofirv.waterlilies.main_app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sofirv.waterlilies.R;

import java.util.List;

public class ScoresAdapter extends RecyclerView.Adapter<ScoresAdapter.ViewHolder> {

    private List<ScoreDBHelper.Score> scores;

    public ScoresAdapter(List<ScoreDBHelper.Score> scores) {
        this.scores = scores;
    }

    @NonNull
    @Override
    public ScoresAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_score, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ScoresAdapter.ViewHolder holder, int position) {
        ScoreDBHelper.Score score = scores.get(position);
        holder.tvPlayer.setText(score.player);
        holder.tvScore.setText(String.valueOf(score.score));
        holder.tvGame.setText(score.game);
        holder.tvDate.setText(score.date);
    }

    @Override
    public int getItemCount() {
        return scores.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvPlayer, tvScore, tvGame, tvDate;
        public ViewHolder(View itemView) {
            super(itemView);
            tvPlayer = itemView.findViewById(R.id.tvPlayer);
            tvScore = itemView.findViewById(R.id.tvScore);
            tvGame = itemView.findViewById(R.id.tvGame);
            tvDate = itemView.findViewById(R.id.tvDate);
        }
    }
}