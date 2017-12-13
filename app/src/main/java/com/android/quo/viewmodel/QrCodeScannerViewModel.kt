package com.android.quo.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.android.quo.db.entity.Place
import com.android.quo.networking.repository.PlaceRepository

/**
 * Created by vitusortner on 11.12.17.
 */
class QrCodeScannerViewModel(private val placeRepository: PlaceRepository) : ViewModel() {

    private var place: MutableLiveData<Place>? = null

    fun getPlace(qrCodeId: String): LiveData<Place> {
        if (place == null) {
            place = MutableLiveData()
            loadPlace(qrCodeId)
        }
        return place as MutableLiveData<Place>
    }

    private fun loadPlace(qrCodeId: String) {
        placeRepository.getPlace(qrCodeId)
                .subscribe({
                    place?.value = it
                }, {
                    Log.e("sync", "$it")
                })
    }
}