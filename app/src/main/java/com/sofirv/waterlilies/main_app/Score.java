package com.sofirv.waterlilies.main_app;

public class Score {
    public int id;
    public String player;
    public int score;
    public String game;
    public String date;

    public Score(int id, String player, int score, String game, String date) {
        this.id = id;
        this.player = player;
        this.score = score;
        this.game = game;
        this.date = date;
    }
}