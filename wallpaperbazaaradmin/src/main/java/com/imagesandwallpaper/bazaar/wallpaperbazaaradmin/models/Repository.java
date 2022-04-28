package com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import retrofit2.Call;

public class Repository {
    private static ApiInterface apiInterface;
    private static Repository repository;
    private final MutableLiveData<PopularModelList> popularModelListMutableLiveData = new MutableLiveData<>();

    public Repository(){
        apiInterface = ApiWebServices.getApiInterface();
    }
    public static Repository getInstance(){
        if (repository == null){
            repository = new Repository();
        }
        return repository;
    }

//    public LiveData<PopularModelList> getPopularLiveData(){
//        Call<PopularModelList> call = apiInterface.uploadPopularImages()
//    }
}
