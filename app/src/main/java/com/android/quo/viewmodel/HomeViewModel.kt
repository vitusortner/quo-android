package com.android.quo.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.android.quo.db.entity.Place
import com.android.quo.db.entity.User
import com.android.quo.repository.PlaceRepository
import com.android.quo.repository.UserRepository
import com.android.quo.service.AuthService
import com.android.quo.util.Constants.Date.MONGO_DB_TIMESTAMP_FORMAT
import com.android.quo.util.extension.*
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.rxkotlin.addTo
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
            .getVisitedPlaces()
            .distinctUntilChanged()
            .filterNotEmpty()
            .sortByTimestamp() // TODO move this ordering to DB level
            .observeOnUi()
            .subscribeBy(
                onNext = { places.value = it },
                onError = { log.e("Error while getting visited places: $it") }
            )
            .addTo(compositeDisposable)

    fun logout() = async { authService.logout() }

    private fun Single<User>.getVisitedPlaces() =
        this.flatMapFlowable { placeRepository.getVisitedPlaces(it.id) }

    private fun Flowable<List<Place>>.sortByTimestamp() =
        this.map { it.sortedByDescending { it.lastScanned.toDate(MONGO_DB_TIMESTAMP_FORMAT) } }

}