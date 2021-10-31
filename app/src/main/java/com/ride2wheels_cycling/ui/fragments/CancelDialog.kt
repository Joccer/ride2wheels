package com.ride2wheels_cycling.ui.fragments

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ride2wheels_cycling.R

class CancelDialog : DialogFragment() {

    private var positiveListener : (() -> Unit)? = null

    fun setPositiveListener(listener: () -> Unit) {
        positiveListener = listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return  MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
            .setTitle("Megszakítja az aktivitást?")
            .setMessage("A megszakítás funkció mentés nélkül törli az összes eddigi adatot.\n\n Biztosan megszakítja?")
            .setIcon(R.drawable.ic_delete)
            .setNegativeButton("Mégse") { dialogInterface, _ -> dialogInterface.cancel()}
            .setPositiveButton("Megszakítás") { _, _ -> positiveListener?.let { yes -> yes() } }
            .create()
    }

}