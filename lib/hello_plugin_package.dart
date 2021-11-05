import 'dart:async';

import 'package:flutter/services.dart';

class HelloPluginPackage {
  static const MethodChannel _channel = MethodChannel('hello_plugin_package');

  static Future<String?> get platformVersion async {
    final String? version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  // Storage space in bytes
  static Future<int> get getStorageFreeSpace async {
    final int freeSpace = await _channel.invokeMethod('getStorageFreeSpace');
    return freeSpace;
  }

  static Future<int> get getStorageTotalSpace async {
    final int totalSpace = await _channel.invokeMethod('getStorageTotalSpace');
    return totalSpace;
  }

  static Future<int> get getStorageUsedSpace async {
    final int usedSpace = await _channel.invokeMethod('getStorageUsedSpace');
    return usedSpace;
  }

  // Memory space in bytes
  static Future<int> get getMemoryTotalSpace async {
    final int totalSpace = await _channel.invokeMethod('getMemoryTotalSpace');
    return totalSpace;
  }

  static Future<int> get getMemoryFreeSpace async {
    final int freeSpace = await _channel.invokeMethod('getMemoryFreeSpace');
    return freeSpace;
  }

  static Future<int> get getMemoryUsedSpace async {
    final int usedSpace = await _channel.invokeMethod('getMemoryUsedSpace');
    return usedSpace;
  }
}
