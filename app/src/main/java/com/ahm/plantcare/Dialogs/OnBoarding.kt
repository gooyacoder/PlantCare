package com.ahm.plantcare.Dialogs

import android.R
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout


object OnBoarding {
    private const val sharedPrefFirstTimeKey = "first_time"
    fun checkOnboardingDialog(context: Context, viewGroup: ViewGroup) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        if (!prefs.contains(sharedPrefFirstTimeKey)) {
            showOnboardingDialog1(context, viewGroup)
            val editor = prefs.edit()
            editor.putBoolean(sharedPrefFirstTimeKey, false).apply()
        }
    }

    private fun showOnboardingDialog1(context: Context, viewGroup: ViewGroup) {
        val builder = AlertDialog.Builder(context, R.style.AlertDialogTheme)
        val view: View = LayoutInflater.from(context).inflate(
            R.layout.onboarding_dialog_1,
            viewGroup.findViewById<View>(R.id.layout_dialog_container) as ConstraintLayout
        )
        builder.setView(view)
        val alertDialog = builder.create()
        alertDialog.setOnCancelListener {
            alertDialog.dismiss()
            showOnboardingDialog2(context, viewGroup)
        }
        view.findViewById<View>(R.id.button).setOnClickListener { alertDialog.cancel() }
        if (alertDialog.window != null) {
            alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        alertDialog.show()
    }

    private fun showOnboardingDialog2(context: Context, viewGroup: ViewGroup) {
        val builder = AlertDialog.Builder(context, R.style.AlertDialogTheme)
        val view: View = LayoutInflater.from(context).inflate(
            R.layout.onboarding_dialog_2,
            viewGroup.findViewById<View>(R.id.layout_dialog_container) as ConstraintLayout
        )
        val textView = view.findViewById<View>(R.id.plant_days) as TextView
        textView.text = context.resources.getQuantityString(R.plurals.days, 3)
        builder.setView(view)
        val alertDialog = builder.create()
        alertDialog.setOnCancelListener {
            alertDialog.dismiss()
            showOnboardingDialog3(context, viewGroup)
        }
        view.findViewById<View>(R.id.button).setOnClickListener { alertDialog.cancel() }
        if (alertDialog.window != null) {
            alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        alertDialog.show()
    }

    private fun showOnboardingDialog3(context: Context, viewGroup: ViewGroup) {
        val builder = AlertDialog.Builder(context, R.style.AlertDialogTheme)
        val view: View = LayoutInflater.from(context).inflate(
            R.layout.onboarding_dialog_3,
            viewGroup.findViewById<View>(R.id.layout_dialog_container) as ConstraintLayout
        )
        builder.setView(view)
        val alertDialog = builder.create()
        view.findViewById<View>(R.id.button).setOnClickListener { alertDialog.dismiss() }
        if (alertDialog.window != null) {
            alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        alertDialog.show()
    }
}
