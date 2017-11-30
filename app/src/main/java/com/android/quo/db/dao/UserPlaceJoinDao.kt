package com.android.quo.db.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.android.quo.db.entity.Place
import com.android.quo.db.entity.User
import com.android.quo.db.entity.UserPlaceJoin

/**
 * Created by FlorianSchlueter on 24.11.2017.
 */

@Dao
interface UserPlaceJoinDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUserPlaceJoin(userPlaceJoin: UserPlaceJoin)

    @Query("SELECT * FROM user INNER JOIN user_place_join ON user.id=user_place_join.user_id WHERE user_place_join.place_id=:placeId")
    fun getUsersFromPlace(placeId: String): List<User>

    @Query("SELECT * FROM place INNER JOIN user_place_join ON place.id=user_place_join.place_id WHERE user_place_join.user_id=:userId")
    fun getPlacesFromUser(userId: String): List<Place>
}