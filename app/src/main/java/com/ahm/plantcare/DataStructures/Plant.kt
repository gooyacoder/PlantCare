package com.ahm.plantcare.DataStructures

import android.graphics.drawable.Drawable
import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import org.threeten.bp.LocalDate
import org.threeten.bp.temporal.ChronoUnit


class Plant : Comparable<Plant?>, Parcelable {
    var plantName: String?
    var iconId: Int
    private var nextWateringDate: LocalDate
    var wateringFrequency: Int

    // Not stored on sharedPrefs, computed dinamically:
    var daysRemaining = 0
    var icon: Drawable? = null

    constructor(
        plantName: String?,
        iconId: Int,
        wateringFrequency: Int,
        nextWateringDate: LocalDate
    ) {
        this.plantName = plantName
        this.iconId = iconId
        this.wateringFrequency = wateringFrequency
        this.nextWateringDate = nextWateringDate
    }

    constructor(
        plantName: String?,
        iconId: Int,
        wateringFrequency: Int,
        nextWateringDate: LocalDate,
        icon: Drawable?
    ) {
        this.plantName = plantName
        this.iconId = iconId
        this.wateringFrequency = wateringFrequency
        this.nextWateringDate = nextWateringDate
        this.icon = icon
    }

    constructor(source: Parcel) {
        plantName = source.readString()
        iconId = source.readInt()
        nextWateringDate = LocalDate.of(source.readInt(), source.readInt(), source.readInt())
        wateringFrequency = source.readInt()
        daysRemaining = source.readInt()
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(plantName)
        dest.writeInt(iconId)
        dest.writeInt(nextWateringDate.getYear())
        dest.writeInt(nextWateringDate.getMonthValue())
        dest.writeInt(nextWateringDate.getDayOfMonth())
        dest.writeInt(wateringFrequency)
        dest.writeInt(daysRemaining)
    }

    override fun equals(o: Any?): Boolean {             // For find function in plantList
        return if (o is Plant) {
            if (o.daysRemaining == daysRemaining && o.iconId == iconId && o.nextWateringDate.equals(
                    nextWateringDate
                ) && o.plantName == plantName && o.wateringFrequency == wateringFrequency
            ) {
                true
            } else {
                false
            }
        } else {
            false
        }
    }

    override operator fun compareTo(comparePlant: Plant): Int {
        val compareDays = comparePlant.daysRemaining
        return daysRemaining - compareDays
    }

    override fun describeContents(): Int {
        return 0
    }

    fun water() {
        val date: LocalDate = LocalDate.now().plusDays(wateringFrequency)
        setNextWateringDate(date)
        daysRemaining = wateringFrequency
    }

    fun computeDaysRemaining() {
        var daysRem = LocalDate.now().until(getNextWateringDate(), ChronoUnit.DAYS) as Int
        if (daysRem < 0) {
            daysRem = 0
        }
        daysRemaining = daysRem
    }

    fun getNextWateringDate(): LocalDate {
        return nextWateringDate
    }

    fun setNextWateringDate(nextWateringDate: LocalDate) {
        this.nextWateringDate = nextWateringDate
    }

    companion object {
        val CREATOR: Creator<Plant> = object : Creator<Plant?> {
            override fun createFromParcel(source: Parcel): Plant? {
                return Plant(source)
            }

            override fun newArray(size: Int): Array<Plant?> {
                return arrayOfNulls(size)
            }
        }
    }
}
