package com.android.quo.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.android.quo.db.entity.Place
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
                // TODO use getVisitedPlaces with user ID when implemented https://app.clickup.com/751518/751948/t/w5hu
                placeRepository.getAllPlaces()
                        .distinctUntilChanged()
                        .subscribe({
                            if (it.isNotEmpty()) {
                                places?.value = it
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