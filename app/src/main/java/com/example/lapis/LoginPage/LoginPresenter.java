package com.example.lapis.LoginPage;

import android.os.Handler;

public class LoginPresenter {
    private final LoginView view;
    private final Handler handler;

    public LoginPresenter(LoginView view, Handler handler) {
        this.view = view;
        this.handler = handler;
    }

    void onEnter(boolean enterButtonIsEnabled, String email, String password) {
        if (!enterButtonIsEnabled) {
            // Fields not filled, showing toast
            view.showToast("Please fill the required fields.");
            return;
        }

        LoginThread loginThread = new LoginThread(this.handler, email, password);
        new Thread(loginThread).start();
    }
}
