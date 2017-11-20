package com.android.quo.data

import android.arch.persistence.room.*

/**
 * Created by FlorianSchlueter on 18.11.2017.
 */

@Dao
interface ComponentDao {
    @Query("SELECT * FROM component")
    fun getAllComponents(): List<Component>

    @Query("SELECT * FROM component WHERE id = :id")
    fun findComponentById(id: Long): Component

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertComponent(component: Component)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateComponent(component: Component)

    @Delete
    fun deleteComponent(component: Component)
}