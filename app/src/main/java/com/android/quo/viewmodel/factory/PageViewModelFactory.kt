package com.android.quo.viewmodel.factory

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.android.quo.networking.repository.ComponentRepository
import com.android.quo.viewmodel.PageViewModel

/**
 * Created by vitusortner on 07.12.17.
 */
class PageViewModelFactory(private val componentRepository: ComponentRepository) :
        ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PageViewModel(componentRepository) as T
    }
}