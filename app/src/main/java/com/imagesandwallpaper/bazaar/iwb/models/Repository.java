package com.imagesandwallpaper.bazaar.iwb.models;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Repository {

    ApiInterface apiInterface;
    public  static Repository repository;

    MutableLiveData<CatModelList> categoryMutableLiveData = new MutableLiveData<>();
    MutableLiveData<ImageItemModelList> imageItemModelListMutableLiveData = new MutableLiveData<>();

    public Repository() {
        apiInterface = ApiWebServices.getApiInterface();
    }

    public static Repository getInstance(){
        if (repository == null){
            repository = new Repository();
        }
        return repository;
    }

    public MutableLiveData<CatModelList> getCategoryMutableLiveData(){
        Call<CatModelList> call = apiInterface.getAllCategory();
        call.enqueue(new Callback<CatModelList>() {
            @Override
            public void onResponse(@NonNull Call<CatModelList> call, @NonNull Response<CatModelList> response) {
                if (response.isSuccessful()){
                    categoryMutableLiveData.setValue(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<CatModelList> call, @NonNull Throwable t) {

            }
        });
        return categoryMutableLiveData;
    }

    public MutableLiveData<ImageItemModelList> getImageItemModelListMutableLiveData(){
        Call<ImageItemModelList> call = apiInterface.getAllImageItem();
        call.enqueue(new Callback<ImageItemModelList>() {
            @Override
            public void onResponse(@NonNull Call<ImageItemModelList> call, @NonNull Response<ImageItemModelList> response) {
                if (response.isSuccessful()){
                    imageItemModelListMutableLiveData.setValue(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ImageItemModelList> call, @NonNull Throwable t) {

            }
        });
        return imageItemModelListMutableLiveData;
    }

}
