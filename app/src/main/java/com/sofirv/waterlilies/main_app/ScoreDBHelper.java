package com.sofirv.waterlilies.main_app;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;

public class ScoreDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "scores.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_SCORES = "scores";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_PLAYER = "player";
    public static final String COLUMN_SCORE = "score";
    public static final String COLUMN_DATE = "date";

    public ScoreDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_SCORES_TABLE = "CREATE TABLE " + TABLE_SCORES + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_PLAYER + " TEXT, "
                + COLUMN_SCORE + " INTEGER, "
                + COLUMN_DATE + " DATETIME DEFAULT CURRENT_TIMESTAMP"
                + ")";
        db.execSQL(CREATE_SCORES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCORES);
        onCreate(db);
    }

    // Guardar puntuación
    public void addScore(String player, int score) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PLAYER, player);
        values.put(COLUMN_SCORE, score);

        db.insert(TABLE_SCORES, null, values);
        db.close();
    }

    // Obtener top N puntuaciones
    public Cursor getTopScores(int limit) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_SCORES,
                null,
                null,
                null,
                null,
                null,
                COLUMN_SCORE + " DESC",
                String.valueOf(limit));
    }
}