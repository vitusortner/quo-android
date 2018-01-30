package com.android.quo.viewmodel

import android.arch.lifecycle.ViewModel
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import com.android.quo.db.entity.User
import com.android.quo.network.model.ServerPicture
import com.android.quo.network.model.ServerPlace
import com.android.quo.repository.ComponentRepository
import com.android.quo.repository.PictureRepository
import com.android.quo.repository.PlaceRepository
import com.android.quo.repository.UserRepository
import com.android.quo.service.UploadService
import com.android.quo.util.Constants
import com.android.quo.util.CreatePlace
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

/**
 * Created by Jung on 11.12.17.
 */
class CreatePlaceViewModel(
        private val componentRepository: ComponentRepository,
        private val pictureRepository: PictureRepository,
        private val placeRepository: PlaceRepository,
        private val userRepository: UserRepository,
        private val uploadService: UploadService
) : ViewModel() {

    fun savePlace() {
        userRepository.getUser {
            it?.let {
                CreatePlace.place.host = it.id

                placeRepository.addPlace(CreatePlace.place) {
                    it?.let { place ->
                        uploadQrCode(place)
                        uploadImage(place)
                    }
                }
            }
        }
    }

    private fun uploadImage(place: ServerPlace) {
        // create ServerPicture for title picture
        val picture = ServerPicture(
                id = null,
                ownerId = place.host,
                placeId = place.id ?: "",
                src = "",
                isVisible = true
        )

        //check if title picture is from user or default image
        place.titlePicture?.let { titlePicture ->
            if (!titlePicture.startsWith("quo_default_")) {
                picture.src = titlePicture

                val uri = Uri.parse(picture.src)
                val image = File(uri.path)

                uploadService.uploadImage(image) {
                    it?.let { image ->
                        place.titlePicture = image.path

                        place.id?.let { placeId ->
                            placeRepository.updatePlace(placeId, place) {
                                it?.let {
                                    uploadComponents(it)
                                }
                            }
                        }
                    }
                }
            } else {
                // get default picture from server and set it as title picture source
                pictureRepository.getDefaultPicture(titlePicture) {
                    it?.let {
                        place.titlePicture = it.path

                        place.id?.let { placeId ->
                            placeRepository.updatePlace(placeId, place) {
                                it?.let {
                                    uploadComponents(it)
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    private fun uploadComponents(place: ServerPlace) {
        // sync all images from components and add the place to the right component
        for (component in CreatePlace.components) {
            if (component.picture != null) {
                val uri = Uri.parse(component.picture)
                val image = File(uri.path)

                uploadService.uploadImage(image) {
                    it?.let {
                        val picture = ServerPicture(
                                id = null,
                                ownerId = place.host,
                                placeId = place.id ?: "",
                                src = it.path,
                                isVisible = true
                        )

                        place.id?.let { placeId ->
                            pictureRepository.addPicture(placeId, picture) {
                                it?.let { picture ->
                                    component.picture = picture.src

                                    componentRepository.addComponent(picture.placeId, component)
                                }
                            }


                        }
                    }
                }
            } else if (component.text != null) {
                //post component with text to server
                place.id?.let { placeId ->
                    componentRepository.addComponent(placeId, component)
                }
            }
        }
    }

    private fun uploadQrCode(place: ServerPlace) {
        val uri = Uri.parse(saveQrCode(CreatePlace.qrCodeImage, place.qrCodeId))
        val image = File(uri.path)

        uploadService.uploadImage(image) {
            it?.let {
                place.qrCode = it.path

                place.id?.let { placeId ->
                    placeRepository.updatePlace(placeId, place)
                }
            }
        }
    }

    private fun saveQrCode(bitmap: Bitmap, qrCodeId: String?): String {
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 40, bytes)

        val path = File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES).absolutePath + Constants.IMAGE_DIR)
        val file = File(path, "$qrCodeId.jpg")
        path.mkdirs()
        file.createNewFile()
        val fo = FileOutputStream(file)
        fo.write(bytes.toByteArray())
        fo.close()
        return file.path
    }

    fun getUser(completionHandler: (User?) -> Unit) {
        userRepository.getUser(completionHandler)
    }
}

