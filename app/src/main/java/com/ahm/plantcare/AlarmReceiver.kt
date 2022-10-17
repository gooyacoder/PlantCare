package com.ahm.plantcare

import android.R
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat



class AlarmReceiver : BroadcastReceiver() {


    override fun onReceive(p0: Context?, p1: Intent?) {
        val builder: NotificationCompat.Builder = NotificationCompat
            .Builder(p0!!)
            .setSmallIcon(R.drawable.ic_dialog_info)
            .setContentTitle(p1!!.getStringExtra("plant_name"))
            .setContentText("آبیاری")
            .setAutoCancel(true)
            .setContentIntent(
                PendingIntent.getActivity
                (p0, 0, Intent(), 0))

        // Add as notification
        val manager: NotificationManager? = p0?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager!!.notify(NotificationID.iD, builder.build())
    }
}