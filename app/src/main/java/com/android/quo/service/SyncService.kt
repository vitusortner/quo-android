package com.android.quo.service

import android.annotation.SuppressLint
import com.android.quo.db.dao.ComponentDao
import com.android.quo.db.dao.PictureDao
import com.android.quo.db.dao.PlaceDao
import com.android.quo.db.entity.Address
import com.android.quo.db.entity.Component
import com.android.quo.db.entity.Picture
import com.android.quo.db.entity.Place
import com.android.quo.network.model.ServerComponent
import com.android.quo.network.model.ServerPicture
import com.android.quo.network.model.ServerPlace
import com.android.quo.network.model.ServerPlaceResponse
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by vitusortner on 30.11.17.
 */
class SyncService(
    private val placeDao: PlaceDao,
    private val componentDao: ComponentDao,
    private val pictureDao: PictureDao
) :
    BaseService() {

    fun saveHostedPlaces(data: List<ServerPlace>) =
        savePlaces(data, true) { toPlace(it, true) }

    fun saveVisitedPlaces(data: List<ServerPlaceResponse>) =
        savePlaces(data, false) { toPlace(it.place, false, it.timestamp) }

    @SuppressLint("SimpleDateFormat")
    fun savePlace(data: ServerPlace, userId: String) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
        val date = dateFormat.format(Date())

        val place = toPlace(data, userId == data.host, date)
        placeDao.deletePlace(place)
        placeDao.insertPlace(place)

        log.i("Place sync success!")
    }

    private fun <T> savePlaces(data: List<T>, isHost: Boolean, mapper: (T) -> Place) {
        if (data.isNotEmpty()) {
            val places = data.map(mapper)
            // delete places before inserting updated places
            placeDao.deletePlaces(isHost)
            placeDao.insertAllPlaces(places)

            log.i("Place sync success! ${places.size} places")
        } else {
            log.i("No places to sync!")
        }
    }

    fun saveComponents(data: List<ServerComponent>, placeId: String) {
        if (data.isNotEmpty()) {
            val components = data.map { component ->
                toComponent(component, placeId)
            }
            // delete components of place before inserting updated comonents
            componentDao.deleteComponentsOfPlace(placeId)
            componentDao.insertAllComponents(components)

            log.i("Component sync success! ${components.size} components")
        } else {
            log.i("No components to sync!")
        }
    }

    fun savePictures(data: List<ServerPicture>, placeId: String) {
        if (data.isNotEmpty()) {
            val pictures = data.map(::toPicture)
            // delete pictures of given place before inserting updated pictures
            pictureDao.deletePicturesOfPlace(placeId)
            pictureDao.insertAllPictures(pictures)

            log.i("Picture sync success! ${pictures.size} pictures")
        } else {
            log.i("No pictures to sync!")
        }
    }

    private fun toPlace(serverPlace: ServerPlace, isHost: Boolean, date: String = ""): Place {
        return Place(
            id = serverPlace.id ?: "",
            isHost = isHost,
            description = serverPlace.description ?: "",
            title = serverPlace.title,
            startDate = serverPlace.startDate,
            endDate = serverPlace.endDate,
            latitude = serverPlace.latitude,
            longitude = serverPlace.longitude,
            address = serverPlace.address?.let { address ->
                Address(
                    street = address.street,
                    city = address.city,
                    zipCode = address.zipCode,
                    name = address.name
                )
            },
            isPhotoUploadAllowed = serverPlace.settings?.isPhotoUploadAllowed,
            hasToValidateGps = serverPlace.settings?.hasToValidateGps,
            titlePicture = serverPlace.titlePicture ?: "",
            qrCodeId = serverPlace.qrCodeId ?: "",
            qrCode = serverPlace.qrCode ?: "",
            timestamp = serverPlace.timestamp,
            lastScanned = date
        )
    }

    private fun toComponent(serverComponent: ServerComponent, placeId: String): Component {
        return Component(
            id = serverComponent.id ?: "",
            picture = serverComponent.picture,
            text = serverComponent.text,
            placeId = placeId
        )
    }

    private fun toPicture(serverPicture: ServerPicture): Picture {
        return Picture(
            id = serverPicture.id ?: "",
            ownerId = serverPicture.ownerId,
            placeId = serverPicture.placeId,
            src = serverPicture.src,
            isVisible = serverPicture.isVisible,
            timestamp = serverPicture.timestamp ?: ""
        )
    }
}