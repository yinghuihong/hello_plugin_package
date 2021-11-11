import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:hello_plugin_package/hello_plugin_package.dart';

void main() {
  const MethodChannel channel =
      MethodChannel('hello_plugin_package', JSONMethodCodec());

  TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '12';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformReleaseVersion', () async {
    expect(await HelloPluginPackage.platformReleaseVersion, '12');
  });
}
