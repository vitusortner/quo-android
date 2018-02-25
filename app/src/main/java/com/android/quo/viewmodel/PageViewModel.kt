package com.android.quo.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.android.quo.db.entity.Component
import com.android.quo.repository.ComponentRepository
import com.android.quo.util.extension.addTo
import com.android.quo.util.extension.observeOnUi
import com.android.quo.util.extension.subscribeOnIo

/**
 * Created by vitusortner on 19.11.17.
 */
class PageViewModel(private val componentRepository: ComponentRepository) : BaseViewModel() {

    private var components = MutableLiveData<List<Component>>()

    fun getComponents(placeId: String): LiveData<List<Component>> {
        loadComponents(placeId)
        return components
    }

    private fun loadComponents(placeId: String) {
        componentRepository.getComponents(placeId)
            .subscribeOnIo()
            .distinctUntilChanged()
            .filter { it.isNotEmpty() }
            .map { it.sortedBy { it.position } }
            .observeOnUi()
            .subscribe(
                { components.value = it },
                { log.e("Error while loading components.", it) }
            )
            .addTo(compositeDisposable)
    }

    fun updateComponents(placeId: String) = loadComponents(placeId)
}