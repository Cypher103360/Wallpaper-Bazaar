package com.imagesandwallpaper.bazaar.iwb.models;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class SubCatViewModel extends AndroidViewModel {
    private final Repository repository;
    String catId;

    public SubCatViewModel(@NonNull Application application) {
        super(application);
        repository = Repository.getInstance();
    }



    public SubCatViewModel(Application application, String catId) {
        super(application);
        repository = Repository.getInstance();
        this.catId = catId;
    }

    public LiveData<SubCatModelList> getSubCategories() {
        return repository.getSubCategoryMutableLiveData(catId);
    }
}
