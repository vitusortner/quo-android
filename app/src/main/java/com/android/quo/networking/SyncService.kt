package com.android.quo.networking

import com.android.quo.QuoApplication
import com.android.quo.db.entity.Address
import com.android.quo.db.entity.Component
import com.android.quo.db.entity.Picture
import com.android.quo.db.entity.Place
import com.android.quo.db.entity.User
import com.android.quo.db.entity.UserPlaceJoin
import com.android.quo.networking.model.ServerPicture
import com.android.quo.networking.model.ServerPlace
import com.android.quo.networking.model.ServerUser
import java.util.*

/**
 * Created by vitusortner on 30.11.17.
 */
object SyncService {

    private val userDao = QuoApplication.database.userDao()
    private val placeDao = QuoApplication.database.placeDao()
    private val componentDao = QuoApplication.database.componentDao()
    private val userPlaceJoinDao = QuoApplication.database.userPlaceJoinDao()

    fun savePlaces(data: List<ServerPlace>) {
        val places = data.map { place ->
            Place(
                    id = place.id,
                    isHost = userDao.findUserById(place.host) != null,
                    title = place.title,
                    // TODO to date required?
                    startDate = Date(),
                    endDate = Date(),
                    latitude = place.latitude,
                    longitude = place.longitude,
                    address = Address(
                            street = place.address.street,
                            city = place.address.city,
                            zipCode = place.address.zipCode
                    ),
                    isPhotoUploadAllowed = place.settings.isPhotoUploadAllowed,
                    hasToValidateGps = place.settings.hasToValidateGps,
                    titlePicture = place.titlePicture,
                    qrCodeId = place.qrCodeId
            )
        }
        placeDao.insertAllPlaces(places)

        val components = data.flatMap { place ->
            place.components.map { component ->
                Component(
                        id = component.id,
                        picture = component.picture,
                        text = component.text,
                        placeId = place.id,
                        position = component.position
                )
            }

        }
        componentDao.insertAllComponents(components)
    }

    fun saveUser(data: ServerUser) {
        data.visitedPlaces?.let {
            val places = it.map { place ->
                Place(
                        id = place.id,
                        isHost = userDao.findUserById(place.host) != null,
                        title = place.title,
                        startDate = Date(),
                        endDate = Date(),
                        latitude = place.latitude,
                        longitude = place.longitude,
                        address = Address(
                                street = place.address.street,
                                city = place.address.city,
                                zipCode = place.address.zipCode
                        ),
                        isPhotoUploadAllowed = place.settings.isPhotoUploadAllowed,
                        hasToValidateGps = place.settings.hasToValidateGps,
                        titlePicture = place.titlePicture,
                        qrCodeId = place.qrCodeId
                )
            }
            placeDao.insertAllPlaces(places)

            val userPlaceJoins = it.map { place ->
                UserPlaceJoin(
                        userId = data.id,
                        placeId = place.id,
                        // TODO timestamp/date
                        timestamp = Date()
                )
            }
            userPlaceJoinDao.insertAllUserPlaceJoins(userPlaceJoins)
        }
        // TODO write token to key chain
        val user = User(data.id)
        userDao.insertUser(user)
    }

    fun savePictures(data: List<ServerPicture>) {
        val pictures = data.map { picture ->
            Picture(
                    id = picture.id,
                    ownerId = picture.ownerId,
                    placeId = picture.placeId,
                    src = picture.src,
                    isVisible = picture.isVisible,
                    // TODO timestamp/date
                    timestamp = Date()
            )
        }
        QuoApplication.database.pictureDao().insertAllPictures(pictures)
    }


}