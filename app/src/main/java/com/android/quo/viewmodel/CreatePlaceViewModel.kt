package com.android.quo.viewmodel

import android.arch.lifecycle.ViewModel
import android.util.Log
import com.android.quo.networking.ApiService
import com.android.quo.networking.model.ServerPicture
import com.android.quo.networking.model.ServerPlace
import com.android.quo.view.myplaces.createplace.CreatePlace
import io.reactivex.schedulers.Schedulers
import java.sql.Timestamp

/**
 * Created by Jung on 11.12.17.
 */

class CreatePlaceViewModel : ViewModel() {
    private val apiService = ApiService.instance

    // TODO change host id
    fun savePlace() {
        CreatePlace.place.host = "10"
        apiService.addPlace(CreatePlace.place)
                .subscribeOn(Schedulers.io())
                .subscribe({
                    Log.e("sync", "$it")
                    //uploadImage($it)
                    val response = it as ServerPlace
                    uploadImage(response)


                }, {
                    Log.e("sync", "$it")
                })

    }

    private fun uploadImage(response: ServerPlace) {

        // create ServerPicture for title picture
        var title = ServerPicture(null, response.host,
                response.id ?: "", "", true,
                Timestamp(System.currentTimeMillis()).toString())

        //check if title picture is from user or default image
        response.titlePicture?.let { titlePicture ->
            if (titlePicture.length > 1) {
                title.src = titlePicture

            } else {
                title.src = "default_event_image$titlePicture.png"
            }
        }

        // sync title picture to server and add the response answer to the created place
        apiService.addPicture(title)
                .subscribeOn(Schedulers.io())
                .subscribe({
                    Log.e("sync image", "$it")
                    response.titlePicture = it.src
                }, {
                    Log.e("sync image", "$it")
                })


        // sync all images from components and add the response to the right component
        for (c in CreatePlace.components) {
            if (c.picture != "") {
                val picture = ServerPicture(null, response.host,
                        response.id ?: "", c.picture ?: "",
                        true, System.currentTimeMillis().toString())

                apiService.addPicture(picture)
                        .subscribeOn(Schedulers.io())
                        .subscribe({
                            Log.i("sync image", "$it")
                            c.picture = it.id
                        }, {
                            Log.i("sync image", "$it")
                        })
            }

        }

        // set list of components to the created place
        response.components = CreatePlace.components

        // put created place with the new image id's
        apiService.putPlace(response.id ?: "", response)
                .subscribeOn(Schedulers.io())
                .subscribe({
                    Log.e("put place", "$it")
                }, {
                    Log.e("put place", "$it")
                })
    }

}