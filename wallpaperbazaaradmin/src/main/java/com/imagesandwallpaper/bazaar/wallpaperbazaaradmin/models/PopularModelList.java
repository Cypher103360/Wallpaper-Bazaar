package com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models;

import java.util.List;

public class PopularModelList {
    List<PopularModel> data = null;

    public PopularModelList(List<PopularModel> data) {
        this.data = data;
    }

    public List<PopularModel> getData() {
        return data;
    }
}
