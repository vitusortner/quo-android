package com.android.quo.service

import android.os.AsyncTask
import com.android.quo.db.dao.ComponentDao
import com.android.quo.db.dao.PictureDao
import com.android.quo.db.dao.PlaceDao
import com.android.quo.db.dao.UserDao
import com.android.quo.db.entity.User
import com.android.quo.network.ApiClient
import com.android.quo.network.model.ServerLogin
import com.android.quo.network.model.ServerSignup
import com.android.quo.util.Constants
import devliving.online.securedpreferencestore.SecuredPreferenceStore
import io.reactivex.schedulers.Schedulers

/**
 * Created by vitusortner on 03.01.18.
 */
class AuthService(
    private val apiClient: ApiClient,
    private val componentDao: ComponentDao,
    private val pictureDao: PictureDao,
    private val placeDao: PlaceDao,
    private val userDao: UserDao,
    private val preferenceStore: SecuredPreferenceStore
) :
    BaseService() {

    fun login(email: String, password: String, completionHandler: (Boolean) -> Unit) {
        apiClient.login(ServerLogin(email, password))
            .subscribeOn(Schedulers.io())
            .subscribe({
                log.i("Login successful: $it")

                // Delete existing user from local DB and insert new user
                userDao.deleteAllUsers()
                userDao.insertUser(User(it.user.id))

                // Delete existing token and insert new token
                preferenceStore.edit().clear().commit()
                preferenceStore.edit().putString(Constants.TOKEN_KEY, it.token).apply()

                completionHandler(true)
            }, {
                // TODO error handling
                log.e("Error while logging in: $it")
                completionHandler(false)
            })
    }

    fun signup(email: String, password: String, completionHandler: (Boolean) -> Unit) {
        apiClient.signup(ServerSignup(email, password))
            .subscribeOn(Schedulers.io())
            .subscribe({
                log.i("Signup successful: $it")

                // Delete existing user from local DB and insert new user
                userDao.deleteAllUsers()
                userDao.insertUser(User(it.user.id))

                // Delete existing token and insert new token
                preferenceStore.edit().clear().commit()
                preferenceStore.edit().putString(Constants.TOKEN_KEY, it.token).apply()

                completionHandler(true)
            }, {
                // TODO error handling
                log.e("Error while signing in: $it")
                completionHandler(false)
            })
    }

    /**
     * Removes user form local DB, removes token from shared preferences
     */
    fun logout() {
        AsyncTask.execute {
            componentDao.deleteAllComponents()
            pictureDao.deleteAllPictures()
            placeDao.deleteAllPlaces()
            userDao.deleteAllUsers()
            preferenceStore.edit().clear().commit()

            log.i("Data cleared and user logged out.")
        }
    }
}