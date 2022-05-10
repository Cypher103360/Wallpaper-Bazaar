package com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models.UserData;

public class UserDataModel {
    String id,email,name;

    public UserDataModel(String id, String email, String name) {
        this.id = id;
        this.email = email;
        this.name = name;
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
}
