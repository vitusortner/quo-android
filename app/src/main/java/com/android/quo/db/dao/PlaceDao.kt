package com.android.quo.db.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
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
    fun getPlaceById(id: String): Place

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllPlaces(places: List<Place>)

    @Delete
    fun deletePlace(place: Place)

    @Query("DELETE FROM place")
    fun deleteAllPlaces()
}