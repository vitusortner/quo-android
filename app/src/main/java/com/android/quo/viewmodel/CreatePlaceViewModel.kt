package com.android.quo.viewmodel

import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import com.android.quo.network.model.ServerComponent
import com.android.quo.network.model.ServerPicture
import com.android.quo.network.model.ServerPlace
import com.android.quo.repository.ComponentRepository
import com.android.quo.repository.PictureRepository
import com.android.quo.repository.PlaceRepository
import com.android.quo.repository.UserRepository
import com.android.quo.service.UploadService
import com.android.quo.util.Constants
import com.android.quo.util.CreatePlace
import com.android.quo.util.extension.subscribeOnIo
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

// TODO ??
@Suppress("IMPLICIT_CAST_TO_ANY")
/**
 * Created by Jung on 11.12.17.
 */
class CreatePlaceViewModel(
    private val componentRepository: ComponentRepository,
    private val pictureRepository: PictureRepository,
    private val placeRepository: PlaceRepository,
    private val userRepository: UserRepository,
    private val uploadService: UploadService
) :
    BaseViewModel() {

    fun savePlace(place: ServerPlace) =
        userRepository.getUserSingle()
            .subscribeOnIo()
            .flatMap {
                CreatePlace.place.host = it.id
                placeRepository.addPlace(place)
            }
            .subscribeBy(
                onSuccess = {
                    addQrCode(it)
                    addTitlePicture(it)
                },
                onError = {
                    log.e("Error while saving place", it)
                }
            )

    private fun addTitlePicture(place: ServerPlace) =
        place.titlePicture?.let { titlePicture ->
            place.id?.let { placeId ->

                val picture = ServerPicture(
                    id = null,
                    ownerId = place.host,
                    placeId = place.id ?: "",
                    src = "",
                    isVisible = true
                )

                if (!titlePicture.startsWith("quo_default_")) {
                    picture.src = titlePicture

                    val uri = Uri.parse(picture.src)
                    val image = File(uri.path)

                    uploadCustomTitlePicture(placeId, image, place)
                } else {
                    uploadDefaultTitlePicture(placeId, titlePicture, place)
                }
            }
        }

    private fun uploadCustomTitlePicture(placeId: String, image: File, place: ServerPlace) =
        uploadService.uploadImage(image)
            .subscribeOnIo()
            .flatMap {
                place.titlePicture = it.path
                placeRepository.updatePlace(placeId, place)
            }
            .subscribeBy(
                onSuccess = { addComponents(it) },
                onError = { log.e("Error while uploading custom title picture", it) }
            )
            .addTo(compositeDisposable)

    private fun uploadDefaultTitlePicture(
        placeId: String,
        titlePicture: String,
        place: ServerPlace
    ) =
        pictureRepository.getDefaultPicture(titlePicture)
            .subscribeOnIo()
            .flatMap {
                place.titlePicture = it.path
                placeRepository.updatePlace(placeId, place)
            }
            .subscribeBy(
                onSuccess = { addComponents(it) },
                onError = { log.e("Error while adding default picture", it) }
            )
            .addTo(compositeDisposable)

    private fun addComponents(place: ServerPlace) =
        place.id?.let { placeId ->
            CreatePlace.components.forEach { component ->
                if (component.picture != null) {
                    val uri = Uri.parse(component.picture)
                    val image = File(uri.path)

                    uploadImageComponent(placeId, image, place, component)
                } else if (component.text != null) {
                    uploadTextComponent(placeId, component)
                }
            }
        }

    private fun uploadImageComponent(
        placeId: String,
        image: File,
        place: ServerPlace,
        component: ServerComponent
    ) =
        uploadService.uploadImage(image)
            .subscribeOnIo()
            .map { createServerPicture(place, it.path) }
            .flatMap { serverPicture -> pictureRepository.addPicture(placeId, serverPicture) }
            .flatMap { serverPicture ->
                component.picture = serverPicture.src
                componentRepository.addComponent(serverPicture.placeId, component)
            }
            .subscribeBy(
                onSuccess = { log.i("Success uploading image component $it") },
                onError = { log.e("Error uploading image component", it) }
            )
            .addTo(compositeDisposable)

    private fun uploadTextComponent(placeId: String, component: ServerComponent) =
        componentRepository.addComponent(placeId, component)
            .subscribeOnIo()
            .subscribeBy(
                onSuccess = { log.i("Success uploading text component $it") },
                onError = { log.e("Error uploading text component", it) }
            )
            .addTo(compositeDisposable)

    private fun createServerPicture(place: ServerPlace, imagePath: String) =
        ServerPicture(
            id = null,
            ownerId = place.host,
            placeId = place.id ?: "",
            src = imagePath,
            isVisible = true
        )

    private fun addQrCode(place: ServerPlace) =
        place.id?.let { placeId ->
            val uri = Uri.parse(saveQrCode(CreatePlace.qrCodeImage, place.qrCodeId))
            val image = File(uri.path)

            uploadService.uploadImage(image)
                .subscribeOnIo()
                .flatMap {
                    place.qrCode = it.path
                    placeRepository.updatePlace(placeId, place)
                }
                .subscribeBy(
                    onSuccess = { log.i("QR Code uploaded and added to place $it") },
                    onError = { log.e("Error while uploading and adding QR Code to place", it) }
                )
                .addTo(compositeDisposable)
        }

    // TODO nicer plz
    private fun saveQrCode(bitmap: Bitmap, qrCodeId: String?): String {
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 40, bytes)

        val path = File(
            Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES
            ).absolutePath + Constants.IMAGE_DIR
        )
        val file = File(path, "$qrCodeId.jpg")
        path.mkdirs()
        file.createNewFile()
        val fo = FileOutputStream(file)
        fo.write(bytes.toByteArray())
        fo.close()
        return file.path
    }

    fun getUser() = userRepository.getUser()
}