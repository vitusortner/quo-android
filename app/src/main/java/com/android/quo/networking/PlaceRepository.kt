package com.android.quo.networking

import android.util.Log
import com.android.quo.db.dao.PlaceDao
import com.android.quo.db.entity.Place
import com.android.quo.networking.model.ServerPlace
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

    fun getAllPlaces(): Flowable<List<Place>> {
        return Flowable.create({ emitter ->
            object : Repository<List<Place>, List<ServerPlace>>(emitter) {

                override fun getRemote(): Single<List<ServerPlace>> = apiService.getAllPlaces()

                override fun getLocal(): Flowable<List<Place>> = placeDao.getAllPlaces()

                override fun sync(data: List<ServerPlace>) {
                    syncService.saveVisitedPlaces(data)
                }
            }
        }, BackpressureStrategy.BUFFER)
    }

    fun getMyPlaces(userId: String): Flowable<List<Place>> {
        return Flowable.create({ emitter ->
            object : Repository<List<Place>, List<ServerPlace>>(emitter) {

                override fun getRemote(): Single<List<ServerPlace>> = apiService.getMyPlaces(userId)

                override fun getLocal(): Flowable<List<Place>> = placeDao.getAllPlaces()

                override fun sync(data: List<ServerPlace>) {
                    syncService.saveMyPlaces(data)
                }
            }
        }, BackpressureStrategy.BUFFER)
    }

    fun getVisitedPlaces(userId: String): Flowable<List<Place>> {
        return Flowable.create({ emitter ->
            object : Repository<List<Place>, List<ServerPlace>>(emitter) {

                override fun getRemote(): Single<List<ServerPlace>> = apiService.getVisitedPlaces(userId)

                override fun getLocal(): Flowable<List<Place>> = placeDao.getAllPlaces()

                override fun sync(data: List<ServerPlace>) {
                    syncService.saveVisitedPlaces(data)
                }
            }
        }, BackpressureStrategy.BUFFER)
    }

    fun getPlace(qrCodeId: String): Flowable<List<Place>> {
        return Flowable.create({ emitter ->
            object : Repository<List<Place>, List<ServerPlace>>(emitter) {

                override fun getRemote(): Single<List<ServerPlace>> = apiService.getPlace(qrCodeId)

                override fun getLocal(): Flowable<List<Place>> = placeDao.getAllPlaces()

                override fun sync(data: List<ServerPlace>) {
                    syncService.saveVisitedPlaces(data)
                }
            }
        }, BackpressureStrategy.BUFFER)
    }
}