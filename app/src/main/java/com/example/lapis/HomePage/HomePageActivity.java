package com.example.lapis.HomePage;

import android.content.Intent;
import android.os.Bundle;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lapis.R;
import com.example.lapis.SearchPage.SearchPageActivity;
import com.example.lapis.UserPage.UserPageActivity;

public class HomePageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        // User button
        ImageView headerLogo = findViewById(R.id.header_user);
        headerLogo.setOnClickListener(view -> {
            // Go to UserPage
            Intent intent = new Intent(HomePageActivity.this, UserPageActivity.class);
            startActivity(intent);
        });

        EditText searchbar = findViewById(R.id.homepage_edit_text_destination);
        searchbar.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_SEARCH) {
                Intent intent = new Intent(HomePageActivity.this, SearchPageActivity.class);

                // Add destination input to intent
                String destination = searchbar.getText().toString().trim();
                intent.putExtra("destination", destination);
                startActivity(intent);
                return true;
            }
            return false;
        });
    }
}
