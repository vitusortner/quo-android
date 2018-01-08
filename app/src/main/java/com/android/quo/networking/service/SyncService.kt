package com.android.quo.networking.service

import android.annotation.SuppressLint
import android.util.Log
import com.android.quo.db.AppDatabase
import com.android.quo.db.entity.Address
import com.android.quo.db.entity.Component
import com.android.quo.db.entity.Picture
import com.android.quo.db.entity.Place
import com.android.quo.networking.model.ServerComponent
import com.android.quo.networking.model.ServerPicture
import com.android.quo.networking.model.ServerPlace
import com.android.quo.networking.model.ServerPlaceResponse
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by vitusortner on 30.11.17.
 */
class SyncService(private val database: AppDatabase) {

    fun saveHostedPlaces(data: List<ServerPlace>) {
        savePlaces(data, true) {
            mapToPlace(it, true)
        }
    }

    fun saveVisitedPlaces(data: List<ServerPlaceResponse>) {
        savePlaces(data, false) {
            mapToPlace(it.place, false, it.timestamp)
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun savePlace(data: ServerPlace, userId: String) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
        val date = dateFormat.format(Date())

        val place = mapToPlace(data, userId == data.host, date)
        database.placeDao().deletePlace(place)
        database.placeDao().insertPlace(place)

        Log.i("sync", "place sync success! $place")
    }

    private fun <T> savePlaces(data: List<T>, isHost: Boolean, mapToPlace: (T) -> Place) {
        if (data.isNotEmpty()) {
            val places = data.map(mapToPlace)
            // delete places before inserting updated places
            database.placeDao().deletePlaces(isHost)
            database.placeDao().insertAllPlaces(places)

            Log.i("sync", "place sync success! $places")
        } else {
            Log.i("sync", "no places to sync!")
        }
    }

    fun saveComponents(data: List<ServerComponent>, placeId: String) {
        if (data.isNotEmpty()) {
            val components = data.map { component ->
                mapToComponent(component, placeId)
            }
            // delete components of place before inserting updated comonents
            database.componentDao().deleteComponentsOfPlace(placeId)
            database.componentDao().insertAllComponents(components)

            Log.i("sync", "component sync success! $components")
        } else {
            Log.i("sync", "no components to sync!")
        }
    }

    fun savePictures(data: List<ServerPicture>, placeId: String) {
        if (data.isNotEmpty()) {
            val pictures = data.map { serverPicture ->
                mapToPicture(serverPicture)
            }
            // delete pictures of given place before inserting updated pictures
            database.pictureDao().deletePicturesOfPlace(placeId)
            database.pictureDao().insertAllPictures(pictures)

            Log.i("sync", "picture sync success! $pictures")
        } else {
            Log.i("sync", "no pictures to sync!")
        }
    }

    private fun mapToPlace(serverPlace: ServerPlace, isHost: Boolean, date: String = ""): Place {
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
                            name = address.name)
                },
                isPhotoUploadAllowed = serverPlace.settings?.isPhotoUploadAllowed,
                hasToValidateGps = serverPlace.settings?.hasToValidateGps,
                titlePicture = serverPlace.titlePicture ?: "",
                qrCodeId = serverPlace.qrCodeId ?: "",
                timestamp = serverPlace.timestamp,
                lastScanned = date
        )
    }

    private fun mapToComponent(serverComponent: ServerComponent, placeId: String): Component {
        return Component(
                id = serverComponent.id ?: "",
                picture = serverComponent.picture,
                text = serverComponent.text,
                placeId = placeId
        )
    }

    private fun mapToPicture(serverPicture: ServerPicture): Picture {
        return Picture(
                id = serverPicture.id ?: "",
                ownerId = serverPicture.ownerId,
                placeId = serverPicture.placeId,
                src = serverPicture.src,
                isVisible = serverPicture.isVisible,
                timestamp = serverPicture.timestamp
        )
    }
}