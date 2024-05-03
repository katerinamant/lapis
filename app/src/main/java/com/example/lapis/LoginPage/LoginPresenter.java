package com.example.lapis.LoginPage;

import android.util.Log;

public class LoginPresenter {
    private final LoginView view;

    public LoginPresenter(LoginView view) {
        this.view = view;
    }

    void onEnter(boolean enterButtonIsEnabled, String email, String password) {
        if (!enterButtonIsEnabled) {
            // Fields not filled, showing toast
            view.showToast("Please fill the required fields.");
            return;
        }

        // TODO: Add credentials lookup
        if (email.equals("guest@example.com") && password.equals("guest")) {
            // Correct credentials
            view.successfulLogIn();
        } else {
            // Incorrect credentials, showing error
            view.showError("Login unsuccessful.", "Wrong credentials. Try again.");
        }
    }
}
