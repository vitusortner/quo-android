package com.android.quo

import android.app.Application
import android.arch.persistence.room.Room
import com.android.quo.db.Database
import com.android.quo.repository.ComponentRepository
import com.android.quo.repository.PictureRepository
import com.android.quo.repository.PlaceRepository
import com.android.quo.repository.UserRepository
import com.android.quo.network.ApiClient
import com.android.quo.service.AuthService
import com.android.quo.service.SyncService
import com.android.quo.service.UploadService
import com.facebook.stetho.Stetho
import com.squareup.leakcanary.LeakCanary
import devliving.online.securedpreferencestore.DefaultRecoveryHandler
import devliving.online.securedpreferencestore.SecuredPreferenceStore

/**
 * Created by FlorianSchlueter on 20.11.2017.
 */
class Application : Application() {

    companion object {

        lateinit var database: Database

        lateinit var apiClient: ApiClient

        lateinit var authService: AuthService
        lateinit var syncService: SyncService
        lateinit var uploadService: UploadService

        lateinit var componentRepository: ComponentRepository
        lateinit var pictureRepository: PictureRepository
        lateinit var placeRepository: PlaceRepository
        lateinit var userRepository: UserRepository
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

        SecuredPreferenceStore.init(applicationContext, DefaultRecoveryHandler())

        val securedPreferenceStore = SecuredPreferenceStore.getSharedInstance()

        database = Room.databaseBuilder(this, Database::class.java, "qouDB").build()

        val componentDao = database.componentDao()
        val pictureDao = database.pictureDao()
        val placeDao = database.placeDao()
        val userDao = database.userDao()

        apiClient = ApiClient.instance(securedPreferenceStore)

        authService = AuthService(
                apiClient,
                componentDao,
                pictureDao,
                placeDao,
                userDao,
                securedPreferenceStore
        )
        syncService = SyncService(placeDao, componentDao, pictureDao)
        uploadService = UploadService(apiClient)

        componentRepository = ComponentRepository(componentDao, apiClient, syncService)
        pictureRepository = PictureRepository(pictureDao, apiClient, syncService)
        placeRepository = PlaceRepository(placeDao, apiClient, syncService)
        userRepository = UserRepository(userDao)
    }
}