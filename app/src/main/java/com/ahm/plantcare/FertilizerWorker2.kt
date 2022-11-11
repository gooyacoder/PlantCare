package com.ahm.plantcare

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters


class FertilizerWorker2(appContext: Context, workerParams: WorkerParameters):
    Worker(appContext, workerParams) {

    override fun doWork(): Result {
        // show notification
        var builder = NotificationCompat.Builder(applicationContext)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setContentTitle(inputData.getString("plant_name"))
            .setContentText(inputData.getString("fertilizer_name"))
            .setAutoCancel(true)
            .setContentIntent(PendingIntent.getActivity
                (applicationContext, 0, Intent(), 0))

        val manager: NotificationManager? = this.applicationContext
            .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager!!.notify(NotificationID.iD, builder.build())
        return Result.success()
    }
}