package com.android.quo.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.android.quo.db.entity.Picture
import com.android.quo.networking.PictureRepository

/**
 * Created by vitusortner on 19.11.17.
 */
class PlaceViewModel(private val pictureRepository: PictureRepository) : ViewModel() {

    private var pictures: MutableLiveData<List<Picture>>? = null

    fun getPictures(): LiveData<List<Picture>> {
        if (pictures == null) {
            pictures = MutableLiveData()
            loadPictures()
        }
        return pictures as MutableLiveData<List<Picture>>
    }

    fun loadPictures() {
        pictureRepository.getAllPictures()
                .subscribe({
                    pictures?.value = it
                }, {
                    Log.e("sync", it.toString())
                })
    }
}