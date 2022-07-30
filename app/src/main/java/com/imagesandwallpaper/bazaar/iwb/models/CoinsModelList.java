package com.imagesandwallpaper.bazaar.iwb.models;

import java.util.List;

public class CoinsModelList {
    List<CoinsModel> data = null;

    public CoinsModelList(List<CoinsModel> data) {
        this.data = data;
    }

    public List<CoinsModel> getData() {
        return data;
    }
}
