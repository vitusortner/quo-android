package com.android.quo.viewmodel

import android.arch.lifecycle.ViewModel
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.util.Log
import com.android.quo.network.model.ServerPicture
import com.android.quo.network.model.ServerPlace
import com.android.quo.network.repository.UserRepository
import com.android.quo.service.ApiService
import com.android.quo.view.createplace.CreatePlace
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

class CreatePlaceViewModel(
        private val apiService: ApiService,
        private val userRepository: UserRepository
) : ViewModel() {

    private val TAG = javaClass.simpleName

    fun savePlace() {
        userRepository.getUser {
            it?.let {
                CreatePlace.place.host = it.id

                apiService.addPlace(CreatePlace.place)
                        .subscribeOn(Schedulers.io())
                        .subscribe({
                            Log.i(TAG, "Add place: $it")

                            uploadQrCode(it)
                            uploadImage(it)
                        }, {
                            Log.e(TAG, "Error while adding place: $it")
                        })
            }
        }
    }

    private fun uploadImage(response: ServerPlace) {
        // create ServerPicture for title picture
        val title = ServerPicture(
                id = null,
                ownerId = response.host,
                placeId = response.id ?: "",
                src = "",
                isVisible = true,
                // TODO timestamp should be set by server
                timestamp = Timestamp(System.currentTimeMillis()).toString()
        )

        //check if title picture is from user or default image
        response.titlePicture?.let { titlePicture ->
            if (!titlePicture.startsWith("quo_default_")) {
                title.src = titlePicture

                val uri = Uri.parse(title.src)
                val file = File(uri.path)

                val requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file)
                val imageFileBody = MultipartBody.Part.createFormData("imgUpload", file.name, requestBody)

                // sync title picture to server and add the response answer to the created place
                apiService.uploadImage(imageFileBody)
                        .subscribeOn(Schedulers.io())
                        .subscribe({
                            Log.i(TAG, "Titlepicture uploaded: $it")
                            response.titlePicture = it.path

                            apiService.updatePlace(response.id ?: "", response)
                                    .subscribeOn(Schedulers.io())
                                    .subscribe({
                                        Log.i(TAG, "Place updated: $it")
                                        uploadComponents(it)
                                    }, {
                                        Log.e(TAG, "Error while updating place: $it")
                                    })

                        }, {
                            Log.e(TAG, "Error while uploading image: $it")
                        })

            } else {
                // get default picture from server and set it as title picture source
                apiService.getDefaultPicture(titlePicture)
                        .subscribeOn(Schedulers.io())
                        .subscribe({
                            Log.i(TAG, "Default picture: $it")

                            response.titlePicture = it.path

                            apiService.updatePlace(response.id ?: "", response)
                                    .subscribeOn(Schedulers.io())
                                    .subscribe({
                                        Log.i(TAG, "Place updated: $it")

                                        uploadComponents(it)
                                    }, {
                                        Log.e(TAG, "Error while saving place: $it")
                                    })

                        }, {
                            Log.e(TAG, "Error while getting default image: $it")
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

                apiService.uploadImage(imageFileBody)
                        .subscribeOn(Schedulers.io())
                        .subscribe({
                            Log.i(TAG, "Picture uploaded: $it")

                            val picture = ServerPicture(
                                    id = null,
                                    ownerId = response.host,
                                    placeId = response.id ?: "",
                                    src = it.path,
                                    isVisible = true,
                                    // TODO should get set by server
                                    timestamp = System.currentTimeMillis().toString()
                            )

                            apiService.addPicture(response.id ?: "", picture)
                                    .subscribeOn(Schedulers.io())
                                    .subscribe({
                                        Log.i(TAG, "Picture added: $it")

                                        c.picture = it.src

                                        //post component to server
                                        apiService.addComponent(it.placeId, c)
                                                .subscribeOn(Schedulers.io())
                                                .subscribe({
                                                    Log.i(TAG, "Component uploaded: $it")
                                                }, {
                                                    Log.e(TAG, "Error while uploading component: $it")
                                                })
                                    }, {
                                        Log.e(TAG, "Error while adding picture: $it")
                                    })
                        }, {
                            Log.e(TAG, "Error while uploading picture: $it")
                        })
            } else if (c.text != null) {
                //post component with text to server
                apiService.addComponent(response.id ?: "", c)
                        .subscribeOn(Schedulers.io())
                        .subscribe({
                            Log.i(TAG, "Component added: $it")
                        }, {
                            Log.e(TAG, "Error while adding component: $it")
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
        apiService.uploadImage(imageFileBody)
                .subscribeOn(Schedulers.io())
                .subscribe({
                    Log.i(TAG, "Picture uploaded: $it")

                    response.qrCode = it.path

                    apiService.updatePlace(response.id ?: "", response)
                            .subscribeOn(Schedulers.io())
                            .subscribe({
                                Log.i(TAG, "Place updated: $it")
                            }, {
                                Log.e(TAG, "Error while updating place: $it")
                            })
                }, {
                    Log.e(TAG, "Error while uploading picture: $it")
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

