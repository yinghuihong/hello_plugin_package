# hello plugin package

Integrate the functions of multiple plug-ins for daily use.

## Get platform version for Android
Get the release and sdk versions of the platform.

## Get device space for Android
Get the information about free, used and total storage or memory space.

## Package Manager for Android

This plugin offers the ability of android's `PackageManager`.
You can retrieve the `app name`, `app launcher icon` through
this package with its `package name`. The package should be
installed on a device.

# How to use

See the `./example` and `./lib` folders for the details.

## Get Space Info

```dart
// Import package
import 'package:hello_plugin_package/hello_plugin_package.dart';

// Access storage space
Future<int> _getStorageTotalSpace() async {
  return await HelloPluginPackage.getStorageTotalSpace;
}

Future<int> _getStorageFreeSpace() async {
  return await HelloPluginPackage.getStorageFreeSpace;
}

Future<int> _getStorageUsedSpace() async {
  return await HelloPluginPackage.getStorageUsedSpace;
}

// Access memory space
Future<int> _getMemoryTotalSpace() async {
  return await HelloPluginPackage.getMemoryTotalSpace;
}

Future<int> _getMemoryFreeSpace() async {
  return await HelloPluginPackage.getMemoryFreeSpace;
}

Future<int> _getMemoryUsedSpace() async {
  return await HelloPluginPackage.getMemoryUsedSpace;
}
```


## Get package information from the package name

```dart
import 'package:hello_plugin_package/hello_plugin_package.dart';

/// ... other codes

Future<PackageInfo> getPackageInfoByPackageName() async {
  final PackageInfo info =
    await HelloPluginPackage.getPackageInfoByPackageName('com.facebook.katana');
  return info;
}
```

`PackageInfo` class contains `packageName`, `appName` and `appIconByteArray`.
`appIconByteArray` is an array of `base64` byte image of app icon.
You can get flutter's `Image` widget icon by `appIcon` getter.
If the app is not installed, than `null` is returned.

## Get package names of the all applications installed on the device

```dart
import 'package:hello_plugin_package/hello_plugin_package.dart';

/// ... other codes

Future<List> getInstalledPackageNames() async {
  // All apps including system apps
  List packages = await HelloPluginPackage.getInstalledPackageNames();

  // Apps installed by user
  List userInstalledPackages = await HelloPluginPackage.getUserInstalledPackages();
  return packages;
}
```

`getUserInstalledPackages` on Android 11 is limit. You should declaration android.permission.QUERY_ALL_PACKAGES permission.
