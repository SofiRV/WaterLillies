package com.sofirv.waterlillies;

import android.content.Context;
import android.graphics.Bitmap;
import android. graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics. Rect;
import android.graphics.drawable. Drawable;
import android.util. AttributeSet;
import android. view.MotionEvent;
import android.view.View;
import android.widget.Toast;
import androidx.core.content. ContextCompat;

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
    private Bitmap waterBitmap;
    private Bitmap shoreBitmap;

    // Drawables XML
    private Drawable waterDrawable;
    private Drawable lilyPadDrawable;
    private Drawable shoreDrawable;

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

        // Cargar imágenes
        loadImages(context);

        // Cargar drawables XML
        waterDrawable = ContextCompat. getDrawable(context, R. drawable.tile_water);
        lilyPadDrawable = ContextCompat.getDrawable(context, R.drawable.tile_lily_pad);
        shoreDrawable = ContextCompat. getDrawable(context, R. drawable.tile_shore);

        levelManager = new LevelManager(context);
        progressManager = levelManager.getProgressManager();
        gameBoard = new GameBoard(11, 13);
        loadCurrentLevel();
    }

    private void loadImages(Context context) {
        try {
            // Cargar imagen del patito
            duckBitmap = BitmapFactory. decodeResource(context.getResources(), R.drawable.duck);

            // Cargar imagen del patito muerto (si existe, sino usar la misma)
            try {
                duckDeadBitmap = BitmapFactory. decodeResource(context.getResources(), R.drawable.dead_duck);
            } catch (Exception e) {
                duckDeadBitmap = duckBitmap; // Usar la misma imagen si no existe duck_dead
            }

            // Cargar imagen del pan
            breadBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.bread);

            // Cargar imágenes opcionales de tiles
            try {
                lilyPadBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.water_lily);
            } catch (Exception e) {
                lilyPadBitmap = null; // Usar drawable XML si no existe
            }

            try {
                waterBitmap = BitmapFactory. decodeResource(context.getResources(), R.drawable.tile_water);
            } catch (Exception e) {
                waterBitmap = null;
            }

            try {
                shoreBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.tile_shore);
            } catch (Exception e) {
                shoreBitmap = null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Error cargando imágenes", Toast. LENGTH_SHORT).show();
        }
    }

    private void loadCurrentLevel() {
        Level level = levelManager.getCurrentLevel();
        if (level != null) {
            gameBoard. loadLevel(level);
            invalidate();
        }
    }

    public void restartLevel() {
        loadCurrentLevel();
        Toast.makeText(getContext(), "Nivel reiniciado", Toast. LENGTH_SHORT).show();
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

        canvas.drawColor(Color.parseColor("#2E7BB4"));

        // Dibujar tiles
        for (int r = 0; r < gameBoard.getRows(); r++) {
            for (int c = 0; c < gameBoard.getCols(); c++) {
                drawTile(canvas, gameBoard.getTile(r, c), r, c);
            }
        }

        // Dibujar patito
        drawDuck(canvas);

        // Dibujar info del nivel
        drawLevelInfo(canvas);

        // Dibujar Game Over si murió
        if (gameBoard. isDead()) {
            drawGameOver(canvas);
        }
    }

    private void drawLevelInfo(Canvas canvas) {
        String levelText = "Nivel " + levelManager.getCurrentLevelNumber() + "/" + levelManager.getTotalLevels();
        String progressText = gameBoard.getLilyPadsVisited() + "/" + gameBoard.getTotalLilyPads();

        canvas.drawText(levelText, 20, 45, textPaint);
        canvas.drawText(progressText, 20, 85, textPaint);
    }

    private void drawGameOver(Canvas canvas) {
        // Fondo semi-transparente
        paint.setColor(Color.argb(200, 0, 0, 0));
        canvas.drawRect(0, 0, getWidth(), getHeight(), paint);

        // Texto "GAME OVER"
        Paint gameOverPaint = new Paint();
        gameOverPaint.setAntiAlias(true);
        gameOverPaint.setColor(Color.RED);
        gameOverPaint. setTextSize(80);
        gameOverPaint. setTextAlign(Paint.Align.CENTER);
        gameOverPaint.setShadowLayer(6, 0, 0, Color.BLACK);

        canvas.drawText("GAME OVER", getWidth() / 2, getHeight() / 2 - 50, gameOverPaint);

        // Texto "Sin movimientos"
        Paint subtitlePaint = new Paint();
        subtitlePaint.setAntiAlias(true);
        subtitlePaint.setColor(Color.WHITE);
        subtitlePaint.setTextSize(40);
        subtitlePaint. setTextAlign(Paint.Align.CENTER);
        subtitlePaint.setShadowLayer(4, 0, 0, Color.BLACK);

        canvas.drawText("¡Sin movimientos posibles!", getWidth() / 2, getHeight() / 2 + 30, subtitlePaint);
        canvas.drawText("Presiona 🔄 para reintentar", getWidth() / 2, getHeight() / 2 + 80, subtitlePaint);
    }

    private void drawTile(Canvas canvas, Tile tile, int row, int col) {
        int left = (int) (offsetX + tile.getCol() * tileSize);
        int top = (int) (offsetY + tile.getRow() * tileSize);
        int right = (int) (left + tileSize);
        int bottom = (int) (top + tileSize);

        Rect rect = new Rect(left, top, right, bottom);

        switch (tile.getType()) {
            case WATER:
                if (waterBitmap != null) {
                    canvas.drawBitmap(waterBitmap, null, rect, paint);
                } else if (waterDrawable != null) {
                    waterDrawable.setBounds(rect);
                    waterDrawable.draw(canvas);
                } else {
                    paint.setColor(Color.parseColor("#87CEEB"));
                    canvas.drawRect(rect, paint);
                }
                break;

            case LILY_PAD:
                if (lilyPadBitmap != null) {
                    canvas.drawBitmap(lilyPadBitmap, null, rect, paint);
                } else if (lilyPadDrawable != null) {
                    lilyPadDrawable.setBounds(rect);
                    lilyPadDrawable.draw(canvas);
                } else {
                    paint.setColor(Color.parseColor("#7CB342"));
                    canvas. drawRect(rect, paint);
                }

                // Si es la casilla del pan, dibujarlo encima
                if (row == gameBoard.getBreadRow() && col == gameBoard.getBreadCol()) {
                    drawBread(canvas, left, top);
                }
                break;

            case SHORE:
                if (shoreBitmap != null) {
                    canvas. drawBitmap(shoreBitmap, null, rect, paint);
                } else if (shoreDrawable != null) {
                    shoreDrawable.setBounds(rect);
                    shoreDrawable.draw(canvas);
                } else {
                    paint.setColor(Color.parseColor("#8D6E63"));
                    canvas.drawRect(rect, paint);
                }
                break;

            case BREAD:
                break;
        }
    }

    private void drawBread(Canvas canvas, float x, float y) {
        if (breadBitmap != null) {
            // Calcular tamaño del pan (95% del tile para que sea más grande)
            float breadSize = tileSize * 0.95f;
            float breadX = x + (tileSize - breadSize) / 2;
            float breadY = y + (tileSize - breadSize) / 2;

            Rect destRect = new Rect(
                    (int) breadX,
                    (int) breadY,
                    (int) (breadX + breadSize),
                    (int) (breadY + breadSize)
            );

            canvas.drawBitmap(breadBitmap, null, destRect, paint);
        } else {
            // Fallback: dibujar cuadrado amarillo
            paint.setColor(Color.parseColor("#FFD54F"));
            float breadSize = tileSize * 0.6f;
            float breadX = x + (tileSize - breadSize) / 2;
            float breadY = y + (tileSize - breadSize) / 2;
            canvas.drawRect(breadX, breadY, breadX + breadSize, breadY + breadSize, paint);
        }
    }

    private void drawDuck(Canvas canvas) {
        Duck duck = gameBoard.getDuck();
        float x = offsetX + duck.getCol() * tileSize;
        float y = offsetY + duck.getRow() * tileSize;

        // Calcular tamaño del patito (95% del tile para que sea más grande)
        float duckSize = tileSize * 0.95f;
        float duckX = x + (tileSize - duckSize) / 2;
        float duckY = y + (tileSize - duckSize) / 2;

        Rect destRect = new Rect(
                (int) duckX,
                (int) duckY,
                (int) (duckX + duckSize),
                (int) (duckY + duckSize)
        );

        // Usar imagen de patito muerto si está muerto
        Bitmap duckImage = gameBoard.isDead() ? duckDeadBitmap : duckBitmap;

        if (duckImage != null) {
            canvas.drawBitmap(duckImage, null, destRect, paint);
        } else {
            // Fallback: dibujar círculo (código anterior)
            float centerX = x + tileSize / 2;
            float centerY = y + tileSize / 2;
            float radius = tileSize / 3.5f;

            if (gameBoard.isDead()) {
                paint.setColor(Color.RED);
            } else {
                paint.setColor(Color.WHITE);
            }

            canvas.drawCircle(centerX, centerY, radius, paint);

            strokePaint.setColor(Color. LTGRAY);
            strokePaint.setStrokeWidth(2);
            canvas.drawCircle(centerX, centerY, radius, strokePaint);

            paint.setColor(Color.parseColor("#FF9800"));
            canvas.drawCircle(centerX + radius / 2, centerY, radius / 4, paint);

            paint. setColor(Color.BLACK);
            if (gameBoard.isDead()) {
                float eyeX = centerX - radius / 4;
                float eyeY = centerY - radius / 4;
                float eyeSize = radius / 6;
                paint. setStrokeWidth(3);
                canvas.drawLine(eyeX - eyeSize, eyeY - eyeSize, eyeX + eyeSize, eyeY + eyeSize, paint);
                canvas.drawLine(eyeX + eyeSize, eyeY - eyeSize, eyeX - eyeSize, eyeY + eyeSize, paint);
            } else {
                canvas.drawCircle(centerX - radius / 4, centerY - radius / 4, radius / 8, paint);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = event.getX();
                startY = event.getY();
                return true;

            case MotionEvent. ACTION_UP:
                float endX = event.getX();
                float endY = event.getY();
                handleSwipe(startX, startY, endX, endY);
                return true;
        }
        return super.onTouchEvent(event);
    }

    private void handleSwipe(float startX, float startY, float endX, float endY) {
        if (gameBoard.isDead()) {
            return;
        }

        float deltaX = endX - startX;
        float deltaY = endY - startY;

        if (Math.abs(deltaX) < MIN_SWIPE_DISTANCE && Math.abs(deltaY) < MIN_SWIPE_DISTANCE) {
            return;
        }

        int moveRow = 0;
        int moveCol = 0;

        if (Math.abs(deltaX) > Math.abs(deltaY)) {
            if (deltaX > 0) {
                moveCol = 1;
            } else {
                moveCol = -1;
            }
        } else {
            if (deltaY > 0) {
                moveRow = 1;
            } else {
                moveRow = -1;
            }
        }

        boolean moved = gameBoard.moveDuck(moveRow, moveCol);

        if (moved) {
            invalidate();

            if (gameBoard.isDead()) {
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), "¡Has muerto!  💀", Toast.LENGTH_LONG).show();
                    }
                }, 100);
            }

            if (gameBoard.isLevelComplete()) {
                handleLevelComplete();
            }
        }
    }

    private void handleLevelComplete() {
        int stars = gameBoard.calculateStars();

        String message;
        if (stars == 3) {
            message = "¡Perfecto! ⭐⭐⭐";
        } else if (stars == 2) {
            message = "¡Muy bien! ⭐⭐";
        } else {
            message = "¡Completado! ⭐";
        }

        // Guardar progreso
        int currentLevel = levelManager.getCurrentLevelNumber();
        progressManager.completeLevel(currentLevel, gameBoard.isPerfectCompletion());

        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();

        postDelayed(new Runnable() {
            @Override
            public void run() {
                if (levelManager.hasNextLevel()) {
                    goToNextLevel();
                } else {
                    // Completó todos los niveles
                    showGameCompleted();
                }
            }
        }, 1500);
    }
    private void showGameCompleted() {
        int totalStars = progressManager.getTotalStars();
        Toast.makeText(getContext(),
                "¡Juego completado!  🎉\nEstrellas totales: " + totalStars + "/15",
                Toast.LENGTH_LONG).show();
    }
}