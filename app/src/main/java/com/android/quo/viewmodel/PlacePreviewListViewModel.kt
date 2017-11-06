package com.android.quo.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.android.quo.model.PlacePreview
import com.android.quo.networking.PlacePreviewListService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

/**
 * Created by vitusortner on 27.10.17.
 */
class PlacePreviewListViewModel : ViewModel() {

    private val compositDisposable = CompositeDisposable()

    private var placePreviewList: MutableLiveData<List<PlacePreview>>? = null

    fun getPlacePreviewList(): LiveData<List<PlacePreview>> {
        if (placePreviewList == null) {
            placePreviewList = MutableLiveData()
            loadPlacePreviewList()
        }
        return placePreviewList as MutableLiveData<List<PlacePreview>>
    }

    private fun loadPlacePreviewList() {
        val placePreviewListService = PlacePreviewListService.service

        compositDisposable.add(
            placePreviewListService.getPlacePreviewList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    placePreviewList?.value = result.list
                }, { error ->
                    // TODO proper error handling
                    Log.i("API error", error.toString())
                })
        )
    }

    fun updatePlacePreviewList() {
        loadPlacePreviewList()
    }

    override fun onCleared() {
        super.onCleared()
        compositDisposable.clear()
    }
}