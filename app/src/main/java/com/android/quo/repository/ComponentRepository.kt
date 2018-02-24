package com.android.quo.repository

import com.android.quo.db.dao.ComponentDao
import com.android.quo.db.entity.Component
import com.android.quo.network.ApiClient
import com.android.quo.network.model.ServerComponent
import com.android.quo.service.SyncService
import com.android.quo.util.NetworkBoundResource
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

/**
 * Created by vitusortner on 09.12.17.
 */
class ComponentRepository(
    private val componentDao: ComponentDao,
    private val apiClient: ApiClient,
    private val syncService: SyncService
) :
    BaseRepository() {

    fun getComponents(placeId: String): Flowable<List<Component>> {
        return Flowable.create({ emitter ->
            object : NetworkBoundResource<List<Component>, List<ServerComponent>>(emitter) {

                override fun getRemote(): Single<List<ServerComponent>> =
                    apiClient.getComponents(placeId)

                override fun getLocal(): Flowable<List<Component>> =
                    componentDao.getComponents(placeId)

                override fun sync(data: List<ServerComponent>) =
                    syncService.saveComponents(data, placeId)
            }
        }, BackpressureStrategy.BUFFER)
    }

    fun addComponent(placeId: String, component: ServerComponent) {
        apiClient.addComponent(placeId, component)
            .subscribeOn(Schedulers.io())
            .subscribe({
                log.i("Component added: $it")
            }, {
                log.e("Error while adding component", it)
            })
    }
}