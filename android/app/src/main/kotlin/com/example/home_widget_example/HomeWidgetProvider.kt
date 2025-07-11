package com.example.home_widget_example

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.widget.RemoteViews

class HomeWidgetProvider : AppWidgetProvider() {
    companion object {
        const val ACTION_UPDATE = "com.example.home_widget_example.ACTION_UPDATE"
        const val PREFS_NAME = "HomeWidgetPrefs"
        const val PREF_TEXT_KEY = "widget_text_"
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        if (intent.action == ACTION_UPDATE) {
            // Handle button click from widget
            val appWidgetId = intent.getIntExtra(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID
            )

            if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
                // Get current text and update with new timestamp
                val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                val currentText =
                    prefs.getString(PREF_TEXT_KEY + appWidgetId, "Default Text") ?: "Default Text"

                prefs.edit()
                    .putString(
                        PREF_TEXT_KEY + appWidgetId,
                        "$currentText (updated at ${System.currentTimeMillis()})"
                    )
                    .apply()

                // Update the widget
                val appWidgetManager = AppWidgetManager.getInstance(context)
                updateAppWidget(context, appWidgetManager, appWidgetId)
            }
        }
    }

    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val widgetText =
            prefs.getString(PREF_TEXT_KEY + appWidgetId, "Default Text") ?: "Default Text"

        val views = RemoteViews(context.packageName, R.layout.home_widget)
        views.setTextViewText(R.id.widget_text, widgetText)

        // Create an Intent to update the widget when button is clicked
        val intent = Intent(context, HomeWidgetProvider::class.java).apply {
            action = ACTION_UPDATE
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.widget_button, pendingIntent)

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
}