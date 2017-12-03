package com.ehulinsky.lemarscommunitylunch;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by ethan on 12/2/17.
 */

@Entity
public class Menu {
    @PrimaryKey
    public int id;

    @ColumnInfo(name = "day")
    private int firstName;

    @ColumnInfo(name = "items")
    private String lastName;

    @ColumnInfo(name = "coldlunch")
    private boolean coldLunch;

}
