package com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.Map;

public class PremiumViewModel extends AndroidViewModel {
    private final Repository repository;
    Map<String,String> map;

    public PremiumViewModel(@NonNull Application application) {
        super(application);
        repository = Repository.getInstance();
    }

    public PremiumViewModel(@NonNull Application application, Map<String, String> map) {
        super(application);
        this.map = map;
        repository = Repository.getInstance();
    }

    public LiveData<ImageItemModelList> getPremiumImages(){
        return repository.getPremiumImageItemModelListMutableLiveData(map);
    }
}
