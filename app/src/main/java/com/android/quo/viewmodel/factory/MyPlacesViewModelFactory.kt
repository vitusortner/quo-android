package com.android.quo.viewmodel.factory

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.android.quo.networking.repository.PlaceRepository
import com.android.quo.viewmodel.MyPlacesViewModel

/**
 * Created by vitusortner on 21.12.17.
 */
class MyPlacesViewModelFactory(private val placeRepository: PlaceRepository) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MyPlacesViewModel(placeRepository) as T
    }
}