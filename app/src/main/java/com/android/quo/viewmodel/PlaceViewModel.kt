package com.android.quo.viewmodel

import com.android.quo.db.entity.User
import com.android.quo.network.model.ServerPicture
import com.android.quo.network.model.ServerUploadImage
import com.android.quo.repository.PictureRepository
import com.android.quo.repository.UserRepository
import com.android.quo.service.UploadService
import com.android.quo.util.extension.observeOnUi
import com.android.quo.util.extension.subscribeOnIo
import io.reactivex.Single
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
            .getUser()
            .createServerPicture(image.path, placeId)
            .addPicture(placeId)
            .observeOnUi()
            .subscribe()
            .addTo(compositeDisposable)

    private fun Single<ServerUploadImage>.getUser() =
        this.flatMap { userRepository.getUserSingle() }

    private fun Single<User>.createServerPicture(imagePath: String, placeId: String) =
        this.map {
            ServerPicture(
                ownerId = it.id,
                placeId = placeId,
                src = imagePath,
                isVisible = true
            )
        }

    private fun Single<ServerPicture>.addPicture(placeId: String) =
        this.flatMap { pictureRepository.addPicture(placeId, it) }

}