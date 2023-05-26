package com.task.roomDB

import androidx.room.*
import com.google.gson.Gson
import com.task.LocationListData

@Dao
interface DefaultGroupDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addLocation(users: ArrayList<LocationListData>)

    @Query("Select * from locationList")
    fun getLocations(): List<LocationListData>

    @Query("DELETE FROM locationList WHERE id = :id")
    fun deleteLocation(id: Int)
}