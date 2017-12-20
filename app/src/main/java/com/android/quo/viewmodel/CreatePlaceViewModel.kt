package com.android.quo.viewmodel

import android.arch.lifecycle.ViewModel
import android.net.Uri
import android.util.Log
import com.android.quo.networking.ApiService
import com.android.quo.networking.model.ServerPicture
import com.android.quo.networking.model.ServerPlace
import com.android.quo.view.myplaces.createplace.CreatePlace
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
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
                    Log.e("post place", "$it")
                    val response = it as ServerPlace
                    uploadImage(response)


                }, {
                    Log.e("post place", "$it")
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
                title.src = "android.resource://com.android.quo/default_event_image$titlePicture.png"
            }
        }

        val uri = Uri.parse(title.src)
        val file = File(uri.path)


        val requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file)
        val imageFileBody = MultipartBody.Part.createFormData("imgUpload", file.name, requestBody)

        // sync title picture to server and add the response answer to the created place
        apiService.uploadPicture(imageFileBody)
                .subscribeOn(Schedulers.io())
                .subscribe({
                    Log.e("upload picture", "$it")
                    response.titlePicture = it.key

                    apiService.putPlace(response.id ?: "", response)
                            .subscribeOn(Schedulers.io())
                            .subscribe({
                                Log.e("put place", "$it")
                                uploadComponents(it)
                            }, {
                                Log.e("put place", "$it")
                            })
                }, {
                    Log.e("upload picture", "$it")
                })
    }

    private fun uploadComponents(response: ServerPlace) {
        // sync all images from components and add the response to the right component
        for (c in CreatePlace.components) {
            if (c.picture != null) {
                val uri = Uri.parse(c.picture)
                val file = File(uri.path)

                val requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file)
                val imageFileBody = MultipartBody.Part.createFormData("imgUpload", file.name, requestBody)

                apiService.uploadPicture(imageFileBody)
                        .subscribeOn(Schedulers.io())
                        .subscribe({
                            Log.e("upload picture", "$it")

                            val picture = ServerPicture(null, response.host,
                                    response.id ?: "", it.key,
                                    true, System.currentTimeMillis().toString())

                            apiService.addPicture(response.id ?: "", picture)
                                    .subscribeOn(Schedulers.io())
                                    .subscribe({
                                        Log.e("add picture", "$it")
                                        c.picture = it.id

                                        //post component to server
                                        apiService.addComponent(it.placeId, c)
                                                .subscribeOn(Schedulers.io())
                                                .subscribe({
                                                    Log.e("post component", "$it")
                                                    // set list of components to the created place
                                                }, {
                                                    Log.e("post component", "$it")
                                                })
                                    }, {
                                        Log.e("add picture", "$it")
                                    })
                        }, {
                            Log.e("upload picture", "$it")
                        })
            } else if (c.text != null) {
                //post component with text to server
                apiService.addComponent(response.id ?: "", c)
                        .subscribeOn(Schedulers.io())
                        .subscribe({
                            Log.e("post component", "$it")
                            // set list of components to the created place
                        }, {
                            Log.e("post component", "$it")
                        })
            }
        }
    }
}

