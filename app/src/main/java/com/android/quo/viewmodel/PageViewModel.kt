package com.android.quo.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.android.quo.db.entity.Component
import com.android.quo.repository.ComponentRepository
import io.reactivex.disposables.CompositeDisposable

/**
 * Created by vitusortner on 19.11.17.
 */
class PageViewModel(private val componentRepository: ComponentRepository) : BaseViewModel() {

    private val compositDisposabel = CompositeDisposable()

    private var components = MutableLiveData<List<Component>>()

    fun getComponents(placeId: String): LiveData<List<Component>> {
        loadComponents(placeId)
        return components
    }

    private fun loadComponents(placeId: String) {
        compositDisposabel.add(
            componentRepository.getComponents(placeId)
                .distinctUntilChanged()
                .subscribe({
                    if (it.isNotEmpty()) {
                        components.value = it.sortedBy { it.position }
                    }
                }, {
                    log.e("Error while loading components.", it)
                })
        )
    }

    fun updateComponents(placeId: String) {
        loadComponents(placeId)
    }

    override fun onCleared() {
        super.onCleared()
        compositDisposabel.dispose()
    }
}