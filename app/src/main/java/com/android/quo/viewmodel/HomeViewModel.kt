package com.android.quo.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.android.quo.db.entity.Place
import com.android.quo.repository.PlaceRepository
import com.android.quo.repository.UserRepository
import com.android.quo.service.AuthService
import com.android.quo.util.extension.toDate
import io.reactivex.disposables.CompositeDisposable

/**
 * Created by vitusortner on 06.12.17.
 */
class HomeViewModel(
    private val placeRepository: PlaceRepository,
    private val userRepository: UserRepository,
    private val authService: AuthService
) :
    BaseViewModel() {

    private val compositDisposabel = CompositeDisposable()

    private var places = MutableLiveData<List<Place>>()

    fun getPlaces(): LiveData<List<Place>> {
        updatePlaces()
        return places
    }

    fun updatePlaces() {
        userRepository.getUser {
            it?.let {
                placeRepository.getVisitedPlaces(it.id)
                    .distinctUntilChanged()
                    .subscribe({
                        if (it.isNotEmpty()) {
                            places.value = it.sortedByDescending { it.lastScanned.toDate() }
                        }
                    }, {
                        log.e("Error while getting visited places: $it")
                    })
            }
        }
    }

    fun logout() {
        authService.logout()
    }

    override fun onCleared() {
        super.onCleared()
        compositDisposabel.dispose()
    }
}