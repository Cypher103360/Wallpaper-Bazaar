package com.imagesandwallpaper.bazaar.iwb.models;

public class CategoryModel {
    String id;
    String Image;
    String Title;

    public CategoryModel(String id, String image, String title) {
        this.id = id;
        Image = image;
        Title = title;
    }

    public String getId() {
        return id;
    }

    public String getImage() {
        return Image;
    }

    public String getTitle() {
        return Title;
    }
}
