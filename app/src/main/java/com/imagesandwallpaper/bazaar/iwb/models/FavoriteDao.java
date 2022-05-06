package com.imagesandwallpaper.bazaar.iwb.models;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface FavoriteDao {

    @Insert
    public long addFavorite(Favorite favorite);

    @Delete
    public void deleteFavorite(Favorite favorite);

    @Query("SELECT * FROM favorite")
    public List<Favorite> getAllFavorite();

    @Query("select * from favorite where Image ==:img")
    public Favorite getFavorite(String img);
}
