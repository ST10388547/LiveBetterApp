package com.example.livebetterapp.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        try {
            NotificationHelper.showNotification(
                context,
                "Daily Reminder",
                "Don't forget to log your expenses today!"
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
