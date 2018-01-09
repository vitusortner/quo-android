package com.android.quo.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.android.quo.db.entity.Component
import com.android.quo.network.repository.ComponentRepository
import io.reactivex.disposables.CompositeDisposable

/**
 * Created by vitusortner on 19.11.17.
 */
class PageViewModel(private val componentRepository: ComponentRepository) : ViewModel() {

    private val compositDisposabel = CompositeDisposable()

    private var components: MutableLiveData<List<Component>>? = null

    fun getComponents(placeId: String): LiveData<List<Component>> {
        if (components == null) {
            components = MutableLiveData()
            loadComponents(placeId)
        }
        return components as MutableLiveData<List<Component>>
    }

    private fun loadComponents(placeId: String) {
        compositDisposabel.add(
                componentRepository.getComponents(placeId)
                        .distinctUntilChanged()
                        .subscribe({
                            if (it.isNotEmpty()) {
                                components?.value = it.sortedBy { it.position }
                            }
                        }, {
                            Log.e("sync", it.toString())
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