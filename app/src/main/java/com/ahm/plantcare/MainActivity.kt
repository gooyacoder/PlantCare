package com.ahm.plantcare

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.AlarmManager.RTC_WAKEUP
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.AlarmManagerCompat.setExact
import androidx.core.view.iterator
import androidx.core.view.size
import androidx.work.WorkManager
import kotlinx.android.synthetic.main.row_item.*
import java.util.*


class MainActivity : AppCompatActivity() {

    var db: DatabaseHelper? = null
    var plants = mutableListOf<Plant>()
    var plant_names_list : MutableList<String> = mutableListOf()
    var plant_images_list : MutableList<Bitmap> = mutableListOf()
    var listView: ListView? = null


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        getSupportActionBar()?.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar()?.setCustomView(R.layout.app_title);

        prepageUI()
    }

    private fun prepageUI() {
        listView = findViewById<ListView>(R.id.list)
    }

    fun fabAddClicked(view: View) {
        //Toast.makeText(applicationContext, "Add New Plant", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, AddPlantActivity::class.java)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        UpdatePlantsList()
    }

    private fun UpdatePlantsList() {
        db = DatabaseHelper(this)
        plants = db!!.getPlants()
        db!!.close()
        plant_images_list.clear()
        plant_names_list.clear()
        if (plants!!.size > 0) {
            for (plant in plants!!) {
                val plant_image = DbBitmapUtility.getImage(plant.image)
                plant_images_list.add(plant_image)
                val plant_name = plant.name
                plant_names_list.add(plant_name)

            }
        }
        val customPlantList = CustomPlantList(this, plant_names_list, plant_images_list)
        listView?.adapter = customPlantList
    }

    fun editButtonClicked(view: View) {
        var intent = Intent(this, EditPlantActivity::class.java)
        var position = listView!!.getPositionForView(view)
        db = DatabaseHelper(this)
        plants = db!!.getPlants()
        db!!.close()
        var name:String = plants[position].name
        intent.putExtra("plantName", name)
        startActivity(intent)
    }

    fun deleteButtonClicked(view: View) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Are you sure you want to delete this Plant?")
            .setCancelable(false)
            .setPositiveButton("Yes") { dialog, id ->

                var plant_name = textViewPlantName.text.toString()
                var db = FertilizerDatabaseHelper(this)
                var fertilizers = db.getFertilizers(plant_name)
                if(fertilizers.size > 0){
                    for (fertilizer in fertilizers){
                        var fertilizer_id = fertilizer.fertilizer_id
                        var uuid = UUID.fromString(fertilizer_id)
                        WorkManager.getInstance(this).cancelWorkById(uuid)
                    }
                }
                var watering = db.getWatering(plant_name)
                if(watering != null){
                    var watering_id = db.getWateringId(plant_name)
                    var uuid = UUID.fromString(watering_id)
                    WorkManager.getInstance(this).cancelWorkById(uuid)
                }
                db.close()
                var plant_db = DatabaseHelper(this)
                plant_db.removePlant(plant_name)
                plant_db.close()
                UpdatePlantsList()

            }
            .setNegativeButton("No") { dialog, id ->
                // Dismiss the dialog
                dialog.dismiss()
            }
        val alert = builder.create()
        alert.show()
    }

}
