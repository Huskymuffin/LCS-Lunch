package com.ehulinsky.lemarscommunitylunch;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

/**
 * Created by ethan on 12/6/17.
 */

@Dao
public interface MenuDao {
    @Query("SELECT * FROM Menu")
    List<Menu> getAll();

    @Insert
    void insertAll(List<Menu> menus);

    @Update
    void update(Menu menu);

    @Delete
    void delete(Menu menu);
}
