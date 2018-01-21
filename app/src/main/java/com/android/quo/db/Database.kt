package com.android.quo.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.android.quo.db.dao.ComponentDao
import com.android.quo.db.dao.PictureDao
import com.android.quo.db.dao.PlaceDao
import com.android.quo.db.dao.UserDao
import com.android.quo.db.entity.Component
import com.android.quo.db.entity.Picture
import com.android.quo.db.entity.Place
import com.android.quo.db.entity.User

/**
 * Created by FlorianSchlueter on 20.11.2017.
 */
@Database(entities = [(User::class), (Place::class), (Picture::class), (Component::class)], version = 2)
abstract class Database : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun placeDao(): PlaceDao
    abstract fun pictureDao(): PictureDao
    abstract fun componentDao(): ComponentDao
}