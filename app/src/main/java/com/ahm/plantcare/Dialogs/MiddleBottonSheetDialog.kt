package com.ahm.plantcare.Dialogs

import android.R
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import com.ahm.plantcare.Dialogs.GoogleLensDialog.showDialog
import com.ahm.plantcare.Utils.OpenPlayStore.open
import com.google.android.material.bottomsheet.BottomSheetDialog


class MiddleBottomSheetDialog(context: Context) :
    BottomSheetDialog(context, R.style.CustomBottomSheetDialogTheme) {
    init {
        val dialogView: View = LayoutInflater.from(context).inflate(
            R.layout.main_middle_dialog,
            findViewById<View>(R.id.main_middle_dialog_container) as ConstraintLayout?
        )
        setContentView(dialogView)
        if (window != null) {
            window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        findViewById<View>(R.id.main_middle_tip_box)!!.setOnClickListener {
            dismiss()
            val viewGroup =
                (findViewById<View>(R.id.content) as ViewGroup?)!!.getChildAt(0) as ViewGroup
            TipOfTheDay.showTip(context, viewGroup)
        }
        findViewById<View>(R.id.main_middle_rate_box)!!.setOnClickListener {
            open(context, context.packageName)
            dismiss()
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            // Cant install Google Lens on Android 5 or lower
            findViewById<View>(R.id.main_middle_lens_box)!!.visibility = View.GONE
        } else {
            findViewById<View>(R.id.main_middle_lens_box)!!.setOnClickListener {
                // Try opening Google Lens app
                val manager = context.packageManager
                val lensIntent =
                    manager.getLaunchIntentForPackage(GoogleLensPackage)
                if (lensIntent != null) {
                    lensIntent.addCategory(Intent.CATEGORY_LAUNCHER)
                    context.startActivity(lensIntent)
                    dismiss()
                } else {
                    // Show dialog explaining that they have to install the app
                    dismiss()
                    val viewGroup =
                        (findViewById<View>(R.id.content) as ViewGroup?)!!.getChildAt(
                            0
                        ) as ViewGroup
                    showDialog(context, viewGroup)
                }
            }
        }
    }

    companion object {
        private const val GoogleLensPackage = "com.google.ar.lens"
    }
}
