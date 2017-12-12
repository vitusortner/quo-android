package com.android.quo.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.android.quo.db.entity.Component
import com.android.quo.networking.repository.ComponentRepository

/**
 * Created by vitusortner on 19.11.17.
 */
class PageViewModel(private val componentRepository: ComponentRepository) : ViewModel() {

    private var components: MutableLiveData<List<Component>>? = null

    fun getComponents(placeId: String): LiveData<List<Component>> {
        if (components == null) {
            components = MutableLiveData()
            loadComponents(placeId)
        }
        return components as MutableLiveData<List<Component>>
    }

    private fun loadComponents(placeId: String) {
        componentRepository.getComponents(placeId)
                .distinctUntilChanged()
                .subscribe({
                    Log.i("page", "$it")
                    components?.value = it
                }, {
                    Log.e("sync", it.toString())
                })
    }

    fun updateComponents(placeId: String) {
        loadComponents(placeId)
    }
}