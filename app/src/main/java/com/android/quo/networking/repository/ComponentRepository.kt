package com.android.quo.networking.repository

import com.android.quo.db.dao.ComponentDao
import com.android.quo.db.entity.Component
import com.android.quo.networking.service.ApiService
import com.android.quo.networking.NetworkBoundResource
import com.android.quo.networking.service.SyncService
import com.android.quo.networking.model.ServerComponent
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Single

/**
 * Created by vitusortner on 09.12.17.
 */
class ComponentRepository(
        private val componentDao: ComponentDao,
        private val apiService: ApiService,
        private val syncService: SyncService
) {

    fun getComponents(placeId: String): Flowable<List<Component>> {
        return Flowable.create({ emitter ->
            object : NetworkBoundResource<List<Component>, List<ServerComponent>>(emitter) {

                override fun getRemote(): Single<List<ServerComponent>> = apiService.getComponents(placeId)

                override fun getLocal(): Flowable<List<Component>> = componentDao.getComponents(placeId)

                override fun sync(data: List<ServerComponent>) = syncService.saveComponents(data, placeId)
            }
        }, BackpressureStrategy.BUFFER)
    }
}