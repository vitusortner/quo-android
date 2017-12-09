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

    private fun savePlaces(data: List<ServerPlace>, isHost: Boolean) {
        if (data.isNotEmpty()) {
            val places = data.map { place ->
                Place(
                        id = place.id ?: "",
                        isHost = isHost,
                        title = place.title,
                        startDate = place.startDate,
                        endDate = place.endDate,
                        latitude = place.latitude,
                        longitude = place.longitude,
                        address = place.address?.let { address ->
                            Address(
                                    street = address.street,
                                    city = address.city,
                                    zipCode = address.zipCode)
                        },
                        isPhotoUploadAllowed = place.settings?.isPhotoUploadAllowed,
                        hasToValidateGps = place.settings?.hasToValidateGps,
                        titlePicture = place.titlePicture ?: "",
                        qrCodeId = place.qrCodeId ?: ""
                )
            }
            database.placeDao().deletePlaces(isHost)
            database.placeDao().insertAllPlaces(places.reversed())
            Log.i("sync", "place sync success! new items: $places")
        }
        Log.i("sync", "noting to sync!")
    }

    fun saveComponents(data: List<ServerComponent>, placeId: String) {
        if (data.isNotEmpty()) {
            val components = data.map { component ->
                Component(
                        id = component.id ?: "",
                        picture = component.picture,
                        text = component.text,
                        placeId = placeId
                )
            }
            database.componentDao().deleteComponentsFromPlace(placeId)
            database.componentDao().insertAllComponents(components.reversed())
            Log.i("sync", "component sync success! new items: $components")
        }
        Log.i("sync", "nothing to sync!")
    }

    fun saveUser(data: ServerUser) {
        // TODO write token to key chain
        val user = User(data.id)
        database.userDao().insertUser(user)
    }

    fun savePictures(data: List<ServerPicture>) {
        if (data.isNotEmpty()) {
            val pictures = data.map { picture ->
                Picture(
                        id = picture.id,
                        ownerId = picture.ownerId,
                        placeId = picture.placeId,
                        src = picture.src,
                        isVisible = picture.isVisible,
                        timestamp = picture.timestamp
                )
            }
            // delete pictures of given place
            database.pictureDao().deletePicturesOfPlace(data[0].placeId)
            database.pictureDao().insertAllPictures(pictures.reversed())
            Log.i("sync", "picture sync success! new items: $pictures")
        }
        Log.i("sync", "nothing to sync!")
    }
}