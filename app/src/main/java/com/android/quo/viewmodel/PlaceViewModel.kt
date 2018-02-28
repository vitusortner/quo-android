package com.android.quo.viewmodel

import com.android.quo.db.entity.User
import com.android.quo.network.model.ServerPicture
import com.android.quo.repository.PictureRepository
import com.android.quo.repository.UserRepository
import com.android.quo.service.UploadService
import com.android.quo.util.extension.observeOnUi
import com.android.quo.util.extension.subscribeOnIo
import io.reactivex.rxkotlin.addTo
import java.io.File

/**
 * Created by vitusortner on 08.01.18.
 */
class PlaceViewModel(
    private val uploadService: UploadService,
    private val pictureRepository: PictureRepository,
    private val userRepository: UserRepository
) :
    BaseViewModel() {

    fun uploadImage(image: File, placeId: String) =
        uploadService.uploadImage(image)
            .subscribeOnIo()
            .flatMap { userRepository.getUserSingle() }
            .map { createServerPicture(it, placeId, image.path) }
            .flatMap { pictureRepository.addPicture(placeId, it) }
            .observeOnUi()
            .subscribe()
            .addTo(compositeDisposable)

    private fun createServerPicture(user: User, placeId: String, imagePath: String) =
        ServerPicture(
            ownerId = user.id,
            placeId = placeId,
            src = imagePath,
            isVisible = true
        )
}