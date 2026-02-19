package com.sofirv.waterlillies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class LevelAdapter extends RecyclerView.Adapter<LevelAdapter.LevelViewHolder> {

    private Context context;
    private int totalLevels;
    private ProgressManager progressManager;
    private OnLevelClickListener listener;

    public interface OnLevelClickListener {
        void onLevelClick(int levelNumber);
    }

    public LevelAdapter(Context context, int totalLevels, ProgressManager progressManager, OnLevelClickListener listener) {
        this.context = context;
        this.totalLevels = totalLevels;
        this.progressManager = progressManager;
        this.listener = listener;
    }

    @NonNull
    @Override
    public LevelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_level, parent, false);
        return new LevelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LevelViewHolder holder, int position) {
        int levelNumber = position + 1;
        holder.tvLevelNumber.setText(String.valueOf(levelNumber));

        boolean unlocked = progressManager.isLevelUnlocked(levelNumber);
        holder.itemView.setEnabled(unlocked);

        // Cambiar apariencia si está bloqueado
        if (!unlocked) {
            holder.ivLilyPad.setAlpha(0.4f);
        } else {
            holder.ivLilyPad.setAlpha(1f);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null && unlocked) {
                listener.onLevelClick(levelNumber);
            }
        });
    }

    @Override
    public int getItemCount() {
        return totalLevels;
    }

    static class LevelViewHolder extends RecyclerView.ViewHolder {
        ImageView ivLilyPad;
        TextView tvLevelNumber;

        public LevelViewHolder(@NonNull View itemView) {
            super(itemView);
            ivLilyPad = itemView.findViewById(R.id.ivLilyPad);
            tvLevelNumber = itemView.findViewById(R.id.tvLevelNumber);
        }
    }
}