package com.example.lapis.LoginPage;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.lapis.LandingPage.LandingPageActivity;
import com.example.lapis.R;

public class LoginActivity extends AppCompatActivity implements LoginView {
    private EditText emailField, passwordField;
    private Button enterButton;
    private boolean enterButtonIsEnabled;
    private String email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final LoginPresenter presenter = new LoginPresenter(this);

        emailField = findViewById(R.id.edit_text_email);
        passwordField = findViewById(R.id.edit_text_password);
        enterButton = findViewById(R.id.btn_enter);

        // Enter button is disabled
        enterButtonIsEnabled = false;
        enterButton.setAlpha(.5f); // set opacity to seem disabled
        emailField.addTextChangedListener(loginWatcher);
        passwordField.addTextChangedListener(loginWatcher);

        enterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.onEnter(enterButtonIsEnabled, email, password);
            }
        });
    }

    TextWatcher loginWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            email = emailField.getText().toString();
            password = passwordField.getText().toString();
            if (!email.isEmpty() && !password.isEmpty()) {
                enterButton.setAlpha(1.0f);
                enterButtonIsEnabled = true;
            } else {
                enterButton.setAlpha(.5f);
                enterButtonIsEnabled = false;
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    // LoginView implementations
    @Override
    public void successfulLogIn() {
        Intent intent = new Intent(LoginActivity.this, LandingPageActivity.class); // placeholder
        startActivity(intent);
    }

    @Override
    public void showError(String title, String msg) {
        new AlertDialog.Builder(this).setCancelable(true).setTitle(title).setMessage(msg).setPositiveButton(R.string.ok, null).create().show();
    }

    @Override
    public void showToast(String msg) {
        Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
    }
}
