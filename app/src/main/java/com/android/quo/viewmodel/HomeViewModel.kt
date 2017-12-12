package com.android.quo.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.android.quo.db.entity.Place
import com.android.quo.networking.repository.PlaceRepository

/**
 * Created by vitusortner on 06.12.17.
 */
class HomeViewModel(private val placeRepository: PlaceRepository) : ViewModel() {

    private var places: MutableLiveData<List<Place>>? = null

    fun getPlaces(): LiveData<List<Place>> {
        if (places == null) {
            places = MutableLiveData()
            loadPlaces()
        }
        return places as MutableLiveData<List<Place>>
    }

    fun loadPlaces() {
        placeRepository.getAllPlaces()
                .distinctUntilChanged()
                .subscribe({
                    Log.i("home", "$it")
                    places?.value = it
                }, {
                    Log.e("sync", "$it")
                })
    }
}