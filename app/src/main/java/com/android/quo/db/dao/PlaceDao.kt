package com.android.quo.db.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import android.arch.persistence.room.Update
import com.android.quo.db.entity.Place
import io.reactivex.Flowable

/**
 * Created by FlorianSchlueter on 18.11.2017.
 */

@Dao
interface PlaceDao {
    @Query("SELECT * FROM place")
    fun getAllPlaces(): Flowable<List<Place>>

    @Query("SELECT * FROM place WHERE id = :id")
    fun findPlaceById(id: String): Place

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPlace(place: Place)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updatePlace(place: Place)

    @Delete
    fun deletePlace(place: Place)
}