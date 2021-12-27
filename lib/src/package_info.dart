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
  });

  /// Construct class from the json map
  factory PackageInfo.fromMap(Map map) => PackageInfo(
        packageName: map['packageName'],
        appName: map['appName'],
        appIconByteArray: _eliminateNewLine(map['appIcon']),
        versionName: map['versionName'],
        versionCode: map['versionCode'],
      );

  final String packageName;
  final String appName;
  final String appIconByteArray;
  final String versionName;
  final int versionCode;

  /// Get flutter's `Image` widget from the byte array of app icon
  Image getAppIcon({
    BoxFit fit = BoxFit.fill,
    double height = 32.0,
    double width = 32.0,
  }) =>
      Image.memory(
        base64Decode(appIconByteArray),
        fit: fit,
        height: height,
        width: width,
      );

  @override
  String toString() {
    return 'PackageInfo{packageName: $packageName, appName: $appName, versionName: $versionName, versionCode: $versionCode, appIconByteArray: $appIconByteArray}';
  }
}

String _eliminateNewLine(String s) => s.replaceAll('\n', '');
