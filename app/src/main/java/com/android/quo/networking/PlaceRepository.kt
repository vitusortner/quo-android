package com.android.quo.networking

import android.util.Log
import com.android.quo.QuoApplication
import com.android.quo.data.Place
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Single

/**
 * Created by vitusortner on 24.11.17.
 */
class PlaceRepository {

    init {
        // TODO move to viewmodel
        getPlaces().subscribe {
            Log.i("data", "$it")
        }
    }

    fun getPlaces(): Flowable<List<Place>> {
        val placeDao = QuoApplication.database.placeDao()

        return Flowable.create({ emitter ->
            object : NetworkBoundSource<List<Place>, List<Place>>(emitter) {

                override fun getRemote(): Single<List<Place>> = PlaceService.instance.getPlaces()

                override fun getLocal(): Flowable<List<Place>> = placeDao.getAllPlaces()

                override fun saveCallResult(data: List<Place>) {
                    data.forEach {
                        placeDao.insertPlace(it)
                    }
                }

            }
        }, BackpressureStrategy.BUFFER)
    }
}