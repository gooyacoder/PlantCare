package com.ahm.plantcare.Utils

import com.ahm.plantcare.Plant
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.threeten.bp.LocalDate


object JsonEncoder {
    private const val name_key = "name"
    private const val icon_id_key = "icon"
    private const val date_day_key = "day"
    private const val date_month_key = "month"
    private const val date_year_key = "year"
    private const val wat_freq_key = "wat_freq"
    fun writePlantList(arrayList: ArrayList<Plant>): String {
        val jsonArray = JSONArray()
        for (i in arrayList.indices) {
            val `object` = JSONObject()
            writePlant(arrayList[i], `object`)
            jsonArray.put(`object`)
        }
        return jsonArray.toString()
    }

    private fun writePlant(plant: Plant, `object`: JSONObject) {
        val date: LocalDate = plant.getNextWateringDate()
        try {
            `object`.put(name_key, plant.getPlantName())
            `object`.put(icon_id_key, plant.getIconId())
            `object`.put(date_day_key, date.getDayOfMonth())
            `object`.put(date_month_key, date.getMonthValue())
            `object`.put(date_year_key, date.getYear())
            `object`.put(wat_freq_key, plant.getWateringFrequency())
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    fun readPlantList(string: String?): ArrayList<Plant> {
        val arrayList = ArrayList<Plant>()
        try {
            val jsonArray = JSONArray(string)
            for (i in 0 until jsonArray.length()) {
                val `object` = jsonArray.optJSONObject(i)
                val plant = readPlant(`object`)
                if (plant != null) {
                    arrayList.add(plant)
                }
            }
            return arrayList
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return arrayList
    }

    private fun readPlant(`object`: JSONObject): Plant? {
        try {
            val plantName = `object`.getString(name_key)
            val iconId = `object`.getInt(icon_id_key)
            val nextWateringDate: LocalDate = LocalDate.of(
                `object`.getInt(date_year_key), `object`.getInt(
                    date_month_key
                ), `object`.getInt(date_day_key)
            )
            val wateringFrequency = `object`.getInt(wat_freq_key)
            return Plant(plantName, iconId, wateringFrequency, nextWateringDate)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return null
    }
}
