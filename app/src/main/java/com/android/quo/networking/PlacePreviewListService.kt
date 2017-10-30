package com.android.quo.networking

import com.android.quo.model.PlacePreviewList
import io.reactivex.Observable
import retrofit2.http.GET

/**
 * Created by vitusortner on 29.10.17.
 */
interface PlacePreviewListService {

    @GET("59f5ee6a3100002c0060025c")
    fun getPlacePreviewList(): Observable<PlacePreviewList>

    companion object {

        val service: PlacePreviewListService by lazy { ApiService.retrofit.create(PlacePreviewListService::class.java) }
    }
}