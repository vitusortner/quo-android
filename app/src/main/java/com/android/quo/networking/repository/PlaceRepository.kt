package com.android.quo.networking.repository

import com.android.quo.db.dao.PlaceDao
import com.android.quo.db.entity.Place
import com.android.quo.networking.ApiService
import com.android.quo.networking.SyncService
import com.android.quo.networking.model.ServerPlace
import com.android.quo.networking.model.ServerPlaceResponse
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Single

/**
 * Created by vitusortner on 24.11.17.
 */
class PlaceRepository(
        private val placeDao: PlaceDao,
        private val apiService: ApiService,
        private val syncService: SyncService
) {

    fun getHostedPlaces(userId: String): Flowable<List<Place>> {
        return Flowable.create({ emitter ->
            object : Repository<List<Place>, List<ServerPlace>>(emitter) {

                override fun getRemote(): Single<List<ServerPlace>> = apiService.getHostedPlaces(userId)

                override fun getLocal(): Flowable<List<Place>> = placeDao.getPlaces(true)

                override fun sync(data: List<ServerPlace>) {
                    syncService.saveHostedPlaces(data)
                }
            }
        }, BackpressureStrategy.BUFFER)
    }

    fun getVisitedPlaces(userId: String): Flowable<List<Place>> {
        return Flowable.create({ emitter ->
            object : Repository<List<Place>, List<ServerPlaceResponse>>(emitter) {

                override fun getRemote(): Single<List<ServerPlaceResponse>> = apiService.getVisitedPlaces(userId)

                override fun getLocal(): Flowable<List<Place>> = placeDao.getPlaces(false)

                override fun sync(data: List<ServerPlaceResponse>) {
                    syncService.saveVisitedPlaces(data)
                }
            }
        }, BackpressureStrategy.BUFFER)
    }

    fun getPlace(qrCodeId: String): Flowable<Place> {
        return Flowable.create({ emitter ->
            object : Repository<Place, ServerPlace>(emitter) {

                override fun getRemote(): Single<ServerPlace> = apiService.getPlace(qrCodeId)

                override fun getLocal(): Flowable<Place> = placeDao.getPlaceByQrCodeId(qrCodeId)

                override fun sync(data: ServerPlace) = syncService.savePlace(data)
            }
        }, BackpressureStrategy.BUFFER)
    }
}