import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Home Widget Example',
      theme: ThemeData(primarySwatch: Colors.blue),
      home: const MyHomePage(),
    );
  }
}

class MyHomePage extends StatefulWidget {
  const MyHomePage({super.key});

  @override
  State<MyHomePage> createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  final _textController = TextEditingController(text: 'Hello from Flutter!');

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Home Widget Control')),
      body: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          children: [
            TextField(
              controller: _textController,
              decoration: const InputDecoration(labelText: 'Widget Text'),
            ),
            const SizedBox(height: 20),
            ElevatedButton(
              onPressed: () async {
                await HomeWidgetService.updateHomeWidgetData({
                  'text': _textController.text,
                  'timestamp': DateTime.now().toString(),
                });
                ScaffoldMessenger.of(context).showSnackBar(
                  const SnackBar(content: Text('Widget updated!')),
                );
              },
              child: const Text('Update Widget'),
            ),
          ],
        ),
      ),
    );
  }
}

class HomeWidgetService {
  static const platform = MethodChannel('com.example.home_widget/channel');

  static Future<void> updateHomeWidgetData(Map<String, dynamic> data) async {
    try {
      await platform.invokeMethod('updateHomeWidget', data);
    } on PlatformException catch (e) {
      debugPrint("Failed to update home widget: '${e.message}'.");
    }
  }
}