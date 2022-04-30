package com.imagesandwallpaper.bazaar.iwb.models;

public class ImageItemModel {
    String id, catId, Image;

    public ImageItemModel(String id, String catId, String image) {
        this.id = id;
        this.catId = catId;
        Image = image;
    }

    public String getId() {
        return id;
    }

    public String getCatId() {
        return catId;
    }

    public String getImage() {
        return Image;
    }
}
