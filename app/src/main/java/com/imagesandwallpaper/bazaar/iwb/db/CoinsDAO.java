package com.imagesandwallpaper.bazaar.iwb.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.imagesandwallpaper.bazaar.iwb.db.entity.Coins;

@Dao
public interface CoinsDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addCoins(Coins coins);

    @Query("select coin from coins")
    String getCoins();

    @Update
    void updateCoins(Coins coins);

}
