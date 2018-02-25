package com.android.quo.service

import com.android.quo.network.ApiClient
import com.android.quo.network.model.ServerUploadImage
import com.android.quo.util.Logger
import io.reactivex.Single
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

/**
 * Created by vitusortner on 08.01.18.
 */
class UploadService(private val apiClient: ApiClient) {

    private val log = Logger(javaClass)

    fun uploadImage(image: File): Single<ServerUploadImage> {
        val requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), image)
        val imageFileBody = MultipartBody.Part.createFormData("imgUpload", image.name, requestBody)

        return apiClient.uploadImage(imageFileBody)
    }
}