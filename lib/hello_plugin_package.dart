import 'dart:async';

import 'package:flutter/services.dart';
import 'package:hello_plugin_package/src/package_info.dart';

export 'src/package_info.dart';

class HelloPluginPackage {
  static const MethodChannel channel =
      MethodChannel('hello_plugin_package', JSONMethodCodec());

  /// Platform release version. e.g. 11
  static Future<String?> get platformReleaseVersion async {
    final String? version =
        await channel.invokeMethod('getPlatformReleaseVersion');
    return version;
  }

  /// Platform sdk version. e.g. 30
  static Future<int> get platformSdkVersion async {
    final int version = await channel.invokeMethod('getPlatformSdkVersion');
    return version;
  }

  /// Storage total space in bytes
  static Future<int> get getStorageTotalSpace async {
    final int totalSpace = await channel.invokeMethod('getStorageTotalSpace');
    return totalSpace;
  }

  /// Storage free space in bytes
  static Future<int> get getStorageFreeSpace async {
    final int freeSpace = await channel.invokeMethod('getStorageFreeSpace');
    return freeSpace;
  }

  /// Storage used space in bytes
  static Future<int> get getStorageUsedSpace async {
    final int usedSpace = await channel.invokeMethod('getStorageUsedSpace');
    return usedSpace;
  }

  /// Memory total space in bytes
  static Future<int> get getMemoryTotalSpace async {
    final int totalSpace = await channel.invokeMethod('getMemoryTotalSpace');
    return totalSpace;
  }

  /// Memory free space in bytes
  static Future<int> get getMemoryFreeSpace async {
    final int freeSpace = await channel.invokeMethod('getMemoryFreeSpace');
    return freeSpace;
  }

  /// Memory used space in bytes
  static Future<int> get getMemoryUsedSpace async {
    final int usedSpace = await channel.invokeMethod('getMemoryUsedSpace');
    return usedSpace;
  }

  /// Get package information of the `name` package
  /// Return: `PackageInfo` class
  static Future<PackageInfo?> getPackageInfoByPackageName(
      String packageName) async {
    Map? result = await channel
        .invokeMethod('getPackageInfoByPackageName', <dynamic>[packageName]);
    return result != null ? PackageInfo.fromMap(result) : null;
  }

  /// Get package information of the `name` package
  /// Return: `PackageInfo` class
  static Future<PackageInfo?> getPackageInfoByApkFile(
      String apkFilePath) async {
    Map? result = await channel
        .invokeMethod('getPackageInfoByApkFile', <dynamic>[apkFilePath]);
    return result != null ? PackageInfo.fromMap(result) : null;
  }

  /// Get the `List<String>` of the installed applications.
  /// This includes the system apps.
  /// You can use this name as a parameter of `getPackageInfo()` call.
  static Future<List> getInstalledPackageNames() async {
    return await channel.invokeMethod('getInstalledPackageNames');
  }

  /// Get the `List<String>` of the ***user installed*** applications.
  /// This does not include the system apps.
  /// You can use this name as a parameter of `getPackageInfo()` call.
  static Future<List> getUserInstalledPackageNames() async {
    return await channel.invokeMethod('getUserInstalledPackageNames');
  }

  /// Get the `List<PackageInfo>` of the installed applications.
  /// This includes the system apps.
  /// You can use this name as a parameter of `getPackageInfo()` call.
  static Future<List<PackageInfo>> getInstalledPackageInfos() async {
    List list = await channel.invokeMethod('getInstalledPackageInfos');
    return list.map((e) => PackageInfo.fromMap(e)).toList();
  }

  /// Get the `List<PackageInfo>` of the installed applications.
  /// This does not include the system apps.
  /// You can use this name as a parameter of `getPackageInfo()` call.
  static Future<List<PackageInfo>> getUserInstalledPackageInfos() async {
    List list = await channel.invokeMethod('getUserInstalledPackageInfos');
    return list.map((e) => PackageInfo.fromMap(e)).toList();
  }

  /// Get the `List<PackageInfo>` of the installed applications.
  /// This does not include the system apps.
  /// You can use this name as a parameter of `getPackageInfo()` call.
  static Future<bool> invokeGetUserInstalledPackageInfos() async {
    return await channel.invokeMethod('invokeGetUserInstalledPackageInfos');
  }

  /// Uninstall apk by package name.
  static Future<void> uninstallApp(String packageName) async {
    channel.invokeMethod('uninstallApp', <dynamic>[packageName]);
  }
}
