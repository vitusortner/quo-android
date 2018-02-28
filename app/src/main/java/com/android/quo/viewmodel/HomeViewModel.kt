package com.android.quo.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.os.AsyncTask
import com.android.quo.db.entity.Place
import com.android.quo.repository.PlaceRepository
import com.android.quo.repository.UserRepository
import com.android.quo.service.AuthService
import com.android.quo.util.extension.addTo
import com.android.quo.util.extension.flatMapFlowable
import com.android.quo.util.extension.observeOnUi
import com.android.quo.util.extension.subscribeOnIo
import com.android.quo.util.extension.toDate
import io.reactivex.rxkotlin.subscribeBy

/**
 * Created by vitusortner on 06.12.17.
 */
class HomeViewModel(
    private val placeRepository: PlaceRepository,
    private val userRepository: UserRepository,
    private val authService: AuthService
) :
    BaseViewModel() {

    private var places = MutableLiveData<List<Place>>()

    fun getPlaces(): LiveData<List<Place>> {
        updatePlaces()
        return places
    }

    fun updatePlaces() =
        userRepository.getUserSingle()
            .subscribeOnIo()
            .flatMapFlowable { placeRepository.getVisitedPlaces(it.id) }
            .distinctUntilChanged()
            .filter { it.isNotEmpty() }
            .map { it.sortedByDescending { it.lastScanned.toDate() } }
            .observeOnUi()
            .subscribeBy(
                onNext = { places.value = it },
                onError = { log.e("Error while getting visited places: $it") }
            )
            .addTo(compositeDisposable)

    fun logout() = AsyncTask.execute { authService.logout() }
}