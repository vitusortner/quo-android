package com.android.quo.viewmodel.factory

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.android.quo.repository.UserRepository
import com.android.quo.network.ApiClient
import com.android.quo.viewmodel.CreatePlaceViewModel

/**
 * Created by Jung on 08.01.18.
 */
class CreatePlaceViewModelFactory(
        private val apiClient: ApiClient,
        private val userRepository: UserRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return CreatePlaceViewModel(apiClient, userRepository) as T
    }
}