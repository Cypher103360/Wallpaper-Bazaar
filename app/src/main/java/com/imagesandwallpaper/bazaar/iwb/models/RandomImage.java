package com.imagesandwallpaper.bazaar.iwb.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "random_images")
public class RandomImage {

    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    private final int id;
    @ColumnInfo(name = "Image")
    String image;
    @ColumnInfo(name = "catId")
    String catId;

    public RandomImage(String image, String catId, int id) {
        this.image = image;
        this.catId = catId;
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public String getCatId() {
        return catId;
    }

    public int getId() {
        return id;
    }
}
