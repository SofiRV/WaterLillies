package com.sofirv.waterlilies.duckgame;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Manages level progress, stars, and completion status for the Duck Game.
 */
public class ProgressManager {
    private static final String PREFS_NAME = "WaterLilliesProgress";
    private static final String KEY_CURRENT_LEVEL = "current_level";
    private static final String KEY_MAX_LEVEL_UNLOCKED = "max_level_unlocked";
    private static final String KEY_LEVEL_STARS = "level_stars_"; // + level number
    private static final String KEY_LEVEL_COMPLETED = "level_completed_"; // + level number

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    public ProgressManager(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    // ===== CURRENT LEVEL =====

    /**
     * Sets the current level (will be loaded on continue game).
     */
    public void setCurrentLevel(int level) {
        editor.putInt(KEY_CURRENT_LEVEL, level);
        editor.apply();
    }

    /**
     * Gets the current level (defaults to level 1).
     */
    public int getCurrentLevel() {
        return prefs.getInt(KEY_CURRENT_LEVEL, 1);
    }

    // ===== UNLOCKED LEVELS =====

    /**
     * Unlocks a level if the given level number is greater than the currently unlocked.
     */
    public void unlockLevel(int level) {
        int maxUnlocked = getMaxLevelUnlocked();
        if (level > maxUnlocked) {
            editor.putInt(KEY_MAX_LEVEL_UNLOCKED, level);
            editor.apply();
        }
    }

    /**
     * Returns the highest unlocked level (defaults to level 1).
     */
    public int getMaxLevelUnlocked() {
        return prefs.getInt(KEY_MAX_LEVEL_UNLOCKED, 1);
    }

    /**
     * Checks if a given level is unlocked.
     */
    public boolean isLevelUnlocked(int level) {
        return level <= getMaxLevelUnlocked();
    }

    // ===== STARS =====

    /**
     * Sets the best star rating for a level. Only updates if greater than previous record.
     */
    public void setLevelStars(int level, int stars) {
        int currentStars = getLevelStars(level);
        if (stars > currentStars) {
            editor.putInt(KEY_LEVEL_STARS + level, stars);
            editor.apply();
        }
    }

    /**
     * Gets the star count for a specific level.
     */
    public int getLevelStars(int level) {
        return prefs.getInt(KEY_LEVEL_STARS + level, 0);
    }

    /**
     * Gets the total number of stars earned throughout all levels (max 10 levels hardcoded).
     */
    public int getTotalStars() {
        int total = 0;
        for (int i = 1; i <= 10; i++) {
            total += getLevelStars(i);
        }
        return total;
    }

    // ===== COMPLETED STATUS =====

    /**
     * Marks a level as completed or not.
     */
    public void setLevelCompleted(int level, boolean completed) {
        editor.putBoolean(KEY_LEVEL_COMPLETED + level, completed);
        editor.apply();
    }

    /**
     * Checks if a specific level is completed.
     */
    public boolean isLevelCompleted(int level) {
        return prefs.getBoolean(KEY_LEVEL_COMPLETED + level, false);
    }

    // ===== RESET PROGRESS =====

    /**
     * Resets all progress: current/maximum level, stars, and completion status.
     */
    public void resetProgress() {
        editor.clear();
        editor.putInt(KEY_CURRENT_LEVEL, 1);
        editor.putInt(KEY_MAX_LEVEL_UNLOCKED, 1);
        editor.apply();
    }

    // ===== COMPLETE LEVEL =====

    /**
     * Marks a level as completed, assigns stars (3 for perfect, 1 otherwise), and unlocks the next level.
     */
    public void completeLevel(int level, boolean perfectCompletion) {
        setLevelCompleted(level, true);
        int stars = perfectCompletion ? 3 : 1;
        setLevelStars(level, stars);
        unlockLevel(level + 1);
    }
}