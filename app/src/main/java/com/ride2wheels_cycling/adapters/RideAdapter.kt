package com.ride2wheels_cycling.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ride2wheels_cycling.R
import com.ride2wheels_cycling.db.Ride
import com.ride2wheels_cycling.other.TrackingUtility
import kotlinx.android.synthetic.main.item_ride.view.*
import java.text.SimpleDateFormat
import java.util.*

class RideAdapter : RecyclerView.Adapter<RideAdapter.RideViewHolder>() {

    inner class RideViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    val diffCallback = object :  DiffUtil.ItemCallback<Ride>() {
        override fun areItemsTheSame(oldItem: Ride, newItem: Ride): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Ride, newItem: Ride): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)

    fun submitList(list: List<Ride>) = differ.submitList(list)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RideViewHolder {
        return RideViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_ride,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RideViewHolder, position: Int) {
        val ride = differ.currentList[position]
        holder.itemView.apply {
            Glide.with(this).load(ride.img).into(ivRunImage)
            val calendar = Calendar.getInstance().apply { timeInMillis = ride.timestamp }
            val dateFormat = SimpleDateFormat("yy.MM.dd", Locale.getDefault())
            tvDate.text = dateFormat.format(calendar.time)
            val avgSpeed = "${ride.avgSpeed}km/h"
            tvAvgSpeed.text = avgSpeed
            val distanceInKm = "${ride.distance / 1000f}km"
            tvDistance.text = distanceInKm
            tvTime.text = TrackingUtility.getFormattedStopperTime(ride.timeMillis)
            val caloriesBurned = "${ride.burnedCalories}kcal"
            tvCalories.text = caloriesBurned
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}