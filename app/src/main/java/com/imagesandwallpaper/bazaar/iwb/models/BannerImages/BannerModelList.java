package com.imagesandwallpaper.bazaar.iwb.models.BannerImages;

import java.util.List;

public class BannerModelList {

    List<BannerModel> data = null;

    public BannerModelList(List<BannerModel> data) {
        this.data = data;
    }

    public List<BannerModel> getData() {
        return data;
    }
}
