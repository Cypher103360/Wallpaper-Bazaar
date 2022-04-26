package com.imagesandwallpaper.bazaar.iwb.models;

import java.util.List;

public class CatModelList {
    List<CategoryModel> data = null;

    public CatModelList(List<CategoryModel> data) {
        this.data = data;
    }

    public List<CategoryModel> getData() {
        return data;
    }
}

