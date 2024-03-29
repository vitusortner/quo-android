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

    @Query("SELECT * FROM place WHERE is_host =:isHost")
    fun getPlaces(isHost: Boolean): Flowable<List<Place>>

    @Query("SELECT * FROM place WHERE qr_code_id = :qrCodeId")
    fun getPlaceByQrCodeId(qrCodeId: String): Flowable<Place>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllPlaces(places: List<Place>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPlace(place: Place)

    @Delete
    fun deletePlace(place: Place)

    @Query("DELETE FROM place WHERE is_host = :isHost")
    fun deletePlaces(isHost: Boolean)

    @Query("DELETE FROM place")
    fun deleteAllPlaces()
}