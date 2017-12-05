package com.android.quo.networking

import com.android.quo.networking.model.ServerComponent
import com.android.quo.networking.model.ServerFacebookSignup
import com.android.quo.networking.model.ServerLogin
import com.android.quo.networking.model.ServerPasswordChange
import com.android.quo.networking.model.ServerPasswordReset
import com.android.quo.networking.model.ServerPicture
import com.android.quo.networking.model.ServerPlace
import com.android.quo.networking.model.ServerSignup
import com.android.quo.networking.model.ServerUser
import io.reactivex.Single
import okhttp3.Headers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * Created by vitusortner on 29.10.17.
 */
interface ApiService {

    // TODO
    @POST("user")
    fun login(@Body data: ServerLogin): Single<ServerUser>

    // TODO
    @POST("user")
    fun singup(@Body data: ServerSignup): Single<ServerUser>

    // TODO which route?
    @POST("user")
    fun signupWithFacebook(@Body data: ServerFacebookSignup): Single<ServerUser>

    // TODO which route? PUT?
    @POST("user")
    fun changePassword(@Body data: ServerPasswordChange): Single<ServerUser>

    // TODO
    @POST("user")
    fun resetPassword(@Body data: ServerPasswordReset)

    // TODO delete user trough ID or email?
    @DELETE("users/{id}")
    fun deleteUser(@Path("id") userId: String)

    // TODO
    @GET("users")
    fun getUser(): Single<ServerUser>

    // TO get called when user scans place
    @POST("users/{id}/places")
    fun addPlaceToUser(@Path("id") userId: String): Single<ServerPlace>

    @POST("places")
    fun addPlace(@Body place: ServerPlace): Single<ServerPlace>

    @GET("places/{id}")
    fun getPlace(@Path("id") qrCodeId: String): Single<List<ServerPlace>>

    @GET("users/{id}/places")
    fun getPlaces(@Path("id") userId: String): Single<List<ServerPlace>>

    @POST("pictures")
    fun addPicture(@Body picture: ServerPicture): Single<ServerPicture>

    @GET("places/{id}/pictures")
    fun getPictures(@Path("id") placeId: String): Single<List<ServerPicture>>

    @POST("components")
    fun addComponent(@Body data: ServerComponent)

    @GET("places/{id}/components")
    fun getComponents()

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