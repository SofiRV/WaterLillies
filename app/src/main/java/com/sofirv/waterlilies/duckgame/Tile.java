package com.sofirv.waterlilies.duckgame;

/**
 * Represents a single tile on the board, with its type and position.
 */
public class Tile {
    public enum TileType {
        WATER,      // Water (cannot walk here)
        LILY_PAD,   // Lily pad (can be stepped on)
        SHORE,      // Shore (pond edge - visual only)
        BREAD       // Bread (goal)
    }

    private TileType type;
    private int row;
    private int col;

    public Tile(int row, int col, TileType type) {
        this.row = row;
        this.col = col;
        this.type = type;
    }

    public TileType getType() {
        return type;
    }

    public void setType(TileType type) {
        this.type = type;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    /**
     * Returns true if this tile is walkable (lily pad or bread).
     */
    public boolean isWalkable() {
        // Only lily pads and bread are walkable
        return type == TileType.LILY_PAD || type == TileType.BREAD;
    }
}