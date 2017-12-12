package com.android.quo


import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.persistence.room.Room
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.android.quo.db.AppDatabase
import com.android.quo.db.dao.ComponentDao
import com.android.quo.db.dao.PictureDao
import com.android.quo.db.dao.PlaceDao
import com.android.quo.db.dao.UserDao
import com.android.quo.db.entity.Address
import com.android.quo.db.entity.Component
import com.android.quo.db.entity.Picture
import com.android.quo.db.entity.Place
import com.android.quo.db.entity.User
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*


/**
 * Created by FlorianSchlueter on 21.11.2017.
 */

@RunWith(AndroidJUnit4::class)
class DataInstrumentedTest {
    lateinit var database: AppDatabase

    var userDao: UserDao? = null
    var placeDao: PlaceDao? = null
    var pictureDao: PictureDao? = null
    var componentDao: ComponentDao? = null

    @Before
    fun createDB() {
        val appContext = InstrumentationRegistry.getTargetContext()
        database = Room.inMemoryDatabaseBuilder(appContext, AppDatabase::class.java)
                .allowMainThreadQueries()
                .build()

        userDao = database.userDao()
        placeDao = database.placeDao()
        pictureDao = database.pictureDao()
        componentDao = database.componentDao()
    }

    @After
    fun closeDB() {
        database.close()
    }

    @Rule
    @JvmField
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun component_insert_test() {
        val date = Date()

        val address = Address("street", "city", 123)
        val place = Place("103", false, "title", date, date, "12", "21", address, true, true, "src.com", "123")
        placeDao?.insertAllPlaces(place)

        val component = Component(id = "78", picture = "pic.com", placeId = place.id, position = 1)
        componentDao?.insertAllComponents(component)
        val foundComponent = componentDao?.getComponentById(component.id)

        Assert.assertEquals(component, foundComponent)
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

    @Test
    fun picture_insert_test() {
        val date = Date()

        val user = User("1234")
        userDao?.insertUser(user)

        val address = Address("street", "city", 123)
        val place = Place("897", false, "title", date, date, "12", "21", address, true, true, "src.com", "123")
        placeDao?.insertPlace(place)

        val picture = Picture("456", user.id, place.id, "src.com", true, date)
        pictureDao?.insertPicture(picture)
        val foundPicture = pictureDao?.getPictureById(picture.id)

        Assert.assertEquals(picture, foundPicture)
    }

    @Test
    fun place_insert_test() {
        val date = Date()

        val address = Address("street", "city", 123)
        val place = Place("123", false, "title", date, date, "12", "21", address, true, true, "src.com", "123")
        placeDao?.insertPlace(place)
        val foundPlace = placeDao?.getPlaceById(place.id)

        Assert.assertEquals(place, foundPlace)
    }

    @Test
    fun user_insert_test() {
        val user = User("1")
        userDao?.insertUser(user)
        val foundUser = userDao?.getUserById(user.id)
        Assert.assertEquals(user, foundUser)
    }
}