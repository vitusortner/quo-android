package com.android.quo.viewmodel.factory

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.android.quo.repository.ComponentRepository
import com.android.quo.repository.PictureRepository
import com.android.quo.repository.PlaceRepository
import com.android.quo.repository.UserRepository
import com.android.quo.service.UploadService
import com.android.quo.viewmodel.CreatePlaceViewModel

/**
 * Created by Jung on 08.01.18.
 */
class CreatePlaceViewModelFactory(
        private val componentRepository: ComponentRepository,
        private val pictureRepository: PictureRepository,
        private val placeRepository: PlaceRepository,
        private val userRepository: UserRepository,
        private val uploadService: UploadService
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return CreatePlaceViewModel(
                componentRepository,
                pictureRepository,
                placeRepository,
                userRepository,
                uploadService
        ) as T
    }
}