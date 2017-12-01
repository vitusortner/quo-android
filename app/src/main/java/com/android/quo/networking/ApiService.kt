package com.android.quo.networking

import com.android.quo.model.PlacePreviewList
import com.android.quo.networking.model.ServerPlace
import io.reactivex.Flowable
import io.reactivex.Single
import okhttp3.Headers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

/**
 * Created by vitusortner on 29.10.17.
 */
interface ApiService {

    @GET("5a2135932d00009f16e0018c")
    fun getPlaces(): Single<List<ServerPlace>>

//    @POST()
//    fun login(@Body data: ServerUser): Single<ServerUser>

//    @POST()
//    fun singup(@Body data: ServerUser): Single<ServerUser>

    //    @GET(Endpoint.USER)
//    fun getUser(): Single<ServerUser>

//    @POST()
//    fun addComponent(@Body data: ServerComponent)

//    @POST(Endpoint.PLACE)
//    fun addPlace(place: ServerPlace)

//    @GET()
//    fun getPlace(data: String) // get place with qr code id


    @GET("59fc37412d00002d3e12436b")
    fun getPlacePreviewListHome(): Flowable<PlacePreviewList>

    @GET("59ff44582e0000650bca5944")
    fun getPlacePreviewListMyPlaces(): Flowable<PlacePreviewList>

    companion object {

        private const val BASE_URL = "http://www.mocky.io/v2/"

        private val retrofit: Retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .client(okClient)
                .build()

        private val okClient: OkHttpClient
            get() {
                val clientBuilder = OkHttpClient.Builder()

                clientBuilder.addInterceptor { chain ->
                    val original = chain.request()
                    val requestBuilder = original.newBuilder().headers(headers)
                    val request = requestBuilder.build()
                    chain.proceed(request)
                }
                return clientBuilder.build()
            }

        private val headers: Headers
            get() {
                val headers = mapOf(
                        "Accept" to "application/json",
                        "Content-Type" to "application/json",
                        // TODO real token
                        "Authorization" to "auth token"
                )
                return Headers.of(headers)
            }

        val instance: ApiService = ApiService.retrofit.create(ApiService::class.java)
    }

    object Endpoint {

        const val PLACE = "place"
        const val USER = "user"

    }
}