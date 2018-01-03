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
import com.android.quo.networking.model.UploadImage
import io.reactivex.Single
import okhttp3.Headers
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

/**
 * Created by vitusortner on 29.10.17.
 */
interface ApiService {

    @POST("users")
    fun login(@Body data: ServerLogin): Single<ServerUser>

    @POST("signup")
    fun singup(@Body data: ServerSignup): Single<ServerUser>

    @POST("signupwithfacebook")
    fun signupWithFacebook(@Body data: ServerFacebookSignup): Single<ServerUser>

    @POST("user")
    fun changePassword(@Body data: ServerPasswordChange): Single<ServerUser>

    @POST("user")
    fun resetPassword(@Body data: ServerPasswordReset)

    @DELETE("users/{id}")
    fun deleteUser(@Path("id") userId: String)

    @POST("places")
    fun addPlace(@Body place: ServerPlace): Single<ServerPlace>

    @GET("places")
    fun getAllPlaces(): Single<List<ServerPlace>>

    @GET("places/qrcode/{id}")
    fun getPlace(@Path("id") qrCodeId: String): Single<ServerPlace>

    @PUT("places/{id}")
    fun putPlace(@Path("id") id: String, @Body place: ServerPlace): Single<ServerPlace>

    @GET("users/{id}/visited_places")
    fun getVisitedPlaces(@Path("id") userId: String): Single<List<ServerPlace>>

    @GET("users/{id}/hosted_places")
    fun getMyPlaces(@Path("id") userId: String): Single<List<ServerPlace>>

    @POST("/places/{id}/pictures")
    fun addPicture(@Path("id") id: String, @Body picture: ServerPicture): Single<ServerPicture>

    @Multipart
    @POST("upload")
    fun uploadPicture(@Part filePart: MultipartBody.Part): Single<UploadImage>

    @GET("upload/{default}")
    fun getDefaultPicture(@Path("default") default: String): Single<UploadImage>

    @GET("pictures")
    fun getAllPictures(): Single<List<ServerPicture>>

    @GET("places/{id}/pictures")
    fun getPictures(@Path("id") placeId: String): Single<List<ServerPicture>>

    @POST("places/{id}/components")
    fun addComponent(@Path("id") componentId: String, @Body data: ServerComponent): Single<ServerComponent>

    @GET("places/{id}/components")
    fun getComponents(@Path("id") placeId: String): Single<List<ServerComponent>>

    @PUT("components/{id}")
    fun updateComponent(@Path("id") componentId: String, @Body data: ServerComponent): Single<ServerComponent>

    companion object {

//        private const val BASE_URL = "http://10.0.2.2:3000/" //local
        private const val BASE_URL = "http://ec2-52-57-50-127.eu-central-1.compute.amazonaws.com/"  //aws

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
                // no headers used right now
//                .client(okClient)
                .build()

        val instance: ApiService = ApiService.retrofit.create(ApiService::class.java)
    }
}