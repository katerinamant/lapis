package com.example.lapis.LoginPage;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.lapis.HomePage.HomePageActivity;
import com.example.lapis.R;
import com.example.lapis.Utils.Utils;

public class LoginActivity extends AppCompatActivity implements LoginView {
    private EditText emailField, passwordField;
    private Button enterButton;
    private boolean enterButtonIsEnabled;
    private String email, password;

    private final Handler handler = new Handler(Looper.getMainLooper(), message -> {
        String status = message.getData().getString(Utils.BODY_FIELD_STATUS);
        assert status != null;
        if (status.equals("OK")) {
            // Correct credentials
            String guestEmail = message.getData().getString(Utils.BODY_FIELD_GUEST_EMAIL);
            this.successfulLogIn(guestEmail);
        } else {
            // Incorrect credentials, showing error
            this.showError("Login unsuccessful.", "Wrong credentials. Try again.");
        }
        return false;
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final LoginPresenter presenter = new LoginPresenter(this, handler);

        emailField = findViewById(R.id.login_edit_text_email);
        passwordField = findViewById(R.id.login_edit_text_password);
        enterButton = findViewById(R.id.login_btn_enter);

        // Enter button is disabled
        enterButtonIsEnabled = false;
        enterButton.setAlpha(.5f); // set opacity to seem disabled
        emailField.addTextChangedListener(loginWatcher);
        passwordField.addTextChangedListener(loginWatcher);

        enterButton.setOnClickListener(view -> presenter.onEnter(enterButtonIsEnabled, email, password));
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
    public void successfulLogIn(String guestEmail) {
        Intent intent = new Intent(LoginActivity.this, HomePageActivity.class);
        intent.putExtra(Utils.BODY_FIELD_GUEST_EMAIL, guestEmail);
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
