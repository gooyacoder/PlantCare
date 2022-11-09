package com.ahm.plantcare.Activities

import android.R
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.widget.CompoundButton
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.constraintlayout.widget.ConstraintLayout
import com.ahm.plantcare.DataStructures.PlantList
import com.ahm.plantcare.JobSchedulers.NotificationJobService
import com.ahm.plantcare.NumberPickers.GreenNumberPicker
import com.ahm.plantcare.Utils.CommunicationKeys
import com.wateria.DataStructures.Settings
import java.lang.String


class SettingsActivity : AppCompatActivity() {
    private var settings: Settings? = null
    private var notifEnablerSwitch: SwitchCompat? = null
    private var notifTimingBox: ConstraintLayout? = null
    private var notifTimingTextView: TextView? = null
    private var notifTimingTimePickerDialog: TimePickerDialog? = null
    private var notifPostponeBox: ConstraintLayout? = null
    private var notifPostponeNumberTextView: TextView? = null
    private var notifPostponeHourTextView: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings_layout)
        settings = Settings(this)

        //Prepare Notification Timing UI
        notifTimingBox = findViewById(R.id.settings_notif_timing_box)
        notifTimingTextView = findViewById(R.id.settings_notif_timing_hour)
        formatNotifTimingTextView()

        //Prepare Notification Postpone UI
        notifPostponeBox = findViewById(R.id.settings_notif_remind_box)
        notifPostponeNumberTextView = findViewById(R.id.settings_notif_remind_num_hours)
        notifPostponeHourTextView = findViewById(R.id.settings_notif_remind_text_hours)
        notifPostponeNumberTextView.setText(String.valueOf(settings.getNotifRepetInterval()))
        notifPostponeHourTextView.setText(
            resources.getQuantityString(
                R.plurals.hours,
                settings.getNotifRepetInterval()
            )
        )

        // Prepare Notification Enabler UI    (after everything because it can modify the upper ones)
        notifEnablerSwitch = findViewById(R.id.settings_notif_enabler_switch)
        notifEnablerSwitch.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { compoundButton, isChecked ->
            if (isChecked) {
                onNotifEnablerSwitchChangedOn()
            } else {
                onNotifEnablerSwitchChangedOff()
            }
        })
        notifEnablerSwitch.setChecked(settings.getNotifEnabled()) // Triggers onCheckedChanged call bc after it
    }

    fun onNotifEnablerSwitchChangedOn() {
        settings.setNotifEnabled(true)
        val orange = resources.getColor(R.color.colorOrange)
        notifTimingTextView!!.setTextColor(orange)
        notifTimingBox!!.isClickable = true
        notifPostponeBox!!.isClickable = true
        notifPostponeNumberTextView!!.setTextColor(orange)
        notifPostponeHourTextView!!.setTextColor(orange)
    }

    fun onNotifEnablerSwitchChangedOff() {
        settings.setNotifEnabled(false)
        val grey = resources.getColor(R.color.colorGrey)
        notifTimingBox!!.isClickable = false
        notifTimingTextView!!.setTextColor(grey)
        notifPostponeBox!!.isClickable = false
        notifPostponeNumberTextView!!.setTextColor(grey)
        notifPostponeHourTextView!!.setTextColor(grey)
    }

    private fun formatNotifTimingTextView() {
        var unpadded = "" + settings.getNotifHour()
        var result = "00".substring(unpadded.length) + unpadded
        unpadded = "" + settings.getNotifMinute()
        result = result + ":" + "00".substring(unpadded.length) + unpadded
        notifTimingTextView!!.text = result
    }

    fun onNotifTimingBoxClick(v: View?) {
        if (notifTimingTimePickerDialog == null) {
            notifTimingTimePickerDialog = TimePickerDialog(this@SettingsActivity,
                { view, clickedHour, clickedMinute ->
                    settings.setNotifHour(clickedHour)
                    settings.setNotifMinute(clickedMinute)
                    //Cancel notifJob with previous timing and set the new one
                    NotificationJobService.cancelScheduledJob(this@SettingsActivity)
                    NotificationJobService.scheduleNextJob(this@SettingsActivity)
                    formatNotifTimingTextView()
                }, settings.getNotifHour(), settings.getNotifMinute(), true
            )
        }
        notifTimingTimePickerDialog!!.show()
        val window = notifTimingTimePickerDialog!!.window
        window!!.setLayout(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
    }

    fun onNotifPostponeBoxClick(v: View?) {
        val builder = AlertDialog.Builder(this, R.style.AlertDialogTheme)
        val view: View = LayoutInflater.from(this).inflate(
            R.layout.settings_postpone_dialog,
            findViewById<View>(R.id.layout_dialog_container) as ConstraintLayout
        )
        builder.setView(view)
        val alertDialog = builder.create()
        val numberPicker = view.findViewById<GreenNumberPicker>(R.id.settings_postpone_numberpicker)
        numberPicker.minValue = 1
        numberPicker.maxValue = 23
        numberPicker.value = settings.getNotifRepetInterval()
        numberPicker.wrapSelectorWheel = false
        view.findViewById<View>(R.id.postpone_accept_button).setOnClickListener { // Store new value
            settings.setNotifRepetInterval(numberPicker.value)
            // Update visual text:
            notifPostponeNumberTextView.setText(String.valueOf(settings.getNotifRepetInterval()))
            notifPostponeHourTextView!!.text =
                resources.getQuantityString(R.plurals.hours, settings.getNotifRepetInterval())
            alertDialog.dismiss()
        }
        if (alertDialog.window != null) {
            alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        alertDialog.show()
    }

    fun onDeleteBoxClick(v: View?) {
        val builder = AlertDialog.Builder(this@SettingsActivity)
        builder.setMessage(R.string.settings_delete_all_warning_text)
            .setTitle(R.string.settings_delete_all_warning_title)
            .setCancelable(true)
            .setPositiveButton(R.string.settings_delete_all_warning_yes,
                DialogInterface.OnClickListener { dialog, id -> //Delete all plants
                    val plantList = PlantList.getInstance(this@SettingsActivity)
                    plantList!!.deleteAll(this@SettingsActivity)
                    setResult(CommunicationKeys.Settings_Main_ResultDeleteAll)
                    dialog.cancel()
                })
            .setNegativeButton(R.string.settings_delete_all_warning_no,
                DialogInterface.OnClickListener { dialog, id -> dialog.cancel() })
        val alert = builder.create()
        alert.show()
    }

    fun onLicensesBoxClick(v: View?) {
        val builder = AlertDialog.Builder(this, R.style.AlertDialogTheme)
        val view: View = LayoutInflater.from(this).inflate(
            R.layout.settings_licenses_dialog,
            findViewById<View>(R.id.layout_dialog_container) as ConstraintLayout
        )

        //Make the links clickable
        val textView = view.findViewById<View>(R.id.settings_license_text) as TextView
        textView.movementMethod = LinkMovementMethod.getInstance()
        builder.setView(view)
        val alertDialog = builder.create()
        view.findViewById<View>(R.id.license_accept_button)
            .setOnClickListener { alertDialog.dismiss() }
        if (alertDialog.window != null) {
            alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        alertDialog.show()
    }

    fun onAboutBoxClick(v: View?) {
        val builder = AlertDialog.Builder(this, R.style.AlertDialogTheme)
        val view: View = LayoutInflater.from(this).inflate(
            R.layout.settings_about_dialog,
            findViewById<View>(R.id.layout_dialog_container) as ConstraintLayout
        )
        builder.setView(view)
        val alertDialog = builder.create()
        view.findViewById<View>(R.id.about_accept_button)
            .setOnClickListener { alertDialog.dismiss() }
        if (alertDialog.window != null) {
            alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        alertDialog.show()
    }

    fun onHomeButtonClick(v: View?) {
        finish() // Carry DeleteAll flag if applicable
    }
}
