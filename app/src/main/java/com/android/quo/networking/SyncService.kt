package com.android.quo.networking

import android.util.Log
import com.android.quo.db.AppDatabase
import com.android.quo.db.entity.Address
import com.android.quo.db.entity.Component
import com.android.quo.db.entity.Picture
import com.android.quo.db.entity.Place
import com.android.quo.db.entity.User
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
        val places = data.map { place ->
            Place(
                    id = place.id ?: "",
                    isHost = isHost,
                    title = place.title,
                    startDate = place.startDate,
                    endDate = place.endDate,
                    latitude = place.latitude,
                    longitude = place.longitude,
                    address = place.address?.let {
                        Address(
                                street = place.address.street,
                                city = place.address.city,
                                zipCode = place.address.zipCode)
                    },
                    isPhotoUploadAllowed = place.settings?.isPhotoUploadAllowed,
                    hasToValidateGps = place.settings?.hasToValidateGps,
                    titlePicture = place.titlePicture ?: "",
                    qrCodeId = place.qrCodeId ?: ""
            )
        }
        database.placeDao().deletePlaces(isHost)
        database.placeDao().insertAllPlaces(places)
        Log.i("sync", "place sync success!")

        saveComponents(data)
    }

    private fun saveComponents(data: List<ServerPlace>) {
        val components = mutableListOf<Component>()
        data.forEach { place ->
            place.components?.let {
                it.forEach {
                    components.add(
                            Component(
                                    id = it.id ?: "",
                                    picture = it.picture,
                                    text = it.text,
                                    placeId = place.id ?: "",
                                    position = it.position ?: 0
                            ))
                }
            }
            place.id?.let {
                database.componentDao().deleteComponentsFromPlace(it)
            }
        }
        if (components.isNotEmpty()) {
            database.componentDao().insertAllComponents(components)
        }
        Log.i("sync", "component sync success!")
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
                        id = picture.id ?: "",
                        ownerId = picture.ownerId,
                        placeId = picture.placeId,
                        src = picture.src,
                        isVisible = picture.isVisible,
                        timestamp = picture.timestamp
                )
            }
            // delete pictures of given place
            database.pictureDao().deletePicturesOfPlace(data[0].placeId)
            database.pictureDao().insertAllPictures(pictures)
        }
        Log.i("sync", "picture sync success!")
    }
}