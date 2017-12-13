package com.android.quo.networking

import android.util.Log
import com.android.quo.db.AppDatabase
import com.android.quo.db.entity.Address
import com.android.quo.db.entity.Component
import com.android.quo.db.entity.Picture
import com.android.quo.db.entity.Place
import com.android.quo.db.entity.User
import com.android.quo.networking.model.ServerComponent
import com.android.quo.networking.model.ServerPicture
import com.android.quo.networking.model.ServerPlace
import com.android.quo.networking.model.ServerUser

/**
 * Created by vitusortner on 30.11.17.
 */
class SyncService(private val database: AppDatabase) {

    fun saveMyPlaces(data: List<ServerPlace>) {
        savePlaces(data, true)
    }

    fun saveVisitedPlaces(data: List<ServerPlace>) {
        savePlaces(data, false)
    }

    fun savePlace(data: ServerPlace) {
        // TODO set isHost correctly
        val place = mapToPlace(data, false)
        database.placeDao().deletePlace(place)
        // TODO insert to correct place https://app.clickup.com/751518/751948/t/w5h2
        database.placeDao().insertPlace(place)

        Log.i("sync", "place sync success! $place")
    }

    private fun savePlaces(data: List<ServerPlace>, isHost: Boolean) {
        if (data.isNotEmpty()) {
            val places = data.map { serverPlace ->
                mapToPlace(serverPlace, isHost)
            }
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

    fun saveUser(data: ServerUser) {
        // TODO write token to key chain
        val user = User(data.id)
        database.userDao().insertUser(user)
    }

    private fun mapToPlace(serverPlace: ServerPlace, isHost: Boolean): Place {
        return Place(
                id = serverPlace.id ?: "",
                isHost = isHost,
                title = serverPlace.title,
                startDate = serverPlace.startDate,
                endDate = serverPlace.endDate,
                latitude = serverPlace.latitude,
                longitude = serverPlace.longitude,
                address = serverPlace.address?.let { address ->
                    Address(
                            street = address.street,
                            city = address.city,
                            zipCode = address.zipCode)
                },
                isPhotoUploadAllowed = serverPlace.settings?.isPhotoUploadAllowed,
                hasToValidateGps = serverPlace.settings?.hasToValidateGps,
                titlePicture = serverPlace.titlePicture ?: "",
                qrCodeId = serverPlace.qrCodeId ?: ""
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
                id = serverPicture.id,
                ownerId = serverPicture.ownerId,
                placeId = serverPicture.placeId,
                src = serverPicture.src,
                isVisible = serverPicture.isVisible,
                timestamp = serverPicture.timestamp
        )
    }
}