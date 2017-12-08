package com.ehulinsky.lemarscommunitylunch;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

/**
 * Created by ethan on 12/6/17.
 */

@Database(entities = {Menu.class}, version = 1)
public abstract class MyDatabase extends RoomDatabase {
    public abstract MenuDao menuDao();
}