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
    var mUserDao: UserDao? = null
    var mPlaceDao: PlaceDao? = null
    var mPictureDao: PictureDao? = null
    var mComponentDao: ComponentDao? = null
    var mUserPlaceJoinDao: UserPlaceJoinDao? = null
    var mDB : AppDatabase? = null

    @Before
    fun createDB(){
        val appContext= InstrumentationRegistry.getTargetContext()
        mDB = Room.inMemoryDatabaseBuilder(appContext, AppDatabase::class.java).build()
        mUserDao = mDB!!.userDao()
        mPlaceDao = mDB!!.placeDao()
        mPictureDao = mDB!!.pictureDao()
        mComponentDao = mDB!!.componentDao()
        mUserPlaceJoinDao = mDB!!.userPlaceJoinDao()
    }

    @After
    fun closeDB(){
        mDB?.close()
    }

    @Test
    fun insert_test(){
        val user = User(1,"name@email.com","123",true, true, true, Date())
        mUserDao?.insertUser(user)
        val userTest = mUserDao?.findUserById(user.id)!!
        Assert.assertEquals(user,userTest)
    }

    @Test
    fun update_test(){
        val user = User(2,"name@email.com","123",true, true, true, Date())
        mUserDao?.insertUser(user)
        val userUpdate = User(2,"other@email.com","321",true, true, true, Date())
        mUserDao?.updateUser(userUpdate)
        val userTest = mUserDao?.findUserById(user.id)!!
        Assert.assertNotEquals(userTest,user)
    }

    @Test
    fun delete_test(){
        val user = User(3,"name@email.com","123",true, true, true, Date())
        mUserDao?.insertUser(user)
        mUserDao?.deleteUser(user)
        val userTest = mUserDao?.findUserById(user.id)
        Assert.assertNull(userTest)
    }

    @Test
    fun realtionship_test(){
        val user = User(4,"name@email.com","123",true, true, true, Date())
        mUserDao?.insertUser(user)
        val place = Place(1,4,"testPlace", Date(), Date(),"00","11",false,false,Date())
        mPlaceDao?.insertPlace(place)
        val tPic = Picture(1,4,1,"",true,true,false,Date())
        mPictureDao?.insertPicture(tPic)
        val qrPic = Picture(2,4,1,"",true,false,true,Date())
        mPictureDao?.insertPicture(qrPic)
        val compPic = Picture(3,4,1,"",true,false,false,Date())
        mPictureDao?.insertPicture(compPic)
        val component = Component(1,3,1,"Picture",1,"", Date())
        mComponentDao?.insertComponent(component)
        val userPlaceJoin = UserPlaceJoin(4,1)
        mUserPlaceJoinDao?.insertUserPlaceJoin(userPlaceJoin)
   }


}