package com.android.quo.viewmodel.factory

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.android.quo.networking.repository.PictureRepository
import com.android.quo.viewmodel.PlaceViewModel

/**
 * Created by vitusortner on 07.12.17.
 */
class PlaceViewModelFactory(private val pictureRepository: PictureRepository) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PlaceViewModel(pictureRepository) as T
    }
}