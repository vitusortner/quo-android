package com.android.quo.networking.mapper

import com.android.quo.QuoApplication
import com.android.quo.db.entity.Address
import com.android.quo.db.entity.Component
import com.android.quo.db.entity.Place
import com.android.quo.db.entity.User
import com.android.quo.networking.model.ServerComponent
import com.android.quo.networking.model.ServerPlace
import com.android.quo.networking.model.ServerUser
import java.util.*

/**
 * Created by vitusortner on 30.11.17.
 */
object EntityMapper {

    private val userDao = QuoApplication.database.userDao()
    private val placeDao = QuoApplication.database.placeDao()

    // serverplace -> place
    fun mapToPlaces(): (List<ServerPlace>) -> List<Place> {
        return {
            it.map {
                Place(
                        id = it.id,
                        isHost = userDao.findUserById(it.host) != null,
                        title = it.title,
                        // TODO to date required?
                        startDate = Date(it.startDate),
                        endDate = Date(it.endDate),
                        latitude = it.latitude,
                        longitude = it.longitude,
                        address = Address(
                                street = it.address.street,
                                city = it.address.city,
                                zipCode = it.address.zipCode
                        ),
                        isPhotoUploadAllowed = it.settings.isPhotoUploadAllowed,
                        hasToValidateGps = it.settings.hasToValidateGps,
                        titlePicture = it.titlePicture,
                        qrCodeId = it.qrCodeId
                )
            }
        }
    }

    fun mapToUser(): (ServerUser) -> User {
        return {
            // TODO insert visited placed of user into DB (userPlaceJoin)
//            it.visitedPlaces
            User(it.id)
        }
    }

    fun mapToComponents(): (List<ServerComponent>) -> List<Component> {
        return {
            it.map {
                Component(
                        id = it.id,
                        picture = it.picture,
                        text = it.text,
                        // TODO
//                        placeId = ,
                        position = it.position
                )
            }
        }
    }
}