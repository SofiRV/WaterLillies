package com.sofirv.waterlillies.game2048;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.GestureDetector;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import java.util.Stack;

public class MainActivity2048 extends AppCompatActivity {

    int[][] grid = new int[4][4];
    int score = 0;
    int highScore = 0;
    private TextView scoreTextView;
    private TextView highScoreTextView;
    private TextView[][] cellTextViews = new TextView[4][4];
    private Map<Integer, Integer> colorMap = new HashMap<>();
    private GestureDetector gestureListener;
    private static final String PREFS_NAME = "game_prefs";
    private static final String HIGH_SCORE_KEY = "high_score";
    private Button resetButton;
    private Button undoButton;
    private static final int MAX_UNDOS = 4;
    private int undosLeft = MAX_UNDOS;
    private Stack<int[][]> gridHistory = new Stack<>();
    private Stack<Integer> scoreHistory = new Stack<>();






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        gestureListener = new GestureDetector(this, new GestureListener(this));
        findViewById(R.id.main).setOnTouchListener((v, event) -> {
            gestureListener.onTouchEvent(event);
            return true;
        });

        scoreTextView = findViewById(R.id.score_value);
        highScoreTextView = findViewById(R.id.high_score_value);
        resetButton = findViewById(R.id.button_reset);
        resetButton.setOnClickListener(v -> resetGame());
        undoButton = findViewById(R.id.button_undo);
        undoButton.setOnClickListener(v -> undoMove());



        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        highScore = prefs.getInt(HIGH_SCORE_KEY, 0);
        highScoreTextView.setText(String.valueOf(highScore));

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                int resID = getResources().getIdentifier("cell_" + i + "_" + j, "id", getPackageName());
                cellTextViews[i][j] = findViewById(resID);
            }
        }

        initColormap();
        startGame();
        updateUndoButton();

    }

    private void startGame() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                grid[i][j] = 0;
                updateCell(i, j);
            }
        }
        addRandomTile();
        addRandomTile();
    }

    private void resetGame(){
        score=0;
        startGame();
        updateUI();
        undosLeft = MAX_UNDOS;
        gridHistory.clear();
        scoreHistory.clear();
        updateUndoButton();

    }

    private boolean isGameOver() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (grid[i][j] == 0) return false;
            }
        }

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (j < 3 && grid[i][j] == grid[i][j + 1]) return false;
                if (i < 3 && grid[i][j] == grid[i + 1][j]) return false;
            }
        }

        return true;
    }


    private void updateCell(int row, int col) {
        TextView cell = cellTextViews[row][col];
        if (cell == null) return;

        int value = grid[row][col];

        if (value == 0) {
            cell.setText("");
            cell.setBackgroundColor(ContextCompat.getColor(this, R.color.baby_pink));
        } else {
            cell.setText(String.valueOf(value));

            int color = colorMap.containsKey(value)
                    ? colorMap.get(value)
                    : ContextCompat.getColor(this, R.color.pink_big); // <-- para > 2048

            cell.setBackgroundColor(color);
        }
    }


    private void addRandomTile() {
        List<int[]> emptyCells = new ArrayList<>();
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++)
                if (grid[i][j] == 0)
                    emptyCells.add(new int[]{i, j});

        if (!emptyCells.isEmpty()) {
            int[] cell = emptyCells.get(new Random().nextInt(emptyCells.size()));
            grid[cell[0]][cell[1]] = 2;
            updateCell(cell[0], cell[1]);
        }
    }

    private void initColormap() {
        colorMap.put(0, ContextCompat.getColor(this, R.color.baby_pink));
        colorMap.put(2, ContextCompat.getColor(this, R.color.pink_2));
        colorMap.put(4, ContextCompat.getColor(this, R.color.pink_4));
        colorMap.put(8, ContextCompat.getColor(this, R.color.pink_8));
        colorMap.put(16, ContextCompat.getColor(this, R.color.pink_16));
        colorMap.put(32, ContextCompat.getColor(this, R.color.pink_32));
        colorMap.put(64, ContextCompat.getColor(this, R.color.pink_64));
        colorMap.put(128, ContextCompat.getColor(this, R.color.pink_128));
        colorMap.put(256, ContextCompat.getColor(this, R.color.pink_256));
        colorMap.put(512, ContextCompat.getColor(this, R.color.pink_512));
        colorMap.put(1024, ContextCompat.getColor(this, R.color.pink_1024));
        colorMap.put(2048, ContextCompat.getColor(this, R.color.pink_2048));

    }

    public void moveRight() {
        savePreviousState();
        boolean moved = false;
        for (int i = 0; i < 4; i++) moved |= moveRowRight(i);
        if (moved) {
            addRandomTile();
            updateUI();
        }else if (!gridHistory.isEmpty()) {
            gridHistory.pop();
            scoreHistory.pop();
        }
    }

    public void moveLeft() {
        savePreviousState();
        boolean moved = false;
        for (int i = 0; i < 4; i++) moved |= moveRowLeft(i);
        if (moved) {
            addRandomTile();
            updateUI();
        } else if (!gridHistory.isEmpty()) {
            gridHistory.pop();
            scoreHistory.pop();
        }
    }

    public void moveUp() {
        savePreviousState();
        boolean moved = false;
        for (int i = 0; i < 4; i++) moved |= moveColumnUp(i);
        if (moved) {
            addRandomTile();
            updateUI();
        }else if (!gridHistory.isEmpty()) {
            gridHistory.pop();
            scoreHistory.pop();
        }
    }

    public void moveDown() {
        savePreviousState();
        boolean moved = false;
        for (int i = 0; i < 4; i++) moved |= moveColumnDown(i);
        if (moved) {
            addRandomTile();
            updateUI();
        }else if (!gridHistory.isEmpty()) {
            gridHistory.pop();
            scoreHistory.pop();
        }
    }

    private void updateUI() {
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++)
                updateCell(i, j);
        updateUndoButton();


        scoreTextView.setText(String.valueOf(score));
        highScoreTextView.setText(String.valueOf(highScore));

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int savedHighScore = prefs.getInt(HIGH_SCORE_KEY, 0);
        if (highScore > savedHighScore) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(HIGH_SCORE_KEY, highScore);
            editor.apply();
        }

        if (isGameOver()) {
            Toast.makeText(this, "Game Over", Toast.LENGTH_LONG).show();
            undosLeft = MAX_UNDOS;
            gridHistory.clear();
            scoreHistory.clear();
        }
    }

    private boolean moveRowRight(int row) {
        boolean moved = false;
        int[] currentRow = grid[row];

        for (int i = 3; i >= 0; i--) {
            if (currentRow[i] == 0) {
                for (int j = i - 1; j >= 0; j--) {
                    if (currentRow[j] != 0) {
                        currentRow[i] = currentRow[j];
                        currentRow[j] = 0;
                        moved = true;
                        break;
                    }
                }
            }
        }

        for (int i = 3; i >= 1; i--) {
            if (currentRow[i] != 0 && currentRow[i] == currentRow[i - 1]) {
                currentRow[i] *= 2;
                currentRow[i - 1] = 0;
                score += currentRow[i];
                if (score > highScore) highScore = score;
                moved = true;
            }
        }

        for (int i = 3; i >= 0; i--) {
            if (currentRow[i] == 0) {
                for (int j = i - 1; j >= 0; j--) {
                    if (currentRow[j] != 0) {
                        currentRow[i] = currentRow[j];
                        currentRow[j] = 0;
                        break;
                    }
                }
            }
        }
        return moved;
    }

    private boolean moveRowLeft(int row) {
        boolean moved = false;
        int[] currentRow = grid[row];

        for (int i = 0; i < 4; i++) {
            if (currentRow[i] == 0) {
                for (int j = i + 1; j < 4; j++) {
                    if (currentRow[j] != 0) {
                        currentRow[i] = currentRow[j];
                        currentRow[j] = 0;
                        moved = true;
                        break;
                    }
                }
            }
        }

        for (int i = 0; i < 3; i++) {
            if (currentRow[i] != 0 && currentRow[i] == currentRow[i + 1]) {
                currentRow[i] *= 2;
                currentRow[i + 1] = 0;
                score += currentRow[i];
                if (score > highScore) highScore = score;
                moved = true;
            }
        }

        for (int i = 0; i < 4; i++) {
            if (currentRow[i] == 0) {
                for (int j = i + 1; j < 4; j++) {
                    if (currentRow[j] != 0) {
                        currentRow[i] = currentRow[j];
                        currentRow[j] = 0;
                        break;
                    }
                }
            }
        }

        return moved;
    }

    private boolean moveColumnUp(int col) {
        boolean moved = false;
        int[] column = new int[4];
        for (int i = 0; i < 4; i++) column[i] = grid[i][col];

        for (int i = 0; i < 4; i++) {
            if (column[i] == 0) {
                for (int j = i + 1; j < 4; j++) {
                    if (column[j] != 0) {
                        column[i] = column[j];
                        column[j] = 0;
                        moved = true;
                        break;
                    }
                }
            }
        }

        for (int i = 0; i < 3; i++) {
            if (column[i] != 0 && column[i] == column[i + 1]) {
                column[i] *= 2;
                column[i + 1] = 0;
                score += column[i];
                if (score > highScore) highScore = score;
                moved = true;
            }
        }

        for (int i = 0; i < 4; i++) {
            if (column[i] == 0) {
                for (int j = i + 1; j < 4; j++) {
                    if (column[j] != 0) {
                        column[i] = column[j];
                        column[j] = 0;
                        break;
                    }
                }
            }
        }

        for (int i = 0; i < 4; i++) grid[i][col] = column[i];

        return moved;
    }

    private boolean moveColumnDown(int col) {
        boolean moved = false;
        int[] column = new int[4];
        for (int i = 0; i < 4; i++) column[i] = grid[i][col];

        for (int i = 3; i >= 0; i--) {
            if (column[i] == 0) {
                for (int j = i - 1; j >= 0; j--) {
                    if (column[j] != 0) {
                        column[i] = column[j];
                        column[j] = 0;
                        moved = true;
                        break;
                    }
                }
            }
        }

        for (int i = 3; i >= 1; i--) {
            if (column[i] != 0 && column[i] == column[i - 1]) {
                column[i] *= 2;
                column[i - 1] = 0;
                score += column[i];
                if (score > highScore) highScore = score;
                moved = true;
            }
        }

        for (int i = 3; i >= 0; i--) {
            if (column[i] == 0) {
                for (int j = i - 1; j >= 0; j--) {
                    if (column[j] != 0) {
                        column[i] = column[j];
                        column[j] = 0;
                        break;
                    }
                }
            }
        }

        for (int i = 0; i < 4; i++) grid[i][col] = column[i];

        return moved;
    }
    private void savePreviousState() {
        if (undosLeft <= 0) return;

        int[][] copy = new int[4][4];
        for (int i = 0; i < 4; i++) {
            System.arraycopy(grid[i], 0, copy[i], 0, 4);
        }

        gridHistory.push(copy);
        scoreHistory.push(score);
    }



    private void undoMove() {
        if (undosLeft <= 0 || gridHistory.isEmpty()) {
            Toast.makeText(this, "No quedan undos", Toast.LENGTH_SHORT).show();
            return;
        }

        grid = gridHistory.pop();
        score = scoreHistory.pop();
        undosLeft--;

        updateUI();
        updateUndoButton();
    }

    private void updateUndoButton(){
        undoButton.setEnabled(undosLeft>0);
        undoButton.setAlpha(undosLeft > 0 ? 1f : 0.5f);
        undoButton.setText("Step back " + undosLeft + "/" + MAX_UNDOS);

    }


}
