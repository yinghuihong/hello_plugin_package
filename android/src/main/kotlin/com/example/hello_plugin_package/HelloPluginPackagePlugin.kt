package com.example.hello_plugin_package;

import android.app.ActivityManager
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.util.Base64
import io.flutter.Log
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.JSONMethodCodec
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import org.json.JSONArray
import java.io.ByteArrayOutputStream

const val TAG = "Flutter Package Manager"

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
        channel = MethodChannel(
                flutterPluginBinding.binaryMessenger,
                "hello_plugin_package",
                JSONMethodCodec.INSTANCE,
        )
        channel.setMethodCallHandler(this)
    }

    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        when (call.method) {
            "getPlatformReleaseVersion" -> result.success(android.os.Build.VERSION.RELEASE)
            "getPlatformSdkVersion" -> result.success(android.os.Build.VERSION.SDK_INT)

            "getStorageFreeSpace" -> result.success(getStorageFreeSpace())
            "getStorageUsedSpace" -> result.success(getStorageUsedSpace())
            "getStorageTotalSpace" -> result.success(getStorageTotalSpace())

            "getMemoryTotalSpace" -> result.success(getMemoryTotalSpace())
            "getMemoryFreeSpace" -> result.success(getMemoryFreeSpace())
            "getMemoryUsedSpace" -> result.success(getMemoryUsedSpace())


            "getPackageInfoByPackageName" -> {
                val args = call.arguments as JSONArray
                result.success(getPackageInfoByPackageName(args[0] as String))
            }
            "getPackageInfoByApkFile" -> {
                val args = call.arguments as JSONArray
                result.success(getPackageInfoByApkFile(args[0] as String))
            }
            "getInstalledPackages" -> result.success(getInstalledPackages())
            "getUserInstalledPackages" -> result.success(getInstalledPackages(true))

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

    /**
     * get all installed packages's package name
     */
    private fun getInstalledPackages(userInstalled: Boolean = false): ArrayList<String> {
        val ret = ArrayList<String>()
        context
                .packageManager
                .getInstalledPackages(0)
                .forEach {
                    var pName: String? = it.packageName
                    if (userInstalled) {
                        val isSystemApp =
                                (it.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) !== 0
                        if (isSystemApp) pName = null
                    }
                    if (pName != null) {
                        ret.add(pName)
                    }
                }
        return ret
    }

    /**
     * get package info (package name, app name, app icon, versionName, versionCode) by packageName
     */
    private fun getPackageInfoByPackageName(packageName: String): HashMap<String, Any?>? {
        try {
            val info: HashMap<String, Any?> = HashMap()
            val appInfo: ApplicationInfo = context.packageManager
                    .getApplicationInfo(packageName, PackageManager.GET_META_DATA)
            val appName: String = context.packageManager.getApplicationLabel(appInfo).toString()
            val appIcon: Drawable = context.packageManager.getApplicationIcon(appInfo.packageName)
            val byteImage = drawableToBase64String(appIcon)

            info["packageName"] = appInfo.packageName
            info["appName"] = appName
            info["appIcon"] = byteImage
            val pi = context.packageManager.getPackageInfo(packageName, 0);
            info["versionName"] = pi.versionName
            info["versionCode"] = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                pi.longVersionCode
            } else {
                pi.versionCode
            }
//      Log.i(TAG, "xxx get the Package $packageName Info $info")
            return info
        } catch (e: Exception) {
            Log.e(TAG, "xxx $packageName not installed", e)
            return null
        }
    }

    /**
     * get package info (package name, app name, app icon, versionName, versionCode) by apkFilePath
     */
    private fun getPackageInfoByApkFile(apkFilePath: String): HashMap<String, Any?>? {
        try {
            val info: HashMap<String, Any?> = HashMap()
            val pm: PackageManager = context.packageManager
            val pi: PackageInfo? = pm.getPackageArchiveInfo(apkFilePath, 0)

            // the secret are these two lines....
            pi!!.applicationInfo.sourceDir = apkFilePath
            pi.applicationInfo.publicSourceDir = apkFilePath;

            val appIcon = pi.applicationInfo.loadIcon(pm)
            val byteImage = drawableToBase64String(appIcon)

            info["packageName"] = pi.packageName
            info["appName"] = pi.applicationInfo.loadLabel(pm) as String
            info["appIcon"] = byteImage
            info["versionName"] = pi.versionName
            info["versionCode"] = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                pi.longVersionCode
            } else {
                pi.versionCode
            }
//            Log.i(TAG, "xxx get the file $apkFilePath Package Info $info")
            return info
        } catch (e: Exception) {
            Log.e(TAG, "xxx get the file $apkFilePath Package info fail", e)
            return null
        }
    }

    /**
     * get base64 encoded string from drawable
     */
    private fun drawableToBase64String(drawable: Drawable): String {
        val bitmap: Bitmap = drawableToBitmap(drawable)
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        val b = baos.toByteArray()
        return Base64.encodeToString(b, Base64.DEFAULT)
    }

    /**
     * get bitmap style drawable
     */
    private fun drawableToBitmap(drawable: Drawable): Bitmap {
        val bitmap: Bitmap?

        if (drawable is BitmapDrawable) {
            if (drawable.bitmap != null) {
                return drawable.bitmap
            }
        }

        bitmap = if (drawable.intrinsicWidth <= 0 || drawable.intrinsicHeight <= 0) {
            Bitmap.createBitmap(
                    1,
                    1,
                    Bitmap.Config.ARGB_8888
            ) // Single color bitmap will be created of 1x1 pixel
        } else {
            Bitmap.createBitmap(
                    drawable.intrinsicWidth,
                    drawable.intrinsicHeight,
                    Bitmap.Config.ARGB_8888
            )
        }

        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }
}
