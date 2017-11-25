package com.android.quo.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.android.quo.model.PlacePreview
import com.android.quo.networking.PlacePreviewListService
import com.android.quo.viewmodel.PlacePreviewListViewModel.FragmentType.HOME
import com.android.quo.viewmodel.PlacePreviewListViewModel.FragmentType.MY_PLACES
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

/**
 * Created by vitusortner on 27.10.17.
 */
class PlacePreviewListViewModel() : ViewModel() {

    enum class FragmentType {
        HOME, MY_PLACES
    }

    private val compositDisposable = CompositeDisposable()

    private var placePreviewListHome: MutableLiveData<List<PlacePreview>>? = null
    private var placePreviewListMyPlaces: MutableLiveData<List<PlacePreview>>? = null

    private val placePreviewListService = PlacePreviewListService.instance

    fun getPlacePreviewList(fragmentType: FragmentType): LiveData<List<PlacePreview>> =
        when (fragmentType) {
            HOME -> {
                if (placePreviewListHome == null) {
                    placePreviewListHome = MutableLiveData()
                    loadPlacePreviewList(fragmentType)
                }
                placePreviewListHome as MutableLiveData<List<PlacePreview>>
            }
            MY_PLACES -> {
                if (placePreviewListMyPlaces == null) {
                    placePreviewListMyPlaces = MutableLiveData()
                    loadPlacePreviewList(fragmentType)
                }
                placePreviewListMyPlaces as MutableLiveData<List<PlacePreview>>
            }
        }

    private fun loadPlacePreviewList(fragmentType: FragmentType) =
        when (fragmentType) {
            HOME -> {
                compositDisposable.add(
                    placePreviewListService.getPlacePreviewListHome()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ result ->
                            placePreviewListHome?.value = result.list
                        }, { error ->
                            // TODO proper error handling
                            Log.i("API error", error.toString())
                        })
                )
            }
            MY_PLACES -> {
                compositDisposable.add(
                    placePreviewListService.getPlacePreviewListMyPlaces()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ result ->
                            placePreviewListMyPlaces?.value = result.list
                        }, { error ->
                            // TODO proper error handling
                            Log.i("API error", error.toString())
                        })
                )
            }
        }

    fun updatePlacePreviewList(fragmentType: FragmentType) = loadPlacePreviewList(fragmentType)

    override fun onCleared() {
        super.onCleared()
        compositDisposable.clear()
    }
}