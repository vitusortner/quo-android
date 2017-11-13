package com.android.quo.networking

import com.android.quo.model.PlacePreviewList
import io.reactivex.Observable
import retrofit2.http.GET

/**
 * Created by vitusortner on 29.10.17.
 */
interface PlacePreviewListService {

    @GET("59fc37412d00002d3e12436b")
    fun getPlacePreviewListHome(): Observable<PlacePreviewList>

    @GET("59ff44582e0000650bca5944")
    fun getPlacePreviewListMyPlaces(): Observable<PlacePreviewList>

    companion object {

        val service: PlacePreviewListService by lazy { ApiService.retrofit.create(PlacePreviewListService::class.java) }
    }
}