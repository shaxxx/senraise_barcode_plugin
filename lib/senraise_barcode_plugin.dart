import 'dart:async';

import 'package:flutter/services.dart';

class SenraiseBarcodePlugin {
  /// Initializes the plugin and starts listening for potential platform events.
  factory SenraiseBarcodePlugin() {
    if (_instance == null) {
      final MethodChannel methodChannel =
          const MethodChannel('senraise_barcode_plugin/methods');
      final EventChannel eventChannel =
          const EventChannel('senraise_barcode_plugin/events');
      _instance = SenraiseBarcodePlugin.private(methodChannel, eventChannel);
    }
    return _instance;
  }

  SenraiseBarcodePlugin.private(this._methodChannel, this._eventChannel);

  static SenraiseBarcodePlugin _instance;
  final MethodChannel _methodChannel;
  final EventChannel _eventChannel;
  Stream<String> _eventStream;

  Stream<String> get barcodeEventStream {
    if (_eventStream == null) {
      _eventStream =
          _eventChannel.receiveBroadcastStream().map<String>((value) {
        if (value["event"] == "barcode_scanned") {
          return value["barcode"];
        }
        throw UnimplementedError(value["event"]);
      });
    }
    return _eventStream;
  }

  Future<String> get platformVersion async {
    final String version =
        await _methodChannel.invokeMethod('getPlatformVersion');
    return version;
  }
}
