package com.ahm.plantcare.Utils

import android.R
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast


object OpenPlayStore {
    fun open(context: Context, packageName: String) {
        var uri = Uri.parse("market://details?id=$packageName")
        var intent = Intent(Intent.ACTION_VIEW, uri)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_MULTIPLE_TASK or Intent.FLAG_ACTIVITY_NEW_DOCUMENT)
        try {
            context.startActivity(intent)
        } catch (e1: ActivityNotFoundException) {
            uri = Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
            intent = Intent(Intent.ACTION_VIEW, uri)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_MULTIPLE_TASK or Intent.FLAG_ACTIVITY_NEW_DOCUMENT)
            try {
                context.startActivity(intent)
            } catch (e2: ActivityNotFoundException) {
                Toast.makeText(
                    context,
                    context.resources.getString(R.string.main_middle_play_store_error_toast),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
