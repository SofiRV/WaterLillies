package com.sofirv.waterlilies.duckgame;

import android.content.Context;

import com.sofirv.waterlilies.R;

/**
 * Manages loading, switching, and progress tracking for game levels.
 */
public class LevelManager {
    private static final int TOTAL_LEVELS = 10;
    private int currentLevelNumber;
    private Context context;
    private ProgressManager progressManager;

    public LevelManager(Context context) {
        this.context = context;
        this.progressManager = new ProgressManager(context);
        // Load the last saved level for the player
        this.currentLevelNumber = progressManager.getCurrentLevel();
    }

    /**
     * Loads a level with the given level number.
     * Returns null if the number is out of bounds.
     */
    public Level loadLevel(int levelNumber) {
        if (levelNumber < 1 || levelNumber > TOTAL_LEVELS) {
            return null;
        }

        int xmlResourceId = getXmlResourceForLevel(levelNumber);
        return LevelLoader.loadLevelFromXml(context, xmlResourceId);
    }

    /**
     * Gets the XML resource ID associated with the given level number.
     */
    private int getXmlResourceForLevel(int levelNumber) {
        switch (levelNumber) {
            case 1:  return R.xml.level_1;
            case 2:  return R.xml.level_2;
            case 3:  return R.xml.level_3;
            case 4:  return R.xml.level_4;
            case 5:  return R.xml.level_5;
            case 6:  return R.xml.level_6;
            case 7:  return R.xml.level_7;
            case 8:  return R.xml.level_8;
            case 9:  return R.xml.level_9;
            case 10: return R.xml.level_10;
            default: return R.xml.level_1;
        }
    }

    /**
     * Loads the current level from saved progress.
     */
    public Level getCurrentLevel() {
        return loadLevel(currentLevelNumber);
    }

    /**
     * Advances to the next level if available and updates progress.
     */
    public void nextLevel() {
        if (currentLevelNumber < TOTAL_LEVELS) {
            currentLevelNumber++;
            progressManager.setCurrentLevel(currentLevelNumber);
        }
    }

    /**
     * Sets the current level to a specific value and updates progress.
     */
    public void setCurrentLevel(int level) {
        if (level >= 1 && level <= TOTAL_LEVELS) {
            currentLevelNumber = level;
            progressManager.setCurrentLevel(level);
        }
    }

    /**
     * Resets progress to level 1.
     */
    public void resetToLevel1() {
        currentLevelNumber = 1;
        progressManager.setCurrentLevel(1);
    }

    public int getCurrentLevelNumber() {
        return currentLevelNumber;
    }

    public int getTotalLevels() {
        return TOTAL_LEVELS;
    }

    /**
     * Returns true if there is a next level available.
     */
    public boolean hasNextLevel() {
        return currentLevelNumber < TOTAL_LEVELS;
    }

    public ProgressManager getProgressManager() {
        return progressManager;
    }
}