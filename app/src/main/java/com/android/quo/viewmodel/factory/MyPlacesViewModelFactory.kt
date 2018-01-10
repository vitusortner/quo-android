package com.android.quo.viewmodel.factory

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.android.quo.network.repository.PlaceRepository
import com.android.quo.network.repository.UserRepository
import com.android.quo.viewmodel.MyPlacesViewModel

/**
 * Created by vitusortner on 21.12.17.
 */
class MyPlacesViewModelFactory(
        private val placeRepository: PlaceRepository,
        private val userRepository: UserRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MyPlacesViewModel(placeRepository, userRepository) as T
    }
}