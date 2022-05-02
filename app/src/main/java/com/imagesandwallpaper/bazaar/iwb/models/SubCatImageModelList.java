package com.imagesandwallpaper.bazaar.iwb.models;

import java.util.List;

public class SubCatImageModelList {
    List<ImageItemModel> data = null;

    public SubCatImageModelList(List<ImageItemModel> data) {
        this.data = data;
    }

    public List<ImageItemModel> getData() {
        return data;
    }
}
