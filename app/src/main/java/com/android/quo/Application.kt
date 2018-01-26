package com.android.quo

import android.app.Application
import com.android.quo.di.Injection
import com.facebook.stetho.Stetho
import com.squareup.leakcanary.LeakCanary
import devliving.online.securedpreferencestore.DefaultRecoveryHandler
import devliving.online.securedpreferencestore.SecuredPreferenceStore

/**
 * Created by FlorianSchlueter on 20.11.2017.
 */
class Application : Application() {

    override fun onCreate() {
        super.onCreate()

        Stetho.initializeWithDefaults(this)

        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not instance your app in this process.
            return
        }
        LeakCanary.install(this)

        SecuredPreferenceStore.init(this, DefaultRecoveryHandler())

        Injection.instance(this)
    }
}