package com.imagesandwallpaper.bazaar.iwb.models;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {RandomImage.class}, version = 1)
public abstract class RandomImgDatabase extends RoomDatabase {

    // Linking the DAO with our Database
    public abstract RandomImgDao getRandomDao();
}
