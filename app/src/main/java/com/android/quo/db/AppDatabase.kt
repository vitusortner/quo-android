package com.android.quo.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.android.quo.db.dao.*
import com.android.quo.db.entity.*

/**
 * Created by FlorianSchlueter on 20.11.2017.
 */
@Database(entities = [(User::class), (Place::class), (Picture::class), (Component::class)], version = 2)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun placeDao(): PlaceDao
    abstract fun pictureDao(): PictureDao
    abstract fun componentDao(): ComponentDao
}