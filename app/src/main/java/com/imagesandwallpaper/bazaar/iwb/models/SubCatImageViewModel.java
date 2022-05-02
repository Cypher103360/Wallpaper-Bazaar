package com.imagesandwallpaper.bazaar.iwb.models;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

public class SubCatImageViewModel extends AndroidViewModel {
    private final Repository repository;
    String id;

    public SubCatImageViewModel(@NonNull Application application) {
        super(application);
        repository = Repository.getInstance();
    }

    public SubCatImageViewModel(@NonNull Application application, String id) {
        super(application);
        this.id = id;
        repository = Repository.getInstance();
    }

    public MutableLiveData<SubCatImageModelList> getSubCatImageItems(){
        return repository.getSubCatImageModelListMutableLiveData(id);
    }
}
