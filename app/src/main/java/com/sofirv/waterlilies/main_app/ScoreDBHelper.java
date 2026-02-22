package com.sofirv.waterlilies.main_app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class ScoreDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "scores.db";
    private static final int DATABASE_VERSION = 4;

    public static final String TABLE_SCORES = "scores";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_PLAYER = "player";
    public static final String COLUMN_SCORE = "score";
    public static final String COLUMN_GAME = "game";
    public static final String COLUMN_DATE = "date";

    public ScoreDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_SCORES + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_PLAYER + " TEXT, "
                + COLUMN_SCORE + " INTEGER, "
                + COLUMN_GAME + " TEXT, "
                + COLUMN_DATE + " DATETIME DEFAULT CURRENT_TIMESTAMP"
                + ")";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCORES);
        onCreate(db);
    }

    // CREATE
    public long addScore(String player, int score, String game) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PLAYER, player);
        values.put(COLUMN_SCORE, score);
        values.put(COLUMN_GAME, game);
        return db.insert(TABLE_SCORES, null, values);
    }

    // READ - todos los scores
    public List<Score> getAllScores() {
        List<Score> scores = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_SCORES, null, null, null, null, null, COLUMN_SCORE + " DESC");
        if (cursor.moveToFirst()) {
            do {
                Score score = new Score(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PLAYER)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SCORE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE))
                );
                scores.add(score);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return scores;
    }

    // READ - filtrar por juego
    public List<Score> getScoresByGame(String game) {
        List<Score> scores = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_SCORES, null, COLUMN_GAME + "=?", new String[]{game}, null, null, COLUMN_SCORE + " DESC");
        if (cursor.moveToFirst()) {
            do {
                scores.add(new Score(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PLAYER)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SCORE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE))
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return scores;
    }

    // UPDATE
    public int updateScore(int id, String player, int score, String game) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PLAYER, player);
        values.put(COLUMN_SCORE, score);
        values.put(COLUMN_GAME, game);
        return db.update(TABLE_SCORES, values, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
    }

    // DELETE
    public int deleteScore(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_SCORES, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
    }

    // Leer scores por juego y orden
    public List<Score> getScoresByGameOrdered(String game, boolean bestFirst) {
        List<Score> scores = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String order = bestFirst ? COLUMN_SCORE + " DESC" : COLUMN_SCORE + " ASC";
        Cursor cursor;
        if ("Todos".equals(game) || game == null) {
            cursor = db.query(TABLE_SCORES, null, null, null, null, null, order);
        } else {
            cursor = db.query(TABLE_SCORES, null, COLUMN_GAME + "=?", new String[]{game}, null, null, order);
        }
        if (cursor.moveToFirst()) {
            do {
                scores.add(new Score(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PLAYER)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SCORE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE))));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return scores;
    }

    public List<Score> searchScores(String query) {
        List<Score> results = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String where = COLUMN_PLAYER + " LIKE ? OR " + COLUMN_GAME + " LIKE ?";
        String[] args = { "%" + query + "%", "%" + query + "%" };
        Cursor cursor = db.query(TABLE_SCORES, null, where, args, null, null, COLUMN_SCORE + " DESC");
        if (cursor.moveToFirst()) {
            do {
                results.add(new Score(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PLAYER)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SCORE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE))
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return results;
    }
}