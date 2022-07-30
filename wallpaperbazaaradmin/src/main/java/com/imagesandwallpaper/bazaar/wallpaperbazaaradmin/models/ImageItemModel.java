package com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models;

public class ImageItemModel {
    String id, catId, Image,lockItem;

    public ImageItemModel(String id, String catId, String image, String lockItem) {
        this.id = id;
        this.catId = catId;
        Image = image;
        this.lockItem = lockItem;
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

    public String getLockItem() {
        return lockItem;
    }
}
