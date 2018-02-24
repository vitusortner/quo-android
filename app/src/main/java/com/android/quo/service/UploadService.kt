package com.android.quo.service

import com.android.quo.network.ApiClient
import com.android.quo.network.model.ServerUploadImage
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

/**
 * Created by vitusortner on 08.01.18.
 */
class UploadService(private val apiClient: ApiClient) : BaseService() {

    fun uploadImage(image: File, completionHandler: (ServerUploadImage?) -> Unit) {
        val requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), image)
        val imageFileBody = MultipartBody.Part.createFormData("imgUpload", image.name, requestBody)

        apiClient.uploadImage(imageFileBody)
            .subscribeOn(Schedulers.io())
            .subscribe({
                log.i("Image uploaded: $it")
                completionHandler(it)
            }, {
                log.e("Error while uploading image: $it")
                completionHandler(null)
            })
    }
}