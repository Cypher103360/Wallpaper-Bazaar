package com.imagesandwallpaper.bazaar.iwb.models.PremiumImages;

import com.imagesandwallpaper.bazaar.iwb.models.ImageItemModel;

import java.util.List;

public class PremiumModelList {
    List<ImageItemModel> data = null;

    public PremiumModelList(List<ImageItemModel> data) {
        this.data = data;
    }

    public List<ImageItemModel> getData() {
        return data;
    }
}
