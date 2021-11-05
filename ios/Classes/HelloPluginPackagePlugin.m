#import "HelloPluginPackagePlugin.h"
#if __has_include(<hello_plugin_package/hello_plugin_package-Swift.h>)
#import <hello_plugin_package/hello_plugin_package-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "hello_plugin_package-Swift.h"
#endif

@implementation HelloPluginPackagePlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftHelloPluginPackagePlugin registerWithRegistrar:registrar];
}
@end
