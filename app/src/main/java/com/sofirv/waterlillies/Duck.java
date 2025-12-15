package com.sofirv. waterlillies;

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

    public void setPosition(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public void move(int deltaRow, int deltaCol) {
        this.row += deltaRow;
        this.col += deltaCol;
    }
}