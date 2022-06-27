package com.imagesandwallpaper.bazaar.iwb.models;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.Map;

public class LiveWallpaperViewModel extends AndroidViewModel {
    private final Repository repository;
    Map<String, String> map;

    public LiveWallpaperViewModel(@NonNull Application application) {
        super(application);
        repository = Repository.getInstance();
    }

    public LiveWallpaperViewModel(@NonNull Application application, Map<String, String> map) {
        super(application);
        this.map = map;
        repository = Repository.getInstance();
    }

    public LiveData<ImageItemModelList> getImageItems() {
        return repository.getLiveWallpaperModelListMutableLiveData(map);
    }

}
