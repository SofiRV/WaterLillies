package com.sofirv.waterlilies.duckgame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.core.content.res.ResourcesCompat;

import com.sofirv.waterlilies.R;
import com.sofirv.waterlilies.main_app.ScoreDBHelper;

public class GameView extends View {
    private GameBoard gameBoard;
    private LevelManager levelManager;
    private ProgressManager progressManager;
    private Paint paint;
    private Paint strokePaint;
    private Paint textPaint;
    private float tileSize;
    private float offsetX, offsetY;

    // Imágenes
    private Bitmap duckBitmap;
    private Bitmap duckDeadBitmap;
    private Bitmap breadBitmap;
    private Bitmap lilyPadBitmap;
    private Bitmap backgroundBitmap; // Fondo de agua

    private float startX, startY;
    private static final int MIN_SWIPE_DISTANCE = 50;

    public GameView(Context context) {
        super(context);
        init(context);
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public GameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        paint = new Paint();
        paint.setAntiAlias(true);

        strokePaint = new Paint();
        strokePaint.setAntiAlias(true);
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setStrokeWidth(3);

        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(36);
        textPaint.setTextAlign(Paint.Align.LEFT);
        textPaint.setShadowLayer(4, 2, 2, Color.BLACK);

        loadImages(context);

        levelManager = new LevelManager(context);
        progressManager = levelManager.getProgressManager();
        gameBoard = new GameBoard(11, 13);
        loadCurrentLevel();
    }

    private void loadImages(Context context) {
        try {
            duckBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.duck);

            try {
                duckDeadBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.duck_dead_duck);
            } catch (Exception e) {
                duckDeadBitmap = duckBitmap;
            }

            breadBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.duck_bread);

            try {
                lilyPadBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.duck_water_lily);
            } catch (Exception e) {
                lilyPadBitmap = null;
            }

            try {
                backgroundBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.duck_bg_water);
            } catch (Exception e) {
                backgroundBitmap = null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Error cargando imágenes", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadCurrentLevel() {
        Level level = levelManager.getCurrentLevel();
        if (level != null) {
            gameBoard.loadLevel(level);
            invalidate();
        }
    }

    public void restartLevel() {
        loadCurrentLevel();
        Toast.makeText(getContext(), "Nivel reiniciado", Toast.LENGTH_SHORT).show();
    }

    public void goToNextLevel() {
        if (levelManager.hasNextLevel()) {
            levelManager.nextLevel();
            loadCurrentLevel();
            calculateTileSize(getWidth(), getHeight());
            Toast.makeText(getContext(),
                    "Nivel " + levelManager.getCurrentLevelNumber(),
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(),
                    "Ya estás en el último nivel",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        calculateTileSize(w, h);
    }

    private void calculateTileSize(int w, int h) {
        float tileSizeByWidth = (float) w / gameBoard.getCols();
        float tileSizeByHeight = (float) h / gameBoard.getRows();
        tileSize = Math.min(tileSizeByWidth, tileSizeByHeight);

        offsetX = (w - (tileSize * gameBoard.getCols())) / 2;
        offsetY = (h - (tileSize * gameBoard.getRows())) / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Dibujar fondo de agua
        if (backgroundBitmap != null) {
            canvas.drawBitmap(backgroundBitmap, null,
                    new Rect(0, 0, getWidth(), getHeight()), paint);
        } else {
            canvas.drawColor(Color.parseColor("#87CEEB")); // fallback
        }

        // Dibujar tiles (solo nenúfares)
        for (int r = 0; r < gameBoard.getRows(); r++) {
            for (int c = 0; c < gameBoard.getCols(); c++) {
                drawTile(canvas, gameBoard.getTile(r, c), r, c);
            }
        }

        drawDuck(canvas);
        drawLevelInfo(canvas);

        if (gameBoard.isDead()) {
            drawGameOver(canvas);
        }
    }

    private void drawTile(Canvas canvas, Tile tile, int row, int col) {
        int left = (int) (offsetX + tile.getCol() * tileSize);
        int top = (int) (offsetY + tile.getRow() * tileSize);

        switch (tile.getType()) {
            case LILY_PAD:
                // Escala grande pero centrada
                float scale = 1.15f + (float)Math.random() * 0.05f; // 115-120%
                float lilyWidth = tileSize * scale;
                float lilyHeight = tileSize * scale;
                // Centrar correctamente
                float lilyX = left + (tileSize - lilyWidth) / 2f;
                float lilyY = top + (tileSize - lilyHeight) / 2f;

                if (lilyPadBitmap != null) {
                    Rect lilyRect = new Rect(
                            (int) lilyX,
                            (int) lilyY,
                            (int) (lilyX + lilyWidth),
                            (int) (lilyY + lilyHeight)
                    );
                    canvas.drawBitmap(lilyPadBitmap, null, lilyRect, paint);
                } else {
                    paint.setColor(Color.parseColor("#7CB342"));
                    canvas.drawOval(lilyX, lilyY, lilyX + lilyWidth, lilyY + lilyHeight, paint);
                }

                if (row == gameBoard.getBreadRow() && col == gameBoard.getBreadCol()) {
                    drawBread(canvas, left, top);
                }
                break;

            case WATER:
            case SHORE:
            case BREAD:
                // No dibujar nada extra
                break;
        }
    }

    private void drawDuck(Canvas canvas) {
        Duck duck = gameBoard.getDuck();
        float x = offsetX + duck.getCol() * tileSize;
        float y = offsetY + duck.getRow() * tileSize;

        // Tamaño grande pero centrado
        float duckSize = tileSize * 1.8f; // algo más equilibrado que 1.9
        float duckX = x + (tileSize - duckSize) / 2f;
        float duckY = y + (tileSize - duckSize) / 2f;

        Rect destRect = new Rect(
                (int) duckX,
                (int) duckY,
                (int) (duckX + duckSize),
                (int) (duckY + duckSize)
        );

        Bitmap duckImage = gameBoard.isDead() ? duckDeadBitmap : duckBitmap;
        if (duckImage != null) {
            canvas.drawBitmap(duckImage, null, destRect, paint);
        }
    }

    private void drawBread(Canvas canvas, float x, float y) {
        if (breadBitmap != null) {
            float breadSize = tileSize * 1.1f;
            float breadX = x + (tileSize - breadSize) / 2;
            float breadY = y + (tileSize - breadSize) / 2;

            Rect destRect = new Rect((int) breadX, (int) breadY,
                    (int) (breadX + breadSize), (int) (breadY + breadSize));
            canvas.drawBitmap(breadBitmap, null, destRect, paint);
        } else {
            paint.setColor(Color.parseColor("#FFD54F"));
            float breadSize = tileSize * 0.6f;
            float breadX = x + (tileSize - breadSize) / 2;
            float breadY = y + (tileSize - breadSize) / 2;
            canvas.drawRect(breadX, breadY, breadX + breadSize, breadY + breadSize, paint);
        }
    }


    private void drawLevelInfo(Canvas canvas) {
        String levelText = "Nivel " + levelManager.getCurrentLevelNumber() + "/" + levelManager.getTotalLevels();
        String progressText = gameBoard.getLilyPadsVisited() + "/" + gameBoard.getTotalLilyPads();

        canvas.drawText(levelText, 20, 45, textPaint);
        canvas.drawText(progressText, 20, 85, textPaint);
    }

    private void drawGameOver(Canvas canvas) {
        // Oscurecer fondo usando el bitmap de agua como base
        if (backgroundBitmap != null) {
            paint.setAlpha(180); // semitransparente
            canvas.drawBitmap(backgroundBitmap, null,
                    new Rect(0, 0, getWidth(), getHeight()), paint);
            paint.setAlpha(255); // reset alpha
        } else {
            // fallback
            paint.setColor(Color.argb(200, 0, 0, 0));
            canvas.drawRect(0, 0, getWidth(), getHeight(), paint);
        }

        // Texto principal "GAME OVER"
        Paint gameOverPaint = new Paint();
        gameOverPaint.setAntiAlias(true);
        gameOverPaint.setColor(Color.parseColor("#FFC0CB"));
        gameOverPaint.setTextSize(90);
        gameOverPaint.setTextAlign(Paint.Align.CENTER);
        gameOverPaint.setShadowLayer(8, 0, 0, Color.BLACK);

        Typeface cuteFont = ResourcesCompat.getFont(getContext(), R.font.press_start_2_p);
        gameOverPaint.setTypeface(cuteFont);

        canvas.drawText("GAME OVER", getWidth()/2, getHeight()/2 - 60, gameOverPaint);

        // Emoji o icono de patito muerto
        Paint emojiPaint = new Paint();
        emojiPaint.setTextSize(60);
        emojiPaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("💀", getWidth()/2, getHeight()/2, emojiPaint);

        // Subtítulo
        Paint subtitlePaint = new Paint();
        subtitlePaint.setAntiAlias(true);
        subtitlePaint.setColor(Color.WHITE);
        subtitlePaint.setTextSize(40);
        subtitlePaint.setTextAlign(Paint.Align.CENTER);
        subtitlePaint.setShadowLayer(4, 0, 0, Color.BLACK);
        canvas.drawText("¡Sin movimientos posibles!", getWidth()/2, getHeight()/2 + 60, subtitlePaint);
        canvas.drawText("Presiona 🔄 para reintentar", getWidth()/2, getHeight()/2 + 110, subtitlePaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = event.getX();
                startY = event.getY();
                return true;
            case MotionEvent.ACTION_UP:
                float endX = event.getX();
                float endY = event.getY();
                handleSwipe(startX, startY, endX, endY);
                return true;
        }
        return super.onTouchEvent(event);
    }

    private void handleSwipe(float startX, float startY, float endX, float endY) {
        if (gameBoard.isDead()) return;

        float deltaX = endX - startX;
        float deltaY = endY - startY;

        if (Math.abs(deltaX) < MIN_SWIPE_DISTANCE && Math.abs(deltaY) < MIN_SWIPE_DISTANCE) return;

        int moveRow = 0;
        int moveCol = 0;

        if (Math.abs(deltaX) > Math.abs(deltaY)) {
            moveCol = (deltaX > 0) ? 1 : -1;
        } else {
            moveRow = (deltaY > 0) ? 1 : -1;
        }

        boolean moved = gameBoard.moveDuck(moveRow, moveCol);
        if (moved) {
            invalidate();
            if (gameBoard.isDead()) {
                postDelayed(() -> Toast.makeText(getContext(), "¡Has muerto! 💀", Toast.LENGTH_LONG).show(), 100);
            }
            if (gameBoard.isLevelComplete()) handleLevelComplete();
        }
    }

    private void handleLevelComplete() {
        int stars = gameBoard.calculateStars();
        String message;
        if (stars == 3) message = "¡Perfecto! ⭐⭐⭐";
        else if (stars == 2) message = "¡Muy bien! ⭐⭐";
        else message = "¡Completado! ⭐";

        int currentLevel = levelManager.getCurrentLevelNumber();
        progressManager.completeLevel(currentLevel, gameBoard.isPerfectCompletion());
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();

        postDelayed(() -> {
            if (levelManager.hasNextLevel()) goToNextLevel();
            else showGameCompleted();
        }, 1500);
    }

    private void showGameCompleted() {
        int totalStars = progressManager.getTotalStars();
        Toast.makeText(getContext(),
                "¡Juego completado! 🎉\nEstrellas totales: " + totalStars + "/15",
                Toast.LENGTH_LONG).show();
        saveScore(gameBoard.calculateStars() * 100); // ejemplo: cada estrella = 100 puntos
    }
    private void saveScore(int score) {
        ScoreDBHelper dbHelper = new ScoreDBHelper(getContext());
        String playerName = "Sofi"; // o pedir al usuario su nombre
        dbHelper.addScore(playerName, score);
    }
}