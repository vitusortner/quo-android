package com.android.quo

import android.app.Application
import com.android.quo.di.modules
import devliving.online.securedpreferencestore.DefaultRecoveryHandler
import devliving.online.securedpreferencestore.SecuredPreferenceStore
import org.koin.android.ext.android.startKoin
import org.koin.log.EmptyLogger

/**
 * Created by FlorianSchlueter on 20.11.2017.
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        SecuredPreferenceStore.init(this, DefaultRecoveryHandler())
        startKoin(this, modules, logger = EmptyLogger())
    }
}