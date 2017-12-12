package com.android.quo.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import com.android.quo.db.dao.*
import com.android.quo.db.entity.*

/**
 * Created by FlorianSchlueter on 20.11.2017.
 */
@Database(entities = arrayOf(User::class, Place::class, Picture::class, Component::class), version = 2)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun placeDao(): PlaceDao
    abstract fun pictureDao(): PictureDao
    abstract fun componentDao(): ComponentDao
}