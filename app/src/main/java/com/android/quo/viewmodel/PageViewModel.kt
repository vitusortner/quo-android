package com.android.quo.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.android.quo.db.entity.Component
import com.android.quo.repository.ComponentRepository
import com.android.quo.util.extension.filterNotEmpty
import com.android.quo.util.extension.observeOnUi
import com.android.quo.util.extension.subscribeOnIo
import io.reactivex.Flowable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy

/**
 * Created by vitusortner on 19.11.17.
 */
data class PageComponent(
    val picture: String?,
    val text: String?
)

class PageViewModel(private val componentRepository: ComponentRepository) : BaseViewModel() {

    private var components = MutableLiveData<List<PageComponent>>()

    fun getComponents(placeId: String): LiveData<List<PageComponent>> {
        loadComponents(placeId)
        return components
    }

    private fun loadComponents(placeId: String) {
        componentRepository.getComponents(placeId)
            .subscribeOnIo()
            .distinctUntilChanged()
            .filterNotEmpty()
            .sortByPosition()
            .toPageComponent()
            .observeOnUi()
            .subscribeBy(
                onNext = { components.value = it },
                onError = { log.e("Error while loading components.", it) }
            )
            .addTo(compositeDisposable)
    }

    fun updateComponents(placeId: String) = loadComponents(placeId)

    private fun Flowable<List<Component>>.sortByPosition() =
        this.map { it.sortedBy { it.position } }

    private fun Flowable<List<Component>>.toPageComponent() =
        this.map { it.map { PageComponent(it.picture, it.text) } }

}