package com.android.quo.di

import com.android.quo.db.Database
import com.android.quo.network.ApiClient
import com.android.quo.repository.ComponentRepository
import com.android.quo.repository.PictureRepository
import com.android.quo.repository.PlaceRepository
import com.android.quo.repository.UserRepository
import com.android.quo.service.AuthService
import com.android.quo.service.SyncService
import com.android.quo.service.UploadService
import com.android.quo.viewmodel.CreatePlaceViewModel
import com.android.quo.viewmodel.GalleryViewModel
import com.android.quo.viewmodel.HomeViewModel
import com.android.quo.viewmodel.LoginViewModel
import com.android.quo.viewmodel.MyPlacesViewModel
import com.android.quo.viewmodel.PageViewModel
import com.android.quo.viewmodel.PlaceViewModel
import com.android.quo.viewmodel.QrCodeScannerViewModel
import devliving.online.securedpreferencestore.SecuredPreferenceStore
import org.koin.android.architecture.ext.viewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module.applicationContext

/**
 * Created by vitusortner on 28.01.18.
 */
private val viewModelsModule = applicationContext {
    viewModel { CreatePlaceViewModel(get(), get(), get(), get(), get()) }
    viewModel { GalleryViewModel(get()) }
    viewModel { HomeViewModel(get(), get(), get()) }
    viewModel { LoginViewModel(get(), get()) }
    viewModel { MyPlacesViewModel(get(), get()) }
    viewModel { PageViewModel(get()) }
    viewModel { PlaceViewModel(get(), get(), get()) }
    viewModel { QrCodeScannerViewModel(get(), get()) }
}

private val daosModule = applicationContext {
    bean { Database.instance(androidApplication()) }
    bean { get<Database>().componentDao() }
    bean { get<Database>().pictureDao() }
    bean { get<Database>().placeDao() }
    bean { get<Database>().userDao() }
}

private val utilsModule = applicationContext {
    bean { ApiClient.instance(get()) }
    bean { SecuredPreferenceStore.getSharedInstance() }
}

private val servicesModule = applicationContext {
    bean { AuthService(get(), get(), get(), get(), get(), get()) }
    bean { SyncService(get(), get(), get()) }
    bean { UploadService(get()) }
}

private val repositoriesModule = applicationContext {
    bean { ComponentRepository(get(), get(), get()) }
    bean { PictureRepository(get(), get(), get()) }
    bean { PlaceRepository(get(), get(), get()) }
    bean { UserRepository(get()) }
}

val modules =
    listOf(viewModelsModule, daosModule, utilsModule, servicesModule, repositoriesModule)