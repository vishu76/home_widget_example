package com.example.home_widget_example
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import androidx.annotation.NonNull
class MainActivity : FlutterActivity(){
    private val CHANNEL = "com.example/my_channel"
    override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL).setMethodCallHandler {
                call, result ->
            when (call.method) {
                "myKotlinMethod" -> {
                    val value = call.argument<Int>("value") // Use the correct key
                    if (value != null) {
                        val response = myKotlinMethod(value)
                        result.success(response)
                    } else {
                        result.error("ERROR", "Value is null", null)
                    }
                }
                else -> result.notImplemented()
            }
        }
    }
    private fun myKotlinMethod(value: Int): String {
        return "Received value: $value"
    }
}