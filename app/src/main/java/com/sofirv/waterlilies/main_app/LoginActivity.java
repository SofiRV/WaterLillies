package com.sofirv.waterlilies.main_app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.sofirv.waterlilies.R;

/**
 * Activity for handling user login and registration.
 */
public class LoginActivity extends AppCompatActivity {
    // Fields for username and password input.
    private EditText usernameField, passwordField;
    // Buttons for login and registration actions.
    private Button loginButton, signUpButton;
    // Database helper for user and score data.
    private ScoreDBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the layout for the login activity.
        setContentView(R.layout.activity_login);

        // Initialize the database helper.
        dbHelper = new ScoreDBHelper(this);

        // Bind UI elements.
        usernameField = findViewById(R.id.editTextUsername);
        passwordField = findViewById(R.id.editTextPassword);
        loginButton = findViewById(R.id.buttonLogin);
        signUpButton = findViewById(R.id.buttonSignup);

        // Handle Login button click.
        loginButton.setOnClickListener(v -> {
            String username = usernameField.getText().toString().trim();
            String password = passwordField.getText().toString().trim();
            if (dbHelper.authenticateUser(username, password)) {
                // Successful login
                Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();
                getSharedPreferences("AppPrefs", MODE_PRIVATE)
                        .edit()
                        .putString("loggedUser", username)
                        .apply();
                // Go to HomeActivity; consider saving user session if needed.
                startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                finish();
            } else {
                // Login failed
                Toast.makeText(this, "Incorrect username or password", Toast.LENGTH_SHORT).show();
            }
        });

        // Handle Sign Up button click.
        signUpButton.setOnClickListener(v -> {
            String username = usernameField.getText().toString().trim();
            String password = passwordField.getText().toString().trim();
            if (dbHelper.registerUser(username, password) != -1) {
                // Registration successful
                Toast.makeText(this, "User registered", Toast.LENGTH_SHORT).show();
            } else {
                // Registration failed
                Toast.makeText(this, "Error registering user", Toast.LENGTH_SHORT).show();
            }
        });
    }
}