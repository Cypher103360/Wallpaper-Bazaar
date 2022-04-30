package com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Repository {
    private static ApiInterface apiInterface;
    private static Repository repository;
    private final MutableLiveData<PopularModelList> popularModelListMutableLiveData = new MutableLiveData<>();
    MutableLiveData<CatModelList> categoryMutableLiveData = new MutableLiveData<>();
    MutableLiveData<SubCatModelList> subCategoryMutableLiveData = new MutableLiveData<>();
    MutableLiveData<CatItemModelList> catItemModelListMutableLiveData = new MutableLiveData<>();

    public Repository() {
        apiInterface = ApiWebServices.getApiInterface();
    }

    public static Repository getInstance() {
        if (repository == null) {
            repository = new Repository();
        }
        return repository;
    }

    public MutableLiveData<CatModelList> getCategoryMutableLiveData() {
        Call<CatModelList> call = apiInterface.getAllCategory();
        call.enqueue(new Callback<CatModelList>() {
            @Override
            public void onResponse(@NonNull Call<CatModelList> call, @NonNull Response<CatModelList> response) {
                if (response.isSuccessful()) {
                    categoryMutableLiveData.setValue(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<CatModelList> call, @NonNull Throwable t) {

            }
        });
        return categoryMutableLiveData;
    }

    public MutableLiveData<SubCatModelList> getSubCategoryMutableLiveData(String catId) {
        Call<SubCatModelList> call = apiInterface.getAllSubCategory(catId);
        call.enqueue(new Callback<SubCatModelList>() {
            @Override
            public void onResponse(@NonNull Call<SubCatModelList> call, @NonNull Response<SubCatModelList> response) {
                if (response.isSuccessful()) {
                    subCategoryMutableLiveData.setValue(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<SubCatModelList> call, @NonNull Throwable t) {

            }
        });
        return subCategoryMutableLiveData;
    }

    public MutableLiveData<CatItemModelList> getCatItemsMutableLiveData(String catId) {
        Call<CatItemModelList> call = apiInterface.fetchCatItems(catId);
        call.enqueue(new Callback<CatItemModelList>() {
            @Override
            public void onResponse(@NonNull Call<CatItemModelList> call, @NonNull Response<CatItemModelList> response) {
                if (response.isSuccessful()) {
                    catItemModelListMutableLiveData.setValue(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<CatItemModelList> call, @NonNull Throwable t) {

            }
        });
        return catItemModelListMutableLiveData;
    }

    public MutableLiveData<CatItemModelList> getSubCatItemsMutableLiveData(String catId) {
        Call<CatItemModelList> call = apiInterface.fetchSubCatItems(catId);
        call.enqueue(new Callback<CatItemModelList>() {
            @Override
            public void onResponse(@NonNull Call<CatItemModelList> call, @NonNull Response<CatItemModelList> response) {
                if (response.isSuccessful()) {
                    catItemModelListMutableLiveData.setValue(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<CatItemModelList> call, @NonNull Throwable t) {

            }
        });
        return catItemModelListMutableLiveData;
    }
}
