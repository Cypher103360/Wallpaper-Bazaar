package com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models;

public class SubCatModel {

    private final String id;
    private final String catId;
    private final String Image;
    private final String Title;

    public SubCatModel(String id, String catId, String image, String title) {
        this.id = id;
        this.catId = catId;
        Image = image;
        Title = title;
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

    public String getTitle() {
        return Title;
    }
}
