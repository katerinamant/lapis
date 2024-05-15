package com.example.lapis.SignUpPage;

public interface SignUpView {
    /**
     * User successfully signed up,
     * and is redirected to HomePageActivity
     */
    void successfulSignUp();

    void showError(String title, String msg);

    void showToast(String msg);
}
