package com.android.quo.repository

import com.android.quo.db.dao.UserDao

/**
 * Created by vitusortner on 10.01.18.
 */
class UserRepository(private val userDao: UserDao) {

    fun getUser() = userDao.getUser()

    fun getUserSingle() = userDao.getUserSingle()
}