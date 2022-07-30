package com.imagesandwallpaper.bazaar.iwb.models.UserData;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.imagesandwallpaper.bazaar.iwb.models.CatItemImage.CatItemImageViewModel;

public class UserDataModelFactory implements ViewModelProvider.Factory{
    Application application;
    String email;

    public UserDataModelFactory(Application application, String email){
        this.application = application;
        this.email = email;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> aClass) {
        return (T) new UserDataViewModel(application, email);
    }

}
