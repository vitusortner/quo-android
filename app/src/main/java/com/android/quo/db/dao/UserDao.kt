package com.android.quo.db.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import android.arch.persistence.room.Query
import com.android.quo.db.entity.User
import io.reactivex.Single

/**
 * Created by FlorianSchlueter on 18.11.2017.
 */
@Dao
interface UserDao {

    @Query("SELECT * FROM user LIMIT 1")
    fun getUser(): User?

    // Returns error when no user is found
    @Query("SELECT * FROM user LIMIT 1")
    fun getUserSingle(): Single<User>

    @Insert(onConflict = REPLACE)
    fun insertUser(user: User)

    @Query("DELETE FROM user")
    fun deleteAllUsers()
}