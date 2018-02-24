package com.android.quo.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.android.quo.db.entity.Place
import com.android.quo.repository.PlaceRepository
import com.android.quo.repository.UserRepository
import io.reactivex.disposables.CompositeDisposable

/**
 * Created by vitusortner on 11.12.17.
 */
class QrCodeScannerViewModel(
    private val placeRepository: PlaceRepository,
    private val userRepository: UserRepository
) :
    BaseViewModel() {

    private val compositDisposabel = CompositeDisposable()

    private var place = MutableLiveData<Place>()

    fun getPlace(qrCodeId: String): LiveData<Place> {
        loadPlace(qrCodeId)
        return place
    }

    private fun loadPlace(qrCodeId: String) {
        userRepository.getUser {
            it?.let { user ->
                placeRepository.getPlace(qrCodeId, user.id)
                    .subscribe({
                        place.value = it
                    }, {
                        log.e("Error while getting place: $it")
                    })
            }
        }
    }

    fun resetLiveData() {
        place.value = null
    }

    override fun onCleared() {
        super.onCleared()
        compositDisposabel.dispose()
    }
}