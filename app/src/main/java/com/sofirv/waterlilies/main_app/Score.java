package com.sofirv.waterlilies.main_app;

/**
 * Represents a score entry for a player in a specific game.
 */
public class Score {
    /**
     * Unique identifier for the score entry.
     */
    public int id;

    /**
     * Name of the player.
     */
    public String player;

    /**
     * Score value.
     */
    public int score;

    /**
     * Name of the game.
     */
    public String game;

    /**
     * Date and time when the score was recorded.
     */
    public String date;

    /**
     * Constructor for creating a Score object.
     * @param id Unique identifier for the score.
     * @param player Name of the player.
     * @param score The score value.
     * @param game The game name.
     * @param date The date/time the score was recorded.
     */
    public Score(int id, String player, int score, String game, String date) {
        this.id = id;
        this.player = player;
        this.score = score;
        this.game = game;
        this.date = date;
    }
}