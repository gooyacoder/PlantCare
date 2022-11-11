package com.ahm.plantcare


import android.content.Context
import androidx.work.*
import java.util.concurrent.TimeUnit

class WaterWorker(appContext: Context, workerParams: WorkerParameters):
    Worker(appContext, workerParams) {


    override fun doWork(): Result {
        val db = FertilizerDatabaseHelper(applicationContext)
        val plant_name = inputData.getString("plant_name")
        val days = inputData.getString("days")
        val data = Data.Builder()
        data.putString("plant_name", plant_name)

        val myWorkBuilder = PeriodicWorkRequest.Builder(
            WaterWorker2::class.java,
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
        workManager.enqueueUniquePeriodicWork("waterworker2",
            ExistingPeriodicWorkPolicy.KEEP, myWork)
        db.addWateringEntry(plant_name, days, work_id.toString())
        db.close()


        return Result.success()
    }
}