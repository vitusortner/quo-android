package com.android.quo.networking.mapper

import com.android.quo.QuoApplication
import com.android.quo.db.entity.Address
import com.android.quo.db.entity.Component
import com.android.quo.db.entity.Place
import com.android.quo.db.entity.User
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
//        val places = data.visitedPlaces.map {
//            Place(
//                    id = it.id,
//                    isHost = userDao.findUserById(it.host) != null,
//                    title = it.title,
//                    startDate = Date(),
//                    endDate = Date(),
//                    latitude = it.latitude,
//                    longitude = it.longitude,
//                    address = Address(
//                            street = it.address.street,
//                            city = it.address.city,
//                            zipCode = it.address.zipCode
//                    ),
//                    isPhotoUploadAllowed = it.settings.isPhotoUploadAllowed,
//                    hasToValidateGps = it.settings.hasToValidateGps,
//                    titlePicture = it.titlePicture,
//                    qrCodeId = it.qrCodeId
//            )
//        }
//        placeDao.insertAllPlaces(places)

        // TODO write token to key chain

        val user = User(data.id)
        userDao.insertUser(user)
    }
}