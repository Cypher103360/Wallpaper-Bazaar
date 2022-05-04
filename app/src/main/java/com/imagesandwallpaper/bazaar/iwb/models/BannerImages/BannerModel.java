package com.imagesandwallpaper.bazaar.iwb.models.BannerImages;

public class BannerModel {

    String id, Image, url;

    public BannerModel(String id, String image, String url) {
        this.id = id;
        Image = image;
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public String getImage() {
        return Image;
    }

    public String getUrl() {
        return url;
    }
}
