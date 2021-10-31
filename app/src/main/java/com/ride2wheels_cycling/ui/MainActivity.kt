package com.ride2wheels_cycling.ui

import android.Manifest.permission.*
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.*
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.ride2wheels_cycling.R
import com.ride2wheels_cycling.other.Constants.ACTION_SHOW_TRACKING_FRAGMENT
import com.ride2wheels_cycling.other.Constants.REQUEST_CODE_LOCATION_PERMISSION
import com.ride2wheels_cycling.other.TrackingUtility
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import pub.devrel.easypermissions.EasyPermissions
import android.location.LocationManager;

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), OnRequestPermissionsResultCallback {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navigateToTrackingFragmentIfNeeded(intent)

        setSupportActionBar(toolbar)
        bottomNavigationView.setupWithNavController(navHostFragment.findNavController())
        bottomNavigationView.setOnItemReselectedListener { /*Do nothing*/ }
        navHostFragment.findNavController()
            .addOnDestinationChangedListener { _, destination, _ ->
                when(destination.id) {
                    R.id.settingsFragment, R.id.rideFragment, R.id.statisticsFragment ->
                        bottomNavigationView.visibility = View.VISIBLE
                    else -> bottomNavigationView.visibility = View.GONE
                }
            }
/*
        requestPermissions(this, arrayOf(ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION, ACCESS_BACKGROUND_LOCATION), 123 )
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q){
            EasyPermissions.requestPermissions(
                this,
                "Az alkalmazás használatához engedélyeznie kell a helymeghatározási szolgáltatást.",
                REQUEST_CODE_LOCATION_PERMISSION,
                ACCESS_COARSE_LOCATION,
                ACCESS_FINE_LOCATION
            )
        } else{
            EasyPermissions.requestPermissions(
                this,
                "Az alkalmazás használatához engedélyeznie kell a helymeghatározási szolgáltatást.",
                REQUEST_CODE_LOCATION_PERMISSION,
                ACCESS_COARSE_LOCATION,
                ACCESS_FINE_LOCATION,
                ACCESS_BACKGROUND_LOCATION
            )
        }*/
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        navigateToTrackingFragmentIfNeeded(intent)
    }

    private fun navigateToTrackingFragmentIfNeeded(intent: Intent?){
        if (intent?.action == ACTION_SHOW_TRACKING_FRAGMENT){
            navHostFragment.findNavController().navigate(R.id.action_global_trackingFragment)
        }
    }
/*
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }*/

}