package com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class SubCatModelFactory implements ViewModelProvider.Factory {
    Application application;
    String catId;

    public SubCatModelFactory(Application application, String catId) {
        this.application = application;
        this.catId = catId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new SubCatViewModel(application, catId);
    }
}
