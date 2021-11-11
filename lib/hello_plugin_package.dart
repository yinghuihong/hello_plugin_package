import 'dart:async';

import 'package:flutter/services.dart';
import 'package:hello_plugin_package/src/package_info.dart';

export 'src/package_info.dart';

class HelloPluginPackage {
  static const MethodChannel _channel =
      MethodChannel('hello_plugin_package', JSONMethodCodec());

  /// Platform release version. e.g. 11
  static Future<String?> get platformReleaseVersion async {
    final String? version =
        await _channel.invokeMethod('getPlatformReleaseVersion');
    return version;
  }

  /// Platform sdk version. e.g. 30
  static Future<int> get platformSdkVersion async {
    final int version = await _channel.invokeMethod('getPlatformSdkVersion');
    return version;
  }

  /// Storage total space in bytes
  static Future<int> get getStorageTotalSpace async {
    final int totalSpace = await _channel.invokeMethod('getStorageTotalSpace');
    return totalSpace;
  }

  /// Storage free space in bytes
  static Future<int> get getStorageFreeSpace async {
    final int freeSpace = await _channel.invokeMethod('getStorageFreeSpace');
    return freeSpace;
  }

  /// Storage used space in bytes
  static Future<int> get getStorageUsedSpace async {
    final int usedSpace = await _channel.invokeMethod('getStorageUsedSpace');
    return usedSpace;
  }

  /// Memory total space in bytes
  static Future<int> get getMemoryTotalSpace async {
    final int totalSpace = await _channel.invokeMethod('getMemoryTotalSpace');
    return totalSpace;
  }

  /// Memory free space in bytes
  static Future<int> get getMemoryFreeSpace async {
    final int freeSpace = await _channel.invokeMethod('getMemoryFreeSpace');
    return freeSpace;
  }

  /// Memory used space in bytes
  static Future<int> get getMemoryUsedSpace async {
    final int usedSpace = await _channel.invokeMethod('getMemoryUsedSpace');
    return usedSpace;
  }

  /// Get package information of the `name` package
  /// Return: `PackageInfo` class
  static Future<PackageInfo?> getPackageInfo(String name) async {
    Map? result =
        await _channel.invokeMethod('getPackageInfo', <dynamic>[name]);
    return result != null ? PackageInfo.fromMap(result) : null;
  }

  /// Get the `List<String>` of the installed applications.
  /// This includes the system apps.
  /// You can use this name as a parameter of `getPackageInfo()` call.
  static Future<List> getInstalledPackages() async {
    return await _channel.invokeMethod('getInstalledPackages');
  }

  /// Get the `List<String>` of the ***user installed*** applications.
  /// This does not include the system apps.
  /// You can use this name as a parameter of `getPackageInfo()` call.
  static Future<List> getUserInstalledPackages() async {
    return await _channel.invokeMethod('getUserInstalledPackages');
  }
}
