package com.android.quo.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.android.quo.db.entity.Picture
import com.android.quo.repository.PictureRepository
import com.android.quo.util.extension.toDate
import io.reactivex.disposables.CompositeDisposable

/**
 * Created by vitusortner on 09.12.17.
 */
class GalleryViewModel(private val pictureRepository: PictureRepository) : BaseViewModel() {

    private val compositDisposabel = CompositeDisposable()

    private var pictures: MutableLiveData<List<Picture>>? = null

    fun getPictures(placeId: String): LiveData<List<Picture>> {
        if (pictures == null) {
            pictures = MutableLiveData()
            loadPictures(placeId)
        }
        return pictures as MutableLiveData<List<Picture>>
    }

    private fun loadPictures(placeId: String) {
        compositDisposabel.add(
            pictureRepository.getPictures(placeId)
                .distinctUntilChanged()
                .subscribe({
                    if (it.isNotEmpty()) {
                        pictures?.value = it.sortedByDescending { it.timestamp.toDate() }
                    }
                }, {
                    log.e("Error while loading pictures", it)
                })
        )
    }

    fun updatePictures(placeId: String) {
        loadPictures(placeId)
    }

    override fun onCleared() {
        super.onCleared()

        compositDisposabel.dispose()
    }
}