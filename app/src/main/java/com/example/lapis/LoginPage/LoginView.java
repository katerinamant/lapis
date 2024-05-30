package com.example.lapis.LoginPage;

public interface LoginView {
    /**
     * User successfully logged in,
     * and is redirected to HomePageActivity
     */
    void successfulLogIn();

    void showError(String title, String msg);

    void showToast(String msg);
}
