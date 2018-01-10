package com.android.quo.network.repository

import android.util.Log
import com.android.quo.db.dao.PictureDao
import com.android.quo.db.entity.Picture
import com.android.quo.service.ApiService
import com.android.quo.network.NetworkBoundResource
import com.android.quo.service.SyncService
import com.android.quo.network.model.ServerPicture
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

/**
 * Created by vitusortner on 05.12.17.
 */
class PictureRepository(
        private val pictureDao: PictureDao,
        private val apiService: ApiService,
        private val syncService: SyncService
) {

    private val TAG = javaClass.simpleName

    fun getPictures(placeId: String): Flowable<List<Picture>> {
        return Flowable.create({ emitter ->
            object : NetworkBoundResource<List<Picture>, List<ServerPicture>>(emitter) {

                override fun getRemote(): Single<List<ServerPicture>> = apiService.getPictures(placeId)

                override fun getLocal(): Flowable<List<Picture>> = pictureDao.getPictures(placeId)

                override fun sync(data: List<ServerPicture>) = syncService.savePictures(data, placeId)
            }
        }, BackpressureStrategy.BUFFER)
    }

    fun addPicture(placeId: String, picture: ServerPicture) {
        apiService.addPicture(placeId, picture)
                .subscribeOn(Schedulers.io())
                .subscribe({
                    Log.i(TAG, "Picture added: $it")
                }, {
                    Log.e(TAG, "Error while adding picture: $it")
                })
    }
}