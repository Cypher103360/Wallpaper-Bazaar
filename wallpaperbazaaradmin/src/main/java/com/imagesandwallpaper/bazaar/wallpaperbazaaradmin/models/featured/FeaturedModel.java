package com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models.featured;

public class FeaturedModel {
    String id,image,url;

    public FeaturedModel(String id, String image, String url) {
        this.id = id;
        this.image = image;
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public String getImage() {
        return image;
    }

    public String getUrl() {
        return url;
    }
}
