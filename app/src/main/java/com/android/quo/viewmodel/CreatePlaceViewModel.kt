package com.android.quo.viewmodel

import android.arch.lifecycle.ViewModel
import android.util.Log
import com.android.quo.networking.ApiService
import com.android.quo.view.myplaces.createplace.CreatePlace
import io.reactivex.schedulers.Schedulers

/**
 * Created by Jung on 11.12.17.
 */

class CreatePlaceViewModel : ViewModel() {
    private val apiService = ApiService.instance


    fun savePlace() {
        apiService.addPlace(CreatePlace.place)
                .subscribeOn(Schedulers.io())
                .subscribe({
                    Log.i("sync", "$it")
                }, {
                    Log.e("sync", "$it")
                })
    }

    fun uploadImage() {
        for (img in CreatePlace.pictures) {
            apiService.addPicture(img)
                    .subscribeOn(Schedulers.io())
                    .subscribe({
                        Log.i("sync", "$it")
                    }, {
                        Log.e("sync", "$it")
                    })
        }
    }
}