import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:hello_plugin_package/hello_plugin_package.dart';

// import 'package:intent/action.dart' as android_action;
// import 'package:intent/intent.dart' as android_intent;

/// https://stackoverflow.com/questions/51998995/invalid-arguments-illegal-argument-in-isolate-message-object-is-a-closure
/// https://docs.flutter.dev/cookbook/networking/background-parsing
/// Callback must be a top-level function
List<PackageInfo> parsePackageInfoList(List list) {
  return list.map((e) => PackageInfo.fromMap(e)).toList();
}

//
class AppManagerScreen extends StatefulWidget {
  const AppManagerScreen({Key? key}) : super(key: key);

  @override
  State<AppManagerScreen> createState() => _AppManagerScreenState();
}

class _AppManagerScreenState extends State<AppManagerScreen> {
  List<PackageInfo> _packageInfos = List.empty(growable: true);

  @override
  void initState() {
    super.initState();

    /// Invoker
    HelloPluginPackage.invokeGetUserInstalledPackageInfos();

    /// Receiver
    HelloPluginPackage.channel.setMethodCallHandler((call) async {
      switch (call.method) {
        case 'receiveGetUserInstalledPackageInfos':
          List list = call.arguments;
          debugPrint(
              "xxx receiveGetUserInstalledPackageInfos ... ${list.length}");
          compute(parsePackageInfoList, list).then((value) {
            _packageInfos = value;
            setState(() {});
          });
          break;
        case 'uninstallAppSuccess':
          String uninstallPackageName = call.arguments;
          _packageInfos = _packageInfos
              .where((element) => element.packageName != uninstallPackageName)
              .toList();
          setState(() {});
          break;
        default:
          return await Future<void>.error('Method not defined');
      }
    });
  }

  @override
  Widget build(BuildContext context) {
    debugPrint("xxx build");
    return Scaffold(
      appBar: AppBar(
        title: const Text('App Manager'),
      ),
      body: _packageInfos.isEmpty
          ? const Center(child: CircularProgressIndicator())
          : ListView.builder(
              shrinkWrap: true,
              itemCount: _packageInfos.length,
              itemBuilder: (context, index) {
                PackageInfo _packageInfo = _packageInfos[index];
                return ListTile(
                  leading: _packageInfo.getAppIcon(),
                  title: Text(_packageInfo.appName),
                  subtitle: Text("${_packageInfo.firstInstallTime}"),
                  onTap: () {
                    HelloPluginPackage.uninstallApp(_packageInfo.packageName);
                  },
                );
              },
            ),
    );
  }
}
