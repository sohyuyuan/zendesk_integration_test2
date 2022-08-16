import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Demo',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: const MyHomePage(title: 'Flutter Demo Home Page'),
    );
  }
}

class MyHomePage extends StatefulWidget {
  const MyHomePage({Key? key, required this.title}) : super(key: key);

  final String title;

  @override
  State<MyHomePage> createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  static const platform = MethodChannel('samples.flutter.dev/battery');

  Future<void> _initZendesk() async {
    try {
      await platform.invokeMethod('initZendeskUnifiedSdk');
    } catch (e, s) {
      print(e);
      print(s);
    }
  }

  Future<void> _showMessagingSdk() async {
    try {
      await platform.invokeMethod('startMessagingActivity');
    } catch (e, s) {
      print(e);
      print(s);
    }
  }

  @override
  void initState() {
    _initZendesk();
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return Material(
      child: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.spaceEvenly,
          children: [
            const Text('Talk to zendesk with :'),
            ElevatedButton(
              onPressed: _showMessagingSdk,
              child: const Text('Messaging SDK'),
            ),
          ],
        ),
      ),
    );
  }
}
