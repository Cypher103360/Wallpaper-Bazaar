package com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models.news_and_reviews;

import java.util.List;

public class DetailsModelList {

    List<DetailsModel> data = null;

    public DetailsModelList(List<DetailsModel> data) {
        this.data = data;
    }

    public List<DetailsModel> getData() {
        return data;
    }
}
