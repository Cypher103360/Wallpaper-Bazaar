package com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models.Ads;

import java.util.List;

public class AdsModelList {
    List<AdsModel> data = null;

    public AdsModelList(List<AdsModel> data) {
        this.data = data;
    }

    public List<AdsModel> getData() {
        return data;
    }
}
