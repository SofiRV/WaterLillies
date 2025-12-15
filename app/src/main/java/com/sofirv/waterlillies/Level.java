package com.sofirv.waterlillies;

public class Level {
    private int levelNumber;
    private int rows;
    private int cols;
    private int[][] layout;
    private int duckStartRow;
    private int duckStartCol;
    private int breadRow;
    private int breadCol;
    private int totalLilyPads;

    public Level(int levelNumber, int rows, int cols) {
        this.levelNumber = levelNumber;
        this.rows = rows;
        this.cols = cols;
        this.layout = new int[rows][cols];
    }

    public int getLevelNumber() {
        return levelNumber;
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public int[][] getLayout() {
        return layout;
    }

    public void setLayout(int[][] layout) {
        this.layout = layout;
        calculateTotalLilyPads();
    }

    public int getDuckStartRow() {
        return duckStartRow;
    }

    public void setDuckStartRow(int duckStartRow) {
        this.duckStartRow = duckStartRow;
    }

    public int getDuckStartCol() {
        return duckStartCol;
    }

    public void setDuckStartCol(int duckStartCol) {
        this.duckStartCol = duckStartCol;
    }

    public int getBreadRow() {
        return breadRow;
    }

    public void setBreadRow(int breadRow) {
        this.breadRow = breadRow;
    }

    public int getBreadCol() {
        return breadCol;
    }

    public void setBreadCol(int breadCol) {
        this.breadCol = breadCol;
    }

    public int getTotalLilyPads() {
        return totalLilyPads;
    }

    private void calculateTotalLilyPads() {
        totalLilyPads = 0;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (layout[r][c] == 1) {
                    totalLilyPads++;
                }
            }
        }
    }
}