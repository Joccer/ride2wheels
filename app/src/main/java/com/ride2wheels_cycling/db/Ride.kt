package com.ride2wheels_cycling.db

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "riding_table")
data class Ride(
    var img: Bitmap? = null,
    var timestamp: Long = 0L,
    var avgSpeed: Float = 0f,
    var distance: Int = 0,
    var timeMillis: Long = 0L,
    var burnedCalories: Int = 0
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}
