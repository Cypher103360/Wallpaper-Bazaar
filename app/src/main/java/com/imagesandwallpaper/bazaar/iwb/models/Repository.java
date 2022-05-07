package com.imagesandwallpaper.bazaar.iwb.models;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.imagesandwallpaper.bazaar.iwb.models.CatItemImage.CatItemImageModelList;
import com.imagesandwallpaper.bazaar.iwb.models.PremiumImages.PremiumModelList;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Repository {

    public static Repository repository;
    ApiInterface apiInterface;
    MutableLiveData<CatModelList> categoryMutableLiveData = new MutableLiveData<>();
    MutableLiveData<CatItemImageModelList> catItemImageModelListMutableLiveData = new MutableLiveData<>();
    MutableLiveData<ImageItemModelList> imageItemModelListMutableLiveData = new MutableLiveData<>();
    MutableLiveData<ImageItemModelList> premiumImageItemModelListMutableLiveData = new MutableLiveData<>();
    MutableLiveData<SubCatModelList> subCategoryMutableLiveData = new MutableLiveData<>();
    MutableLiveData<SubCatImageModelList> subCatImageModelListMutableLiveData = new MutableLiveData<>();
    MutableLiveData<PremiumModelList> premiumModelListMutableLiveData = new MutableLiveData<>();

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

    public MutableLiveData<ImageItemModelList> getImageItemModelListMutableLiveData(Map<String, String> map) {
        ExecutorService service = Executors.newSingleThreadExecutor();
        service.execute(() -> {
            // Background work

            Call<ImageItemModelList> call = apiInterface.getPopularImageItem(map);
            call.enqueue(new Callback<ImageItemModelList>() {
                @Override
                public void onResponse(@NonNull Call<ImageItemModelList> call, @NonNull Response<ImageItemModelList> response) {
                    if (response.isSuccessful()) {
                        imageItemModelListMutableLiveData.setValue(response.body());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ImageItemModelList> call, @NonNull Throwable t) {

                }
            });
        });
        return imageItemModelListMutableLiveData;
    }

    public MutableLiveData<ImageItemModelList> getPremiumImageItemModelListMutableLiveData(Map<String, String> map) {
        Call<ImageItemModelList> call = apiInterface.getPremiumImages(map);
        call.enqueue(new Callback<ImageItemModelList>() {
            @Override
            public void onResponse(@NonNull Call<ImageItemModelList> call, @NonNull Response<ImageItemModelList> response) {
                if (response.isSuccessful()) {
                    premiumImageItemModelListMutableLiveData.setValue(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ImageItemModelList> call, @NonNull Throwable t) {

            }
        });
        return premiumImageItemModelListMutableLiveData;
    }

    public MutableLiveData<SubCatModelList> getSubCategoryMutableLiveData(String catId) {
        ExecutorService service = Executors.newSingleThreadExecutor();
        service.execute(() -> {
            // Background work

            Call<SubCatModelList> call = apiInterface.getSubCategories(catId);
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
        });
        return subCategoryMutableLiveData;
    }

    public MutableLiveData<SubCatImageModelList> getSubCatImageModelListMutableLiveData(String catId) {
        ExecutorService service = Executors.newSingleThreadExecutor();
        service.execute(() -> {
            // Background work

            Call<SubCatImageModelList> call = apiInterface.getSubCategoryItemsImages(catId);
            call.enqueue(new Callback<SubCatImageModelList>() {
                @Override
                public void onResponse(@NonNull Call<SubCatImageModelList> call, @NonNull Response<SubCatImageModelList> response) {
                    if (response.isSuccessful()) {
                        subCatImageModelListMutableLiveData.setValue(response.body());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<SubCatImageModelList> call, @NonNull Throwable t) {

                }
            });
        });
        return subCatImageModelListMutableLiveData;
    }

    public MutableLiveData<CatItemImageModelList> getCatItemImageModelListMutableLiveData(String catId) {

        ExecutorService service = Executors.newSingleThreadExecutor();
        service.execute(() -> {
            // Background work

            Call<CatItemImageModelList> call = apiInterface.getCatItemImages(catId);
            call.enqueue(new Callback<CatItemImageModelList>() {
                @Override
                public void onResponse(@NonNull Call<CatItemImageModelList> call, @NonNull Response<CatItemImageModelList> response) {
                    if (response.isSuccessful()) {
                        catItemImageModelListMutableLiveData.setValue(response.body());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<CatItemImageModelList> call, @NonNull Throwable t) {

                }
            });
        });
        return catItemImageModelListMutableLiveData;
    }

    public MutableLiveData<PremiumModelList> getPremiumModelListMutableLiveData(Map<String, String> map) {

        ExecutorService service = Executors.newSingleThreadExecutor();
        service.execute(() -> {
            // Background work

            Call<PremiumModelList> call = apiInterface.getAllPremium(map);
            call.enqueue(new Callback<PremiumModelList>() {
                @Override
                public void onResponse(@NonNull Call<PremiumModelList> call, @NonNull Response<PremiumModelList> response) {
                    if (response.isSuccessful()) {
                        premiumModelListMutableLiveData.setValue(response.body());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<PremiumModelList> call, @NonNull Throwable t) {

                }
            });
        });

        return premiumModelListMutableLiveData;
    }

}
