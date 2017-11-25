package com.android.quo

import android.app.Application
import android.arch.persistence.room.Room
import com.android.quo.data.AppDatabase

/**
 * Created by FlorianSchlueter on 20.11.2017.
 */
class QuoApplication : Application() {
    companion object {
        lateinit var database: AppDatabase
    }

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(this, AppDatabase::class.java, "qouDB").build()
    }
}