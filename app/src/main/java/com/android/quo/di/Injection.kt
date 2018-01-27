package com.android.quo.di

import android.arch.persistence.room.Room
import android.content.Context
import com.android.quo.db.Database
import com.android.quo.network.ApiClient
import com.android.quo.repository.ComponentRepository
import com.android.quo.repository.PictureRepository
import com.android.quo.repository.PlaceRepository
import com.android.quo.repository.UserRepository
import com.android.quo.service.AuthService
import com.android.quo.service.SyncService
import com.android.quo.service.UploadService
import devliving.online.securedpreferencestore.SecuredPreferenceStore

/**
 * Created by vitusortner on 26.01.18.
 */
object Injection {

    lateinit var authService: AuthService
    lateinit var uploadService: UploadService

    lateinit var componentRepository: ComponentRepository
    lateinit var pictureRepository: PictureRepository
    lateinit var placeRepository: PlaceRepository
    lateinit var userRepository: UserRepository

    @Volatile
    private var _INSTANCE: Injection? = null

    fun instance(context: Context): Injection {
        return _INSTANCE ?: synchronized(this) {
            _INSTANCE ?: init(context).also { _INSTANCE = it }
        }
    }

    private fun init(context: Context): Injection {
        val securedPreferenceStore = SecuredPreferenceStore.getSharedInstance()

        val database = Room.databaseBuilder(context, Database::class.java, "qouDB").build()

        val componentDao = database.componentDao()
        val pictureDao = database.pictureDao()
        val placeDao = database.placeDao()
        val userDao = database.userDao()

        val apiClient = ApiClient.instance(securedPreferenceStore)

        authService = AuthService(
                apiClient,
                componentDao,
                pictureDao,
                placeDao,
                userDao,
                securedPreferenceStore
        )
        uploadService = UploadService(apiClient)
        val syncService = SyncService(placeDao, componentDao, pictureDao)

        componentRepository = ComponentRepository(componentDao, apiClient, syncService)
        pictureRepository = PictureRepository(pictureDao, apiClient, syncService)
        placeRepository = PlaceRepository(placeDao, apiClient, syncService)
        userRepository = UserRepository(userDao)

        return Injection
    }
}