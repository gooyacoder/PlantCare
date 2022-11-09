package com.ahm.plantcare.Services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationManagerCompat
import com.ahm.plantcare.Plant
import com.ahm.plantcare.Utils.CommunicationKeys
import com.jakewharton.threetenabp.AndroidThreeTen
import com.wateria.DataStructures.PlantList
import com.wateria.Notifications.NotificationClass


class WaterPlantService : Service() {
    override fun onBind(intent: Intent): IBinder? {
//        // TODO: Return the communication channel to the service.
//        throw new UnsupportedOperationException("Not yet implemented");
        return null
    }

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(applicationContext)
        //        android.os.Debug.waitForDebugger();
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        // 1º Get plantList from memory
        val plantList: PlantList = PlantList.getInstance(this)

        // 2º Get specific plant to water
        val plantToWater: Plant =
            intent.getParcelableExtra(CommunicationKeys.NotificationClass_WaterSinglePlantService_PlantToWater)

        // 3º Water specific plant and save(if found)
        val index: Int = plantList.find(plantToWater)
        if (index != -1) {
            plantList.waterPlant(index)
        }

        // 4º Remove notification from status bar              TODO: remove notification just on the oncreate, for better user exp
        val notificationManager = NotificationManagerCompat.from(
            applicationContext
        )
        notificationManager.cancel(NotificationClass.notificationId)
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
