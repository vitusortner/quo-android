package com.android.quo.network.repository

import com.android.quo.db.dao.PictureDao
import com.android.quo.db.entity.Picture
import com.android.quo.service.ApiService
import com.android.quo.network.NetworkBoundResource
import com.android.quo.service.SyncService
import com.android.quo.network.model.ServerPicture
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Single

/**
 * Created by vitusortner on 05.12.17.
 */
class PictureRepository(
        private val pictureDao: PictureDao,
        private val apiService: ApiService,
        private val syncService: SyncService
) {

    fun getPictures(placeId: String): Flowable<List<Picture>> {
        return Flowable.create({ emitter ->
            object : NetworkBoundResource<List<Picture>, List<ServerPicture>>(emitter) {

                override fun getRemote(): Single<List<ServerPicture>> = apiService.getPictures(placeId)

                override fun getLocal(): Flowable<List<Picture>> = pictureDao.getPictures(placeId)

                override fun sync(data: List<ServerPicture>) = syncService.savePictures(data, placeId)
            }
        }, BackpressureStrategy.BUFFER)
    }
}