package com.ride2wheels_cycling.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [Ride::class],
    version = 1
)

@TypeConverters(Converters::class)
abstract class RidingDatabase: RoomDatabase() {

    abstract fun getRideDao(): RideDAO
}