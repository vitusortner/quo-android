package com.android.quo.service

import android.util.Log
import com.android.quo.network.model.ServerUploadImage
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

/**
 * Created by vitusortner on 08.01.18.
 */
class UploadService(private val apiService: ApiService) {

    private val TAG = javaClass.simpleName

    fun uploadImage(image: File, completionHandler: (ServerUploadImage?) -> Unit) {
        val requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), image)
        val imageFileBody = MultipartBody.Part.createFormData("imgUpload", image.name, requestBody)

        apiService.uploadImage(imageFileBody)
                .subscribeOn(Schedulers.io())
                .subscribe({
                    Log.i(TAG, "Image uploaded: $it")
                    completionHandler(it)
                }, {
                    Log.e(TAG, "Error while uploading image: $it")
                    completionHandler(null)
                })
    }
}