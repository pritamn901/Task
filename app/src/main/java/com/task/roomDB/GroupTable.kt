package com.task

import androidx.room.*
import com.task.roomDB.Converters

@Entity(tableName = "locationList")
class LocationListData (
    @PrimaryKey
    @ColumnInfo(name = "id")
    var id:Int=1,
    var lat:Double=0.0,
    var lng:Double=0.0
    )