package com.android.quo

import android.app.Application
import android.arch.persistence.room.Room
import com.android.quo.db.AppDatabase
import com.android.quo.network.repository.ComponentRepository
import com.android.quo.network.repository.PictureRepository
import com.android.quo.network.repository.PlaceRepository
import com.android.quo.network.repository.UserRepository
import com.android.quo.service.ApiService
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
        lateinit var database: AppDatabase

        lateinit var apiService: ApiService
        lateinit var authService: AuthService
        lateinit var syncService: SyncService
        lateinit var uploadService: UploadService

        lateinit var pictureRepository: PictureRepository
        lateinit var componentRepository: ComponentRepository
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

        val preferenceStore = SecuredPreferenceStore.getSharedInstance()

        database = Room.databaseBuilder(this, AppDatabase::class.java, "qouDB").build()

        val userDao = database.userDao()
        val pictureDao = database.pictureDao()
        val componentDao = database.componentDao()
        val placeDao = database.placeDao()

        apiService = ApiService.instance
        authService = AuthService(apiService, userDao, preferenceStore)
        syncService = SyncService(placeDao, componentDao, pictureDao)
        uploadService = UploadService(apiService)

        pictureRepository = PictureRepository(pictureDao, apiService, syncService)
        componentRepository = ComponentRepository(componentDao, apiService, syncService)
        placeRepository = PlaceRepository(placeDao, apiService, syncService)
        userRepository = UserRepository(userDao)
    }
}