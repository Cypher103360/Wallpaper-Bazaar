package com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models;

import java.util.List;

public class ProWallModelList {
    List<ProWallModel> data = null;

    public ProWallModelList(List<ProWallModel> data) {
        this.data = data;
    }

    public List<ProWallModel> getData() {
        return data;
    }
}
