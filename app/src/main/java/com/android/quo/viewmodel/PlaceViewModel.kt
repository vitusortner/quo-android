package com.android.quo.viewmodel

import android.arch.lifecycle.ViewModel
import com.android.quo.network.model.ServerPicture
import com.android.quo.network.repository.PictureRepository
import com.android.quo.network.repository.UserRepository
import com.android.quo.service.UploadService
import java.io.File

/**
 * Created by vitusortner on 08.01.18.
 */
class PlaceViewModel(
        private val uploadService: UploadService,
        private val pictureRepository: PictureRepository,
        private val userRepository: UserRepository
) : ViewModel() {


    fun uploadImage(image: File, placeId: String) {
        uploadService.uploadImage(image) {
            it?.let { image ->
                userRepository.getUser { user ->
                    user?.let {
                        val picture = ServerPicture(
                                ownerId = user.id,
                                placeId = placeId,
                                src = image.path,
                                isVisible = true,
                                // TODO server sets timestamp
                                // make timestamp nullable
                                timestamp = ""
                        )

                        pictureRepository.addPicture(placeId, picture)
                    }
                }
            }
        }
    }
}