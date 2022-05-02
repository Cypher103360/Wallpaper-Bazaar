package com.imagesandwallpaper.bazaar.iwb.models.CatItemImage;

import java.util.List;

public class CatItemImageModelList {
    List<CatItemImageModel> data = null;

    public CatItemImageModelList(List<CatItemImageModel> data) {
        this.data = data;
    }

    public List<CatItemImageModel> getData() {
        return data;
    }
}
