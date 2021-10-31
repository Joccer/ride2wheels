package com.ride2wheels_cycling.repositories

import com.ride2wheels_cycling.db.Ride
import com.ride2wheels_cycling.db.RideDAO
import com.ride2wheels_cycling.db.RidingDatabase
import javax.inject.Inject

class MainRepository @Inject constructor(
    val rideDAO: RideDAO
) {
    suspend fun insertRide(ride: Ride) = rideDAO.insertRide(ride)

    suspend fun deleteRide(ride: Ride) = rideDAO.deleteRide(ride)

    fun getAllRidesSortedByDate() = rideDAO.getAllRidesSortedByDate()

    fun getAllRidesSortedByAvgSpeed() = rideDAO.getAllRidesSortedByAvgSpeed()

    fun getAllRidesSortedByDistance() = rideDAO.getAllRidesSortedByDistance()

    fun getAllRidesSortedByDuration() = rideDAO.getAllRidesSortedByDuration()

    fun getAllRidesSortedByBurnedCalories() = rideDAO.getAllRidesSortedByBurnedCalories()

    fun getTotalAvgSpeed() = rideDAO.getTotalAvgSpeed()

    fun getTotalDistance() = rideDAO.getTotalDistance()

    fun getTotalDuration() = rideDAO.getTotalDuration()

    fun getTotalBurnedCalories() = rideDAO.getTotalBurnedCalories()
}