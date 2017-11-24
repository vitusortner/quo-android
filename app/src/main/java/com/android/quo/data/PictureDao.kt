package com.android.quo.data

import android.arch.persistence.room.*

/**
 * Created by FlorianSchlueter on 18.11.2017.
 */

@Dao
interface PictureDao {
    @Query("SELECT * FROM picture")
    fun getAllPictures(): List<Picture>

    @Query("SELECT * FROM picture WHERE id = :id")
    fun findPictureById(id: Long): Picture

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPicture(picture: Picture)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updatePicture(picture: Picture)

    @Delete
    fun deletePicture(picture: Picture)
}