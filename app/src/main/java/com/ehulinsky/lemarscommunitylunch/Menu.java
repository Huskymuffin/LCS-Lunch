package com.ehulinsky.lemarscommunitylunch;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by ethan on 12/2/17.
 */

@Entity
public class Menu {
    @PrimaryKey (autoGenerate = true)
    public int id;

    @ColumnInfo(name = "items")
    private String items;

    public String getItems() {
        return items;
    }

    public void setItems(String items) {
        this.items = items;
    }

    public int getId() {
        return this.id;
    }

  /*  @ColumnInfo(name = "coldlunch")
    private boolean coldLunch;*/



}
