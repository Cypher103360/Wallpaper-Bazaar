package com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.Map;

public class CatItemsViewModel extends AndroidViewModel {
    private final Repository repository;
    String id;


    public CatItemsViewModel(@NonNull Application application, String id) {
        super(application);
        this.id = id;
        repository = Repository.getInstance();
    }

    public LiveData<CatItemModelList> getCatItems() {
        return repository.getCatItemsMutableLiveData(id);
    }public LiveData<CatItemModelList> getSubCatItems() {
        return repository.getSubCatItemsMutableLiveData(id);
    }
}
