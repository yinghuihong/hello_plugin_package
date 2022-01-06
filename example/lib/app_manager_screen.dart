import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:hello_plugin_package/hello_plugin_package.dart';

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
    Future.delayed(const Duration(milliseconds: 500), () {
      // _loadPackageInfos();
      HelloPluginPackage.notifyGetUserInstalledPackageInfos();
    });

    HelloPluginPackage.channel.setMethodCallHandler((call) async {
      switch (call.method) {
        case 'receiveGetUserInstalledPackageInfos':
          List list = call.arguments;
          debugPrint(
              "xxx receiveGetUserInstalledPackageInfos ... ${list.length}");
          _packageInfos = list.map((e) => PackageInfo.fromMap(e)).toList();
          // _packageInfos = await compute(parsePackageInfoList, list);
          debugPrint("xxx ?? ${_packageInfos.length}");
          setState(() {});
          break;
        default:
          return await Future<void>.error('Method not defined');
      }
    });
  }

  List<PackageInfo> parsePackageInfoList(List list) {
    return list.map((e) => PackageInfo.fromMap(e)).toList();
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
                    _packageInfo.packageName;
                  },
                );
              },
            ),
    );
  }

// void _loadPackageInfos() async {
//   debugPrint("xxx start ${DateTime.now()}");
//   await HelloPluginPackage.getUserInstalledPackageInfos().then((value) {
//     _packageInfos = value;
//   });
//   debugPrint("xxx end ${DateTime.now()}");
//   setState(() {});
// }
}
