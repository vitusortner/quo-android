package com.android.quo.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.android.quo.db.entity.Picture
import com.android.quo.repository.PictureRepository
import com.android.quo.util.Constants.Date.MONGO_DB_TIMESTAMP
import com.android.quo.util.extension.observeOnUi
import com.android.quo.util.extension.subscribeOnIo
import com.android.quo.util.extension.toDate
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy

/**
 * Created by vitusortner on 09.12.17.
 */
class GalleryViewModel(private val pictureRepository: PictureRepository) : BaseViewModel() {

    private var pictures = MutableLiveData<List<Picture>>()

    fun getPictures(placeId: String): LiveData<List<Picture>> {
        loadPictures(placeId)
        return pictures
    }

    private fun loadPictures(placeId: String) =
        pictureRepository.getPictures(placeId)
            .subscribeOnIo()
            .distinctUntilChanged()
            .filter { it.isNotEmpty() }
            .map { it.sortedByDescending { it.timestamp.toDate(MONGO_DB_TIMESTAMP) } }
            .observeOnUi()
            .subscribeBy(
                onNext = { pictures.value = it },
                onError = { log.e("Error while loading pictures", it) }
            )
            .addTo(compositeDisposable)

    fun updatePictures(placeId: String) = loadPictures(placeId)
}