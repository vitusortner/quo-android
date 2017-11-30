package com.android.quo.networking

import com.android.quo.db.entity.Place
import com.android.quo.model.PlacePreviewList
import com.android.quo.networking.model.ServerPlace
import io.reactivex.Flowable
import io.reactivex.Single
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers

/**
 * Created by vitusortner on 29.10.17.
 */
interface ApiService {

    @Headers("Accept: application/json")
    @GET("5a18d454300000020a63f44c")
    fun getPlaces(): Single<List<ServerPlace>>

    @GET("59fc37412d00002d3e12436b")
    fun getPlacePreviewListHome(): Flowable<PlacePreviewList>

    @GET("59ff44582e0000650bca5944")
    fun getPlacePreviewListMyPlaces(): Flowable<PlacePreviewList>

    companion object {

        private val BASE_URL = "http://www.mocky.io/v2/"

        private val retrofit: Retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build()

        val instance: ApiService = ApiService.retrofit.create(ApiService::class.java)
    }
}