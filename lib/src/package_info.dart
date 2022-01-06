import 'dart:convert';

import 'package:flutter/material.dart';

/// The class for package information.
/// Contains package name (e.g., com.facebook.katana), app name (e.g., Facebook), and app icon
class PackageInfo {
  PackageInfo({
    required this.packageName,
    required this.appName,
    required this.appIconByteArray,
    required this.versionName,
    required this.versionCode,
    this.firstInstallTime = -1,
    this.lastUpdateTime = -1,
  });

  /// Construct class from the json map
  factory PackageInfo.fromMap(Map map) => PackageInfo(
        packageName: map['packageName'],
        appName: map['appName'],
        appIconByteArray: _eliminateNewLine(map['appIcon']),
        versionName: map['versionName'],
        versionCode: map['versionCode'],
        firstInstallTime: map['firstInstallTime'],
        lastUpdateTime: map['lastUpdateTime'],
      );

  final String packageName;
  final String appName;
  final String? appIconByteArray;
  final String versionName;
  final int versionCode;
  final int firstInstallTime;
  final int lastUpdateTime;

  /// Get flutter's `Image` widget from the byte array of app icon
  Widget getAppIcon({
    BoxFit fit = BoxFit.fill,
    double size = 32.0,
  }) =>
      appIconByteArray != null
          ? Image.memory(
              base64Decode(appIconByteArray!),
              fit: fit,
              height: size,
              width: size,
            )
          : FlutterLogo(
              size: size,
            );

  @override
  String toString() {
    return 'PackageInfo{packageName: $packageName, appName: $appName, versionName: $versionName, versionCode: $versionCode, firstInstallTime: $firstInstallTime, lastUpdateTime: $lastUpdateTime}';
  }
}

String? _eliminateNewLine(String? s) => s?.replaceAll('\n', '');
