package com.sofirv.waterlilies.game2048;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.view.GestureDetector;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.sofirv.waterlilies.R;
import com.sofirv.waterlilies.main_app.HomeActivity;
import com.sofirv.waterlilies.main_app.ScoreDBHelper;
import com.sofirv.waterlilies.main_app.Score;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Stack;

public class MainActivity2048 extends AppCompatActivity {

    int[][] grid = new int[4][4];
    int score = 0;
    private TextView scoreTextView;
    private TextView highScoreTextView;
    private TextView[][] cellTextViews = new TextView[4][4];
    private Map<Integer, Integer> colorMap = new HashMap<>();
    private GestureDetector gestureListener;
    private Button resetButton;
    private Button undoButton;
    private Button homeButton;
    private static final int MAX_UNDOS = 4;
    private int undosLeft = MAX_UNDOS;
    private Stack<int[][]> gridHistory = new Stack<>();
    private Stack<Integer> scoreHistory = new Stack<>();

    // Timer variables
    private TextView timerTextView;
    private boolean isAscendingTimer = true; // Default: ascendente
    private CountDownTimer countDownTimer;
    private long timerMillis = 0;
    private static final long DESCENDING_TIME_MILLIS = 5 * 60 * 1000; // 5 minutos
    private long startTimeMillis;
    private Handler timerHandler = new Handler(Looper.getMainLooper());
    private Runnable timerRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.game_2048_activity_main);

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
        timerTextView = findViewById(R.id.timer_value);

        resetButton = findViewById(R.id.button_reset);
        resetButton.setOnClickListener(v -> resetGame());
        undoButton = findViewById(R.id.button_undo);
        undoButton.setOnClickListener(v -> undoMove());
        homeButton = findViewById(R.id.button_home);
        homeButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity2048.this, HomeActivity.class);
            startActivity(intent);
        });

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                int resID = getResources().getIdentifier("cell_" + i + "_" + j, "id", getPackageName());
                cellTextViews[i][j] = findViewById(resID);
            }
        }

        initColormap();
        showTimerChoiceDialog();
        updateUndoButton();

        // Al iniciar, muestra el mejor score de la BD
        updateHighScoreFromDB();
    }

    private void updateHighScoreFromDB() {
        ScoreDBHelper dbHelper = new ScoreDBHelper(this);
        List<Score> scores = dbHelper.getScoresByGameOrdered("2048", true);
        int bestScore = scores.isEmpty() ? 0 : scores.get(0).score;
        highScoreTextView.setText(String.valueOf(bestScore));
    }

    private void startGame() {
        score = 0;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                grid[i][j] = 0;
                updateCell(i, j);
            }
        }
        addRandomTile();
        addRandomTile();
        updateUI();
    }

    private void resetGame() {
        score = 0;
        undosLeft = MAX_UNDOS;
        gridHistory.clear();
        scoreHistory.clear();
        stopTimer();
        // Puedes elegir si quieres preguntar de nuevo el temporizador o reiniciar el mismo tipo:
        // showTimerChoiceDialog();   // = para elegir de nuevo
        startGameWithTimer(isAscendingTimer); // = para mantener la elección anterior
        updateUndoButton();
    }

    // Detener cualquier timer existente
    private void stopTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
        if (timerRunnable != null)
            timerHandler.removeCallbacks(timerRunnable);
        timerMillis = 0;
        timerTextView.setText(formatTime(0));
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
        // GAME OVER: Guarda el score en la BD
        onGameOver(score);
        stopTimer();
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
                    : ContextCompat.getColor(this, R.color.pink_big);
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
        } else if (!gridHistory.isEmpty()) {
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
        } else if (!gridHistory.isEmpty()) {
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
        } else if (!gridHistory.isEmpty()) {
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
        updateHighScoreFromDB();

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

    private void updateUndoButton() {
        undoButton.setEnabled(undosLeft > 0);
        undoButton.setAlpha(undosLeft > 0 ? 1f : 0.5f);
        undoButton.setText("Step back " + undosLeft + "/" + MAX_UNDOS);
    }

    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
        );
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    // Guarda el score al terminar la partida
    private void onGameOver(int finalScore) {
        ScoreDBHelper dbHelper = new ScoreDBHelper(this);
        dbHelper.addScore("Jugador", finalScore, "2048");
        Toast.makeText(this, "Game Over! Puntaje: " + finalScore, Toast.LENGTH_SHORT).show();
        updateHighScoreFromDB();
    }

    private void showTimerChoiceDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Selecciona tipo de temporizador")
                .setMessage("¿Quieres un temporizador ascendente o descendente?")
                .setPositiveButton("Ascendente", (dialog, which) -> {
                    startGameWithTimer(true); // true = ascendente
                })
                .setNegativeButton("Descendente", (dialog, which) -> {
                    startGameWithTimer(false); // false = descendente
                })
                .setCancelable(false)
                .show();
    }

    private void startGameWithTimer(boolean ascending) {
        isAscendingTimer = ascending;
        stopTimer(); // Por si venías de otra partida/tipo
        if (isAscendingTimer) {
            startAscendingTimer();
        } else {
            startDescendingTimer();
        }
        startGame();
    }

    private void startAscendingTimer() {
        startTimeMillis = System.currentTimeMillis();
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                timerMillis = System.currentTimeMillis() - startTimeMillis;
                timerTextView.setText(formatTime(timerMillis));
                timerHandler.postDelayed(this, 1000);
            }
        };
        timerHandler.post(timerRunnable);
    }

    private void startDescendingTimer() {
        countDownTimer = new CountDownTimer(DESCENDING_TIME_MILLIS, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timerMillis = millisUntilFinished;
                timerTextView.setText(formatTime(timerMillis));
            }

            @Override
            public void onFinish() {
                timerTextView.setText("00:00");
                endGameDueToTimeout();
            }
        };
        countDownTimer.start();
    }

    private void endGameDueToTimeout() {
        stopTimer();
        new AlertDialog.Builder(this)
                .setTitle("Fin del juego")
                .setMessage("El tiempo se ha agotado. ¿Quieres volver a intentar?")
                .setPositiveButton("Reiniciar", (dialog, which) -> resetGame())
                .setNegativeButton("Salir", (dialog, which) -> finish())
                .show();
    }

    private String formatTime(long millis) {
        int seconds = (int) (millis / 1000) % 60;
        int minutes = (int) ((millis / (1000 * 60)) % 60);
        return String.format("%02d:%02d", minutes, seconds);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopTimer();
    }
}