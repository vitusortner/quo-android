package com.android.quo.db.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import android.arch.persistence.room.Update
import com.android.quo.db.entity.Picture

/**
 * Created by FlorianSchlueter on 18.11.2017.
 */

@Dao
interface PictureDao {
    @Query("SELECT * FROM picture")
    fun getAllPictures(): List<Picture>

    @Query("SELECT * FROM picture WHERE id = :id")
    fun findPictureById(id: String): Picture

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPicture(picture: Picture)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updatePicture(picture: Picture)

    @Delete
    fun deletePicture(picture: Picture)

    // TODO get Titlepicture
}