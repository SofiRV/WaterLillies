package com.sofirv.waterlillies;

public class Tile {
    public enum TileType {
        WATER,          // Agua (donde no se puede estar)
        LILY_PAD,       // Nenúfar (se puede pisar)
        SHORE,          // Orilla (bordes del estanque - SOLO VISUAL)
        BREAD           // Pan (objetivo)
    }

    private TileType type;
    private int row;
    private int col;

    public Tile(int row, int col, TileType type) {
        this.row = row;
        this. col = col;
        this. type = type;
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

    public boolean isWalkable() {
        // Solo los nenúfares y el pan son caminables
        return type == TileType.LILY_PAD || type == TileType.BREAD;
    }
}