package com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models;

public class CategoryModel {
    String id;
    String Image;
    String Title;
    String subCat;
    String item;

    public CategoryModel(String id, String image, String title, String subCat, String item) {
        this.id = id;
        Image = image;
        Title = title;
        this.subCat = subCat;
        this.item = item;
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

    public String getSubCat() {
        return subCat;
    }

    public String getItem() {
        return item;
    }
}
