import 'package:flutter/material.dart';
import 'package:home_widget/home_widget.dart';
import 'package:intl/intl.dart';
import 'package:workmanager/workmanager.dart';
import 'package:flutter_local_notifications/flutter_local_notifications.dart';

void callbackDispatcher() {
  Workmanager().executeTask((task, inputData) async {
    final plugin = FlutterLocalNotificationsPlugin();
    const android = AndroidNotificationDetails(
      'reminder_channel', 'Reminders',
      importance: Importance.max, priority: Priority.high,
    );
    const notif = NotificationDetails(android: android);
    await plugin.show(0, 'Reminder', 'This is your reminder after 20 seconds', notif);
    return Future.value(true);
  });
}

void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  await Workmanager().initialize(callbackDispatcher, isInDebugMode: true);
  runApp(MyApp());
}

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) => MaterialApp(home: HomeScreen());
}

class HomeScreen extends StatelessWidget {
  Future<void> _updateTime() async {
    final now = DateTime.now();
    String formattedDate = DateFormat('yyyy-MM-dd – kk:mm:ss').format(now);
    await HomeWidget.saveWidgetData('time', 'Time $formattedDate');
    await HomeWidget.updateWidget(name: 'HomeWidgetProvider', iOSName: 'HomeWidget');
  }

  Future<void> _setReminder() async {
    await Workmanager().registerOneOffTask(
      'reminderTask', 'reminderTask',
      initialDelay: Duration(seconds: 20),
    );
  }
  void _openAppFromWidget(Uri? uri) {
    // handle deep link if needed
    if (uri?.host == "update_time") {
      _updateTime();
      print('_openAppFromWidget update_time${uri?.host}');
    }
  }
  @override
  Widget build(BuildContext context) {
    HomeWidget.registerInteractivityCallback(_openAppFromWidget);
    return Scaffold(
      appBar: AppBar(title: Text('Home Widget Demo')),
      body: Center(
        child: Column(mainAxisAlignment: MainAxisAlignment.center, children: [
          ElevatedButton(onPressed: _updateTime, child: Text('Update Widget Time')),
          ElevatedButton(onPressed: _setReminder, child: Text('Set Reminder (20s)')),
        ]),
      ),
    );
  }
}
