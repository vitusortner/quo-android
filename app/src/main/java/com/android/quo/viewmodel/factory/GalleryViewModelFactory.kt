package com.android.quo.viewmodel.factory

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.android.quo.networking.repository.PictureRepository
import com.android.quo.viewmodel.GalleryViewModel

/**
 * Created by vitusortner on 09.12.17.
 */
class GalleryViewModelFactory(private val pictureRepository: PictureRepository) :
        ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return GalleryViewModel(pictureRepository) as T
    }
}