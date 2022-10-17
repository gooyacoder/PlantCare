package com.ahm.plantcare

import android.app.AlarmManager
import android.app.AlarmManager.RTC_WAKEUP
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AbsListView.CHOICE_MODE_SINGLE
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.work.Data
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import kotlinx.android.synthetic.main.activity_edit_plant.*
import java.util.*
import java.util.concurrent.TimeUnit


class EditPlantActivity : AppCompatActivity() {

    var list_position = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_plant)
        val items = arrayOf("آهن", "۲۰-۲۰-۲۰", "۱۲-۱۲-۳۶", "۱۰-۵۲-۱۰",
            "هیومیک اسید", "قارچ کش", "اسید آمینه", "جلبک دریایی")
        val adapter: ArrayAdapter<Any?> = ArrayAdapter<Any?>(
            this, R.layout.custom_spinner_view, items)
        fertilizerDropDownSpinner.setAdapter(adapter)
        var plant_name = intent.getStringExtra("plantName")
        plantName.text = plant_name
        fertilizer_list.choiceMode = CHOICE_MODE_SINGLE
        fertilizer_list.setOnItemClickListener { parent, view, position, id ->
            list_position = position

        }
        updateFertilizerList()
        updateCurrentWatering()

    }


    fun closeButtonClicked(view: View) {
        finish()
    }

    fun fertilizerBtnClicked(view: View) {
        var db = FertilizerDatabaseHelper(this)
        var plant_name = intent.getStringExtra("plantName")
        var fertilizer_name = fertilizerDropDownSpinner.selectedItem.toString()
        var days: Int = fertilizing_days.text.toString().toInt()
        val data = Data.Builder()
        data.putString("plant_name", plant_name)
        data.putString("fertilizer_name", fertilizer_name)
        val fertilizdrWorkRequest: WorkRequest =
            PeriodicWorkRequestBuilder<FertilizerWorker>(
                days.toLong()*24*60,
                TimeUnit.MINUTES,
            )
                .setInitialDelay(days.toLong()*24*60, TimeUnit.MINUTES)
                .setInputData(data.build())
                .build()

        val result = WorkManager
            .getInstance(applicationContext)
            .enqueue(fertilizdrWorkRequest)
        val request_id = fertilizdrWorkRequest.id
        db.addFertilizer(plant_name, fertilizer_name, days.toString(), request_id.toString())
        db.close()

        updateFertilizerList()

    }

    private fun setAlarm(plantName: String, repeat: Long, id: Int) {


        // creating alarmManager instance
        val alarmManager = this.applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        // adding intent and pending intent to go to AlarmReceiver Class in future
        val intent = Intent(applicationContext, AlarmReceiver::class.java)
        intent.putExtra("plant_name", plantName)
        val pendingIntent = PendingIntent.getBroadcast(applicationContext,
            id, intent, PendingIntent.FLAG_IMMUTABLE)
        // when using setAlarmClock() it displays a notification until alarm rings and when pressed it takes us to mainActivity
        val mainActivityIntent = Intent(applicationContext, MainActivity::class.java)
        val basicPendingIntent = PendingIntent.getActivity(applicationContext,
            id, mainActivityIntent, PendingIntent.FLAG_IMMUTABLE)
        // setting the alarm
        alarmManager.setRepeating(RTC_WAKEUP, repeat, repeat, pendingIntent)
    }

    fun day_to_millisec(days: Long) : Long{
        return days*24*60*60*1000
    }

    fun wateringBtnClicked(view: View) {
        val db = FertilizerDatabaseHelper(this)
        val plant_name = intent.getStringExtra("plantName")
        val days: Long = watering_days.text.toString().toLong()
        val id = NotificationID.iD
        setAlarm(plant_name!!, day_to_millisec(days), id)
//        val data = Data.Builder()
//        data.putString("plant_name", plant_name)
//        //data.putString("fertilizer_name", fertilizer_name)
//        val wateringWorkRequest: WorkRequest =
//            PeriodicWorkRequestBuilder<WaterWorker>(
//                days.toLong()*15,
//                TimeUnit.MINUTES,
//            )
//                .setInitialDelay(days.toLong()*15, TimeUnit.MINUTES)
//                .setInputData(data.build())
//                .build()
//
//        val result = WorkManager
//            .getInstance(applicationContext)
//            .enqueue(wateringWorkRequest)
//        val request_id = wateringWorkRequest.id
        db.addWateringEntry(plant_name, days.toString(), id.toString())
        db.close()
        updateCurrentWatering()
    }

    fun removeFertilizerBtnClicked(view: View) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Are you sure you want to Delete Fertilizer?")
            .setCancelable(false)
            .setPositiveButton("Yes") { dialog, id ->
                var plant_name = intent.getStringExtra("plantName")

                var db = FertilizerDatabaseHelper(this)
                var fertilizer_name = fertilizer_list.adapter.
                getItem(fertilizer_list.checkedItemPosition)
                    .toString()
                var fertilizer_id = db.getFertilizerId(plant_name, fertilizer_name)

                var uuid = UUID.fromString(fertilizer_id)
                WorkManager.getInstance(this).cancelWorkById(uuid)

                db.removeFertilizer(plant_name, fertilizer_name)

                db.close()
                updateFertilizerList()
            }
            .setNegativeButton("No") { dialog, id ->
                // Dismiss the dialog
                dialog.dismiss()
            }
        val alert = builder.create()
        alert.show()

    }

    private fun updateFertilizerList() {
        var db = FertilizerDatabaseHelper(this)
        var fertilizers = db.getFertilizers(intent.getStringExtra("plantName"))
        val fertilizerAdapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1)
        for(fertilizer in fertilizers){

            Thread(Runnable {
                runOnUiThread{
                    fertilizerAdapter.add(fertilizer.fertilizerName)
                }
            }).start()
        }
        fertilizer_list.adapter = fertilizerAdapter
        db.close()
    }

    private fun updateCurrentWatering() {
        var db = FertilizerDatabaseHelper(this)
        var water = db.getWatering(intent.getStringExtra("plantName"))
        if(water.days != null){
            currentWatering.text = water.days + " روز "
        }else{
            currentWatering.text = ""
        }
        db.close()
    }

    fun removeWateringBtnClicked(view: View) {
        // cancel watering by getting the watering id of the plant from database
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Are you sure you want to Delete Watering?")
            .setCancelable(false)
            .setPositiveButton("Yes") { dialog, id ->
                var plant_name = intent.getStringExtra("plantName")

                var db = FertilizerDatabaseHelper(this)

                var watering_id = db.getWateringId(plant_name)

                var uuid = UUID.fromString(watering_id)
                WorkManager.getInstance(this).cancelWorkById(uuid)

                db.removeWatering(plant_name)

                db.close()
                updateCurrentWatering()
            }
            .setNegativeButton("No") { dialog, id ->
                // Dismiss the dialog
                dialog.dismiss()
            }
        val alert = builder.create()
        alert.show()
    }


}