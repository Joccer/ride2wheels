package com.ride2wheels_cycling.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface RideDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRide(ride: Ride)

    @Delete
    suspend fun deleteRide(ride: Ride)

    @Query("SELECT * FROM riding_table ORDER BY timestamp DESC")
    fun getAllRidesSortedByDate(): LiveData<List<Ride>>

    @Query("SELECT * FROM riding_table ORDER BY avgSpeed DESC")
    fun getAllRidesSortedByAvgSpeed(): LiveData<List<Ride>>

    @Query("SELECT * FROM riding_table ORDER BY distance DESC")
    fun getAllRidesSortedByDistance(): LiveData<List<Ride>>

    @Query("SELECT * FROM riding_table ORDER BY timeMillis DESC")
    fun getAllRidesSortedByDuration(): LiveData<List<Ride>>

    @Query("SELECT * FROM riding_table ORDER BY burnedCalories DESC")
    fun getAllRidesSortedByBurnedCalories(): LiveData<List<Ride>>


    @Query("SELECT AVG(avgSpeed) FROM riding_table")
    fun getTotalAvgSpeed(): LiveData<Float>

    @Query("SELECT SUM(distance) FROM riding_table")
    fun getTotalDistance(): LiveData<Int>

    @Query("SELECT SUM(timeMillis) FROM riding_table")
    fun getTotalDuration(): LiveData<Long>

    @Query("SELECT SUM(burnedCalories) FROM riding_table")
    fun getTotalBurnedCalories(): LiveData<Int>
}