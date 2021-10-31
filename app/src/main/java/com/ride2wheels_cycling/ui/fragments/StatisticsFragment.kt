package com.ride2wheels_cycling.ui.fragments

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.ride2wheels_cycling.R
import com.ride2wheels_cycling.other.PopUpInfo
import com.ride2wheels_cycling.other.TrackingUtility
import com.ride2wheels_cycling.ui.viewmodels.MainViewModel
import com.ride2wheels_cycling.ui.viewmodels.StatisticsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_setup.*
import kotlinx.android.synthetic.main.fragment_statistics.*
import kotlin.math.round

@AndroidEntryPoint
class StatisticsFragment : Fragment(R.layout.fragment_statistics){

    private val viewModel: StatisticsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeToObservers()
        barChart()
    }

    private fun subscribeToObservers() {
        viewModel.totalAvgSpeed.observe(viewLifecycleOwner, Observer { it?.let {
            val avgSpeed = round(it)
            val avgSpeedString = "${avgSpeed} km/h"
            tvAverageSpeed.text = avgSpeedString
        } })
        viewModel.totalDistance.observe(viewLifecycleOwner, Observer { it?.let {
            val km = it / 1000f
            val totalDistance = round(km * 10f) / 10f
            val totalDistanceString = "${totalDistance} km"
            tvTotalDistance.text = totalDistanceString
        } })
        viewModel.totalRideTime.observe(viewLifecycleOwner, Observer { it?.let {
            val totalRideTime = TrackingUtility.getFormattedStopperTime(it)
            tvTotalTime.text = totalRideTime
        } })
        viewModel.totalCalories.observe(viewLifecycleOwner, Observer { it?.let {
            val totalCalories = "${it} kcal"
            tvTotalCalories.text = totalCalories
        } })
        viewModel.ridesSortedByDate.observe(viewLifecycleOwner, Observer { it?.let {
            val allAvgSpeeds = it.indices.map {  i -> BarEntry(i.toFloat(), it[i].avgSpeed) }
            val bardataSet = BarDataSet(allAvgSpeeds, "AVG Speed over Time").apply {
                valueTextColor = Color.WHITE
                color = ContextCompat.getColor(requireContext(), R.color.colorAccent)
            }
            barChart.data = BarData(bardataSet)
            barChart.marker = PopUpInfo(it, requireContext(), R.layout.popup_info)
            barChart.invalidate()
        } })
    }

    private fun barChart(){
        barChart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            setDrawLabels(false)
            axisLineColor = Color.WHITE
            textColor = Color.WHITE
            setDrawGridLines(false)
        }
        barChart.axisLeft.apply {
            axisLineColor = Color.WHITE
            textColor = Color.WHITE
            setDrawGridLines(false)
        }
        barChart.axisRight.apply {
            axisLineColor = Color.WHITE
            textColor = Color.WHITE
            setDrawGridLines(false)
        }
        barChart.apply {
            description.text = "Aktivitások átlagsebessége"
            legend.isEnabled = false
        }
    }
}