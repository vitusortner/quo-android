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

    fun getComponents(): LiveData<List<Component>> {
        if (components == null) {
            components = MutableLiveData()
            loadComponents()
        }
        return components as MutableLiveData<List<Component>>
    }

    private fun loadComponents() {
        // TODO real place id
        componentRepository.getComponents("5a2a87c6ba3a14853a8f2ca6")
                .subscribe({
                    components?.value = it
                }, {
                    Log.e("sync", it.toString())
                })
    }

    fun updateComponents() {
        loadComponents()
    }
}