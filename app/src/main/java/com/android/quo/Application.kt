package com.android.quo

import android.app.Application
import android.arch.persistence.room.Room
import com.android.quo.db.AppDatabase
import com.facebook.stetho.Stetho
import com.squareup.leakcanary.LeakCanary
import devliving.online.securedpreferencestore.DefaultRecoveryHandler
import devliving.online.securedpreferencestore.SecuredPreferenceStore

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

        Stetho.initializeWithDefaults(this)

        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return
        }
        LeakCanary.install(this)

        SecuredPreferenceStore.init(applicationContext, DefaultRecoveryHandler())
    }
}