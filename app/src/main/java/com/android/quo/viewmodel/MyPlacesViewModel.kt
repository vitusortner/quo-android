package com.android.quo.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.android.quo.db.entity.Place
import com.android.quo.repository.PlaceRepository
import com.android.quo.repository.UserRepository
import com.android.quo.util.extension.toDate

/**
 * Created by vitusortner on 21.12.17.
 */
class MyPlacesViewModel(
    private val placeRepository: PlaceRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val TAG = javaClass.simpleName

    private var places = MutableLiveData<List<Place>>()

    fun getPlaces(): LiveData<List<Place>> {
        updatePlaces()
        return places
    }

    fun updatePlaces() {
        userRepository.getUser {
            it?.let {
                placeRepository.getHostedPlaces(it.id)
                    .distinctUntilChanged()
                    .subscribe({
                        if (it.isNotEmpty()) {
                            places.value = it.sortedByDescending { it.timestamp.toDate() }
                        }
                    }, {
                        Log.e(TAG, "Error while getting hosted places", it)
                    })
            }
        }
    }
}