package com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models.UserData;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models.Repository;

public class UserDataViewModel extends AndroidViewModel {
    Repository repository;

    public UserDataViewModel(@NonNull Application application) {
        super(application);
        repository = Repository.getInstance();
    }

    public LiveData<UserDataModelList> getAllUserData(){
        return repository.getUserDataModelListMutableLiveData();
    }
}
