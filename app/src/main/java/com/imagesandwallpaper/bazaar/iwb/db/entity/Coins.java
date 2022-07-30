package com.imagesandwallpaper.bazaar.iwb.db.entity;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "coins")
public class Coins implements Serializable {

    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    private final int id;

    @ColumnInfo(name = "coin", defaultValue = "10")
    private final String coin;

    public Coins(int id, String coin) {
        this.id = id;
        this.coin = coin;
    }

    public int getId() {
        return id;
    }

    public String getCoin() {
        return coin;
    }
}
