package com.imagesandwallpaper.bazaar.iwb.models;

public class ImageItemModel {
    String id,Image;

    public ImageItemModel(String id, String itemImage) {
        this.id = id;
        this.Image = itemImage;
    }

    public String getId() {
        return id;
    }

    public String getItemImage() {
        return Image;
    }
}
