package com.android.quo.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.os.Parcelable
import com.android.quo.db.entity.Picture
import com.android.quo.repository.PictureRepository
import com.android.quo.util.Constants.Date.MONGO_DB_TIMESTAMP_FORMAT
import com.android.quo.util.extension.filterNotEmpty
import com.android.quo.util.extension.observeOnUi
import com.android.quo.util.extension.subscribeOnIo
import com.android.quo.util.extension.toDate
import io.reactivex.Flowable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.parcel.Parcelize

/**
 * Created by vitusortner on 09.12.17.
 */
@Parcelize
data class GalleryPicture(val src: String) : Parcelable

class GalleryViewModel(private val pictureRepository: PictureRepository) : BaseViewModel() {

    private var pictures = MutableLiveData<List<GalleryPicture>>()

    fun getPictures(placeId: String): LiveData<List<GalleryPicture>> {
        loadPictures(placeId)
        return pictures
    }

    private fun loadPictures(placeId: String) =
        pictureRepository.getPictures(placeId)
            .subscribeOnIo()
            .distinctUntilChanged()
            .filterNotEmpty()
            .sortByTimestamp()
            .toGalleryPicture()
            .observeOnUi()
            .subscribeBy(
                onNext = { pictures.value = it },
                onError = { log.e("Error while loading pictures", it) }
            )
            .addTo(compositeDisposable)

    fun updatePictures(placeId: String) = loadPictures(placeId)

    private fun Flowable<List<Picture>>.sortByTimestamp() =
        this.map { it.sortedByDescending { it.timestamp.toDate(MONGO_DB_TIMESTAMP_FORMAT) } }

    private fun Flowable<List<Picture>>.toGalleryPicture() =
        this.map { it.map { GalleryPicture(it.src) } }

}