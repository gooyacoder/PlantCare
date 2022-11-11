package com.ahm.plantcare


import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.*
import java.util.concurrent.TimeUnit


class FertilizerWorker(appContext: Context, workerParams: WorkerParameters):
    Worker(appContext, workerParams) {


    override fun doWork(): Result {
        val db = FertilizerDatabaseHelper(applicationContext)
        val plant_name = inputData.getString("plant_name")
        val fertilizer_name = inputData.getString("fertilizer_name")
        val days = inputData.getString("days")
        val data = Data.Builder()
        data.putString("plant_name", plant_name)
        data.putString("fertilizer_name", fertilizer_name)

        val myWorkBuilder = PeriodicWorkRequest.Builder(
            FertilizerWorker2::class.java,
            days!!.toLong(),
            TimeUnit.DAYS
        )
        val constraints: Constraints = Constraints.Builder()
            .build()
        val myWork = myWorkBuilder
            .setConstraints(constraints)
            .setInputData(data.build())
            .build()
        val workManager = WorkManager.getInstance(applicationContext)
        val work_id = myWork.id
        workManager.enqueueUniquePeriodicWork("fertilizerworker2", ExistingPeriodicWorkPolicy.KEEP, myWork)
        db.addFertilizer(plant_name, fertilizer_name, days, work_id.toString())
        db.close()

        return Result.success()
    }
}