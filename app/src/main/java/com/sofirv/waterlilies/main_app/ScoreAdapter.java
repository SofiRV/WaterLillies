package com.sofirv.waterlilies.main_app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sofirv.waterlilies.R;

import java.util.List;

/**
 * RecyclerView Adapter for displaying a list of Score objects.
 */
public class ScoreAdapter extends RecyclerView.Adapter<ScoreAdapter.ViewHolder> {

    // List of Score objects to display
    private final List<Score> scoreList;

    /**
     * Constructor for ScoreAdapter.
     * @param scoreList The list of Score objects to bind and display.
     */
    public ScoreAdapter(List<Score> scoreList) {
        this.scoreList = scoreList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout for each score row
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_score, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Bind Score data to view elements
        Score score = scoreList.get(position);
        holder.tvPlayer.setText("Player: " + score.player);
        holder.tvScore.setText("Score: " + score.score);
        holder.tvGame.setText("Game: " + score.game);
        holder.tvDate.setText("Date: " + score.date);
    }

    @Override
    public int getItemCount() {
        return scoreList.size();
    }

    /**
     * ViewHolder class for Score items.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvPlayer, tvScore, tvGame, tvDate;

        /**
         * Constructor for ViewHolder.
         * @param itemView The view representing each score item.
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPlayer = itemView.findViewById(R.id.tvPlayer);
            tvScore = itemView.findViewById(R.id.tvScore);
            tvGame = itemView.findViewById(R.id.tvGame);
            tvDate = itemView.findViewById(R.id.tvDate);
        }
    }
}