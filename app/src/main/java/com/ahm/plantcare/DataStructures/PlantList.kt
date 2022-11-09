package com.ahm.plantcare.DataStructures

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.ahm.plantcare.Utils.IconTagDecoder.idToDrawable
import com.ahm.plantcare.Utils.JsonEncoder.readPlantList
import com.ahm.plantcare.Utils.JsonEncoder.writePlantList
import org.threeten.bp.LocalDate
import org.threeten.bp.temporal.ChronoUnit
import java.util.*


class PlantList private constructor(context: Context) {
    private var plantList: ArrayList<Plant>
    private val prefs: SharedPreferences
    private val appContext: Context

    init {
        plantList = ArrayList()
        appContext = context.applicationContext
        prefs = PreferenceManager.getDefaultSharedPreferences(appContext)
    }

    private fun loadFromPrefs() {
        val json = prefs.getString(sharedPrefPlantListKey, null)
        if (json != null) {
            plantList = readPlantList(json)
            setDaysRemaining()
            sort()
            setIcons()
        } else {
            plantList = ArrayList()
        }
    }

    private fun saveToPrefs() {
        val json = writePlantList(plantList)
        val editor = prefs.edit()
        editor.putString(sharedPrefPlantListKey, json)
        editor.apply()
    }

    fun deleteAll(context: Context?) {
        plantList.clear()
        saveToPrefs()
    }

    /**
     * Shouldn't modify the returned plant
     */
    operator fun get(position: Int): Plant {
        return plantList[position]
    }

    private fun sort() {
        Collections.sort(plantList)
    }

    private fun setIcons() {
        for (plant in plantList) {
            plant.icon = idToDrawable(appContext, plant.iconId)
        }
    }

    private fun setDaysRemaining() {
        for (plant in plantList) {
            plant.computeDaysRemaining()
        }
    }

    fun waterPlant(position: Int): Int {
        val currentPlant = plantList[position]
        currentPlant.water()
        Collections.sort(plantList)
        saveToPrefs()
        return plantList.indexOf(currentPlant)
    }

    fun find(plant: Plant): Int {
        return plantList.indexOf(plant)
    }

    fun exists(plantName: String): Boolean {
        for (plant in plantList) {
            if (plant.plantName == plantName) {
                return true
            }
        }
        return false
    }

    fun get0daysRemSublist(): ArrayList<Plant> {
        val sublist = ArrayList<Plant>()
        for (plant in plantList) {
            if (plant.daysRemaining <= 0) {
                sublist.add(plant)
            }
        }
        return sublist
    }

    fun insertPlant(plant: Plant): Int {
        // Compute daysRemaining of new:
        plant.daysRemaining = LocalDate.now().until(plant.getNextWateringDate(), ChronoUnit.DAYS)
        // Insert to the list:
        plantList.add(plant)
        // Sort the list:
        Collections.sort(plantList)
        //Save the list:
        saveToPrefs()
        // Adapter:
        return plantList.indexOf(plant)
    }

    fun removePlant(position: Int): Int {
        return if (position >= 0 && position < plantList.size) {
            plantList.removeAt(position)
            saveToPrefs()
            position
        } else {
            throw ArrayIndexOutOfBoundsException()
        }
    }

    fun modifyPlant(plant: Plant, prevPosInPlantList: Int): Int {
        return if (prevPosInPlantList >= 0 && prevPosInPlantList < plantList.size) {
            // Compute daysRemaining of new:
            plant.daysRemaining =
                LocalDate.now().until(plant.getNextWateringDate(), ChronoUnit.DAYS)
            plantList[prevPosInPlantList] = plant
            Collections.sort(plantList)
            saveToPrefs()
            plantList.indexOf(plant)
        } else {
            throw ArrayIndexOutOfBoundsException()
        }
    }

    val size: Int
        get() = plantList.size

    companion object {
        private var instance: PlantList? = null
        private const val sharedPrefPlantListKey = "plantlistkey"
        fun getInstance(context: Context): PlantList? {
            if (instance == null) {
                instance = PlantList(context)
                instance!!.loadFromPrefs()
            }
            return instance
        }
    }
}
