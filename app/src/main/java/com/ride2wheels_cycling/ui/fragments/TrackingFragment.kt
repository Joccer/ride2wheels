package com.ride2wheels_cycling.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.ride2wheels_cycling.R
import com.ride2wheels_cycling.db.Ride
import com.ride2wheels_cycling.db.RideDAO
import com.ride2wheels_cycling.other.Constants.ACTION_PAUSE_SERVICE
import com.ride2wheels_cycling.other.Constants.ACTION_START_OR_RESUME_SERVICE
import com.ride2wheels_cycling.other.Constants.ACTION_STOP_SERVICE
import com.ride2wheels_cycling.other.Constants.MAP_ZOOM
import com.ride2wheels_cycling.other.Constants.POLYLINE_COLOR
import com.ride2wheels_cycling.other.Constants.POLYLINE_WIDTH
import com.ride2wheels_cycling.other.TrackingUtility
import com.ride2wheels_cycling.repositories.MainRepository
import com.ride2wheels_cycling.services.Polyline
import com.ride2wheels_cycling.services.TrackingService
import com.ride2wheels_cycling.ui.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_tracking.*
import java.util.*
import javax.inject.Inject
import kotlin.math.round

const val CANCEL_DIALOG_TAG = "CancelDialog"

@AndroidEntryPoint
class TrackingFragment : Fragment(R.layout.fragment_tracking){

    private val viewModel: MainViewModel by viewModels()
    private var map: GoogleMap? = null

    private var isTracking = false
    private var pathPoints = mutableListOf<Polyline>()

    private var currentTimeInMillis = 0L

    private var menu: Menu? = null

    @set:Inject
    var weight = 80f

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.toolbar_tracking_menu, menu)
        this.menu = menu
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        if(currentTimeInMillis > 0L){
            this.menu?.getItem(0)?.isVisible = true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.cancelTracking -> { cancelTrackingDialog() }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun cancelTrackingDialog(){
        CancelDialog().apply { setPositiveListener { stopRide() } }.show(parentFragmentManager, CANCEL_DIALOG_TAG)
    }

    private fun stopRide() {
        sendCommandToService(ACTION_STOP_SERVICE)
        findNavController().navigate(R.id.action_trackingFragment_to_rideFragment)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView.onCreate(savedInstanceState)
        btnToggleRun.setOnClickListener{
            toggleRide()
        }
        if (savedInstanceState != null){
            val cancelDialog = parentFragmentManager.findFragmentByTag(CANCEL_DIALOG_TAG) as CancelDialog?
            cancelDialog?.setPositiveListener { stopRide() }
        }
        btnFinishRun.setOnClickListener {
            zoomOutFullRide()
            endRideAndSave()
        }
        mapView.getMapAsync {
            map = it
            addAllPolylines()
        }
        subscribeToObservers()
    }
    private fun sendCommandToService(action: String) =
        Intent(requireContext(), TrackingService::class.java).also {
            it.action = action
            requireContext().startService(it)
        }

    //drawing the polyline
    private fun addLatestPolyline() {
        if(pathPoints.isNotEmpty() && pathPoints.last().size > 1) {
            val preLastLatLng = pathPoints.last()[pathPoints.last().size - 2]
            val lastLatLng = pathPoints.last().last()
            val polylineOptions = PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .add(preLastLatLng)
                .add(lastLatLng)
            map?.addPolyline(polylineOptions)

        }
    }

    //redrawing polylines in land mode, bc if we turn the phone, all data has lost
    private fun addAllPolylines(){
        for(polyline in pathPoints){
            val polylineOptions = PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .addAll(polyline)
            map?.addPolyline(polylineOptions)
        }
    }

    private fun zoomOutFullRide(){
        val bounds = LatLngBounds.Builder()
        for(polyline in pathPoints){
            for (pos in polyline){
                bounds.include(pos)
            }
        }
        map?.moveCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds.build(),
                mapView.width,
                mapView.height,
                (mapView.height * 0.05f).toInt()
            )
        )
    }

    private fun endRideAndSave(){
        map?.snapshot { bmp ->
            var distance = 0
            for(polyline in pathPoints){
                distance += TrackingUtility.calculatePolylineLength(polyline).toInt()
            }
            val avgSpeed = round((distance / 1000f) / (currentTimeInMillis / 1000f / 60 / 60) * 10) / 10f
            val dateTimeStamp = Calendar.getInstance().timeInMillis
            val caloriesBurned = ((distance / 1000f) * weight).toInt()
            val ride = Ride(bmp, dateTimeStamp, avgSpeed, distance, currentTimeInMillis, caloriesBurned)
            viewModel.insertRide(ride)
            Snackbar.make(
                requireActivity().findViewById(R.id.rootView),
                "Sikeres mentés",
                Snackbar.LENGTH_LONG
            ).show()
            stopRide()
        }
    }

    //animated camera
    private fun moveCamera(){
        if (pathPoints.isNotEmpty() && pathPoints.last().isNotEmpty()){
            map?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    pathPoints.last().last(),
                    MAP_ZOOM
                )
            )
        }
    }

    //updating tracking data
    private fun  updateTracking(isTracking: Boolean){
        this.isTracking = isTracking
        if (!isTracking && currentTimeInMillis > 0L){
            btnToggleRun.text = "Folytatás"
            btnFinishRun.visibility = View.VISIBLE
        } else if (isTracking) {
            btnToggleRun.text = "Szünet"
            menu?.getItem(0)?.isVisible = true
            btnFinishRun.visibility = View.GONE
        }
    }

    private fun toggleRide(){
        if (isTracking){
            menu?.getItem(0)?.isVisible = true
            sendCommandToService(ACTION_PAUSE_SERVICE)
        } else {
            sendCommandToService(ACTION_START_OR_RESUME_SERVICE)
        }
    }

    private fun subscribeToObservers(){
        TrackingService.isTracking.observe(viewLifecycleOwner, Observer {
            updateTracking(it) })
        TrackingService.pathPoints.observe(viewLifecycleOwner, Observer {
            pathPoints = it
            addLatestPolyline()
            moveCamera()
        })

        TrackingService.timeRideInMillis.observe(viewLifecycleOwner, Observer {
            currentTimeInMillis = it
            val formattedTime = TrackingUtility.getFormattedStopperTime(currentTimeInMillis, true)
            tvTimer.text = formattedTime
        })
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onDestroy() {
        if(mapView != null) {
            mapView.onDestroy();
        }
        super.onDestroy();

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }
}