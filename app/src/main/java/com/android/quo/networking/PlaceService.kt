package com.android.quo.networking

import com.android.quo.data.Place
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Headers

/**
 * Created by vitusortner on 24.11.17.
 */
interface PlaceService {

    @Headers("Accept: application/json")
    @GET("5a18d454300000020a63f44c")
    fun getPlaces(): Single<List<Place>>

    companion object {

        val instance: PlaceService = ApiService.instance.create(PlaceService::class.java)
    }
}