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
import com.ahm.plantcare.Utils.MyFirebaseLogger.logTip
import org.threeten.bp.LocalDate
import java.util.*


object TipOfTheDay {
    private const val sharedPrefTipIdx = "tip_idx"
    private const val sharedPrefLastDay = "last_day"
    private const val MAX_TIPS = 7
    fun showTip(context: Context, viewGroup: ViewGroup) {
        val dialog = AlertDialog.Builder(context, R.style.AlertDialogTheme).create()
        if (dialog.window != null) {
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }

        // Depends on tipIdx
        val view = decideView(context, viewGroup)

        // Depends on time
        val unlockText = view.findViewById<View>(R.id.text_unlock) as TextView
        unlockText.text =
            context.resources.getString(R.string.tip_unlock_text, computeHours(), computeMins())
        view.findViewById<View>(R.id.button).setOnClickListener { dialog.cancel() }
        dialog.setView(view)
        dialog.show()
    }

    private fun decideView(context: Context, viewGroup: ViewGroup): View {
        val tipIdx = decideTipIdx(context)
        val rootView: ViewGroup =
            viewGroup.findViewById<View>(R.id.layout_dialog_container) as ConstraintLayout
        return when (tipIdx % MAX_TIPS) {
            0 -> LayoutInflater.from(context).inflate(R.layout.tip_fertilizer_dialog, rootView)
            1 -> LayoutInflater.from(context).inflate(R.layout.tip_repotting_dialog, rootView)
            2 -> LayoutInflater.from(context).inflate(R.layout.tip_spray_dialog, rootView)
            3 -> LayoutInflater.from(context).inflate(R.layout.tip_brain_dialog, rootView)
            4 -> LayoutInflater.from(context).inflate(R.layout.tip_purify_dialog, rootView)
            5 -> LayoutInflater.from(context).inflate(R.layout.tip_finger_dialog, rootView)
            6 -> LayoutInflater.from(context).inflate(R.layout.tip_window_dialog, rootView)
            else -> LayoutInflater.from(context).inflate(R.layout.tip_fertilizer_dialog, rootView)
        }
    }

    private fun decideTipIdx(context: Context): Int {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        var tipIdx = prefs.getInt(sharedPrefTipIdx, 0)
        val today: Int = LocalDate.now().getDayOfYear()
        val lastDay = prefs.getInt(sharedPrefLastDay, today)
        if (lastDay != today) {   //Show a new tip every day
            tipIdx++
        }
        prefs.edit().putInt(sharedPrefTipIdx, tipIdx).putInt(sharedPrefLastDay, today).apply()
        logTip(context, tipIdx, today - lastDay)
        return tipIdx
    }

    private fun computeMins(): Int {
        return (60 - Calendar.getInstance()[Calendar.MINUTE]) % 60 // modulus to change case of 60-0=60  -> to 0
    }

    private fun computeHours(): Int {
        val hours = 24 - Calendar.getInstance()[Calendar.HOUR_OF_DAY]
        return if (computeMins() > 0) hours - 1 else hours // -1 because offset is counted on minutes
    }
}
