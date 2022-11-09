package com.ahm.plantcare.Notifications

import android.R
import android.content.Context
import com.ahm.plantcare.Plant


class MultiplePlantsNotifBuilder(private val plantList: ArrayList<Plant>, context: Context?) :
    NotifBuilder(context) {
    protected fun addSpecificFeatures() {
        builder.setContentTitle(context.getResources().getString(R.string.app_name))
        val text = plantList.size.toString() + " " + context.getResources()
            .getString(R.string.notification_title_several_plants)
        builder.setContentText(text)
        builder.setNumber(plantList.size) // For badge next to icon on Android8+ devices
    }
}
