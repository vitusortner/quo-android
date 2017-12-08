package com.android.quo.db.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.android.quo.db.entity.Picture
import io.reactivex.Flowable

/**
 * Created by FlorianSchlueter on 18.11.2017.
 */

@Dao
interface PictureDao {
    @Query("SELECT * FROM picture WHERE place_id = :placeId")
    fun getPictures(placeId: String): Flowable<List<Picture>>

    // TODO remove
    @Query("SELECT * FROM picture")
    fun getAllPictures(): Flowable<List<Picture>>

    @Query("SELECT * FROM picture WHERE id = :id")
    fun getPictureById(id: String): Flowable<Picture>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllPictures(pictures: List<Picture>)

    @Delete
    fun deletePicture(picture: Picture)

    @Query("DELETE FROM picture WHERE place_id = :placeId")
    fun deletePicturesOfPlace(placeId: String)

    @Query("DELETE FROM picture")
    fun deleteAllPictures()
}