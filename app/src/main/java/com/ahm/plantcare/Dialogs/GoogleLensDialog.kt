package com.ahm.plantcare.Dialogs

import android.R
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import com.ahm.plantcare.Utils.OpenPlayStore.open


object GoogleLensDialog {
    private const val GoogleLensPackage = "com.google.ar.lens"
    fun showDialog(context: Context?, viewGroup: ViewGroup) {
        val builder = AlertDialog.Builder(
            context!!, R.style.AlertDialogTheme
        )
        val view: View = LayoutInflater.from(context).inflate(
            R.layout.google_lens_install_dialog,
            viewGroup.findViewById<View>(R.id.layout_dialog_container) as ConstraintLayout
        )
        builder.setView(view)
        val alertDialog = builder.create()
        view.findViewById<View>(R.id.button).setOnClickListener {
            open(context, GoogleLensPackage)
            alertDialog.cancel()
        }
        if (alertDialog.window != null) {
            alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        alertDialog.show()
    }
}
