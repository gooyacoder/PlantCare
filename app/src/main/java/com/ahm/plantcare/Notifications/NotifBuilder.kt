package com.ahm.plantcare.Notifications

import android.R
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.ahm.plantcare.MainActivity
import com.ahm.plantcare.Services.RemindLaterService


abstract class NotifBuilder(protected var context: Context) {
    protected var builder: NotificationCompat.Builder? = null
    fun getBuilder(): NotificationCompat.Builder? {
        builder = NotificationCompat.Builder(context, NotificationClass.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setColor(context.resources.getColor(R.color.colorPrimary))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
        addOnTouchIntent()
        addRemindLaterAction()
        addSpecificFeatures()
        return builder
    }

    protected abstract fun addSpecificFeatures()
    private fun addOnTouchIntent() {
        // General intent to open app on notification touch
        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
        builder!!.setContentIntent(pendingIntent)
    }

    private fun addRemindLaterAction() {
        // Intent for Remind Later action
        val intent = Intent(context, RemindLaterService::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getService(context, 0, intent, 0)
        builder!!.addAction(
            R.drawable.icon_clock_remind_later_white,
            context.resources.getString(R.string.notification_remind_later_text), pendingIntent
        )
    }
}
