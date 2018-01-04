package com.android.quo.networking.repository

import android.util.Log
import com.android.quo.db.dao.UserDao
import com.android.quo.db.entity.User
import com.android.quo.networking.ApiService
import com.android.quo.networking.model.ServerLogin
import com.android.quo.networking.model.ServerSignup
import io.reactivex.schedulers.Schedulers

/**
 * Created by vitusortner on 03.01.18.
 */
class AuthRepository(
        private val apiService: ApiService,
        private val userDao: UserDao
) {

    fun login(email: String, password: String, callback: (Boolean) -> Unit) {
        apiService.login(ServerLogin(email, password))
                .subscribeOn(Schedulers.io())
                .subscribe({
                    Log.i("login", "Login successful: $it")

                    userDao.deleteAllUsers()
                    userDao.insertUser(User(it.user.id))

                    callback(true)
                    // TODO write user to local DB and insert token to keystore
                }, {
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
                    // TODO write user to local DB and insert token to keystore
                }, {
                    Log.e("signup", "Error while signing in: $it")
                    callback(false)
                })
    }
}