package com.android.quo.viewmodel.factory

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.android.quo.network.repository.PictureRepository
import com.android.quo.network.repository.UserRepository
import com.android.quo.service.UploadService
import com.android.quo.viewmodel.PlaceViewModel

/**
 * Created by vitusortner on 08.01.18.
 */
class PlaceViewModelFactory(
        private val uploadService: UploadService,
        private val pictureRepository: PictureRepository,
        private val userRepository: UserRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return PlaceViewModel(uploadService, pictureRepository, userRepository) as T
    }
}