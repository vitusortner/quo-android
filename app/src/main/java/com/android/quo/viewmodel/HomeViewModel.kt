package com.android.quo.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.android.quo.db.dao.UserDao
import com.android.quo.db.entity.Place
import com.android.quo.extension.toDate
import com.android.quo.network.repository.PlaceRepository
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

/**
 * Created by vitusortner on 06.12.17.
 */
class HomeViewModel(
        private val placeRepository: PlaceRepository,
        private val userDao: UserDao
) : ViewModel() {

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
        compositDisposabel.add(userDao.getUser()
                .observeOn(Schedulers.io())
                .subscribe {
                    placeRepository.getVisitedPlaces(it.id)
                            .distinctUntilChanged()
                            .subscribe({
                                if (it.isNotEmpty()) {
                                    places?.value = it.sortedByDescending { it.lastScanned.toDate() }
                                }
                            }, {
                                Log.e("sync", "$it")
                            })
                }
        )
    }

    override fun onCleared() {
        super.onCleared()

        compositDisposabel.dispose()
    }
}