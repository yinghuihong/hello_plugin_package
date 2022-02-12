package com.example.hello_plugin_package;

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.*
import android.util.Base64
import io.flutter.Log
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.JSONMethodCodec
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.PluginRegistry
import org.json.JSONArray
import java.io.ByteArrayOutputStream


const val TAG = "Flutter Package Manager"

/** HelloPluginPackagePlugin  */
class HelloPluginPackagePlugin : FlutterPlugin, MethodChannel.MethodCallHandler, ActivityAware, PluginRegistry.ActivityResultListener {

    private lateinit var activity: Activity

    /// The MethodChannel that will the communication between Flutter and native Android
    ///
    /// This local reference serves to register the plugin with the Flutter Engine and unregister it
    /// when the Flutter Engine is detached from the Activity
    private lateinit var channel: MethodChannel
    private lateinit var context: Context

//    fun registerWith(registrar: Registrar) {
//        val instance = HelloPluginPackagePlugin()
//        instance.setupChannels(registrar.messenger(), registrar.activity())
//        registrar.addActivityResultListener(instance)
//        registrar.addRequestPermissionsResultListener(instance)
//    }

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
            "getPlatformReleaseVersion" -> result.success(Build.VERSION.RELEASE)
            "getPlatformSdkVersion" -> result.success(Build.VERSION.SDK_INT)

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
            "getInstalledPackageInfos" -> result.success(getInstalledPackageInfos())
            "getUserInstalledPackageInfos" -> result.success(getInstalledPackageInfos(true))
            "invokeGetUserInstalledPackageInfos" -> {
                val handler = Handler(Looper.getMainLooper()) { msg ->
                    if (msg.what == 1) {
                        channel.invokeMethod("receiveGetUserInstalledPackageInfos", msg.obj)
                    }
                    true
                }
                Thread {
                    val list = getInstalledPackageInfos(true)
                    val message = Message.obtain(handler)
                    message.what = 1
                    message.obj = list
                    handler.sendMessage(message)
                }.start()
                result.success(true)
            }
            "getInstalledPackageNames" -> result.success(getInstalledPackageNames())
            "getUserInstalledPackageNames" -> result.success(getInstalledPackageNames(true))

            "uninstallApp" -> {
                val args = call.arguments as JSONArray
                uninstallApp(args[0] as String)
                result.success(true)
            }

            else -> result.notImplemented()
        }
    }

    private fun getStorageTotalSpace(): Long {
        // path：/data
//        val path = Environment.getExternalStorageDirectory()
//        val stat = StatFs(path.path)
//        return stat.totalBytes
        return SDCardUtil.total(context);
    }

    private fun getStorageFreeSpace(): Long {
//        val path = Environment.getExternalStorageDirectory()
//        val stat = StatFs(path.path)
//        return stat.availableBytes
        return SDCardUtil.free(context);
    }

    private fun getStorageUsedSpace(): Long {
//        val path = Environment.getExternalStorageDirectory()
//        val stat = StatFs(path.path)
//        return stat.totalBytes - stat.availableBytes
        return SDCardUtil.total(context) - SDCardUtil.free(context);
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
        return memoryInfo.totalMem - memoryInfo.availMem
    }

    /**
     * get all installed package's package info
     */
    private fun getInstalledPackageInfos(userInstalled: Boolean = false): ArrayList<HashMap<String, Any?>> {
        val ret = ArrayList<HashMap<String, Any?>>()
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
                        getPackageInfoByPackageName(pName)?.let { it1 -> ret.add(it1) }
                    }
                }
        return ret
    }

    /**
     * get all installed package's package name
     */
    private fun getInstalledPackageNames(userInstalled: Boolean = false): ArrayList<String> {
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

            info["packageName"] = appInfo.packageName
            val appName: String = context.packageManager.getApplicationLabel(appInfo).toString()
            info["appName"] = appName
            val appIcon: Drawable = context.packageManager.getApplicationIcon(appInfo.packageName)
            val byteImage = drawableToBase64String(appIcon)
            info["appIcon"] = byteImage
            val pi = context.packageManager.getPackageInfo(packageName, 0)
            info["versionName"] = pi.versionName
            info["versionCode"] = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                pi.longVersionCode
            } else {
                pi.versionCode
            }
            info["firstInstallTime"] = pi.firstInstallTime
            info["lastUpdateTime"] = pi.lastUpdateTime
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
            pi.applicationInfo.publicSourceDir = apkFilePath

            info["packageName"] = pi.packageName
            info["appName"] = pi.applicationInfo.loadLabel(pm) as String
            val appIcon = pi.applicationInfo.loadIcon(pm)
            val byteImage = drawableToBase64String(appIcon)
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
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
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

    private var uninstallAppPackageName: String? = null

    private fun uninstallApp(packageName: String) {
        uninstallAppPackageName = packageName
        try {
            Log.d(TAG, "xxx uninstall apk $packageName ..")
            val intent = Intent(Intent.ACTION_DELETE, Uri.parse("package:$packageName"))
            activity.startActivityForResult(intent, 1000)
        } catch (e: Exception) {
            Log.e(TAG, "xxx uninstall apk $packageName failed", e)
        }
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        activity = binding.activity
        binding.addActivityResultListener(this);
    }

    override fun onDetachedFromActivityForConfigChanges() {
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
    }

    override fun onDetachedFromActivity() {
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        Log.d(TAG, "xxx onActivityResult requestCode $requestCode resultCode $resultCode data $data")
        if (requestCode == 1000) {
            /// resultCode always be RESULT_CANCELED. Should be check uninstall result..
            uninstallAppPackageName?.let {
                if (getPackageInfoByPackageName(it) == null) {
                    channel.invokeMethod("uninstallAppSuccess", uninstallAppPackageName)
                }
            }
        }
        return false
    }
}

