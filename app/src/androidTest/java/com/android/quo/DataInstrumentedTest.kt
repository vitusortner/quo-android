package com.android.quo


import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.runner.RunWith


import android.arch.persistence.room.Room
import com.android.quo.data.AppDatabase
import com.android.quo.data.Place
import com.android.quo.data.User
import com.android.quo.data.UserDao




/**
 * Created by FlorianSchlueter on 21.11.2017.
 */

@RunWith(AndroidJUnit4::class)
class DataInstrumentedTest {
    var mUserDao : UserDao? = null
    var mDB : AppDatabase? = null

    @Before
    fun createDB(){
        val appContext= InstrumentationRegistry.getTargetContext()
        mDB = Room.inMemoryDatabaseBuilder(appContext, AppDatabase::class.java).build()
        mUserDao = mDB!!.userDao()
    }

    @After
    fun closeDB(){
        mDB?.close()
    }

    @Test
    fun insert_test(){
        val user = User(1,"name@email.com","123",true)
        mUserDao?.insertUser(user)
        val userTest = mUserDao?.findUserById(user.id)!!
        Assert.assertEquals(user.email,userTest.email)
    }


}