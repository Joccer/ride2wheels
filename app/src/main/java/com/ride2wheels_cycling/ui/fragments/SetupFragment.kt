package com.ride2wheels_cycling.ui.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.ride2wheels_cycling.R
import com.ride2wheels_cycling.other.Constants.KEY_FIRST_TIME_TOGGLE
import com.ride2wheels_cycling.other.Constants.KEY_NAME
import com.ride2wheels_cycling.other.Constants.KEY_WEIGHT
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_setup.*
import javax.inject.Inject

@AndroidEntryPoint
class SetupFragment : Fragment(R.layout.fragment_setup){

    @Inject
    lateinit var preferences: SharedPreferences

    @set:Inject
    var isFirstAppOpen = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!isFirstAppOpen){
            val navOptions = NavOptions.Builder().setPopUpTo(R.id.setupFragment, true).build()
            findNavController().navigate(R.id.action_setupFragment_to_rideFragment, savedInstanceState, navOptions)
        }

        tvContinue.setOnClickListener {
            val success = writePersonalDataToPreferences()
            if (success){
                findNavController().navigate(R.id.action_setupFragment_to_rideFragment)
            } else{
                Snackbar.make(requireView(), "Kérlek töltsd ki az összes mezőt", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun writePersonalDataToPreferences(): Boolean{
        val name = etName.text.toString()
        val weight = etWeight.text.toString()
        if (name.isEmpty() || weight.isEmpty()){
            return false
        }
        preferences.edit()
            .putString(KEY_NAME, name)
            .putFloat(KEY_WEIGHT, weight.toFloat())
            .putBoolean(KEY_FIRST_TIME_TOGGLE, false)
            .apply()
        val toolbarText = "Üdv, $name"
        requireActivity().tvToolbarTitle.text = toolbarText
        return true
    }
}