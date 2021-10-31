package com.ride2wheels_cycling.ui.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.ride2wheels_cycling.R
import com.ride2wheels_cycling.other.Constants
import com.ride2wheels_cycling.other.Constants.KEY_NAME
import com.ride2wheels_cycling.other.Constants.KEY_WEIGHT
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.android.synthetic.main.fragment_setup.*
import kotlinx.android.synthetic.main.fragment_setup.etName
import kotlinx.android.synthetic.main.fragment_setup.etWeight
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : Fragment(R.layout.fragment_settings){

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadFileldsFromPreferences()
        btnApplyChanges.setOnClickListener {
            val success = applyChangesToPreferences()
            if (success) {
                Snackbar.make(view, "Változások mentve", Snackbar.LENGTH_LONG).show()
            } else {
                Snackbar.make(view, "Kérlek töltsd ki az összes mezőt", Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun applyChangesToPreferences(): Boolean{
        val nameText = etName.text.toString()
        val weightText = etWeight.text.toString()
        if (nameText.isEmpty() || weightText.isEmpty()){
            return false
        }
        sharedPreferences.edit()
            .putString(Constants.KEY_NAME, nameText)
            .putFloat(Constants.KEY_WEIGHT, weightText.toFloat())
            .apply()
        val toolbarText = "Üdv, $nameText"
        requireActivity().tvToolbarTitle.text = toolbarText
        return true
    }

    private fun loadFileldsFromPreferences() {
        val name = sharedPreferences.getString(KEY_NAME, "")
        val weight = sharedPreferences.getFloat(KEY_WEIGHT, 80f)
        etName.setText(name)
        etWeight.setText(weight.toString())
        }
    }