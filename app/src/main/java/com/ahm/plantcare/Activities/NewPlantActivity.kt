package com.ahm.plantcare.Activities

import android.R
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.CompoundButton
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import com.ahm.plantcare.Animations.PulseAnim
import com.ahm.plantcare.DataStructures.PlantList
import com.ahm.plantcare.NumberPickers.BlueNumberPicker
import com.ahm.plantcare.NumberPickers.RedNumberPicker
import com.ahm.plantcare.Utils.CommunicationKeys
import com.ahm.plantcare.Utils.IconTagDecoder.idToDrawable
import com.ahm.plantcare.Utils.IconTagDecoder.tagToId
import com.ahm.plantcare.Utils.MyFirebaseLogger.logNewPlant
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputLayout
import com.wateria.DataStructures.Plant
import org.threeten.bp.LocalDate


class NewPlantActivity : AppCompatActivity() {
    private var plantList: PlantList? = null
    private var nameTextInputEditText: TextView? = null
    private var nameTextInputLayout: TextInputLayout? = null
    private var iconImageView: ImageButton? = null
    private var watFrequencyNumberPicker: BlueNumberPicker? = null
    private var firstWateringTextViewUpcoming: TextView? = null
    private var firstWatNumberPicker: RedNumberPicker? = null
    private var firstWatTextViewDays: TextView? = null
    private var firstWatSwitch: SwitchCompat? = null
    private var dialog: BottomSheetDialog? = null
    private var dialogView: View? = null
    private var dialogInflaterThread: Thread? = null
    private var iconId: Int? = null
    private var showPulseAnim = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_plant)
        plantList = PlantList.getInstance(this)
        prepareUI()
        dialogInflaterThread = Thread {
            dialogView = layoutInflater.inflate(R.layout.dialog_select_icon_layout, null)
        }
        dialogInflaterThread!!.start()
    }

    override fun onStop() {
        super.onStop()
        if (dialogInflaterThread!!.isAlive) {
            dialogInflaterThread!!.interrupt()
        }
    }

    private fun prepareUI() {
        nameTextInputEditText = findViewById(R.id.new_plant_options_name_textinputedittext)
        nameTextInputLayout = findViewById(R.id.new_plant_options_name_textinputlayout)
        iconImageView = findViewById(R.id.new_plant_options_plant_icon)
        watFrequencyNumberPicker =
            findViewById(R.id.new_plant_options_watering_frequency_numberpicker)
        firstWateringTextViewUpcoming = findViewById(R.id.new_plant_options_first_watering_text)
        firstWatNumberPicker = findViewById(R.id.new_plant_options_first_watering_numberpicker)
        firstWatTextViewDays = findViewById(R.id.new_plant_options_first_watering_text_days)
        firstWatSwitch = findViewById(R.id.new_plant_options_first_watering_icon_switch)
        iconId = tagToId(this, (iconImageView.getTag() as String)) // Default one: ic_common_1
        watFrequencyNumberPicker.setMinValue(1)
        watFrequencyNumberPicker.setMaxValue(40)
        watFrequencyNumberPicker.setValue(5)
        watFrequencyNumberPicker.setWrapSelectorWheel(false)
        firstWatNumberPicker.setEnabled(false)
        firstWatNumberPicker.setMinValue(0)
        firstWatNumberPicker.setMaxValue(40)
        firstWatNumberPicker.setValue(5)
        firstWatNumberPicker.setWrapSelectorWheel(false)
        firstWatSwitch.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { compoundButton, isChecked ->
            if (isChecked) {
                switchChangedOn()
            } else {
                switchChangedOff()
            }
        })
        nameTextInputEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                if (nameTextInputEditText.getText().length > 0) {
                    nameTextInputLayout.setError(null)
                }
            }
        })
    }

    fun onAcceptButtonClicked(view: View?) {
        if (nameTextInputEditText!!.text.length <= 0) {
            // Empty name
            nameTextInputLayout!!.error = resources.getString(R.string.new_plant_options_name_error)
        } else if (plantList!!.exists(nameTextInputEditText!!.text.toString())) {
            // Name has changed, and new name is already used by an existing plant
            nameTextInputLayout!!.error =
                resources.getString(R.string.new_plant_options_name_error_already_exists)
        } else if (showPulseAnim) {
            PulseAnim().show(findViewById<View>(R.id.lottie) as LottieAnimationView)
        } else {
            val name = nameTextInputEditText!!.text.toString()
            val wateringFreq = watFrequencyNumberPicker!!.value
            val nextWateringDate: LocalDate = computeNextWateringDate(wateringFreq)
            val plant = Plant(
                name, iconId, wateringFreq, nextWateringDate, idToDrawable(
                    this,
                    iconId!!
                )
            )
            val pos = plantList!!.insertPlant(plant)
            logNewPlant(this, plant)
            val intent = Intent()
            intent.putExtra(CommunicationKeys.NewPlant_Main_PlantPos, pos)
            setResult(RESULT_OK, intent)
            finish()
        }
    }

    fun onCancelButtonClicked(view: View?) {
        finish()
    }

    fun onIconClicked(view: View?) {
        try {
            dialogInflaterThread!!.join()
        } catch (e: InterruptedException) {
            dialogInflaterThread!!.interrupt()
            dialogView = layoutInflater.inflate(R.layout.dialog_select_icon_layout, null)
        }
        if (dialog == null) {
            dialog = BottomSheetDialog(this)
            dialog!!.setContentView(dialogView!!)
        }
        dialog!!.show()
        showPulseAnim = false
    }

    fun switchChangedOn() {
        val red = resources.getColor(R.color.colorRed)
        firstWateringTextViewUpcoming!!.setTextColor(red)
        firstWatNumberPicker!!.value = watFrequencyNumberPicker!!.value
        firstWatNumberPicker!!.visibility = View.VISIBLE
        firstWatTextViewDays!!.visibility = View.VISIBLE
        firstWatNumberPicker!!.isEnabled = true
    }

    fun switchChangedOff() {
        val grey = resources.getColor(R.color.colorGrey)
        firstWateringTextViewUpcoming!!.setTextColor(grey)
        firstWatNumberPicker!!.visibility = View.INVISIBLE
        firstWatTextViewDays!!.visibility = View.INVISIBLE
        firstWatNumberPicker!!.isEnabled = false
    }

    fun onDialogChoiceClicked(view: View) {
        val tag = view.tag as String //Example: "res/drawable/ic_common_1.xml"
        iconId = tagToId(this, tag)
        iconImageView!!.setImageDrawable(idToDrawable(this, iconId!!))
        dialog!!.dismiss()
    }

    private fun computeNextWateringDate(wateringFreq: Int): LocalDate {
        var date: LocalDate = LocalDate.now()
        date = if (firstWatSwitch!!.isChecked) {
            date.plusDays(firstWatNumberPicker!!.value)
        } else {
            date.plusDays(wateringFreq)
        }
        return date
    }
}
