package com.sofirv.waterlillies;

import android.content.Context;
import android.content.SharedPreferences;

public class ProgressManager {
    private static final String PREFS_NAME = "WaterLilliesProgress";
    private static final String KEY_CURRENT_LEVEL = "current_level";
    private static final String KEY_MAX_LEVEL_UNLOCKED = "max_level_unlocked";
    private static final String KEY_LEVEL_STARS = "level_stars_"; // + level number
    private static final String KEY_LEVEL_COMPLETED = "level_completed_"; // + level number

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    public ProgressManager(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context. MODE_PRIVATE);
        editor = prefs.edit();
    }

    // ===== NIVEL ACTUAL =====

    public void setCurrentLevel(int level) {
        editor.putInt(KEY_CURRENT_LEVEL, level);
        editor.apply();
    }

    public int getCurrentLevel() {
        return prefs.getInt(KEY_CURRENT_LEVEL, 1); // Por defecto nivel 1
    }

    // ===== NIVELES DESBLOQUEADOS =====

    public void unlockLevel(int level) {
        int maxUnlocked = getMaxLevelUnlocked();
        if (level > maxUnlocked) {
            editor.putInt(KEY_MAX_LEVEL_UNLOCKED, level);
            editor.apply();
        }
    }

    public int getMaxLevelUnlocked() {
        return prefs. getInt(KEY_MAX_LEVEL_UNLOCKED, 1); // Por defecto nivel 1
    }

    public boolean isLevelUnlocked(int level) {
        return level <= getMaxLevelUnlocked();
    }

    // ===== ESTRELLAS =====

    public void setLevelStars(int level, int stars) {
        // Solo actualizar si es mejor puntuación
        int currentStars = getLevelStars(level);
        if (stars > currentStars) {
            editor.putInt(KEY_LEVEL_STARS + level, stars);
            editor. apply();
        }
    }

    public int getLevelStars(int level) {
        return prefs.getInt(KEY_LEVEL_STARS + level, 0);
    }

    public int getTotalStars() {
        int total = 0;
        // Asumiendo 5 niveles, ajusta según tu juego
        for (int i = 1; i <= 5; i++) {
            total += getLevelStars(i);
        }
        return total;
    }

    // ===== COMPLETADO =====

    public void setLevelCompleted(int level, boolean completed) {
        editor.putBoolean(KEY_LEVEL_COMPLETED + level, completed);
        editor.apply();
    }

    public boolean isLevelCompleted(int level) {
        return prefs.getBoolean(KEY_LEVEL_COMPLETED + level, false);
    }

    // ===== REINICIAR PROGRESO =====

    public void resetProgress() {
        editor.clear();
        editor.putInt(KEY_CURRENT_LEVEL, 1);
        editor.putInt(KEY_MAX_LEVEL_UNLOCKED, 1);
        editor.apply();
    }

    // ===== COMPLETAR NIVEL =====

    public void completeLevel(int level, boolean perfectCompletion) {
        // Marcar como completado
        setLevelCompleted(level, true);

        // Asignar estrellas
        int stars = perfectCompletion ? 3 : 1;
        setLevelStars(level, stars);

        // Desbloquear siguiente nivel
        unlockLevel(level + 1);
    }
}