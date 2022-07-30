package com.imagesandwallpaper.bazaar.iwb.models.UserData;

import java.util.List;

public class UserDataModelList {
    List<UserDataModel> data = null;

    public UserDataModelList(List<UserDataModel> data) {
        this.data = data;
    }

    public List<UserDataModel> getData() {
        return data;
    }
}
