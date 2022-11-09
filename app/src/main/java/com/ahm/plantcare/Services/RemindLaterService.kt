package com.ahm.plantcare.Services

import android.R
import android.app.Service
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.widget.Toast
import androidx.core.app.NotificationManagerCompat
import com.jakewharton.threetenabp.AndroidThreeTen
import com.wateria.DataStructures.Settings
import com.wateria.JobSchedulers.NotificationJobService
import com.wateria.JobSchedulers.RemindLaterNotificationJobService
import com.wateria.Notifications.NotificationClass


class RemindLaterService : Service() {
    private var appContext: Context? = null
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
        AndroidThreeTen.init(appContext)

        // Remove notification from status bar
        val notificationManager = NotificationManagerCompat.from(appContext)
        notificationManager.cancel(NotificationClass.notificationId)
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        // 1º Get elapse time from memory
        val hoursToDelay: Int = Settings(appContext).getNotifRepetInterval()
        val jobScheduler = getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler
        val serviceName = ComponentName(packageName, NotificationJobService::class.java.getName())
        val builder =
            JobInfo.Builder(RemindLaterNotificationJobService.REMIND_LATER_JOB_ID, serviceName)
                .setPersisted(true)
                .setMinimumLatency((hoursToDelay * 60 * 60 * 1000).toLong())
        val jobInfo = builder.build()
        jobScheduler.schedule(jobInfo)
        val message = (resources.getString(R.string.notif_postpone_toast_message) + " "
                + hoursToDelay + " " + resources.getQuantityString(R.plurals.hours, hoursToDelay))
        Toast.makeText(appContext, message, Toast.LENGTH_LONG).show()
        return START_NOT_STICKY
    }
}
