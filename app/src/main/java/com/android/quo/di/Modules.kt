package com.android.quo.di

import com.android.quo.Application
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
    provide { Application.database.componentDao() }
    provide { Application.database.pictureDao() }
    provide { Application.database.placeDao() }
    provide { Application.database.userDao() }
}

private val utilsModule = applicationContext {
    provide { ApiClient.instance(get()) }
    provide { SecuredPreferenceStore.getSharedInstance() }
}

private val servicesModule = applicationContext {
    provide { AuthService(get(), get(), get(), get(), get(), get()) }
    provide { SyncService(get(), get(), get()) }
    provide { UploadService(get()) }
}

private val repositoriesModule = applicationContext {
    provide { ComponentRepository(get(), get(), get()) }
    provide { PictureRepository(get(), get(), get()) }
    provide { PlaceRepository(get(), get(), get()) }
    provide { UserRepository(get()) }
}

val modules = listOf(viewModelsModule, daosModule, utilsModule, servicesModule, repositoriesModule)