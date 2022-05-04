package com.imagesandwallpaper.bazaar.iwb.models.PremiumImages;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.imagesandwallpaper.bazaar.iwb.models.ImageItemViewModel;

import java.util.Map;

public class PremiumModelFactory implements ViewModelProvider.Factory {
    Application application;
    Map<String, String> map;

    public PremiumModelFactory(Application application, Map<String, String> map) {
        this.application = application;
        this.map = map;
    }


    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> aClass) {
        return (T) new PremiumViewModel(application, map);
    }
}
