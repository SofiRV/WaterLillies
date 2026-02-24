package com.sofirv.waterlilies.main_app;

/**
 * Represents a user with an id, username, and password.
 */
public class User {

    /**
     * Unique identifier for the user.
     */
    public int id;

    /**
     * Username for login.
     */
    public String username;

    /**
     * User's password.
     */
    public String password;

    /**
     * Constructs a User object.
     * @param id The user's unique identifier.
     * @param username The user's username.
     * @param password The user's password.
     */
    public User(int id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }
}