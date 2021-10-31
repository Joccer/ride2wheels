package com.ride2wheels_cycling.other

import android.content.Context
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import com.ride2wheels_cycling.db.Ride
import kotlinx.android.synthetic.main.popup_info.view.*
import java.text.SimpleDateFormat
import java.util.*

class PopUpInfo(
    val rides: List<Ride>,
    c: Context,
    layoutId: Int
    ): MarkerView(c, layoutId) {


    override fun getOffset(): MPPointF {
        return MPPointF(-width / 1.5f, -height.toFloat())

    }

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        super.refreshContent(e, highlight)
        if (e == null){
            return
        }
        val curRideId = e.x.toInt()
        val ride = rides[curRideId]
        val calendar = Calendar.getInstance().apply { timeInMillis = ride.timestamp }
        val dateFormat = SimpleDateFormat("yy.MM.dd", Locale.getDefault())
        tvDate.text = dateFormat.format(calendar.time)
        tvAvgSpeed.text = "${ride.avgSpeed}km/h"
        tvDistance.text = "${ride.distance / 1000f}km"
        tvDuration.text = TrackingUtility.getFormattedStopperTime(ride.timeMillis)
        tvCaloriesBurned.text = "${ride.burnedCalories}kcal"
    }
}