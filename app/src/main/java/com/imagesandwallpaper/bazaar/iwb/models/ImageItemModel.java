package com.imagesandwallpaper.bazaar.iwb.models;

public class ImageItemModel {
    String id;
    int itemImage;

    public ImageItemModel(int itemImage) {
        this.itemImage = itemImage;
    }

    public String getId() {
        return id;
    }

    public int getItemImage() {
        return itemImage;
    }
}
