package com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models;

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

