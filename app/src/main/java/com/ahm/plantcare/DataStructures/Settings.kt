package com.ahm.plantcare.DataStructures

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager


class Settings(context: Context) {
    private val prefs: SharedPreferences
    private val notifEnabledKey = "notif_enabled"
    private val notifEnabledDefault = true
    private val notifHourKey = "notif_hour"
    private val notifHourDefault = 18
    private val notifMinuteKey = "notif_minute"
    private val notifMinuteDefault = 0
    private val notifRepetIntervalKey = "notif_repetition"
    private val notifRepetIntervalDefault = 1

    init {
        prefs = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
    }

    var notifHour: Int
        get() = prefs.getInt(notifHourKey, notifHourDefault)
        set(newValue) {
            if (newValue >= 0 && newValue <= 23) {
                prefs.edit().putInt(notifHourKey, newValue).apply()
            }
        }
    var notifMinute: Int
        get() = prefs.getInt(notifMinuteKey, notifMinuteDefault)
        set(newValue) {
            if (newValue >= 0 && newValue <= 59) {
                prefs.edit().putInt(notifMinuteKey, newValue).apply()
            }
        }

    // Default value
    val notifSecond: Int
        get() = 0 // Default value

    var notifRepetInterval: Int
        get() = prefs.getInt(notifRepetIntervalKey, notifRepetIntervalDefault)
        set(newValue) {
            if (newValue >= 1 && newValue <= 23) {
                prefs.edit().putInt(notifRepetIntervalKey, newValue).apply()
            }
        }
    var notifEnabled: Boolean
        get() = prefs.getBoolean(notifEnabledKey, notifEnabledDefault)
        set(value) {
            prefs.edit().putBoolean(notifEnabledKey, value).apply()
        }
}
