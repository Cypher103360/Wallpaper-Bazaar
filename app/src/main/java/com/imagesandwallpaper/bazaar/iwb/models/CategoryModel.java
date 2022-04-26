package com.imagesandwallpaper.bazaar.iwb.models;

public class CategoryModel {
    int catImage;
    String catId, catTitle;

    public CategoryModel(int catImage, String catId, String catTitle) {
        this.catImage = catImage;
        this.catId = catId;
        this.catTitle = catTitle;
    }

    public int getCatImage() {
        return catImage;
    }

    public String getCatId() {
        return catId;
    }

    public String getCatTitle() {
        return catTitle;
    }
}
