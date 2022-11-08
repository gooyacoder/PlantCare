package com.ahm.plantcare.Utils

import android.R
import android.content.Context
import android.os.Bundle
import com.ahm.plantcare.Plant
import com.google.firebase.analytics.FirebaseAnalytics
import org.threeten.bp.LocalDate


object MyFirebaseLogger {
    private const val new_plant_event = "new_plant_event"
    private const val new_plant_param_name = "new_plant_name"
    private const val new_plant_param_icon_changed = "new_plant_icon_changed"
    private const val new_plant_param_first_wat_used = "new_plant_first_wat_used"
    private const val tip_event = "tip_event"
    private const val tip_param_idx = "tip_idx"
    private const val tip_param_time_diff = "tip_time_diff"
    fun logNewPlant(context: Context?, plant: Plant) {
        val firebaseAnalytics: FirebaseAnalytics = FirebaseAnalytics.getInstance(context)
        val bundle = Bundle()
        bundle.putString(new_plant_param_name, plant.getPlantName())
        bundle.putBoolean(
            new_plant_param_icon_changed,
            plant.getIconId() !== R.drawable.ic_common_1
        )
        bundle.putBoolean(
            new_plant_param_first_wat_used,
            plant.getNextWateringDate().getDayOfYear() !== LocalDate.now()
                .plusDays(plant.getWateringFrequency()).getDayOfYear()
        )
        firebaseAnalytics.setDefaultEventParameters(bundle)
        firebaseAnalytics.logEvent(new_plant_event, bundle)
    }

    fun logTip(context: Context?, tipIdx: Int, timeDiff: Int) {
        val firebaseAnalytics: FirebaseAnalytics = FirebaseAnalytics.getInstance(context)
        val bundle = Bundle()
        bundle.putInt(tip_param_idx, tipIdx)
        bundle.putInt(tip_param_time_diff, timeDiff)
        firebaseAnalytics.logEvent(tip_event, bundle)
    }
}
