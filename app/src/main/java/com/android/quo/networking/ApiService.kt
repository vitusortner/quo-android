package com.android.quo.networking

import com.android.quo.model.PlacePreviewList
import com.android.quo.networking.model.ServerComponent
import com.android.quo.networking.model.ServerLogin
import com.android.quo.networking.model.ServerPlace
import com.android.quo.networking.model.ServerPasswordReset
import com.android.quo.networking.model.ServerPicture
import com.android.quo.networking.model.ServerSignup
import com.android.quo.networking.model.ServerUser
import io.reactivex.Flowable
import io.reactivex.Single
import okhttp3.Headers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * Created by vitusortner on 29.10.17.
 */
interface ApiService {

    // TODO
    @POST("")
    fun login(@Body data: ServerLogin): Single<ServerUser>

    // TODO
    @POST("")
    fun singup(@Body data: ServerSignup): Single<ServerUser>

    // TODO
    @POST("")
    fun resetPassword(@Body data: ServerPasswordReset)

    // TODO
    @GET("users")
    fun getUser(): Single<ServerUser>

    @POST("places")
    fun addPlace(place: ServerPlace): Single<ServerPlace>

    @GET("places/{id}")
    fun getPlace(@Path("id") placeId: String): Single<ServerPlace>

//    @GET("5a2135932d00009f16e0018c")
    @GET("places")
    fun getPlaces(): Single<List<ServerPlace>>

    @GET("users/{id}/places")
    fun getPlaces(@Path("id") userId: String): Single<List<ServerPlace>>

    @GET("places/{id}/pictures")
    fun getPictures(@Path("id") placeId: String): Single<List<ServerPicture>>

    @POST("components")
    fun addComponent(@Body data: ServerComponent)


    @GET("59fc37412d00002d3e12436b")
    fun getPlacePreviewListHome(): Flowable<PlacePreviewList>

    @GET("59ff44582e0000650bca5944")
    fun getPlacePreviewListMyPlaces(): Flowable<PlacePreviewList>

    companion object {

//        private const val BASE_URL = "http://www.mocky.io/v2/"
        private const val BASE_URL = "http://localhost:3000/"

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
                        "Content-Type" to "application/json"
                        // TODO real token
//                        "Authorization" to "auth token"
                )
                return Headers.of(headers)
            }

        private val retrofit: Retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .client(okClient)
                .build()

        val instance: ApiService = ApiService.retrofit.create(ApiService::class.java)
    }
}