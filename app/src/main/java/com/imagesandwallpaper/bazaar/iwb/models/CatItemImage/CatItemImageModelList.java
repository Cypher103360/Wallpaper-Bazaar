package com.imagesandwallpaper.bazaar.iwb.models.CatItemImage;

import com.imagesandwallpaper.bazaar.iwb.models.ImageItemModel;

import java.util.List;

public class CatItemImageModelList {
    List<ImageItemModel> data = null;

    public CatItemImageModelList(List<ImageItemModel> data) {
        this.data = data;
    }

    public List<ImageItemModel> getData() {
        return data;
    }
}
