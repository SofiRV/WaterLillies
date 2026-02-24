package com.sofirv.waterlilies.main_app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Database helper class for managing scores and users.
 */
public class ScoreDBHelper extends SQLiteOpenHelper {

    // Database constants
    private static final String DATABASE_NAME = "scores.db";
    private static final int DATABASE_VERSION = 5;

    // Scores table and columns
    public static final String TABLE_SCORES = "scores";
    public static final String COLUMN_SCORE_ID = "_id";
    public static final String COLUMN_SCORE_PLAYER = "player";
    public static final String COLUMN_SCORE_VALUE = "score";
    public static final String COLUMN_SCORE_GAME = "game";
    public static final String COLUMN_SCORE_DATE = "date";

    // Users table and columns
    public static final String TABLE_USERS = "users";
    public static final String COLUMN_USER_ID = "_id";
    public static final String COLUMN_USER_USERNAME = "username";
    public static final String COLUMN_USER_PASSWORD = "password";

    /**
     * Constructor for ScoreDBHelper.
     * @param context Application context.
     */
    public ScoreDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Called when the database is created for the first time.
     * @param db The database.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the scores table
        String createScoresTable = "CREATE TABLE " + TABLE_SCORES + "("
                + COLUMN_SCORE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_SCORE_PLAYER + " TEXT, "
                + COLUMN_SCORE_VALUE + " INTEGER, "
                + COLUMN_SCORE_GAME + " TEXT, "
                + COLUMN_SCORE_DATE + " DATETIME DEFAULT CURRENT_TIMESTAMP"
                + ")";
        db.execSQL(createScoresTable);

        // Create the users table
        String createUsersTable = "CREATE TABLE " + TABLE_USERS + "("
                + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_USER_USERNAME + " TEXT UNIQUE, "
                + COLUMN_USER_PASSWORD + " TEXT)";
        db.execSQL(createUsersTable);
    }

    /**
     * Called when the database needs to be upgraded.
     * @param db The database.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop old tables if they exist and recreate the database
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCORES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    /**
     * Inserts a new score into the scores table.
     * @param player The player's name.
     * @param score The score value.
     * @param game The game name.
     * @return The row ID of the newly inserted row, or -1 if an error occurred.
     */
    public long insertScore(String player, int score, String game) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_SCORE_PLAYER, player);
        values.put(COLUMN_SCORE_VALUE, score);
        values.put(COLUMN_SCORE_GAME, game);
        return db.insert(TABLE_SCORES, null, values);
    }

    /**
     * Retrieves a list with all scores, ordered by score in descending order.
     * @return List of all scores.
     */
    public List<Score> getAllScores() {
        List<Score> scores = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_SCORES, null, null, null, null, null, COLUMN_SCORE_VALUE + " DESC");
        if (cursor.moveToFirst()) {
            do {
                Score score = new Score(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SCORE_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SCORE_PLAYER)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SCORE_VALUE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SCORE_GAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SCORE_DATE))
                );
                scores.add(score);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return scores;
    }

    /**
     * Retrieves a list of scores for a specific game, ordered by score in descending order.
     * @param game The game name.
     * @return List of scores for that game.
     */
    public List<Score> getScoresByGame(String game) {
        List<Score> scores = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_SCORES, null, COLUMN_SCORE_GAME + "=?", new String[]{game}, null, null, COLUMN_SCORE_VALUE + " DESC");
        if (cursor.moveToFirst()) {
            do {
                scores.add(new Score(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SCORE_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SCORE_PLAYER)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SCORE_VALUE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SCORE_GAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SCORE_DATE))
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return scores;
    }

    /**
     * Updates an existing score entry.
     * @param id The score's row id.
     * @param player The updated player name.
     * @param score The updated score value.
     * @param game The updated game name.
     * @return The number of rows affected.
     */
    public int updateScore(int id, String player, int score, String game) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_SCORE_PLAYER, player);
        values.put(COLUMN_SCORE_VALUE, score);
        values.put(COLUMN_SCORE_GAME, game);
        return db.update(TABLE_SCORES, values, COLUMN_SCORE_ID + "=?", new String[]{String.valueOf(id)});
    }

    /**
     * Deletes a score from the scores table.
     * @param id The score's row id.
     * @return The number of rows deleted.
     */
    public int deleteScore(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_SCORES, COLUMN_SCORE_ID + "=?", new String[]{String.valueOf(id)});
    }

    /**
     * Retrieves a list of scores for a given game, ordered either by best or worst score first.
     * @param game The game name ("All" for all games).
     * @param bestFirst True for descending order, false for ascending.
     * @return List of scores.
     */
    public List<Score> getScoresByGameOrdered(String game, boolean bestFirst) {
        List<Score> scores = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String order = bestFirst ? COLUMN_SCORE_VALUE + " DESC" : COLUMN_SCORE_VALUE + " ASC";
        Cursor cursor;
        if ("All".equals(game) || game == null) {
            cursor = db.query(TABLE_SCORES, null, null, null, null, null, order);
        } else {
            cursor = db.query(TABLE_SCORES, null, COLUMN_SCORE_GAME + "=?", new String[]{game}, null, null, order);
        }
        if (cursor.moveToFirst()) {
            do {
                scores.add(new Score(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SCORE_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SCORE_PLAYER)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SCORE_VALUE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SCORE_GAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SCORE_DATE))
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return scores;
    }

    /**
     * Searches scores by player name or game name, ordered by score descending.
     * @param query The search query (partial match).
     * @return List of scores matching the query.
     */
    public List<Score> searchScores(String query) {
        List<Score> results = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String where = COLUMN_SCORE_PLAYER + " LIKE ? OR " + COLUMN_SCORE_GAME + " LIKE ?";
        String[] args = { "%" + query + "%", "%" + query + "%" };
        Cursor cursor = db.query(TABLE_SCORES, null, where, args, null, null, COLUMN_SCORE_VALUE + " DESC");
        if (cursor.moveToFirst()) {
            do {
                results.add(new Score(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SCORE_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SCORE_PLAYER)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SCORE_VALUE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SCORE_GAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SCORE_DATE))
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return results;
    }

    /**
     * Inserts a new user into the users table.
     * @param username The new username.
     * @param password The password.
     * @return The row ID of the newly inserted row, or -1 if an error occurred.
     */
    public long registerUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_USERNAME, username);
        values.put(COLUMN_USER_PASSWORD, password);
        return db.insert(TABLE_USERS, null, values);
    }

    /**
     * Authenticates a user using username and password.
     * @param username The user's username.
     * @param password The user's password.
     * @return True if authenticated, false otherwise.
     */
    public boolean authenticateUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, null, COLUMN_USER_USERNAME + "=? AND " + COLUMN_USER_PASSWORD + "=?",
                new String[]{username, password}, null, null, null);
        boolean authenticated = cursor.moveToFirst();
        cursor.close();
        return authenticated;
    }
}