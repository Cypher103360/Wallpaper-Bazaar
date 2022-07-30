package com.imagesandwallpaper.bazaar.iwb.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.imagesandwallpaper.bazaar.iwb.db.entity.Coins;

@Database(entities = {Coins.class}, version = 1)
public abstract class CoinsDatabase extends RoomDatabase {
    // Linking our DAO to with our database
    public abstract CoinsDAO getCoinsDAO();
}
