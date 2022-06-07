package com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models.featured;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models.Repository;

public class FeaturedViewModel extends AndroidViewModel {
    Repository repository;

    public FeaturedViewModel(@NonNull Application application) {
        super(application);
        repository = Repository.getInstance();
    }

    public LiveData<FeaturedModelList> getFeatured(){
        return repository.getFeaturedModelMutableLiveData();
    }

}
