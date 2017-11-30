package com.android.quo.db.dao

import android.arch.persistence.room.*
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import com.android.quo.db.entity.User

/**
 * Created by FlorianSchlueter on 18.11.2017.
 */

@Dao
interface UserDao {
    @Query("SELECT * FROM user")
    fun getAllUsers(): List<User>

    @Query("SELECT * FROM user WHERE id = :id")
    fun findUserById(id: Long): User

    @Insert(onConflict = REPLACE)
    fun insertUser(user: User)

    @Update(onConflict = REPLACE)
    fun updateUser(user: User)

    @Delete
    fun deleteUser(user: User)
}