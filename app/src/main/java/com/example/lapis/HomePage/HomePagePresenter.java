package com.example.lapis.HomePage;

import android.os.Handler;

public class HomePagePresenter {
    private Handler handler;

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public void onPageLoad() {
        HomePageThread homePageThread = new HomePageThread(this.handler);
        new Thread(homePageThread).start();
    }
}
