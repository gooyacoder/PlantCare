package com.ahm.plantcare.Notifications

import android.R
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationManagerCompat
import com.ahm.plantcare.Plant


object NotificationClass {
    const val notificationId = 1
    const val CHANNEL_ID = "channel" // For notification
    fun createNotificationChannel(mainContext: Context) {
        // SHOULD BE CALLED ONSTART (doesn't mind if called repeatedly)

        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = mainContext.resources.getString(R.string.notif_channel_name)
            val description = mainContext.resources.getString(R.string.notif_channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance)
            channel.description = description
            val notificationManager = mainContext.getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun pushNotification(context: Context?, zeroDaysRemList: ArrayList<Plant?>) {
        val myBuilder: NotifBuilder
        if (zeroDaysRemList.size <= 1) {
            myBuilder = SinglePlantNotifBuilder(zeroDaysRemList[0], context)
        } else {
            myBuilder = MultiplePlantsNotifBuilder(zeroDaysRemList, context)
        }
        val notificationManager = NotificationManagerCompat.from(context!!)
        notificationManager.notify(notificationId, myBuilder.getBuilder()!!.build())
    }
}
