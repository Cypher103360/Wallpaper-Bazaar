package com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models.featured;

import java.util.List;

public class FeaturedModelList {
    List<FeaturedModel> data = null;

    public FeaturedModelList(List<FeaturedModel> data) {
        this.data = data;
    }

    public List<FeaturedModel> getData() {
        return data;
    }
}
