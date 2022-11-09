package com.ahm.plantcare.NumberPickers

import android.R
import android.content.Context
import android.util.AttributeSet
import android.widget.NumberPicker
import androidx.appcompat.view.ContextThemeWrapper


class RedNumberPicker @JvmOverloads constructor(context: Context?, attrs: AttributeSet? = null) :
    NumberPicker(ContextThemeWrapper(context, R.style.NumberPickerRedStyle), attrs)
