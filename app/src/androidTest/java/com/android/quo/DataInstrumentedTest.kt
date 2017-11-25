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
import java.util.*
import android.arch.core.executor.testing.InstantTaskExecutorRule
import io.reactivex.functions.Predicate
import org.junit.Rule


/**
 * Created by FlorianSchlueter on 21.11.2017.
 */

@RunWith(AndroidJUnit4::class)
class DataInstrumentedTest {
    var userDao: UserDao? = null
    var database: AppDatabase? = null

    @Before
    fun createDB() {
        val appContext = InstrumentationRegistry.getTargetContext()
        database = Room.inMemoryDatabaseBuilder(appContext, AppDatabase::class.java)
                .allowMainThreadQueries()
                .build()
        userDao = database!!.userDao()
    }

    @After
    fun closeDB() {
        database?.close()
    }

    @Rule
    @JvmField
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun insert_test() {
        val user = User(1, "name@email.com", "123", true)
        userDao?.insertUser(user)
        val userTest = userDao?.findUserById(user.id)!!
        Assert.assertEquals(user.email, userTest.email)
    }

    @Test
    fun place_test() {
        val place = Place(1, 1L, 1L, 1L, "Title")
        database?.placeDao()?.insertPlace(place)

        val place2 = Place(1, 1L, 1L, 1L, "Title")
        database?.placeDao()?.insertPlace(place2)

        database?.placeDao()?.getAllPlaces()
                ?.test()
                ?.assertValue { places ->
                    var boolean = true
                    places.forEach {
                        if (it.id != place.id) {
                            boolean = false
                        }
                    }
                    boolean
                }
    }

}