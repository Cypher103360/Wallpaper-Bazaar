package com.imagesandwallpaper.bazaar.iwb.models;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.Map;

public class ImageItemModelFactory implements ViewModelProvider.Factory {
    Application application;
    Map<String, String> map;

    public ImageItemModelFactory(Application application, Map<String, String> map) {
        this.application = application;
        this.map = map;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> aClass) {
        return (T) new ImageItemViewModel(application, map);
    }
}
