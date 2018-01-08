package com.android.quo.viewmodel

import android.arch.lifecycle.ViewModel
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.util.Log
import com.android.quo.networking.ApiService
import com.android.quo.networking.model.ServerPicture
import com.android.quo.networking.model.ServerPlace
import com.android.quo.view.myplaces.createplace.CreatePlace
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.sql.Timestamp


/**
 * Created by Jung on 11.12.17.
 */

class CreatePlaceViewModel(private val apiService: ApiService) : ViewModel() {

    // TODO change host id
    fun savePlace() {
//        CreatePlace.place.host = "5a4788298608db84ae2d86a9" //local
        CreatePlace.place.host = "5a2aac590b0132796939a3f6" //aws

        apiService.addPlace(CreatePlace.place)
                .subscribeOn(Schedulers.io())
                .subscribe({
                    Log.i("save post place", "$it")
                    val response = it as ServerPlace
                    uploadQrCode(response)
                    uploadImage(response)


                }, {
                    Log.e("save post place", "$it")
                })

    }

    private fun uploadImage(response: ServerPlace) {
        // create ServerPicture for title picture
        var title = ServerPicture(null, response.host,
                response.id ?: "", "", true,
                Timestamp(System.currentTimeMillis()).toString())

        //check if title picture is from user or default image
        response.titlePicture?.let { titlePicture ->
            if (!titlePicture.startsWith("quo_default_")) {
                title.src = titlePicture

                val uri = Uri.parse(title.src)
                val file = File(uri.path)


                val requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file)
                val imageFileBody = MultipartBody.Part.createFormData("imgUpload", file.name, requestBody)

                // sync title picture to server and add the response answer to the created place
                apiService.uploadPicture(imageFileBody)
                        .subscribeOn(Schedulers.io())
                        .subscribe({
                            Log.i("save upload picture", "$it")
                            response.titlePicture = it.path

                            apiService.putPlace(response.id ?: "", response)
                                    .subscribeOn(Schedulers.io())
                                    .subscribe({
                                        Log.i("save put place", "$it")
                                        uploadComponents(it)
                                    }, {
                                        Log.e("save put place", "$it")
                                    })

                        }, {
                            Log.e("save upload picture", "$it")
                        })

            } else {
                // get default picture from server and set it as title picture source
                apiService.getDefaultPicture(titlePicture)
                        .subscribeOn(Schedulers.io())
                        .subscribe({
                            Log.i("save get default pic", "$it")
                            response.titlePicture = it.path
                            apiService.putPlace(response.id ?: "", response)
                                    .subscribeOn(Schedulers.io())
                                    .subscribe({
                                        Log.i("save put place", "$it")
                                        uploadComponents(it)
                                    }, {
                                        Log.e("save put place", "$it")
                                    })

                        }, {
                            Log.e("save get default pic", "$it")
                        })
            }
        }
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
                            Log.i("save upload picture com", "$it")

                            val picture = ServerPicture(null, response.host,
                                    response.id ?: "", it.path,
                                    true, System.currentTimeMillis().toString())

                            apiService.addPicture(response.id ?: "", picture)
                                    .subscribeOn(Schedulers.io())
                                    .subscribe({
                                        Log.i("save add picture", "$it")
                                        c.picture = it.src

                                        //post component to server
                                        apiService.addComponent(it.placeId, c)
                                                .subscribeOn(Schedulers.io())
                                                .subscribe({
                                                    Log.i("save post component", "$it")
                                                }, {
                                                    Log.e("save post component", "$it")
                                                })
                                    }, {
                                        Log.e("save add picture", "$it")
                                    })
                        }, {
                            Log.e("save upload picture com", "$it")
                        })
            } else if (c.text != null) {
                //post component with text to server
                apiService.addComponent(response.id ?: "", c)
                        .subscribeOn(Schedulers.io())
                        .subscribe({
                            Log.i("save post component", "$it")
                        }, {
                            Log.e("save post component", "$it")
                        })
            }
        }
    }

    private fun uploadQrCode(response: ServerPlace) {
        val uri = Uri.parse(saveQrCode(CreatePlace.qrCodeImage, response.qrCodeId))
        val file = File(uri.path)

        val requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file)
        val imageFileBody = MultipartBody.Part.createFormData("imgUpload", file.name, requestBody)

        // sync title picture to server and add the response answer to the created place
        apiService.uploadPicture(imageFileBody)
                .subscribeOn(Schedulers.io())
                .subscribe({
                    Log.i("save upload qr code", "$it")
                    response.qrCode = it.path
                    apiService.putPlace(response.id ?: "", response)
                            .subscribeOn(Schedulers.io())
                            .subscribe({
                                Log.i("save put place", "$it")
                            }, {
                                Log.e("save put place", "$it")
                            })
                }, {
                    Log.e("save upload qr code", "$it")
                })
    }

    private fun saveQrCode(bitmap: Bitmap, qrCodeId: String?): String {
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 40, bytes)

        val path = File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES).absolutePath + "/Quo/")
        val file = File(path, "$qrCodeId.jpg")
        path.mkdirs()
        file.createNewFile()
        val fo = FileOutputStream(file)
        fo.write(bytes.toByteArray())
        fo.close()
        return file.path
    }
}

