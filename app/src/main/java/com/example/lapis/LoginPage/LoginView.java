package com.example.lapis.LoginPage;

public interface LoginView {
    void successfulLogIn();

    void showError(String title, String msg);

    void showToast(String msg);
}
