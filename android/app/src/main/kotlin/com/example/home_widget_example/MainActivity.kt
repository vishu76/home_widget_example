package com.example.home_widget_example
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import android.content.Context
import android.content.SharedPreferences
import android.content.ComponentName
import android.appwidget.AppWidgetManager

class MainActivity: FlutterActivity() {
    val CHANNEL = "com.example.home_widget/channel"

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)

        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL).setMethodCallHandler { call, result ->
            when (call.method) {
                "updateHomeWidget" -> {
                    val data = call.arguments as Map<*, *>
                    updateWidgetData(context, data)
                    result.success(null)
                }
                else -> result.notImplemented()
            }
        }
    }

      fun updateWidgetData(context: Context, data: Map<*, *>) {
        val prefs = context.getSharedPreferences(HomeWidgetProvider.PREFS_NAME, Context.MODE_PRIVATE)
        val text = data["text"]?.toString() ?: "No text provided"

        // Save data for all widgets
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(
            ComponentName(context, HomeWidgetProvider::class.java)
        )

        appWidgetIds.forEach { appWidgetId ->
            prefs.edit()
                .putString(HomeWidgetProvider.PREF_TEXT_KEY + appWidgetId, text)
                .apply()
        }

        // Notify widgets to update
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_text)
    }
}