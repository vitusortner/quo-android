package com.android.quo.networking

import com.android.quo.db.dao.PictureDao
import com.android.quo.db.entity.Picture
import com.android.quo.networking.model.ServerPicture
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Single

/**
 * Created by vitusortner on 05.12.17.
 */
class PictureRepository(private val pictureDao: PictureDao, private val apiService: ApiService) {

    fun getPictures(placeId: String): Flowable<List<Picture>> {
        return Flowable.create({ emitter ->
            object : Repository<List<Picture>, List<ServerPicture>>(emitter) {

                override fun getRemote(): Single<List<ServerPicture>> = apiService.getPictures(placeId)

                override fun getLocal(): Flowable<List<Picture>> = pictureDao.getPictures(placeId)

                override fun sync(data: List<ServerPicture>) {
                    SyncService.savePictures(data)
                }
            }
        }, BackpressureStrategy.BUFFER)
    }

    fun getAllPictures(): Flowable<List<Picture>> {
        return Flowable.create({ emitter ->
            object : Repository<List<Picture>, List<ServerPicture>>(emitter) {

                override fun getRemote(): Single<List<ServerPicture>> = apiService.getAllPictures()

                override fun getLocal(): Flowable<List<Picture>> = pictureDao.getAllPictures()

                override fun sync(data: List<ServerPicture>) {
                    SyncService.savePictures(data)
                }
            }
        }, BackpressureStrategy.BUFFER)
    }
}