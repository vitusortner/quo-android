package com.android.quo

import android.app.Application
import com.android.quo.db.Database
import com.android.quo.di.modules
import devliving.online.securedpreferencestore.DefaultRecoveryHandler
import devliving.online.securedpreferencestore.SecuredPreferenceStore
import org.koin.android.ext.android.startKoin

/**
 * Created by FlorianSchlueter on 20.11.2017.
 */
class App : Application() {

    companion object {
        lateinit var database: Database
    }

    override fun onCreate() {
        super.onCreate()

        database = Database.instance(this)

        SecuredPreferenceStore.init(this, DefaultRecoveryHandler())

        startKoin(this, modules)
    }
}