package com.android.quo.db.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.android.quo.db.entity.Component
import io.reactivex.Flowable

/**
 * Created by FlorianSchlueter on 18.11.2017.
 */

@Dao
interface ComponentDao {
    @Query("SELECT * FROM component WHERE place_id = :placeId")
    fun getComponents(placeId: String): Flowable<List<Component>>

    @Query("SELECT * FROM component WHERE id = :id")
    fun getComponentById(id: String): Component

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllComponents(components: List<Component>)

    @Delete
    fun deleteComponent(component: Component)

    @Query("DELETE FROM component WHERE place_id = :placeId")
    fun deleteComponentsOfPlace(placeId: String)

    @Query("DELETE FROM component")
    fun deleteAllComponents()
}