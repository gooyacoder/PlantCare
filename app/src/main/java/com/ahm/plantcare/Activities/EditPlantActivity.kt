package com.ahm.plantcare.Activities

import android.R
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.ahm.plantcare.DataStructures.PlantList
import com.ahm.plantcare.NumberPickers.BlueNumberPicker
import com.ahm.plantcare.NumberPickers.RedNumberPicker
import com.ahm.plantcare.Utils.CommunicationKeys
import com.ahm.plantcare.Utils.IconTagDecoder.idToDrawable
import com.ahm.plantcare.Utils.IconTagDecoder.tagToId
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputLayout
import com.wateria.DataStructures.Plant
import org.threeten.bp.LocalDate


class EditPlantActivity : AppCompatActivity() {
    private var plantList: PlantList? = null
    private var plantToEdit: Plant? = null
    private var iconId = 0
    private var positionInPlantList: Int? = null
    private var nameTextInputEditText: TextView? = null
    private var nameTextInputLayout: TextInputLayout? = null
    private var iconImageView: ImageButton? = null
    private var watFrequencyNumberPicker: BlueNumberPicker? = null
    private var firstWatNumberPicker: RedNumberPicker? = null
    private var dialog: BottomSheetDialog? = null
    private var dialogView: View? = null
    private var dialogInflaterThread: Thread? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_plant)
        val intent = intent
        positionInPlantList =
            intent.getIntExtra(CommunicationKeys.Main_EditPlant_ExtraPlantPosition, 0)
        plantList = PlantList.getInstance(this)
        plantToEdit = plantList!![positionInPlantList!!]

        //Set daysRemaining
        plantToEdit.computeDaysRemaining()
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
        nameTextInputEditText = findViewById(R.id.edit_plant_options_name_textinputedittext)
        nameTextInputLayout = findViewById(R.id.edit_plant_options_name_textinputlayout)
        iconImageView = findViewById(R.id.edit_options_plant_icon)
        watFrequencyNumberPicker =
            findViewById(R.id.edit_plant_options_watering_frequency_numberpicker)
        firstWatNumberPicker = findViewById(R.id.edit_plant_options_first_watering_numberpicker)
        iconId = plantToEdit.getIconId()
        nameTextInputEditText.setText(plantToEdit.getPlantName())
        iconImageView.setImageDrawable(idToDrawable(this, iconId))
        watFrequencyNumberPicker.setMinValue(1)
        watFrequencyNumberPicker.setMaxValue(40)
        watFrequencyNumberPicker.setValue(plantToEdit.getWateringFrequency())
        watFrequencyNumberPicker.setWrapSelectorWheel(false)
        firstWatNumberPicker.setMinValue(0)
        firstWatNumberPicker.setMaxValue(40)
        firstWatNumberPicker.setValue(plantToEdit.getDaysRemaining())
        firstWatNumberPicker.setWrapSelectorWheel(false)
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
            nameTextInputLayout!!.error =
                resources.getString(R.string.edit_plant_options_name_error)
        } else if (nameTextInputEditText!!.text.toString() != plantToEdit.getPlantName()
            && plantList!!.exists(nameTextInputEditText!!.text.toString())
        ) {
            // Name has changed, and new name is already used by an existing plant
            nameTextInputLayout!!.error =
                resources.getString(R.string.new_plant_options_name_error_already_exists)
        } else {
            plantToEdit.setPlantName(nameTextInputEditText!!.text.toString())
            plantToEdit.setWateringFrequency(watFrequencyNumberPicker!!.value)
            plantToEdit.setNextWateringDate(LocalDate.now().plusDays(firstWatNumberPicker!!.value))
            plantToEdit.setIconId(iconId)
            plantToEdit.setDaysRemaining(firstWatNumberPicker!!.value)
            plantToEdit.setIcon(idToDrawable(this, iconId))
            val newPos = plantList!!.modifyPlant(plantToEdit, positionInPlantList!!)
            val intent = Intent()
            intent.putExtra(CommunicationKeys.EditPlant_Main_PlantPrevPosition, positionInPlantList)
            intent.putExtra(CommunicationKeys.EditPlant_Main_PlantNewPosition, newPos)
            setResult(RESULT_OK, intent)
            finish()
        }
    }

    fun onDeleteButtonClicked(view: View?) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(R.string.edit_plant_delete_dialog_text)
            .setTitle(R.string.edit_plant_delete_dialog_title)
            .setCancelable(true)
            .setPositiveButton(R.string.edit_plant_delete_dialog_accept,
                DialogInterface.OnClickListener { dialog, id ->
                    plantList!!.removePlant(positionInPlantList!!)
                    val intent = Intent()
                    intent.putExtra(
                        CommunicationKeys.EditPlant_Main_PlantPrevPosition,
                        positionInPlantList
                    )
                    setResult(CommunicationKeys.EditPlant_Main_ResultDelete, intent)
                    finish()
                })
            .setNegativeButton(R.string.edit_plant_delete_dialog_cancel,
                DialogInterface.OnClickListener { dialog, id -> dialog.cancel() })
        val alert = builder.create()
        alert.show()
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
    }

    fun onDialogChoiceClicked(view: View) {
        val tag = view.tag as String //Example: "res/drawable/ic_common_1.xml"
        iconId = tagToId(this, tag)
        iconImageView!!.setImageDrawable(idToDrawable(this, iconId))
        dialog!!.dismiss()
    }
}
