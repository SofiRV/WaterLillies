package com.sofirv.waterlillies;

import android.content.Context;

public class LevelManager {
    private static final int TOTAL_LEVELS = 5;
    private int currentLevelNumber;
    private Context context;
    private ProgressManager progressManager;

    public LevelManager(Context context) {
        this.context = context;
        this.progressManager = new ProgressManager(context);

        // Cargar nivel guardado
        this.currentLevelNumber = progressManager.getCurrentLevel();
    }

    public Level loadLevel(int levelNumber) {
        if (levelNumber < 1 || levelNumber > TOTAL_LEVELS) {
            return null;
        }

        int xmlResourceId = getXmlResourceForLevel(levelNumber);
        return LevelLoader.loadLevelFromXml(context, xmlResourceId);
    }

    private int getXmlResourceForLevel(int levelNumber) {
        switch (levelNumber) {
            case 1: return R.xml. level_1;
            case 2: return R.xml.level_2;
            case 3: return R.xml.level_3;
            case 4: return R.xml.level_4;
            case 5: return R. xml.level_5;
            default: return R.xml.level_1;
        }
    }

    public Level getCurrentLevel() {
        return loadLevel(currentLevelNumber);
    }

    public void nextLevel() {
        if (currentLevelNumber < TOTAL_LEVELS) {
            currentLevelNumber++;
            progressManager.setCurrentLevel(currentLevelNumber);
        }
    }

    public void setCurrentLevel(int level) {
        if (level >= 1 && level <= TOTAL_LEVELS) {
            currentLevelNumber = level;
            progressManager.setCurrentLevel(level);
        }
    }

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

    public boolean hasNextLevel() {
        return currentLevelNumber < TOTAL_LEVELS;
    }

    public ProgressManager getProgressManager() {
        return progressManager;
    }
}