package com.imagesandwallpaper.bazaar.iwb.models.UserData;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.imagesandwallpaper.bazaar.iwb.models.Repository;

public class UserDataViewModel extends AndroidViewModel {
    Repository repository;
    String email;

    public UserDataViewModel(@NonNull Application application,String email) {
        super(application);
        this.email = email;
        repository = Repository.getInstance();
    }

    public LiveData<UserDataModel> getAllUserData(){
        return repository.getUserDataModelListMutableLiveData(email);
    }
}
