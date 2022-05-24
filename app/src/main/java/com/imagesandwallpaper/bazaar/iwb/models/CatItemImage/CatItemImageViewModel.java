package com.imagesandwallpaper.bazaar.iwb.models.CatItemImage;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.imagesandwallpaper.bazaar.iwb.models.ImageItemModelList;
import com.imagesandwallpaper.bazaar.iwb.models.Repository;

public class CatItemImageViewModel extends AndroidViewModel {
    private final Repository repository;
    String id;

    public CatItemImageViewModel(@NonNull Application application,String id) {
        super(application);
        this.id = id;
        repository = Repository.getInstance();
    }

    public LiveData<CatItemImageModelList> getCatItemImages(){
        return repository.getCatItemImageModelListMutableLiveData(id);
    }
    public LiveData<ImageItemModelList> getNewImageItems(){
        return repository.getNewImageItemModelListMutableLiveData(id);
    }
}
