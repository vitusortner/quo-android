package com.android.quo.networking

import com.android.quo.QuoApplication
import com.android.quo.db.entity.Address
import com.android.quo.db.entity.Component
import com.android.quo.db.entity.Picture
import com.android.quo.db.entity.Place
import com.android.quo.db.entity.User
import com.android.quo.networking.model.ServerPicture
import com.android.quo.networking.model.ServerPlace
import com.android.quo.networking.model.ServerUser
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by vitusortner on 30.11.17.
 */
object SyncService {

    fun savePlaces(data: List<ServerPlace>) {
        val places = data.map { place ->
            Place(
                    id = place.id,
                    // TODO has this check to be done on client side? or always send user id with request and check on server?
                    isHost = QuoApplication.database.userDao().getUserById(place.host) != null,
                    title = place.title,
                    // TODO to date function required? (cast from string to date)
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
        // TODO delete all before inserting?
        QuoApplication.database.placeDao().insertAllPlaces(places)

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
        QuoApplication.database.componentDao().insertAllComponents(components)
    }

    fun saveUser(data: ServerUser) {
        // TODO write token to key chain
        val user = User(data.id)
        QuoApplication.database.userDao().insertUser(user)
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
        // TODO delete all before inserting?
        QuoApplication.database.pictureDao().insertAllPictures(pictures)
    }

    // TODO move to extensions class
    private fun String?.toDate(): Date? {
        this?.let {
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
            simpleDateFormat.timeZone = TimeZone.getTimeZone("UTC")
            return simpleDateFormat.parse(it)
        } ?: run {
            return null
        }
    }
}