package com.example.lapis.HomePage;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lapis.R;
import com.example.lapis.UserPage.UserPageActivity;

public class HomePageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        // User button
        ImageView headerLogo = findViewById(R.id.header_user);
        headerLogo.setOnClickListener(view -> goToUserPage());
    }

    private void goToUserPage() {
        Intent intent = new Intent(HomePageActivity.this, UserPageActivity.class);
        startActivity(intent);
    }
}
