package com.ahm.plantcare

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters

class WaterWorker2(appContext: Context, workerParams: WorkerParameters):
    Worker(appContext, workerParams) {


    override fun doWork(): Result {
        val builder: NotificationCompat.Builder = NotificationCompat.Builder(applicationContext)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setContentTitle(inputData.getString("plant_name"))
            .setContentText("آبیاری")
            .setAutoCancel(true)
            .setContentIntent(PendingIntent.getActivity
                (applicationContext, 0, Intent(), 0))

        // Add as notification
        val manager: NotificationManager? = this.applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager!!.notify(NotificationID.iD, builder.build())

        return Result.success()
    }
}