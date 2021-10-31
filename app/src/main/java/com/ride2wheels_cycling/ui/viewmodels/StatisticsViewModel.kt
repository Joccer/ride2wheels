package com.ride2wheels_cycling.ui.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.ride2wheels_cycling.repositories.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor (
    val mainRepository: MainRepository
): ViewModel() {

    val totalAvgSpeed = mainRepository.getTotalAvgSpeed()
    val totalDistance = mainRepository.getTotalDistance()
    val totalRideTime = mainRepository.getTotalDuration()
    val totalCalories = mainRepository.getTotalBurnedCalories()

    val ridesSortedByDate = mainRepository.getAllRidesSortedByDate()
}