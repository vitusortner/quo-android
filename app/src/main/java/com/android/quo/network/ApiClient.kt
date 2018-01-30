package com.android.quo.network

import com.android.quo.network.ApiClient.Companion.Endpoints.AUTH
import com.android.quo.network.ApiClient.Companion.Endpoints.PLACES
import com.android.quo.network.ApiClient.Companion.Endpoints.UPLOAD
import com.android.quo.network.ApiClient.Companion.Endpoints.USERS
import com.android.quo.network.model.ServerComponent
import com.android.quo.network.model.ServerLogin
import com.android.quo.network.model.ServerLoginResponse
import com.android.quo.network.model.ServerPicture
import com.android.quo.network.model.ServerPlace
import com.android.quo.network.model.ServerPlaceResponse
import com.android.quo.network.model.ServerSignup
import com.android.quo.network.model.ServerSignupResponse
import com.android.quo.network.model.ServerUploadImage
import com.android.quo.util.Constants
import devliving.online.securedpreferencestore.SecuredPreferenceStore
import io.reactivex.Single
import okhttp3.Headers
import okhttp3.HttpUrl
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

/**
 * Created by vitusortner on 29.10.17.
 */
interface ApiClient {

    @POST("$AUTH/login")
    fun login(@Body data: ServerLogin): Single<ServerLoginResponse>

    @POST("$AUTH/register")
    fun signup(@Body data: ServerSignup): Single<ServerSignupResponse>

    @POST(PLACES)
    fun addPlace(@Body place: ServerPlace): Single<ServerPlace>

    @GET("$PLACES/qrcode/{qr_code_id}/{user_id}")
    fun getPlace(
            @Path("qr_code_id") qrCodeId: String,
            @Path("user_id") userId: String
    ): Single<ServerPlace>

    @GET("$USERS/{id}/visited_places")
    fun getVisitedPlaces(@Path("id") userId: String): Single<List<ServerPlaceResponse>>

    @GET("$USERS/{id}/hosted_places")
    fun getHostedPlaces(@Path("id") userId: String): Single<List<ServerPlace>>

    @PUT("$PLACES/{id}")
    fun updatePlace(@Path("id") placeId: String, @Body place: ServerPlace): Single<ServerPlace>

    @POST("$PLACES/{id}/pictures")
    fun addPicture(
            @Path("id") placeId: String,
            @Body picture: ServerPicture
    ): Single<ServerPicture>

    @GET("$PLACES/{id}/pictures")
    fun getPictures(@Path("id") placeId: String): Single<List<ServerPicture>>

    @GET("$UPLOAD/{default}")
    fun getDefaultPicture(@Path("default") default: String): Single<ServerUploadImage>

    @Multipart
    @POST(UPLOAD)
    fun uploadImage(@Part filePart: MultipartBody.Part): Single<ServerUploadImage>

    @POST("$PLACES/{id}/components")
    fun addComponent(
            @Path("id") placeId: String,
            @Body data: ServerComponent
    ): Single<ServerComponent>

    @GET("$PLACES/{id}/components")
    fun getComponents(@Path("id") placeId: String): Single<List<ServerComponent>>

    companion object {

        @Volatile
        private var INSTANCE: ApiClient? = null

        fun instance(securedPreferenceStore: SecuredPreferenceStore): ApiClient {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: retrofit(securedPreferenceStore)
                        .create(ApiClient::class.java)
                        .also { INSTANCE = it }
            }
        }

        private fun okClient(securedPreferenceStore: SecuredPreferenceStore): OkHttpClient {
            val clientBuilder = OkHttpClient.Builder()

            clientBuilder.addInterceptor { chain ->
                val original = chain.request()
                val requestBuilder = original
                        .newBuilder()
                        .headers(headers(original.url(), securedPreferenceStore))
                val request = requestBuilder.build()
                chain.proceed(request)
            }
            return clientBuilder.build()
        }

        private fun headers(url: HttpUrl, securedPreferenceStore: SecuredPreferenceStore): Headers {
            val headers = mutableMapOf("Accept" to "application/json")

            if (Endpoints.needsBearerToken(url)) {
                val token = securedPreferenceStore.getString(Constants.TOKEN_KEY, "")

                headers["Authorization"] = "Bearer $token"
            }
            if (Endpoints.isMultipartRequest(url)) {
                headers["Content-Type"] = "multipart/form-data"
            } else {
                headers["Content-Type"] = "application/json"
            }
            return Headers.of(headers)
        }

        private fun retrofit(securedPreferenceStore: SecuredPreferenceStore): Retrofit {
            return Retrofit.Builder()
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(Constants.BASE_URL)
                    .client(okClient(securedPreferenceStore))
                    .build()
        }

        object Endpoints {

            const val AUTH = "auth"
            const val PLACES = "places"
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