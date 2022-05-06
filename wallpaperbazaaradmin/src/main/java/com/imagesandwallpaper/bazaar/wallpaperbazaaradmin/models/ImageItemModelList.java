package com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models;

import java.util.List;

public class ImageItemModelList {
    List<ImageItemModel> data = null;

    public ImageItemModelList(List<ImageItemModel> data) {
        this.data = data;
    }

    public List<ImageItemModel> getData() {
        return data;
    }
}
