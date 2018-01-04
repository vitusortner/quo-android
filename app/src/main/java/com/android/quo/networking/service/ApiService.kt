package com.android.quo.networking.service

import com.android.quo.general.Constants
import com.android.quo.networking.service.ApiService.Companion.Endpoints.AUTH
import com.android.quo.networking.service.ApiService.Companion.Endpoints.PLACES
import com.android.quo.networking.service.ApiService.Companion.Endpoints.USERS
import com.android.quo.networking.model.ServerComponent
import com.android.quo.networking.model.ServerFacebookSignup
import com.android.quo.networking.model.ServerLogin
import com.android.quo.networking.model.ServerLoginResponse
import com.android.quo.networking.model.ServerPasswordChange
import com.android.quo.networking.model.ServerPasswordReset
import com.android.quo.networking.model.ServerPicture
import com.android.quo.networking.model.ServerPlace
import com.android.quo.networking.model.ServerPlaceResponse
import com.android.quo.networking.model.ServerSignup
import com.android.quo.networking.model.ServerSignupResponse
import com.android.quo.networking.model.ServerUser
import devliving.online.securedpreferencestore.SecuredPreferenceStore
import io.reactivex.Single
import okhttp3.Headers
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

/**
 * Created by vitusortner on 29.10.17.
 */
interface ApiService {

    @POST("$AUTH/login")
    fun login(@Body data: ServerLogin): Single<ServerLoginResponse>

    @POST("$AUTH/register")
    fun signup(@Body data: ServerSignup): Single<ServerSignupResponse>

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

    @GET("$PLACES/qrcode/{qr_code_id}/{user_id}")
    fun getPlace(@Path("qr_code_id") qrCodeId: String, @Path("user_id") userId: String): Single<ServerPlace>

    @GET("$USERS/{id}/visited_places")
    fun getVisitedPlaces(@Path("id") userId: String): Single<List<ServerPlaceResponse>>

    @GET("$USERS/{id}/hosted_places")
    fun getHostedPlaces(@Path("id") userId: String): Single<List<ServerPlace>>

    @POST("$PLACES/{id}/pictures")
    fun addPicture(@Path("id") placeId: String, @Body picture: ServerPicture): Single<ServerPicture>

    @GET("$PLACES/{id}/pictures")
    fun getPictures(@Path("id") placeId: String): Single<List<ServerPicture>>

    @POST("$PLACES/{id}/components")
    fun addComponent(@Path("id") placeId: String, @Body data: ServerComponent): Single<ServerComponent>

    @GET("$PLACES/{id}/components")
    fun getComponents(@Path("id") placeId: String): Single<List<ServerComponent>>

    @PUT("components/{id}")
    fun updateComponent(@Path("id") componentId: String, @Body data: ServerComponent): Single<ServerComponent>

    companion object {

        //        private const val BASE_URL = "http://10.0.2.2:3000/"
        private const val BASE_URL = "http://192.168.178.23:3000/"

        private val okClient: OkHttpClient
            get() {
                val clientBuilder = OkHttpClient.Builder()

                clientBuilder.addInterceptor { chain ->
                    val original = chain.request()
                    val requestBuilder = original.newBuilder().headers(headers(original.url()))
                    val request = requestBuilder.build()
                    chain.proceed(request)
                }
                return clientBuilder.build()
            }

        private fun headers(url: HttpUrl): Headers {
            val headers = mutableMapOf("Accept" to "application/json")

            if (Endpoints.needsBearerToken(url)) {
                val preferenceStore = SecuredPreferenceStore.getSharedInstance()
                val token = preferenceStore.getString(Constants.TOKEN_KEY, "")

                headers.put("Authorization", "Bearer $token")
            }
            if (Endpoints.isMultipartRequest(url)) {
                headers.put("Content-Type", "multipart/form-data")
            } else {
                headers.put("Content-Type", "application/json")
            }
            return Headers.of(headers)
        }

        private val retrofit: Retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .client(okClient)
                .build()

        val instance: ApiService = retrofit.create(ApiService::class.java)

        object Endpoints {

            const val AUTH = "auth"
            const val PLACES = "places"
            const val PICTURES = "pictures"
            const val COMPONENTS = "components"
            const val USERS = "users"
            const val UPLOAD = "upload"

            fun needsBearerToken(url: HttpUrl): Boolean {
                val path = url.encodedPath()
                return path.split("/").first() != AUTH
            }

            fun isMultipartRequest(url: HttpUrl): Boolean {
                val path = url.encodedPath()
                return path.split("/").first() == UPLOAD
            }
        }
    }
}