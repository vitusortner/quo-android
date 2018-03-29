package com.android.quo.viewmodel

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
            .map {
                ServerPicture(
                    ownerId = it.id,
                    placeId = placeId,
                    src = image.path,
                    isVisible = true
                )
            }
            .flatMap { pictureRepository.addPicture(placeId, it) }
            .observeOnUi()
            .subscribe()
            .addTo(compositeDisposable)
}