package com.imagesandwallpaper.bazaar.iwb.models;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Favorite.class}, version = 1)
public abstract class FavoriteAppDatabase extends RoomDatabase {

    // Linking the DAO with our Database
    public abstract FavoriteDao getFavoriteDao();
}
