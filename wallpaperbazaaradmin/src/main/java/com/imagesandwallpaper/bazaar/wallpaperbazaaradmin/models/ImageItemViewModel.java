package com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.Map;

public class ImageItemViewModel extends AndroidViewModel {
    private final Repository repository;
    Map<String,String> map;

    public ImageItemViewModel(@NonNull Application application) {
        super(application);
        repository = Repository.getInstance();
    }

    public ImageItemViewModel(@NonNull Application application, Map<String, String> map) {
        super(application);
        this.map = map;
        repository = Repository.getInstance();
    }

    public LiveData<ImageItemModelList> getImageItems(){
        return repository.getImageItemModelListMutableLiveData(map);
    }
    public LiveData<ImageItemModelList> getPremiumImageItems(){
        return repository.getPremiumImageItemModelListMutableLiveData(map);
    }
}
