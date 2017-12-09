package com.android.quo.viewmodel.factory

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.android.quo.networking.PlaceRepository
import com.android.quo.viewmodel.HomeViewModel


/**
 * Created by vitusortner on 07.12.17.
 */
class HomeViewModelFactory(private val placeRepository: PlaceRepository) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeViewModel(placeRepository) as T
    }
}