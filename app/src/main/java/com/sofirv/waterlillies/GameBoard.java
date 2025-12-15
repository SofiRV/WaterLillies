package com.sofirv.waterlillies;

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

    private void generateShore() {
        Tile. TileType[][] tempBoard = new Tile.TileType[rows][cols];

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                tempBoard[r][c] = board[r][c]. getType();
            }
        }

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (tempBoard[r][c] == Tile.TileType. WATER) {
                    if (hasAdjacentLilyPad(r, c, tempBoard)) {
                        board[r][c]. setType(Tile.TileType.SHORE);
                    }
                }
            }
        }
    }

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

    public boolean moveDuck(int deltaRow, int deltaCol) {
        if (isDead) {
            return false;
        }

        int newRow = duck.getRow() + deltaRow;
        int newCol = duck.getCol() + deltaCol;

        // Verificar límites
        if (newRow < 0 || newRow >= rows || newCol < 0 || newCol >= cols) {
            return false;
        }

        Tile targetTile = board[newRow][newCol];

        // Verificar si puede moverse a esa casilla
        if (! targetTile.isWalkable()) {
            return false;
        }

        // Hundir el nenúfar actual (se convierte en AGUA)
        Tile currentTile = board[duck.getRow()][duck.getCol()];
        if (currentTile.getType() == Tile.TileType. LILY_PAD) {
            currentTile.setType(Tile.TileType.WATER);
            lilyPadsVisited++;
        }

        // Mover el patito
        duck.move(deltaRow, deltaCol);

        // Verificar si llegó al objetivo (para no marcar muerte si ganó)
        if (isLevelComplete()) {
            return true;
        }

        // Verificar si el patito ha muerto (sin movimientos posibles)
        if (!hasValidMoves()) {
            isDead = true;
        }

        return true;
    }

    // Verifica si el patito tiene algún movimiento válido disponible
    public boolean hasValidMoves() {
        int currentRow = duck.getRow();
        int currentCol = duck.getCol();

        // Verificar las 4 direcciones (arriba, abajo, izquierda, derecha)
        int[][] directions = {
                {-1, 0},  // Arriba
                {1, 0},   // Abajo
                {0, -1},  // Izquierda
                {0, 1}    // Derecha
        };

        for (int[] dir :  directions) {
            int newRow = currentRow + dir[0];
            int newCol = currentCol + dir[1];

            // Verificar límites
            if (newRow >= 0 && newRow < rows && newCol >= 0 && newCol < cols) {
                Tile tile = board[newRow][newCol];
                // Si hay al menos una casilla caminable, tiene movimientos
                if (tile.isWalkable()) {
                    return true;
                }
            }
        }

        return false; // No hay movimientos válidos = MUERTE
    }

    public boolean isLevelComplete() {
        return duck.getRow() == breadRow && duck.getCol() == breadCol;
    }

    public boolean isPerfectCompletion() {
        return isLevelComplete() && lilyPadsVisited == totalLilyPads;
    }

    public boolean isDead() {
        return isDead;
    }

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
    // Agregar al final de la clase GameBoard:

    public int calculateStars() {
        if (! isLevelComplete()) {
            return 0;
        }

        if (isPerfectCompletion()) {
            return 3; // ⭐⭐⭐ Perfecto
        } else {
            float percentage = getCompletionPercentage();
            if (percentage >= 80) {
                return 2; // ⭐⭐ Bueno
            } else {
                return 1; // ⭐ Completado
            }
        }
    }
}