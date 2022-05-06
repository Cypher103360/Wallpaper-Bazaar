package com.imagesandwallpaper.bazaar.iwb.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "favorite")
public class Favorite {

    @ColumnInfo(name = "Image")
    String image;
    @ColumnInfo(name = "catId")
    String catId;
    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    private int id;

    public Favorite(String image, String catId, int id) {
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
