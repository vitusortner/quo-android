package com.android.quo.db.dao

import android.arch.persistence.room.Dao
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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllPictures(pictures: List<Picture>)

    @Query("DELETE FROM picture WHERE place_id = :placeId")
    fun deletePicturesOfPlace(placeId: String)

    @Query("DELETE FROM picture")
    fun deleteAllPictures()
}