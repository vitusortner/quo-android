package com.android.quo.networking

import com.android.quo.model.PlacePreviewList
import io.reactivex.Flowable
import io.reactivex.Observable
import retrofit2.http.GET

/**
 * Created by vitusortner on 29.10.17.
 */
interface PlacePreviewListService {

    @GET("59fc37412d00002d3e12436b")
    fun getPlacePreviewListHome(): Flowable<PlacePreviewList>

    @GET("59ff44582e0000650bca5944")
    fun getPlacePreviewListMyPlaces(): Flowable<PlacePreviewList>

    companion object {

        val instance: PlacePreviewListService = ApiService.instance.create(PlacePreviewListService::class.java)
    }
}