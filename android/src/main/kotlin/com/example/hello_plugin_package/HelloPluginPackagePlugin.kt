package com.example.hello_plugin_package;

import android.app.ActivityManager
import android.content.Context
import android.os.Environment
import android.os.StatFs
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel

/** HelloPluginPackagePlugin  */
class HelloPluginPackagePlugin : FlutterPlugin, MethodChannel.MethodCallHandler {
    /// The MethodChannel that will the communication between Flutter and native Android
    ///
    /// This local reference serves to register the plugin with the Flutter Engine and unregister it
    /// when the Flutter Engine is detached from the Activity
    private lateinit var channel: MethodChannel
    private lateinit var context: Context
    override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        context = flutterPluginBinding.applicationContext
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "hello_plugin_package")
        channel.setMethodCallHandler(this)
    }

    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        when (call.method) {
            "getPlatformVersion" -> result.success("Android " + android.os.Build.VERSION.RELEASE)

            "getStorageFreeSpace" -> result.success(getStorageFreeSpace())
            "getStorageUsedSpace" -> result.success(getStorageUsedSpace())
            "getStorageTotalSpace" -> result.success(getStorageTotalSpace())

            "getMemoryTotalSpace" -> result.success(getMemoryTotalSpace())
            "getMemoryFreeSpace" -> result.success(getMemoryFreeSpace())
            "getMemoryUsedSpace" -> result.success(getMemoryUsedSpace())

            else -> result.notImplemented()
        }
    }

    private fun getStorageTotalSpace(): Long {
        // path：/data
        val path = Environment.getDataDirectory()
        val stat = StatFs(path.path)
        return stat.totalBytes
    }

    private fun getStorageFreeSpace(): Long {
        val path = Environment.getDataDirectory()
        val stat = StatFs(path.path)
        return stat.availableBytes
    }

    private fun getStorageUsedSpace(): Long {
        val path = Environment.getDataDirectory()
        val stat = StatFs(path.path)
        return stat.totalBytes - stat.availableBytes
    }

    private fun getMemoryTotalSpace(): Long {
        val mActivityManager: ActivityManager =
            context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memoryInfo: ActivityManager.MemoryInfo = ActivityManager.MemoryInfo()
        mActivityManager.getMemoryInfo(memoryInfo)
        return memoryInfo.totalMem
    }

    private fun getMemoryFreeSpace(): Long {
        val mActivityManager: ActivityManager =
            context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memoryInfo: ActivityManager.MemoryInfo = ActivityManager.MemoryInfo()
        mActivityManager.getMemoryInfo(memoryInfo)
        return memoryInfo.availMem
    }

    private fun getMemoryUsedSpace(): Long {
        val mActivityManager: ActivityManager =
            context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memoryInfo: ActivityManager.MemoryInfo = ActivityManager.MemoryInfo()
        mActivityManager.getMemoryInfo(memoryInfo)
        return memoryInfo.totalMem - memoryInfo.availMem;
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }
}
