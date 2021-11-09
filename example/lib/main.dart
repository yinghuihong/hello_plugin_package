import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:hello_plugin_package/hello_plugin_package.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';

  int _storageTotalSpace = 0;
  int _storageFreeSpace = 0;
  int _storageUsedSpace = 0;
  int _memoryTotalSpace = 0;
  int _memoryFreeSpace = 0;
  int _memoryUsedSpace = 0;

  int _userInstallPackagesNum = 0;
  PackageInfo? _packageInfo;

  @override
  void initState() {
    super.initState();
    initPlatformState();
    initSpaceState();
    initPackageState();
  }

  /// Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    String platformVersion;
    // Platform messages may fail, so we use a try/catch PlatformException.
    // We also handle the message potentially returning null.
    try {
      platformVersion = await HelloPluginPackage.platformVersion ??
          'Unknown platform version';
    } on PlatformException {
      platformVersion = 'Failed to get platform version.';
    }

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;

    setState(() {
      _platformVersion = platformVersion;
    });
  }

  /// Include storage and memory space
  void initSpaceState() async {
    int storageTotalSpace = await HelloPluginPackage.getStorageTotalSpace;
    int storageFreeSpace = await HelloPluginPackage.getStorageFreeSpace;
    int storageUsedSpace = await HelloPluginPackage.getStorageUsedSpace;
    int memoryTotalSpace = await HelloPluginPackage.getMemoryTotalSpace;
    int memoryFreeSpace = await HelloPluginPackage.getMemoryFreeSpace;
    int memoryUsedSpace = await HelloPluginPackage.getMemoryUsedSpace;
    setState(() {
      _storageTotalSpace = storageTotalSpace;
      _storageFreeSpace = storageFreeSpace;
      _storageUsedSpace = storageUsedSpace;
      _memoryTotalSpace = memoryTotalSpace;
      _memoryFreeSpace = memoryFreeSpace;
      _memoryUsedSpace = memoryUsedSpace;
    });
  }

  /// Should declaration android.permission.QUERY_ALL_PACKAGES permission for Android 11
  void initPackageState() async {
    List packages = (await HelloPluginPackage.getUserInstalledPackages());
    PackageInfo? packageInfo =
        (await HelloPluginPackage.getPackageInfo(packages[0]));
    setState(() {
      _userInstallPackagesNum = packages.length;
      _packageInfo = packageInfo;
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: Column(
            children: [
              Text('Running on: $_platformVersion\n'),
              Text('Memory total: $_memoryTotalSpace\n'),
              Text('Memory free: $_memoryFreeSpace\n'),
              Text('Memory used: $_memoryUsedSpace\n'),
              Text('Storage total: $_storageTotalSpace\n'),
              Text('Storage free: $_storageFreeSpace\n'),
              Text('Storage used: $_storageUsedSpace\n'),
              Text('User installed apps num: $_userInstallPackagesNum\n'),
              Center(
                child: _packageInfo?.getAppIcon(),
              )
            ],
          ),
        ),
      ),
    );
  }
}
