package com.android.quo.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.util.Log
import com.android.quo.db.entity.Place
import com.android.quo.networking.PlaceRepository
import io.reactivex.android.schedulers.AndroidSchedulers

/**
 * Created by vitusortner on 06.12.17.
 */
class HomeViewModel(private val placeRepository: PlaceRepository) {

    private var places: MutableLiveData<List<Place>>? = null

    fun getPlaces(): LiveData<List<Place>> {
        if (places == null) {
            places = MutableLiveData()
            loadPlaces()
        }
        return places as MutableLiveData<List<Place>>
    }

    private fun loadPlaces() {
        placeRepository.getAllPlaces()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    places?.value = it
                }, {
                    Log.e("sync", it.toString())
                })
    }
}