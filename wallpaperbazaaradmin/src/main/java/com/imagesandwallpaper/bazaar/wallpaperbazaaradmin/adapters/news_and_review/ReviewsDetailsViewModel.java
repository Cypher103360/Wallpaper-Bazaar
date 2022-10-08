package com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.adapters.news_and_review;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models.Repository;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models.news_and_reviews.DetailsModelList;

public class ReviewsDetailsViewModel extends AndroidViewModel {
    Repository repository;

    public ReviewsDetailsViewModel(@NonNull Application application) {
        super(application);
        repository = Repository.getInstance();
    }

    public LiveData<DetailsModelList> getReviewsDetails(){
        return repository.getReviewDetailsModelListMutableLiveData();
    }
}
