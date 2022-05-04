package com.imagesandwallpaper.bazaar.iwb.models.PremiumImages;

import com.imagesandwallpaper.bazaar.iwb.models.ImageItemModel;

import java.util.List;

public class PremiumModelList {
    List<PremiumModel> data = null;

    public PremiumModelList(List<PremiumModel> data) {
        this.data = data;
    }

    public List<PremiumModel> getData() {
        return data;
    }
}
