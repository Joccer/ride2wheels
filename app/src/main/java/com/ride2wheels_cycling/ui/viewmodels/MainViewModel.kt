package com.ride2wheels_cycling.ui.viewmodels


import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ride2wheels_cycling.db.Ride
import com.ride2wheels_cycling.other.SortType
import com.ride2wheels_cycling.repositories.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor (
    val mainRepository: MainRepository
): ViewModel() {

    private val ridesSortedByDate = mainRepository.getAllRidesSortedByDate()
    private val ridesSortedByAvgSpeed = mainRepository.getAllRidesSortedByAvgSpeed()
    private val ridesSortedByDistance = mainRepository.getAllRidesSortedByDistance()
    private val ridesSortedByDuration = mainRepository.getAllRidesSortedByDuration()
    private val ridesSortedByBurnedCalories = mainRepository.getAllRidesSortedByBurnedCalories()

    val rides = MediatorLiveData<List<Ride>>()

    var sortType = SortType.DATE

    init {
        rides.addSource(ridesSortedByDate) { result ->
            if (sortType == SortType.DATE){
                result?.let { rides.value = it }
            }
        }
        rides.addSource(ridesSortedByAvgSpeed) { result ->
            if (sortType == SortType.AVG_SPEED){
                result?.let { rides.value = it }
            }
        }
        rides.addSource(ridesSortedByDistance) { result ->
            if (sortType == SortType.DISTANCE){
                result?.let { rides.value = it }
            }
        }
        rides.addSource(ridesSortedByDuration) { result ->
            if (sortType == SortType.DURATION){
                result?.let { rides.value = it }
            }
        }
        rides.addSource(ridesSortedByBurnedCalories) { result ->
            if (sortType == SortType.BURNED_CALORIES){
                result?.let { rides.value = it }
            }
        }
    }

    fun sortRides(sortType: SortType) = when(sortType){
        SortType.DATE -> ridesSortedByDate.value?.let { rides.value = it }
        SortType.AVG_SPEED -> ridesSortedByAvgSpeed.value?.let { rides.value = it }
        SortType.DISTANCE -> ridesSortedByDistance.value?.let { rides.value = it }
        SortType.DURATION -> ridesSortedByDuration.value?.let { rides.value = it }
        SortType.BURNED_CALORIES -> ridesSortedByBurnedCalories.value?.let { rides.value = it }
    }.also {
        this.sortType = sortType
    }

    fun insertRide(ride: Ride) = viewModelScope.launch {
        mainRepository.insertRide(ride)
    }
    fun deleteRide(ride: Ride) = viewModelScope.launch{ mainRepository.deleteRide(ride) }
}

