package com.android.quo


import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.runner.RunWith


import android.arch.persistence.room.Room
import com.android.quo.data.*
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
    var userPlaceJoinDao: UserPlaceJoinDao? = null

    @Before
    fun createDB(){
        val appContext= InstrumentationRegistry.getTargetContext()
        database = Room.inMemoryDatabaseBuilder(appContext, AppDatabase::class.java).build()

        userDao = database.userDao()
        placeDao = database.placeDao()
        pictureDao = database.pictureDao()
        componentDao = database.componentDao()
        userPlaceJoinDao = database.userPlaceJoinDao()
    }

    @After
    fun closeDB(){
        database?.close()
    }

    @Test
    fun insert_test(){
        val user = User(1,"name@email.com","123",true, true, true, Date())
        userDao?.insertUser(user)
        val userTest = userDao?.findUserById(user.id)!!
        Assert.assertEquals(user,userTest)
    }

    @Test
    fun update_test(){
        val user = User(2,"name@email.com","123",true, true, true, Date())
        userDao?.insertUser(user)
        val userUpdate = User(2,"other@email.com","321",true, true, true, Date())
        userDao?.updateUser(userUpdate)
        val userTest = userDao?.findUserById(user.id)!!
        Assert.assertNotEquals(userTest,user)
    }

    @Test
    fun delete_test(){
        val user = User(3,"name@email.com","123",true, true, true, Date())
        userDao?.insertUser(user)
        userDao?.deleteUser(user)
        val userTest = userDao?.findUserById(user.id)
        Assert.assertNull(userTest)
    }

    @Test
    fun realtionship_test(){
        val user = User(4,"name@email.com","123",true, true, true, Date())
        userDao?.insertUser(user)
        val place = Place(1,4,"testPlace", Date(), Date(),"00","11",false,false,Date())
        placeDao?.insertPlace(place)
        val tPic = Picture(1,4,1,"",true,true,false,Date())
        pictureDao?.insertPicture(tPic)
        val qrPic = Picture(2,4,1,"",true,false,true,Date())
        pictureDao?.insertPicture(qrPic)
        val compPic = Picture(3,4,1,"",true,false,false,Date())
        pictureDao?.insertPicture(compPic)
        val component = Component(1,3,1,"Picture",1,"", Date())
        componentDao?.insertComponent(component)
        val userPlaceJoin = UserPlaceJoin(4,1)
        userPlaceJoinDao?.insertUserPlaceJoin(userPlaceJoin)
   }


}