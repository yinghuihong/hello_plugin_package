import 'dart:async';
import 'dart:io';
import 'dart:math';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:hello_plugin_package/hello_plugin_package.dart';
import 'package:hello_plugin_package_example/text_view.dart';
import 'package:permission_handler/permission_handler.dart';

import 'app_manager_screen.dart';
import 'load_image_screen.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformReleaseVersion = 'Unknown';
  int _platformSdkVersion = 0;

  int _storageTotalSpace = 0;
  int _storageFreeSpace = 0;
  int _storageUsedSpace = 0;
  int _memoryTotalSpace = 0;
  int _memoryFreeSpace = 0;
  int _memoryUsedSpace = 0;

  int _userInstallPackagesNum = 0;
  PackageInfo? _packageInfoByPackageName;
  PackageInfo? _packageInfoByApkFile;

  @override
  void initState() {
    super.initState();
    _initPlatformState();
    _initSpaceState();
    _getPackageInfoByPackageName();
    _getPackageInfoByApkFile();
  }

  /// Platform messages are asynchronous, so we initialize in an async method.
  Future<void> _initPlatformState() async {
    String platformReleaseVersion;
    // Platform messages may fail, so we use a try/catch PlatformException.
    // We also handle the message potentially returning null.
    try {
      platformReleaseVersion =
          await HelloPluginPackage.platformReleaseVersion ??
              'Unknown platform release version';
    } on PlatformException {
      platformReleaseVersion = 'Failed to get platform release version.';
    }

    int platformSdkVersion = await HelloPluginPackage.platformSdkVersion;

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;

    setState(() {
      _platformReleaseVersion = platformReleaseVersion;
      _platformSdkVersion = platformSdkVersion;
    });
  }

  /// Include storage and memory space
  void _initSpaceState() async {
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
  void _getPackageInfoByPackageName() async {
    List packageNames =
        (await HelloPluginPackage.getUserInstalledPackageNames());
    PackageInfo? packageInfo =
        (await HelloPluginPackage.getPackageInfoByPackageName(packageNames[0]));
    print(packageInfo);
    _userInstallPackagesNum = packageNames.length;
    _packageInfoByPackageName = packageInfo;
    setState(() {});
  }

  /// Should declaration android.permission.QUERY_ALL_PACKAGES permission for Android 11
  void _getPackageInfoByApkFile() async {
    PackageInfo? packageInfo2 =
        (await HelloPluginPackage.getPackageInfoByApkFile(
            "/storage/emulated/0/Documents/SHAREit.apk"));
    print(packageInfo2);
    _packageInfoByApkFile = packageInfo2;
    setState(() {});
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Builder(builder: (context) {
        return Scaffold(
          appBar: AppBar(
            title: const Text('Plugin example app'),
            actions: [
              IconButton(
                onPressed: () {
                  _checkPermission(true);
                },
                icon: const Icon(Icons.security),
              ),
              IconButton(
                onPressed: () {
                  _controller!.setText("${Random().nextInt(100)}");
                },
                icon: const Icon(Icons.cancel),
              ),
            ],
          ),
          body: Center(
            child: Column(
              children: [
                Text(
                    'Running on: ${Platform.operatingSystem} $_platformReleaseVersion sdk $_platformSdkVersion \n'),
                Text('Memory total: $_memoryTotalSpace\n'),
                Text('Memory free: $_memoryFreeSpace\n'),
                Text('Memory used: $_memoryUsedSpace\n'),
                Text('Storage total: $_storageTotalSpace\n'),
                Text('Storage free: $_storageFreeSpace\n'),
                Text('Storage used: $_storageUsedSpace\n'),
                Text('User installed apps num: $_userInstallPackagesNum\n'),
                Center(
                  child: _packageInfoByPackageName?.getAppIcon(),
                ),
                Center(
                  child: _packageInfoByApkFile?.getAppIcon(),
                ),
                SizedBox(
                  height: 100,
                  child: TextView(
                    onTextViewCreated: _onTextViewCreated,
                  ),
                ),
                TextButton(
                  onPressed: () {
                    Navigator.of(context)
                        .push(MaterialPageRoute(builder: (context) {
                      return const AppManagerScreen();
                    }));
                  },
                  child: const Text("App Manager"),
                ),
                TextButton(
                  onPressed: () {
                    Navigator.of(context)
                        .push(MaterialPageRoute(builder: (context) {
                      return const LoadImageScreen();
                    }));
                  },
                  child: const Text("Load Image"),
                ),
              ],
            ),
          ),
        );
      }),
    );
  }

  TextViewController? _controller;

  void _onTextViewCreated(TextViewController controller) {
    _controller = controller;
    controller.setText('Hello from Android!');
  }

  /// Check or request storage permission
  void _checkPermission(bool shouldRequest) async {
    // Android 10 or below use storage permission
    Permission manageStoragePermission = Permission.storage;
    if ((await HelloPluginPackage.platformSdkVersion) >= 30) {
      // Android 11 use manageExternalStorage permission
      manageStoragePermission = Permission.manageExternalStorage;
    }
    PermissionStatus permissionStatus = await manageStoragePermission.status;

    if (shouldRequest) {
      debugPrint("xxx permission status before request $permissionStatus");
      permissionStatus = await manageStoragePermission.request();
      debugPrint("xxx permission status after request $permissionStatus");
      if (permissionStatus.isPermanentlyDenied) {
        // The user opted to never again see the permission request dialog for this
        // app. The only way to change the permission's status now is to let the
        // user manually enable it in the system settings.
        bool hadOpenAppSettings = await openAppSettings();
        debugPrint(
            "xxx [manager external] storageAccess isPermanentlyDenied. openAppSettings $hadOpenAppSettings");
        return;
      }
    }

    debugPrint("xxx permission status $permissionStatus");
  }
}
