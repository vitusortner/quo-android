package com.android.quo.data

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase

/**
 * Created by FlorianSchlueter on 20.11.2017.
 */
@Database(entities = arrayOf(User::class, Place::class, Picture::class, Component::class), version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun placeDao(): PlaceDao
    abstract fun pictureDao(): PictureDao
    abstract fun componentDao(): ComponentDao
}