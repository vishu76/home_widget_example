package com.example.home_widget_example

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.util.Log
import android.net.Uri
class HomeWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        val views = RemoteViews(context.packageName, R.layout.home_widget)

        val prefs = context.getSharedPreferences("HomeWidgetPreferences", Context.MODE_PRIVATE)
        val time = prefs.getString("time", "Time not set")
        views.setTextViewText(R.id.time_text, time)

        // Intent to update time via broadcast
        val updateIntent = Intent(context, HomeWidgetProvider::class.java).apply {
            action = "UPDATE_TIME"
        }

        // Intent to open app
        val openAppIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        val openPendingIntent = PendingIntent.getActivity(context, 0, openAppIntent, PendingIntent.FLAG_IMMUTABLE)

        // Intent to set reminder (handled in Flutter)
        val setReminderIntent = Intent(context, HomeWidgetProvider::class.java).apply {
            action = "SET_REMINDER"
        }

        views.setOnClickPendingIntent(R.id.update_time, PendingIntent.getBroadcast(context, 0, updateIntent, PendingIntent.FLAG_IMMUTABLE))
        views.setOnClickPendingIntent(R.id.open_app, openPendingIntent)
        views.setOnClickPendingIntent(R.id.set_reminder, PendingIntent.getBroadcast(context, 1, setReminderIntent, PendingIntent.FLAG_IMMUTABLE))

        for (widgetId in appWidgetIds) {
            appWidgetManager.updateAppWidget(widgetId, views)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        if (intent.action == "UPDATE_TIME") {
//            val appWidgetManager = AppWidgetManager.getInstance(context)
//            val widgetComponent = ComponentName(context, HomeWidgetProvider::class.java)
//            val widgetIds = appWidgetManager.getAppWidgetIds(widgetComponent)
//            onUpdate(context, appWidgetManager, widgetIds)
//
            // Save current time to SharedPreferences
            val currentTime = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date())
            val prefs = context.getSharedPreferences("HomeWidgetPreferences", Context.MODE_PRIVATE)
            prefs.edit().putString("time", currentTime).apply()

            // Trigger widget update
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val widgetComponent = ComponentName(context, HomeWidgetProvider::class.java)
            val widgetIds = appWidgetManager.getAppWidgetIds(widgetComponent)
            onUpdate(context, appWidgetManager, widgetIds)

            Log.e("TAG", "UPDATE_TIME ->>>>>>>>>>>>>>>")
        } else if (intent.action == "SET_REMINDER") {
            // Open app to trigger WorkManager task from Dart
            val openIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)
            openIntent?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(openIntent)
            Log.e("TAG", "SET_REMINDER ->>>>>>>>>>>>>>>")
        }
    }
}
