package com.imagesandwallpaper.bazaar.iwb.models;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class CatViewModel extends AndroidViewModel {
    Repository repository;

    public CatViewModel(@NonNull Application application) {
        super(application);
        repository = Repository.getInstance();
    }

    public LiveData<CatModelList> getCategories(){
        return repository.getCategoryMutableLiveData();
    }

}
