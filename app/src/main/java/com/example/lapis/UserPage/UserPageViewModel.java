package com.example.lapis.UserPage;

import androidx.lifecycle.ViewModel;

public class UserPageViewModel extends ViewModel {
    private final UserPagePresenter presenter;

    public UserPageViewModel() {
        this.presenter = new UserPagePresenter();
    }

    public UserPagePresenter getPresenter() {
        return this.presenter;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        // release resources
    }
}
