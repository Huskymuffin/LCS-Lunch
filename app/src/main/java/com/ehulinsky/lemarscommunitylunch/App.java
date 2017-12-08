package com.ehulinsky.lemarscommunitylunch;


import android.app.Application;
import android.arch.persistence.room.Room;
import android.content.SharedPreferences;

/**
 * Created by ethan on 12/6/17.
 */

public class App extends Application {

    public static App INSTANCE;
    private static final String DATABASE_NAME = "MyDatabase";
    private static final String PREFERENCES = "LCSDLunch.preferences";

    private MyDatabase database;

    public static App get() {
        return INSTANCE;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // create database
        database = Room.databaseBuilder(getApplicationContext(), MyDatabase.class, DATABASE_NAME)
                .build();

        INSTANCE = this;
    }

    public MyDatabase getDB() {
        return database;
    }

    public SharedPreferences getSP() {
        return getSharedPreferences(PREFERENCES, MODE_PRIVATE);
    }
}
