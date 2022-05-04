package com.imagesandwallpaper.bazaar.iwb.models.PremiumImages;

public class PremiumModel {
    String id, catId, Image;

    public PremiumModel(String id, String catId, String image) {
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
