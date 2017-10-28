package com.android.quo.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.os.Handler
import com.android.quo.model.PlacePreview

/**
 * Created by vitusortner on 27.10.17.
 */
class PlacePreviewListViewModel : ViewModel() {

    private var placePreviewList: MutableLiveData<List<PlacePreview>>? = null

    fun getPlacePreviewList(): LiveData<List<PlacePreview>> {
        if (placePreviewList == null) {
            placePreviewList = MutableLiveData()
            loadPlacePreviewList()
        }
        return placePreviewList as MutableLiveData<List<PlacePreview>>
    }

    private fun loadPlacePreviewList() {
        // Fake API request with 3 sec delay
        val handler = Handler()
        handler.postDelayed({
            val fakePlacePreviewList = ArrayList<PlacePreview>()
            fakePlacePreviewList.add(PlacePreview("Lorem ipsum", "Curabitur nisl dolor, dictum a eros facilisis."))
            fakePlacePreviewList.add(PlacePreview("Lorem ipsum", "Curabitur nisl dolor, dictum a eros facilisis."))
            fakePlacePreviewList.add(PlacePreview("Lorem ipsum", "Curabitur nisl dolor, dictum a eros facilisis."))
            fakePlacePreviewList.add(PlacePreview("Lorem ipsum", "Curabitur nisl dolor, dictum a eros facilisis."))
            fakePlacePreviewList.add(PlacePreview("Lorem ipsum", "Curabitur nisl dolor, dictum a eros facilisis."))
            fakePlacePreviewList.add(PlacePreview("Lorem ipsum", "Curabitur nisl dolor, dictum a eros facilisis."))
            fakePlacePreviewList.add(PlacePreview("Lorem ipsum", "Curabitur nisl dolor, dictum a eros facilisis."))
            fakePlacePreviewList.add(PlacePreview("Lorem ipsum", "Curabitur nisl dolor, dictum a eros facilisis."))
            fakePlacePreviewList.add(PlacePreview("Lorem ipsum", "Curabitur nisl dolor, dictum a eros facilisis."))
            fakePlacePreviewList.add(PlacePreview("Lorem ipsum", "Curabitur nisl dolor, dictum a eros facilisis."))
            fakePlacePreviewList.add(PlacePreview("Lorem ipsum", "Curabitur nisl dolor, dictum a eros facilisis."))
            fakePlacePreviewList.add(PlacePreview("Lorem ipsum", "Curabitur nisl dolor, dictum a eros facilisis."))
            fakePlacePreviewList.add(PlacePreview("Lorem ipsum", "Curabitur nisl dolor, dictum a eros facilisis."))
            fakePlacePreviewList.add(PlacePreview("Lorem ipsum", "Curabitur nisl dolor, dictum a eros facilisis."))
            fakePlacePreviewList.add(PlacePreview("Lorem ipsum", "Curabitur nisl dolor, dictum a eros facilisis."))
            fakePlacePreviewList.add(PlacePreview("Lorem ipsum", "Curabitur nisl dolor, dictum a eros facilisis."))

            // When placePreviewList not null set value
            placePreviewList?.let { it.value = fakePlacePreviewList }
        }, 3000)
    }
}