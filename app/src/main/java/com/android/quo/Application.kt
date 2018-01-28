package com.android.quo

import android.app.Application
import com.android.quo.db.Database
import com.android.quo.di.modules
import com.facebook.stetho.Stetho
import com.squareup.leakcanary.LeakCanary
import devliving.online.securedpreferencestore.DefaultRecoveryHandler
import devliving.online.securedpreferencestore.SecuredPreferenceStore
import org.koin.android.ext.android.startKoin

/**
 * Created by FlorianSchlueter on 20.11.2017.
 */
class Application : Application() {

    companion object {

        lateinit var database: Database
    }

    override fun onCreate() {
        super.onCreate()

        Stetho.initializeWithDefaults(this)

        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return
        }
        LeakCanary.install(this)

        database = Database.instance(this)

        SecuredPreferenceStore.init(this, DefaultRecoveryHandler())

        startKoin(this, modules)
    }
}