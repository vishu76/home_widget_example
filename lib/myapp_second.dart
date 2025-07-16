import 'package:flutter/material.dart';
import 'package:home_widget/home_widget.dart';
import 'package:workmanager/workmanager.dart';
import 'package:flutter_local_notifications/flutter_local_notifications.dart';

class MySecondPage extends StatelessWidget {
  @override
  Widget build(BuildContext context) => MaterialApp(home: HomeScreen());
}

class HomeScreen extends StatelessWidget {
  final FlutterLocalNotificationsPlugin notificationsPlugin = FlutterLocalNotificationsPlugin();

  Future<void> _updateTime() async {
    final currentTime = DateTime.now().toIso8601String();
    await HomeWidget.saveWidgetData<String>('time', currentTime);
    await HomeWidget.updateWidget(name: 'HomeWidgetProvider', iOSName: 'HomeWidget');
  }

  void _openAppFromWidget(Uri? uri) {
    // handle deep link if needed
    if (uri?.host == "update_time") {
      _updateTime();
    }
  }

  Future<void> _setReminder() async {
    await Workmanager().registerOneOffTask(
      'reminderTask',
      'reminderTask',
      initialDelay: Duration(seconds: 20),
    );
  }

  @override
  Widget build(BuildContext context) {
    HomeWidget.registerBackgroundCallback(_openAppFromWidget);
    return Scaffold(
      appBar: AppBar(title: Text('Home Widget Example')),
      body: Center(
        child: Column(mainAxisAlignment: MainAxisAlignment.center, children: [
          ElevatedButton(
            onPressed: _updateTime,
            child: Text('Update Widget Time'),
          ),
          ElevatedButton(
            onPressed: _setReminder,
            child: Text('Set 20s Reminder'),
          ),
        ]),
      ),
    );
  }
}
