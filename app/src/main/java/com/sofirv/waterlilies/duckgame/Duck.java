package com.sofirv.waterlilies.duckgame;

/**
 * Represents the duck's position and movement on the game board.
 */
public class Duck {
    private int row;
    private int col;

    public Duck(int startRow, int startCol) {
        this.row = startRow;
        this.col = startCol;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    /**
     * Sets the duck's position.
     * @param row Target row.
     * @param col Target column.
     */
    public void setPosition(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /**
     * Moves the duck by the specified delta values.
     * @param deltaRow Change in row (can be negative or positive).
     * @param deltaCol Change in column (can be negative or positive).
     */
    public void move(int deltaRow, int deltaCol) {
        this.row += deltaRow;
        this.col += deltaCol;
    }
}