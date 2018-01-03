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

class CreatePlaceViewModel : ViewModel() {
    private val apiService = ApiService.instance

    // TODO change host id
    fun savePlace() {
        Log.e("save place", "*****")
//        CreatePlace.place.host = "5a4788298608db84ae2d86a9" //local
        CreatePlace.place.host = "5a2aac590b0132796939a3f6" //aws

        apiService.addPlace(CreatePlace.place)
                .subscribeOn(Schedulers.io())
                .subscribe({
                    Log.e("post place", "$it")
                    val response = it as ServerPlace
                    uploadQrCode(response)
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
                            Log.e("upload picture", "$it")
                            response.titlePicture = it.path

                            putPlace(response)

                        }, {
                            Log.e("upload picture", "$it")
                        })

            } else {
                // get default picture from server and set it as title picture source
                apiService.getDefaultPicture(titlePicture)
                        .subscribeOn(Schedulers.io())
                        .subscribe({
                            Log.e("get default picture", "$it")
                            response.titlePicture = it.path
                            putPlace(response)

                        }, {
                            Log.e("get default picture", "$it")
                        })
            }
        }
    }

    private fun putPlace(response: ServerPlace) {
        apiService.putPlace(response.id ?: "", response)
                .subscribeOn(Schedulers.io())
                .subscribe({
                    Log.e("put place", "$it")
                    uploadComponents(it)
                }, {
                    Log.e("put place", "$it")
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
                                    response.id ?: "", it.path,
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
                        }, {
                            Log.e("post component", "$it")
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
                    Log.e("upload qr code", "$it")
                    response.qrCode = it.path
                    putPlace(response)
                }, {
                    Log.e("upload qr code", "$it")
                })
    }

    private fun saveQrCode(bitmap: Bitmap, qrCodeId: String?): String {
        Log.e("save qr code", "*******")


        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 40, bytes)

        val file = File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES).absolutePath + "/Quo/$qrCodeId.jpg")
        file.createNewFile()
        val fo = FileOutputStream(file)
        fo.write(bytes.toByteArray())
        fo.close()

        Log.e("save qr code", file.path)
        return file.path
    }
}

