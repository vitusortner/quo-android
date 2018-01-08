package com.android.quo.viewmodel.factory

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.android.quo.networking.service.ApiService
import com.android.quo.viewmodel.CreatePlaceViewModel

/**
 * Created by Jung on 08.01.18.
 */
class CreatePlaceViewModelFactory(private val apiService: ApiService) :
        ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return CreatePlaceViewModel(apiService) as T
    }
}