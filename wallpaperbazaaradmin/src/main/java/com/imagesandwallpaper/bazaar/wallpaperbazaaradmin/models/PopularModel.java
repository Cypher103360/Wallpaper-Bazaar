package com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models;

public class PopularModel {
    String id,popularImage;

    public PopularModel(String id, String popularImage) {
        this.id = id;
        this.popularImage = popularImage;
    }

    public String getId() {
        return id;
    }

    public String getPopularImage() {
        return popularImage;
    }
}
