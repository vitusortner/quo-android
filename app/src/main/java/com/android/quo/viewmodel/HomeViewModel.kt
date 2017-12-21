package com.android.quo.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.android.quo.db.entity.Place
import com.android.quo.extensions.toDate
import com.android.quo.networking.repository.PlaceRepository
import io.reactivex.disposables.CompositeDisposable

/**
 * Created by vitusortner on 06.12.17.
 */
class HomeViewModel(private val placeRepository: PlaceRepository) : ViewModel() {

    private val compositDisposabel = CompositeDisposable()

    private var places: MutableLiveData<List<Place>>? = null

    fun getPlaces(): LiveData<List<Place>> {
        if (places == null) {
            places = MutableLiveData()
            loadPlaces()
        }
        return places as MutableLiveData<List<Place>>
    }

    fun loadPlaces() {
        compositDisposabel.add(
                // TODO use correct user id
                placeRepository.getVisitedPlaces("5a3835952abb591b0b1fd69b")
                        .distinctUntilChanged()
                        .subscribe({
                            if (it.isNotEmpty()) {
                                places?.value = it.sortedByDescending { it.lastScanned }
                            }
                        }, {
                            Log.e("sync", "$it")
                        })
        )
    }

    override fun onCleared() {
        super.onCleared()

        compositDisposabel.dispose()
    }
}