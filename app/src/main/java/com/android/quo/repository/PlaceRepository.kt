package com.android.quo.repository

import com.android.quo.db.dao.PlaceDao
import com.android.quo.db.entity.Place
import com.android.quo.network.ApiClient
import com.android.quo.network.model.ServerPlace
import com.android.quo.network.model.ServerPlaceResponse
import com.android.quo.service.SyncService
import com.android.quo.util.NetworkBoundResource
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Single

/**
 * Created by vitusortner on 24.11.17.
 */
class PlaceRepository(
    private val placeDao: PlaceDao,
    private val apiClient: ApiClient,
    private val syncService: SyncService
) {

    fun getHostedPlaces(userId: String): Flowable<List<Place>> =
        Flowable.create({ emitter ->
            object : NetworkBoundResource<List<Place>, List<ServerPlace>>(emitter) {

                override fun getRemote(): Single<List<ServerPlace>> =
                    apiClient.getHostedPlaces(userId)

                override fun getLocal(): Flowable<List<Place>> =
                    placeDao.getPlaces(true)

                override fun sync(data: List<ServerPlace>) =
                    syncService.saveHostedPlaces(data)
            }
        }, BackpressureStrategy.BUFFER)

    fun getVisitedPlaces(userId: String): Flowable<List<Place>> =
        Flowable.create({ emitter ->
            object : NetworkBoundResource<List<Place>, List<ServerPlaceResponse>>(emitter) {

                override fun getRemote(): Single<List<ServerPlaceResponse>> =
                    apiClient.getVisitedPlaces(userId)

                override fun getLocal(): Flowable<List<Place>> =
                    placeDao.getPlaces(false)

                override fun sync(data: List<ServerPlaceResponse>) =
                    syncService.saveVisitedPlaces(data)
            }
        }, BackpressureStrategy.BUFFER)

    fun getPlace(qrCodeId: String, userId: String): Flowable<Place> =
        Flowable.create({ emitter ->
            object : NetworkBoundResource<Place, ServerPlace>(emitter) {

                override fun getRemote(): Single<ServerPlace> =
                    apiClient.getPlace(qrCodeId, userId)

                override fun getLocal(): Flowable<Place> =
                    placeDao.getPlaceByQrCodeId(qrCodeId)

                override fun sync(data: ServerPlace) =
                    syncService.savePlace(data, userId)
            }
        }, BackpressureStrategy.BUFFER)

    fun addPlace(place: ServerPlace) = apiClient.addPlace(place)

    fun updatePlace(placeId: String, place: ServerPlace) = apiClient.updatePlace(placeId, place)
}