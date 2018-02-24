package com.android.quo.repository

import com.android.quo.db.dao.PictureDao
import com.android.quo.db.entity.Picture
import com.android.quo.network.ApiClient
import com.android.quo.network.model.ServerPicture
import com.android.quo.network.model.ServerUploadImage
import com.android.quo.service.SyncService
import com.android.quo.util.NetworkBoundResource
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

/**
 * Created by vitusortner on 05.12.17.
 */
class PictureRepository(
    private val pictureDao: PictureDao,
    private val apiClient: ApiClient,
    private val syncService: SyncService
) :
    BaseRepository() {

    fun getPictures(placeId: String): Flowable<List<Picture>> {
        return Flowable.create({ emitter ->
            object : NetworkBoundResource<List<Picture>, List<ServerPicture>>(emitter) {

                override fun getRemote(): Single<List<ServerPicture>> =
                    apiClient.getPictures(placeId)

                override fun getLocal(): Flowable<List<Picture>> = pictureDao.getPictures(placeId)

                override fun sync(data: List<ServerPicture>) =
                    syncService.savePictures(data, placeId)
            }
        }, BackpressureStrategy.BUFFER)
    }

    fun addPicture(
        placeId: String,
        picture: ServerPicture,
        completionHandler: ((ServerPicture?) -> Unit)? = null
    ) {
        apiClient.addPicture(placeId, picture)
            .subscribeOn(Schedulers.io())
            .subscribe({
                log.i("Picture added: $it")
                completionHandler?.invoke(it)
            }, {
                log.e("Error while adding picture", it)
                completionHandler?.invoke(null)
            })
    }

    fun getDefaultPicture(
        titlePicture: String,
        completionHandler: ((ServerUploadImage?) -> Unit)? = null
    ) {
        apiClient.getDefaultPicture(titlePicture)
            .subscribeOn(Schedulers.io())
            .subscribe({
                completionHandler?.invoke(it)
            }, {
                log.e("Error while getting default picture", it)
                completionHandler?.invoke(null)
            })
    }
}