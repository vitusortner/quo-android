package com.android.quo.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.android.quo.db.entity.Place
import com.android.quo.db.entity.User
import com.android.quo.repository.PlaceRepository
import com.android.quo.repository.UserRepository
import com.android.quo.util.Constants.Date.MONGO_DB_TIMESTAMP_FORMAT
import com.android.quo.util.extension.filterNotEmpty
import com.android.quo.util.extension.flatMapFlowable
import com.android.quo.util.extension.observeOnUi
import com.android.quo.util.extension.subscribeOnIo
import com.android.quo.util.extension.toDate
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy

/**
 * Created by vitusortner on 21.12.17.
 */
class MyPlacesViewModel(
    private val placeRepository: PlaceRepository,
    private val userRepository: UserRepository
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
            .getHostedPlaces()
            .distinctUntilChanged()
            .filterNotEmpty()
            .sortByTimestamp()
            .observeOnUi()
            .subscribeBy(
                onNext = { places.value = it },
                onError = { log.e("Error while getting hosted places: $it") }
            )
            .addTo(compositeDisposable)

    private fun Single<User>.getHostedPlaces() =
        this.flatMapFlowable { placeRepository.getHostedPlaces(it.id) }

    private fun Flowable<List<Place>>.sortByTimestamp() =
        this.map { it.sortedByDescending { it.timestamp.toDate(MONGO_DB_TIMESTAMP_FORMAT) } }
}