package com.ahm.plantcare.Notifications

import android.R
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import androidx.core.graphics.drawable.DrawableCompat
import com.ahm.plantcare.Plant
import com.ahm.plantcare.Services.WaterPlantService
import com.ahm.plantcare.Utils.CommunicationKeys
import com.ahm.plantcare.Utils.IconTagDecoder.idToDrawable


class SinglePlantNotifBuilder(private val plant: Plant, context: Context?) :
    NotifBuilder(context!!) {
    override fun addSpecificFeatures() {
        builder!!.setContentTitle(plant.getPlantName())
        val text = context.resources.getString(R.string.notification_text_one_plant)
        builder!!.setContentText(text)
        builder!!.setLargeIcon(bitmapFromVectorDrawable)
        addWateringAction()
    }

    private fun addWateringAction() {
        // Intent for water action
        val waterIntent = Intent(context, WaterPlantService::class.java)
        waterIntent.putExtra(
            CommunicationKeys.NotificationClass_WaterSinglePlantService_PlantToWater,
            plant
        )
        waterIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        val waterPendingIntent =
            PendingIntent.getService(context, 0, waterIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        builder!!.addAction(
            R.drawable.icon_watering,
            context.resources.getString(R.string.notification_water_action_text), waterPendingIntent
        )
    }

    private val bitmapFromVectorDrawable: Bitmap
        private get() {
            var drawable = idToDrawable(context, plant.getIconId())
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                drawable = DrawableCompat.wrap(drawable).mutate()
            }
            val bitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth,
                drawable.intrinsicHeight, Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            return bitmap
        }
}
