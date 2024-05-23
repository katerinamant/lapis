package com.example.lapis.SearchPage;

import androidx.lifecycle.ViewModel;

public class SearchPageViewModel extends ViewModel {
    private final SearchPagePresenter presenter;

    public SearchPageViewModel() {
        this.presenter = new SearchPagePresenter();
    }

    public SearchPagePresenter getPresenter() {
        return this.presenter;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        // release resources
    }
}
