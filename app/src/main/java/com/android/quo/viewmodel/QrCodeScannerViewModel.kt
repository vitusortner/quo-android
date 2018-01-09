package com.android.quo.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.android.quo.db.dao.UserDao
import com.android.quo.db.entity.Place
import com.android.quo.network.repository.PlaceRepository
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

/**
 * Created by vitusortner on 11.12.17.
 */
class QrCodeScannerViewModel(
        private val placeRepository: PlaceRepository,
        private val userDao: UserDao
) : ViewModel() {

    private val compositDisposabel = CompositeDisposable()

    private var place: MutableLiveData<Place>? = null

    fun getPlace(qrCodeId: String): LiveData<Place> {
        if (place == null) {
            place = MutableLiveData()
            loadPlace(qrCodeId)
        }
        return place as MutableLiveData<Place>
    }

    private fun loadPlace(qrCodeId: String) {
        compositDisposabel.add(userDao.getUser()
                .observeOn(Schedulers.io())
                .subscribe { user ->
                    placeRepository.getPlace(qrCodeId, user.id)
                            .subscribe({
                                place?.value = it
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