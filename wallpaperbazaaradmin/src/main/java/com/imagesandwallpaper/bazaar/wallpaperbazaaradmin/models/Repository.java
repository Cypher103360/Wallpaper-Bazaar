package com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models.UserData.UserDataModelList;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models.featured.FeaturedModelList;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models.news_and_reviews.DetailsModelList;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
    MutableLiveData<ImageItemModelList> imageItemModelListMutableLiveData = new MutableLiveData<>();
    MutableLiveData<ImageItemModelList> premiumImageItemModelListMutableLiveData = new MutableLiveData<>();
    MutableLiveData<UserDataModelList> userDataModelListMutableLiveData = new MutableLiveData<>();
    MutableLiveData<FeaturedModelList> featuredModelListMutableLiveData = new MutableLiveData<>();
    MutableLiveData<DetailsModelList> newsDetailsModelListMutableLiveData = new MutableLiveData<>();
    MutableLiveData<DetailsModelList> reviewDetailsModelListMutableLiveData = new MutableLiveData<>();


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

    public MutableLiveData<ImageItemModelList> getImageItemModelListMutableLiveData(Map<String, String> map) {
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

    public MutableLiveData<FeaturedModelList> getFeaturedModelMutableLiveData() {
        ExecutorService service = Executors.newSingleThreadExecutor();
        service.execute(() -> {
            // Background work
            Call<FeaturedModelList> call = apiInterface.getAllFeatured();
            call.enqueue(new Callback<FeaturedModelList>() {
                @Override
                public void onResponse(@NonNull Call<FeaturedModelList> call, @NonNull Response<FeaturedModelList> response) {
                    if (response.isSuccessful()) {
                        featuredModelListMutableLiveData.setValue(response.body());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<FeaturedModelList> call, @NonNull Throwable t) {

                }
            });
        });
        return featuredModelListMutableLiveData;
    }

    public MutableLiveData<UserDataModelList> getUserDataModelListMutableLiveData() {
        Call<UserDataModelList> call = apiInterface.getAllUserData();
        call.enqueue(new Callback<UserDataModelList>() {
            @Override
            public void onResponse(@NonNull Call<UserDataModelList> call, @NonNull Response<UserDataModelList> response) {
                if (response.isSuccessful()) {
                    userDataModelListMutableLiveData.setValue(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserDataModelList> call, @NonNull Throwable t) {

            }
        });
        return userDataModelListMutableLiveData;
    }

    public MutableLiveData<DetailsModelList> getNewsDetailsModelListMutableLiveData() {
        Call<DetailsModelList> call = apiInterface.getNewsDetails();
        call.enqueue(new Callback<DetailsModelList>() {
            @Override
            public void onResponse(@NonNull Call<DetailsModelList> call, @NonNull Response<DetailsModelList> response) {
                if (response.isSuccessful()) {
                    newsDetailsModelListMutableLiveData.setValue(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<DetailsModelList> call, @NonNull Throwable t) {

            }
        });
        return newsDetailsModelListMutableLiveData;
    }

    public MutableLiveData<DetailsModelList> getReviewDetailsModelListMutableLiveData() {
        Call<DetailsModelList> call = apiInterface.getReviewsDetails();
        call.enqueue(new Callback<DetailsModelList>() {
            @Override
            public void onResponse(@NonNull Call<DetailsModelList> call, @NonNull Response<DetailsModelList> response) {
                if (response.isSuccessful()) {
                    reviewDetailsModelListMutableLiveData.setValue(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<DetailsModelList> call, @NonNull Throwable t) {

            }
        });
        return reviewDetailsModelListMutableLiveData;
    }
}
