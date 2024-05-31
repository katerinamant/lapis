package com.example.lapis.SignUpPage;

import android.os.Handler;

public class SignUpPresenter {
    private final SignUpView view;
    private final Handler handler;

    public SignUpPresenter(SignUpView view, Handler handler) {
        this.view = view;
        this.handler = handler;
    }

    void onSignUp(boolean signUpButtonIsEnabled, String email, String password, String firstName, String lastName, String phoneNumber) {
        if (!signUpButtonIsEnabled) {
            // Fields not filled, showing toast
            view.showToast("Please fill all the fields.");
            return;
        }

        if (password.length() < 8) {
            view.showError("Invalid password.",
                    "Please use at least 8 characters, " +
                            "at least one number, " +
                            "at least one upper case letter, " +
                            "at least one lower case letter, and " +
                            "at least one special character.");
            return;
        }

        if (phoneNumber.length() != 10) {
            view.showError("Invalid phone number.", "Please limit phone number to 10 digits.");
            return;
        }

        SignUpThread signUpThread = new SignUpThread(this.handler, email, password, firstName, lastName, phoneNumber);
        new Thread(signUpThread).start();
    }
}
