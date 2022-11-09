package com.ahm.plantcare.NumberPickers

import android.R
import android.content.Context
import android.util.AttributeSet
import android.widget.NumberPicker
import androidx.appcompat.view.ContextThemeWrapper


class BlueNumberPicker @JvmOverloads constructor(context: Context?, attrs: AttributeSet? = null) :
    NumberPicker(ContextThemeWrapper(context, R.style.NumberPickerBlueStyle), attrs)