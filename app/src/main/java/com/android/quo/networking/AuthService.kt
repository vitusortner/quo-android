package com.android.quo.networking

import android.util.Log
import com.android.quo.db.dao.UserDao
import com.android.quo.db.entity.User
import com.android.quo.general.Constants
import com.android.quo.networking.model.ServerLogin
import com.android.quo.networking.model.ServerSignup
import devliving.online.securedpreferencestore.SecuredPreferenceStore
import io.reactivex.schedulers.Schedulers

/**
 * Created by vitusortner on 03.01.18.
 */
class AuthService(
        private val apiService: ApiService,
        private val userDao: UserDao,
        private val preferenceStore: SecuredPreferenceStore
) {

    fun login(email: String, password: String, callback: (Boolean) -> Unit) {
        apiService.login(ServerLogin(email, password))
                .subscribeOn(Schedulers.io())
                .subscribe({
                    Log.i("login", "Login successful: $it")

                    userDao.deleteAllUsers()
                    userDao.insertUser(User(it.user.id))

                    callback(true)

                    // Delete existing token and insert new token
                    preferenceStore.edit().clear().commit()
                    preferenceStore.edit().putString(Constants.TOKEN_KEY, it.token).apply()
                }, {
                    // TODO error handling
                    Log.e("login", "Error while logging in: $it")
                    callback(false)
                })
    }

    fun signup(email: String, password: String, callback: (Boolean) -> Unit) {
        apiService.signup(ServerSignup(email, password))
                .subscribeOn(Schedulers.io())
                .subscribe({
                    Log.i("signup", "Signup successful: $it")

                    userDao.deleteAllUsers()
                    userDao.insertUser(User(it.user.id))

                    callback(true)

                    // Delete existing token and insert new token
                    preferenceStore.edit().clear().commit()
                    preferenceStore.edit().putString(Constants.TOKEN_KEY, it.token).apply()
                }, {
                    // TODO error handling
                    Log.e("signup", "Error while signing in: $it")
                    callback(false)
                })
    }
}