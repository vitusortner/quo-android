package com.android.quo.viewmodel

import android.arch.lifecycle.ViewModel
import android.util.Log
import com.android.quo.networking.ApiService
import com.android.quo.networking.model.ServerPicture
import com.android.quo.view.myplaces.createplace.CreatePlace
import io.reactivex.schedulers.Schedulers
import java.sql.Timestamp

/**
 * Created by Jung on 11.12.17.
 */

class CreatePlaceViewModel : ViewModel() {
    private val apiService = ApiService.instance


    fun savePlace() {
        CreatePlace.place.host = "10"
        apiService.addPlace(CreatePlace.place)
                .subscribeOn(Schedulers.io())
                .subscribe({
                    uploadImage()
                }, {
                    Log.e("sync", "$it")
                })
    }

    //TODO use ownerid and use place id instead of qr code id
    private fun uploadImage() {
        CreatePlace.place.titlePicture?.let { titlePicture ->
            if (titlePicture.length > 2) {
                CreatePlace.place.qrCodeId?.let { qrCodeId ->
                    CreatePlace.pictures.add(ServerPicture("", "10",
                            qrCodeId, titlePicture, true,
                            Timestamp(System.currentTimeMillis()).toString()))
                }
            }

        }

        for (c in CreatePlace.components){
            c.picture?.let { picture ->
                CreatePlace.place.qrCodeId?.let { qrCodeId ->
                    CreatePlace.pictures.add(ServerPicture("", "10",
                            qrCodeId, picture, true,
                            Timestamp(System.currentTimeMillis()).toString()))
                }
            }
        }


        for (img in CreatePlace.pictures) {
            apiService.addPicture(img)
                    .subscribeOn(Schedulers.io())
                    .subscribe({

                    }, {
                        Log.e("sync", "$it")
                    })
        }
    }
}