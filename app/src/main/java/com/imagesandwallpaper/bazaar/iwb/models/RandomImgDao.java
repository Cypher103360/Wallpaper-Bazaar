package com.imagesandwallpaper.bazaar.iwb.models;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface RandomImgDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long addRandomImg(RandomImage randomImage);

    @Query("select * from random_images where id ==:imgId")
    RandomImage getRandom(long imgId);

    @Query("SELECT * FROM random_images")
    List<RandomImage> getAllRandomImages();

    @Query("DELETE FROM random_images WHERE id NOT IN (SELECT id FROM random_images ORDER BY id DESC LIMIT 50)")
    void deleteItemBYLimit();
}
