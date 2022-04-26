package com.imagesandwallpaper.bazaar.iwb.models;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class ImageItemViewModel extends AndroidViewModel {
    Repository repository;
    public ImageItemViewModel(@NonNull Application application) {
        super(application);
        repository = Repository.getInstance();
    }

    public LiveData<ImageItemModelList> getImageItems(){
        return repository.getImageItemModelListMutableLiveData();
    }
}
