package com.ahm.plantcare.JobSchedulers

import android.app.job.JobInfo
import android.app.job.JobParameters
import android.app.job.JobScheduler
import android.app.job.JobService
import android.content.ComponentName
import android.content.Context
import com.ahm.plantcare.Notifications.NotificationClass.createNotificationChannel
import com.ahm.plantcare.Notifications.NotificationClass.pushNotification
import com.ahm.plantcare.Plant
import com.jakewharton.threetenabp.AndroidThreeTen
import com.wateria.DataStructures.PlantList
import com.wateria.DataStructures.Settings
import java.util.*


class NotificationJobService : JobService() {
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

        // Schedule next one:
        scheduleNextJob(context)
        return false
    }

    override fun onStopJob(params: JobParameters): Boolean {
        return true
    }

    companion object {
        const val NOTIF_JOB_ID = 0
        fun scheduleNextJob(context: Context) {
            val jobScheduler = context.getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler
            val serviceName = ComponentName(
                context.packageName,
                NotificationJobService::class.java.name
            )
            val builder = JobInfo.Builder(NOTIF_JOB_ID, serviceName)
                .setPersisted(true)
                .setMinimumLatency(computeTriggerTime(context))
            val jobInfo = builder.build()
            jobScheduler.schedule(jobInfo)
        }

        fun cancelScheduledJob(context: Context) {
            val jobScheduler = context.getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler
            if (!jobScheduler.allPendingJobs.isEmpty()) {
                jobScheduler.cancel(NOTIF_JOB_ID)
                jobScheduler.cancel(RemindLaterNotificationJobService.REMIND_LATER_JOB_ID)
                /*
            for (JobInfo jobInfo : jobScheduler.getAllPendingJobs()){
                if(jobInfo.getId() == NOTIF_JOB_ID){
                    jobScheduler.cancel(NOTIF_JOB_ID);
                }
                else if(jobInfo.getId() == RemindLaterNotificationJobService.REMIND_LATER_JOB_ID)){
                    jobScheduler.cancel(RemindLaterNotificationJobService.REMIND_LATER_JOB_ID);
                }
            }
            */
            }
        }

        private fun computeTriggerTime(context: Context): Long {
            val settings = Settings(context)
            val now = Calendar.getInstance()
            val triggerMoment = Calendar.getInstance()
            triggerMoment[Calendar.HOUR_OF_DAY] = settings.getNotifHour()
            triggerMoment[Calendar.MINUTE] = settings.getNotifMinute()
            triggerMoment[Calendar.SECOND] = settings.getNotifSecond()
            if (now.compareTo(triggerMoment) > 0) {
                // Set the tomorrows's alarm because today's moment have already passed
                triggerMoment.add(Calendar.DATE, 1)
            }
            return triggerMoment.timeInMillis - now.timeInMillis
        }
    }
}
