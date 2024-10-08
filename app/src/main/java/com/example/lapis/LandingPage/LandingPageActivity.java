package com.example.lapis.LandingPage;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lapis.LoginPage.LoginActivity;
import com.example.lapis.R;
import com.example.lapis.SignUpPage.SignUpActivity;

public class LandingPageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_page);

        Button getStartedButton = findViewById(R.id.btn_get_started);
        getStartedButton.setOnClickListener(v -> {
            Intent intent = new Intent(LandingPageActivity.this, SignUpActivity.class);
            startActivity(intent);
        });

        Button loginButton = findViewById(R.id.btn_login);
        loginButton.setOnClickListener(v -> {
            Intent intent = new Intent(LandingPageActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }
}
