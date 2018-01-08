package com.android.quo.viewmodel

import android.arch.lifecycle.ViewModel
import android.util.Log
import com.android.quo.network.model.ServerPicture
import com.android.quo.service.ApiService
import com.android.quo.service.UploadService
import io.reactivex.schedulers.Schedulers
import java.io.File

/**
 * Created by vitusortner on 08.01.18.
 */
class PlaceViewModel(
        private val uploadService: UploadService,
        private val apiService: ApiService
) : ViewModel() {

    private val TAG = javaClass.simpleName

    fun uploadImage(image: File, placeId: String) {
        uploadService.uploadImage(image) {
            it?.let { image ->
                val picture = ServerPicture(
                        // TODO get real id
                        ownerId = "5a2aac590b0132796939a3f6",
                        placeId = placeId,
                        src = image.path,
                        isVisible = true,
                        // TODO server sets timestamp
                        // make timestamp nullable
                        timestamp = ""
                )

                apiService.addPicture(placeId, picture)
                        .subscribeOn(Schedulers.io())
                        .subscribe({
                            Log.i(TAG, "Picture added: $it")
                        }, {
                            Log.e(TAG, "Error while adding picture: $it")
                        })
            }
        }
    }
}