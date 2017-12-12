package com.android.quo.viewmodel.factory

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.android.quo.networking.repository.PlaceRepository
import com.android.quo.viewmodel.QrCodeScannerViewModel

/**
 * Created by vitusortner on 11.12.17.
 */
class QrCodeScannerViewModelFactory(private val placeRepository: PlaceRepository) :
        ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return QrCodeScannerViewModel(placeRepository) as T
    }
}