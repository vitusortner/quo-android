package com.android.quo.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.android.quo.db.entity.Place
import com.android.quo.db.entity.User
import com.android.quo.repository.PlaceRepository
import com.android.quo.repository.UserRepository
import com.android.quo.util.extension.flatMapFlowable
import com.android.quo.util.extension.observeOnUi
import com.android.quo.util.extension.subscribeOnIo
import io.reactivex.Single
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy

/**
 * Created by vitusortner on 11.12.17.
 */
class QrCodeScannerViewModel(
    private val placeRepository: PlaceRepository,
    private val userRepository: UserRepository
) :
    BaseViewModel() {

    private var place = MutableLiveData<Place>()

    fun getPlace(qrCodeId: String): LiveData<Place> {
        loadPlace(qrCodeId)
        return place
    }

    private fun loadPlace(qrCodeId: String) =
        userRepository.getUserSingle()
            .subscribeOnIo()
            .getPlace(qrCodeId)
            .observeOnUi()
            .subscribeBy(
                onNext = { place.value = it },
                onError = { log.e("Error while getting place: $it") }
            )
            .addTo(compositeDisposable)

    fun resetLiveData() {
        place.value = null
    }

    private fun Single<User>.getPlace(qrCodeId: String) =
        this.flatMapFlowable { placeRepository.getPlace(qrCodeId, it.id) }

}