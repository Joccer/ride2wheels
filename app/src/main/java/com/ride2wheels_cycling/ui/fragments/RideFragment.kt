package com.ride2wheels_cycling.ui.fragments

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.ride2wheels_cycling.R
import com.ride2wheels_cycling.adapters.RideAdapter
import com.ride2wheels_cycling.other.Constants.REQUEST_CODE_LOCATION_PERMISSION
import com.ride2wheels_cycling.other.SortType
import com.ride2wheels_cycling.other.TrackingUtility
import com.ride2wheels_cycling.ui.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_ride.*
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import android.content.pm.PackageManager

import androidx.annotation.NonNull




@AndroidEntryPoint
class RideFragment : Fragment(R.layout.fragment_ride), EasyPermissions.PermissionCallbacks {

    private val viewModel: MainViewModel by viewModels()

    private lateinit var rideAdapter: RideAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requestPermissions()
        setupRecycleView()

        when(viewModel.sortType){
            SortType.DATE -> spFilter.setSelection(0)
            SortType.AVG_SPEED -> spFilter.setSelection(1)
            SortType.DISTANCE -> spFilter.setSelection(2)
            SortType.DURATION -> spFilter.setSelection(3)
            SortType.BURNED_CALORIES -> spFilter.setSelection(4)
        }

        spFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                when(pos){
                    0 -> viewModel.sortRides(SortType.DATE)
                    1 -> viewModel.sortRides(SortType.AVG_SPEED)
                    2 -> viewModel.sortRides(SortType.DISTANCE)
                    3 -> viewModel.sortRides(SortType.DURATION)
                    4 -> viewModel.sortRides(SortType.BURNED_CALORIES)
                }
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }
        viewModel.rides.observe(viewLifecycleOwner, Observer { rideAdapter.submitList(it) })
        newActivity.setOnClickListener {
            findNavController().navigate(R.id.action_rideFragment_to_trackingFragment)
        }

        //swipe and delete rides
        val itemTouchHelperCallBack = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.ACTION_STATE_IDLE,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val ride = rideAdapter.differ.currentList[position]
                viewModel.deleteRide(ride)
                Snackbar.make(requireActivity().findViewById(R.id.rootView), "Aktivitás sikeresen törölve", Snackbar.LENGTH_LONG).apply {
                    setAction("Visszavonás"){
                        viewModel.insertRide(ride)
                    }
                    show()
                }
            }
        }

        ItemTouchHelper(itemTouchHelperCallBack).apply {
            attachToRecyclerView(rvRides)
        }
    }

    private fun requestPermissions(){
        if(TrackingUtility.hasLocationPermissions(requireContext())){
            return
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q){
            EasyPermissions.requestPermissions(
                this,
                "Az alkalmazás használatához engedélyeznie kell a helymeghatározási szolgáltatást.",
                REQUEST_CODE_LOCATION_PERMISSION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        } else{
            EasyPermissions.requestPermissions(
                this,
                "Az alkalmazás használatához engedélyeznie kell a helymeghatározási szolgáltatást.",
                REQUEST_CODE_LOCATION_PERMISSION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        }
    }

    private fun setupRecycleView() = rvRides.apply {
        rideAdapter = RideAdapter()
        adapter = rideAdapter
        layoutManager = LinearLayoutManager(requireContext())
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        Snackbar.make(requireActivity().findViewById(R.id.rootView), "Permission Granted", Snackbar.LENGTH_LONG).apply {
            show()
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if(EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        }else {
            requestPermissions()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

}