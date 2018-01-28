package com.android.quo.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
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
) : ViewModel() {

    private val TAG = javaClass.simpleName

    private val compositDisposabel = CompositeDisposable()

    private var places: MutableLiveData<List<Place>>? = null

    fun getPlaces(): LiveData<List<Place>> {
        if (places == null) {
            places = MutableLiveData()
            updatePlaces()
        }
        return places as MutableLiveData<List<Place>>
    }

    fun updatePlaces() {
        userRepository.getUser {
            it?.let {
                placeRepository.getVisitedPlaces(it.id)
                        .distinctUntilChanged()
                        .subscribe({
                            if (it.isNotEmpty()) {
                                places?.value = it.sortedByDescending { it.lastScanned.toDate() }
                            }
                        }, {
                            Log.e(TAG, "Error while getting visited places: $it")
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