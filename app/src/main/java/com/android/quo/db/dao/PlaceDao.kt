package com.android.quo.db.dao

import android.arch.persistence.room.*
import com.android.quo.db.entity.Place

/**
 * Created by FlorianSchlueter on 18.11.2017.
 */

@Dao
interface PlaceDao {
    @Query("SELECT * FROM place")
    fun getAllPlaces(): List<Place>

    @Query("SELECT * FROM place WHERE id = :id")
    fun findPlaceById(id: Long): Place

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPlace(place: Place)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updatePlace(place: Place)

    @Delete
    fun deletePlace(place: Place)
}