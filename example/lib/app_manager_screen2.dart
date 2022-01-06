import 'package:flutter/material.dart';
import 'package:hello_plugin_package/hello_plugin_package.dart';

//
class AppManagerScreen2 extends StatefulWidget {
  const AppManagerScreen2({Key? key}) : super(key: key);

  @override
  State<AppManagerScreen2> createState() => _AppManagerScreen2State();
}

class _AppManagerScreen2State extends State<AppManagerScreen2> {
  List _packageInfos = List.empty(growable: true);

  @override
  void initState() {
    super.initState();
    _loadPackageInfos();
  }

  @override
  Widget build(BuildContext context) {
    debugPrint("xxx build");
    return Scaffold(
      appBar: AppBar(
        title: const Text('App Manager'),
      ),
      body: ListView.builder(
        itemCount: _packageInfos.length,
        itemBuilder: (context, index) {
          return ListTile(
            title: Text(_packageInfos[index]),
          );
        },
      ),
    );
  }

  void _loadPackageInfos() async {
    debugPrint("xxx start ${DateTime.now()}");
    await HelloPluginPackage.getUserInstalledPackageNames().then((value) {
      _packageInfos = value;
    });
    debugPrint("xxx end ${DateTime.now()}");
    setState(() {});
  }
}
