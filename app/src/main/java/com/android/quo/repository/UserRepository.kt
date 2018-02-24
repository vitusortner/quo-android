package com.android.quo.repository

import com.android.quo.db.dao.UserDao
import com.android.quo.db.entity.User
import io.reactivex.schedulers.Schedulers

/**
 * Created by vitusortner on 10.01.18.
 */
class UserRepository(private val userDao: UserDao) {

    fun getUser(completionHandler: (User?) -> Unit) {
        userDao.getUser()
            .subscribeOn(Schedulers.io())
            .subscribe({ completionHandler(it) }, { completionHandler(null) })
    }
}