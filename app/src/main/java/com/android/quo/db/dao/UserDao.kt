package com.android.quo.db.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import android.arch.persistence.room.Query
import com.android.quo.db.entity.User
import io.reactivex.Flowable
import io.reactivex.Single

/**
 * Created by FlorianSchlueter on 18.11.2017.
 */

@Dao
interface UserDao {

    // Returns error when no user is found
    @Query("SELECT * FROM user LIMIT 1")
    fun getUser(): Single<User>

    @Query("SELECT * FROM user WHERE id = :id LIMIT 1")
    fun getUserById(id: String): Flowable<User>

    @Insert(onConflict = REPLACE)
    fun insertUser(user: User)

    @Delete
    fun deleteUser(user: User)

    @Query("DELETE FROM user")
    fun deleteAllUsers()
}