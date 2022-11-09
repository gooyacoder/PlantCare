package com.ahm.plantcare.JobSchedulers

import android.app.job.JobParameters
import android.app.job.JobService
import com.ahm.plantcare.Notifications.NotificationClass.createNotificationChannel
import com.ahm.plantcare.Notifications.NotificationClass.pushNotification
import com.ahm.plantcare.Plant
import com.jakewharton.threetenabp.AndroidThreeTen
import com.wateria.DataStructures.PlantList
import com.wateria.DataStructures.Settings


class RemindLaterNotificationJobService : JobService() {
    override fun onStartJob(params: JobParameters): Boolean {
        // Create the new notification
        val context = applicationContext
        AndroidThreeTen.init(context)
        val settings = Settings(context)

        // Get a sublist filled with the plants that need to be watered (0 days remaining)
        val zeroDaysList: ArrayList<Plant?> = PlantList.getInstance(this).get0daysRemSublist()
        if (zeroDaysList.size > 0 && settings.getNotifEnabled()) {
            //Compute notifications
            createNotificationChannel(context)
            pushNotification(context, zeroDaysList)
        }

        // No rescheduling
        return false
    }

    override fun onStopJob(params: JobParameters): Boolean {
        return true
    }

    companion object {
        const val REMIND_LATER_JOB_ID = 1
    }
}
