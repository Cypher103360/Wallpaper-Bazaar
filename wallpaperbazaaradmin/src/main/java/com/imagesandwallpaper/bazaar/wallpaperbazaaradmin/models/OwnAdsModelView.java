package com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class OwnAdsModelView extends AndroidViewModel {
    private final Repository repository;
    String appId;

    public OwnAdsModelView(@NonNull Application application, String appId) {
        super(application);
        repository = Repository.getInstance();
        this.appId = appId;

    }

    public LiveData<List<OwnAdsModel>> getOwnAds() {
        return repository.fetchOwnAds(appId);
    }
}
