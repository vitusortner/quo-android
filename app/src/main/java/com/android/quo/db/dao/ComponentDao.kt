package com.android.quo.db.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import android.arch.persistence.room.Update
import com.android.quo.db.entity.Component

/**
 * Created by FlorianSchlueter on 18.11.2017.
 */

@Dao
interface ComponentDao {
    @Query("SELECT * FROM component")
    fun getAllComponents(): List<Component>

    @Query("SELECT * FROM component WHERE id = :id")
    fun findComponentById(id: String): Component

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertComponent(component: Component)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateComponent(component: Component)

    @Delete
    fun deleteComponent(component: Component)

    //TODO get all components for placeID
}