package com.example.lapis.SignUpPage;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.lapis.HomePage.HomePageActivity;
import com.example.lapis.R;

public class SignUpActivity extends AppCompatActivity implements SignUpView {
    private EditText emailField, passwordField, firstNameField, lastNameField, phoneNumberField;
    private Button signUpButton;
    private boolean signUpButtonIsEnabled;
    private String email, password, firstName, lastName, phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        final SignUpPresenter presenter = new SignUpPresenter(this);

        emailField = findViewById(R.id.signup_edit_text_email);
        passwordField = findViewById(R.id.signup_edit_text_password);
        firstNameField = findViewById(R.id.signup_edit_text_first_name);
        lastNameField = findViewById(R.id.signup_edit_text_last_name);
        phoneNumberField = findViewById(R.id.signup_edit_text_phone_number);
        signUpButton = findViewById(R.id.sign_up_btn);

        // SignUp button is disabled
        signUpButtonIsEnabled = false;
        signUpButton.setAlpha(.5f); // set opacity to seem disabled
        emailField.addTextChangedListener(signUpWatcher);
        passwordField.addTextChangedListener(signUpWatcher);
        firstNameField.addTextChangedListener(signUpWatcher);
        lastNameField.addTextChangedListener(signUpWatcher);
        phoneNumberField.addTextChangedListener(signUpWatcher);

        signUpButton.setOnClickListener(view -> presenter.onSignUp(signUpButtonIsEnabled, email, password, firstName, lastName, phoneNumber));
    }

    TextWatcher signUpWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            email = emailField.getText().toString().trim();
            password = passwordField.getText().toString();
            firstName = firstNameField.getText().toString().trim();
            lastName = lastNameField.getText().toString().trim();
            phoneNumber = phoneNumberField.getText().toString();
            if (!email.isEmpty() && !password.isEmpty() && !firstName.isEmpty() && !lastName.isEmpty() && !phoneNumber.isEmpty()) {
                signUpButton.setAlpha(1.0f);
                signUpButtonIsEnabled = true;
            } else {
                signUpButton.setAlpha(.5f);
                signUpButtonIsEnabled = false;
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    // SignUpView implementations
    @Override
    public void successfulSignUp() {
        Intent intent = new Intent(SignUpActivity.this, HomePageActivity.class);
        startActivity(intent);
    }

    @Override
    public void showError(String title, String msg) {
        new AlertDialog.Builder(this).setCancelable(true).setTitle(title).setMessage(msg).setPositiveButton(R.string.ok, null).create().show();
    }

    @Override
    public void showToast(String msg) {
        Toast.makeText(SignUpActivity.this, msg, Toast.LENGTH_SHORT).show();
    }
}
