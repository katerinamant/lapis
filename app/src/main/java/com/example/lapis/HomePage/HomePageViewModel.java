package com.example.lapis.HomePage;

import androidx.lifecycle.ViewModel;

public class HomePageViewModel extends ViewModel {
    private final HomePagePresenter presenter;

    public HomePageViewModel() {
        this.presenter = new HomePagePresenter();
    }

    public HomePagePresenter getPresenter() {
        return this.presenter;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        // release resources
    }
}
