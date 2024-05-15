package com.example.lapis.SignUpPage;

public class SignUpPresenter {
    private final SignUpView view;

    public SignUpPresenter(SignUpView view) {
        this.view = view;
    }

    void onSignUp(boolean signUpButtonIsEnabled, String email, String password, String firstName, String lastName, String phoneNumber) {
        if (!signUpButtonIsEnabled) {
            // Fields not filled, showing toast
            view.showToast("Please fill all the fields.");
            return;
        }

        if (password.length() < 8) {
            view.showError("Invalid password.", "Please use at least 8 digits.");
            return;
        }

        if (phoneNumber.length() != 10) {
            view.showError("Invalid phone number.", "Please limit phone number to 10 digits.");
            return;
        }

        // TODO: Send new user to server
        view.successfulSignUp();
    }
}
