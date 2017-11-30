package com.android.quo.networking

import android.util.Log
import com.android.quo.db.dao.PlaceDao
import com.android.quo.db.entity.Place
import com.android.quo.networking.mapper.EntityMapper
import com.android.quo.networking.model.ServerPlace
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Single

/**
 * Created by vitusortner on 24.11.17.
 */
class PlaceRepository(private val placeDao: PlaceDao, private val apiService: ApiService) {

    init {
        // TODO move to viewmodel
        getPlaces().subscribe {
            Log.i("data", "$it")
        }
    }

    fun getPlaces(): Flowable<List<Place>> {
        return Flowable.create({ emitter ->
            object : NetworkBoundSource<List<Place>, List<ServerPlace>>(emitter) {

                override fun getRemote(): Single<List<ServerPlace>> = apiService.getPlaces()

                override fun getLocal(): Flowable<List<Place>> = placeDao.getAllPlaces()

                override fun saveCallResult(data: List<Place>) {
                    data.forEach {
                        placeDao.insertPlace(it)
                    }
                }

                override fun mapper(): (List<ServerPlace>) -> List<Place> {
                    return EntityMapper.mapToPlaces()
                }
            }
        }, BackpressureStrategy.BUFFER)
    }
}