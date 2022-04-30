package com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models;

public class CatItemModel {

    String id, catId, Image;

    public CatItemModel(String id, String catId, String image) {
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
