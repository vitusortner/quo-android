package com.android.quo.viewmodel.factory

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.android.quo.db.dao.UserDao
import com.android.quo.network.repository.PlaceRepository
import com.android.quo.viewmodel.QrCodeScannerViewModel

/**
 * Created by vitusortner on 11.12.17.
 */
class QrCodeScannerViewModelFactory(
        private val placeRepository: PlaceRepository,
        private val userDao: UserDao
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return QrCodeScannerViewModel(placeRepository, userDao) as T
    }
}