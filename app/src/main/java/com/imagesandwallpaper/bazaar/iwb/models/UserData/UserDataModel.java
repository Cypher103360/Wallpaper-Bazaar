package com.imagesandwallpaper.bazaar.iwb.models.UserData;

public class UserDataModel {
    String id,email,name,coins;

    public UserDataModel(String id, String email, String name,String coins) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.coins = coins;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getCoins() {
        return coins;
    }
}
