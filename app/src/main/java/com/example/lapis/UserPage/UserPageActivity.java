package com.example.lapis.UserPage;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lapis.HomePage.HomePageActivity;
import com.example.lapis.R;

public class UserPageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_page);

        // Home button
        ImageView headerLogo = findViewById(R.id.userpage_header_logo);
        headerLogo.setOnClickListener(view -> {
            // Go to HomePage
            Intent intent = new Intent(UserPageActivity.this, HomePageActivity.class);
            startActivity(intent);
        });
    }
}
