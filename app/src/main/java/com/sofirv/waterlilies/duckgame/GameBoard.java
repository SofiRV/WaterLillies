package com.sofirv.waterlilies.duckgame;

/**
 * Represents the logical board of the duck game. Handles all board state, duck movement, win/lose logic, and lily pad progression.
 */
public class GameBoard {
    private Tile[][] board;
    private Duck duck;
    private int rows;
    private int cols;
    private int breadRow;
    private int breadCol;
    private int lilyPadsVisited;
    private int totalLilyPads;
    private Level currentLevel;
    private boolean isDead;

    public GameBoard(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        board = new Tile[rows][cols];
        this.isDead = false;
    }

    /**
     * Loads a level, sets up the board, and places the duck and bread.
     */
    public void loadLevel(Level level) {
        this.currentLevel = level;
        this.rows = level.getRows();
        this.cols = level.getCols();
        this.board = new Tile[rows][cols];
        this.lilyPadsVisited = 0;
        this.totalLilyPads = level.getTotalLilyPads();
        this.isDead = false;

        int[][] layout = level.getLayout();

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (layout[r][c] == 1) {
                    board[r][c] = new Tile(r, c, Tile.TileType.LILY_PAD);
                } else {
                    board[r][c] = new Tile(r, c, Tile.TileType.WATER);
                }
            }
        }

        duck = new Duck(level.getDuckStartRow(), level.getDuckStartCol());
        breadRow = level.getBreadRow();
        breadCol = level.getBreadCol();

        generateShore();
    }

    /**
     * Updates tiles of type WATER, adding SHORE type next to lily pads
     */
    private void generateShore() {
        Tile.TileType[][] tempBoard = new Tile.TileType[rows][cols];

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                tempBoard[r][c] = board[r][c].getType();
            }
        }

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (tempBoard[r][c] == Tile.TileType.WATER) {
                    if (hasAdjacentLilyPad(r, c, tempBoard)) {
                        board[r][c].setType(Tile.TileType.SHORE);
                    }
                }
            }
        }
    }

    /**
     * Checks if at least one adjacent tile (any of 8 directions) is a lily pad.
     */
    private boolean hasAdjacentLilyPad(int row, int col, Tile.TileType[][] tempBoard) {
        int[][] directions = {
                {-1, -1}, {-1, 0}, {-1, 1},
                {0, -1},           {0, 1},
                {1, -1},  {1, 0},  {1, 1}
        };

        for (int[] dir : directions) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];

            if (newRow >= 0 && newRow < rows && newCol >= 0 && newCol < cols) {
                Tile.TileType tileType = tempBoard[newRow][newCol];
                if (tileType == Tile.TileType.LILY_PAD) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Attempts to move the duck by (deltaRow, deltaCol).
     * Returns true if the move is successful, false otherwise (out of bounds, non-walkable, or dead).
     * Handles lily pad sinking, win detection, and dead state.
     */
    public boolean moveDuck(int deltaRow, int deltaCol) {
        if (isDead) {
            return false;
        }

        int newRow = duck.getRow() + deltaRow;
        int newCol = duck.getCol() + deltaCol;

        // Check board limits
        if (newRow < 0 || newRow >= rows || newCol < 0 || newCol >= cols) {
            return false;
        }

        Tile targetTile = board[newRow][newCol];

        // Check if target tile is walkable
        if (!targetTile.isWalkable()) {
            return false;
        }

        // Sink the current lily pad (turn it into water)
        Tile currentTile = board[duck.getRow()][duck.getCol()];
        if (currentTile.getType() == Tile.TileType.LILY_PAD) {
            currentTile.setType(Tile.TileType.WATER);
            lilyPadsVisited++;
        }

        // Move the duck
        duck.move(deltaRow, deltaCol);

        // If reached the bread, return true (level complete)
        if (isLevelComplete()) {
            return true;
        }

        // Check if duck is dead (no valid moves left)
        if (!hasValidMoves()) {
            isDead = true;
        }

        return true;
    }

    /**
     * Checks if the duck has at least one valid move available (up, down, left, right).
     */
    public boolean hasValidMoves() {
        int currentRow = duck.getRow();
        int currentCol = duck.getCol();

        // Check all 4 cardinal directions
        int[][] directions = {
                {-1, 0},  // Up
                {1, 0},   // Down
                {0, -1},  // Left
                {0, 1}    // Right
        };

        for (int[] dir :  directions) {
            int newRow = currentRow + dir[0];
            int newCol = currentCol + dir[1];

            // Check limits
            if (newRow >= 0 && newRow < rows && newCol >= 0 && newCol < cols) {
                Tile tile = board[newRow][newCol];
                // If at least one adjacent tile is walkable, duck is not dead
                if (tile.isWalkable()) {
                    return true;
                }
            }
        }

        return false; // No valid moves left means dead
    }

    /**
     * Returns true if the duck is located on the bread tile.
     */
    public boolean isLevelComplete() {
        return duck.getRow() == breadRow && duck.getCol() == breadCol;
    }

    /**
     * Returns true if the level was completed by visiting all lily pads (perfect run).
     */
    public boolean isPerfectCompletion() {
        return isLevelComplete() && lilyPadsVisited == totalLilyPads - 1;
    }

    /**
     * Returns true if the duck is dead (no valid moves left).
     */
    public boolean isDead() {
        return isDead;
    }

    /**
     * Get the tile at the specified (row, col), or null if out of bounds.
     */
    public Tile getTile(int row, int col) {
        if (row < 0 || row >= rows || col < 0 || col >= cols) {
            return null;
        }
        return board[row][col];
    }

    public Duck getDuck() {
        return duck;
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public int getBreadRow() {
        return breadRow;
    }

    public int getBreadCol() {
        return breadCol;
    }

    public int getLilyPadsVisited() {
        return lilyPadsVisited;
    }

    public int getTotalLilyPads() {
        return totalLilyPads;
    }

    public float getCompletionPercentage() {
        return (float) lilyPadsVisited / totalLilyPads * 100;
    }

    /**
     * Calculates how many stars to award for this completion:
     * 3 stars: perfect (all lily pads visited)
     * 2 stars: at least 80% lily pads visited
     * 1 star: level completed but not perfect/not >= 80%
     * 0 stars: not completed
     */
    public int calculateStars() {
        if (!isLevelComplete()) {
            return 0;
        }
        if (isPerfectCompletion()) {
            return 3; // ⭐⭐⭐ Perfect
        } else {
            float percentage = getCompletionPercentage();
            if (percentage >= 80) {
                return 2; // ⭐⭐ Good
            } else {
                return 1; // ⭐ Completed
            }
        }
    }
}