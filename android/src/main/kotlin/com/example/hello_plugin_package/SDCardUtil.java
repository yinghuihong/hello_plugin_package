package com.example.hello_plugin_package;

import android.app.usage.StorageStatsManager;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

/**
 * Created by yinghuihong on 2/10/22.
 */
public class SDCardUtil {

    public static long free(Context context) {
        long j2Free;
        long j3Total;
        long j4;
        StorageManager storageManager = (StorageManager) context.getSystemService("storage");
        int i2 = Build.VERSION.SDK_INT;
        if (i2 < 23) {
            try {
                StorageVolume[] storageVolumeArr = (StorageVolume[]) StorageManager.class.getDeclaredMethod("getVolumeList", new Class[0]).invoke(storageManager, new Object[0]);
                if (storageVolumeArr != null) {
                    Method method = null;
                    long j5Free = 0;
                    long j6Total = 0;
                    for (StorageVolume storageVolume : storageVolumeArr) {
                        if (method == null) {
                            method = storageVolume.getClass().getDeclaredMethod("getPathFile", new Class[0]);
                        }
                        File file = (File) method.invoke(storageVolume, new Object[0]);
                        j6Total += file.getTotalSpace();
                        j5Free += file.getUsableSpace();
                    }
                    j3Total = j6Total;
                    j2Free = j5Free;
                } else {
                    j3Total = 0;
                    j2Free = 0;
                }
                return j2Free;
            } catch (IllegalAccessException e2) {
                e2.printStackTrace();
                return 0;
            } catch (NoSuchMethodException e3) {
                e3.printStackTrace();
                return 0;
            } catch (InvocationTargetException e4) {
                e4.printStackTrace();
                return 0;
            }
        } else {
            try {
                Iterator it = ((List) StorageManager.class.getDeclaredMethod("getVolumes", new Class[0]).invoke(storageManager, new Object[0])).iterator();
                long j7Used = 0;
                long j8Total = 0;
                while (it.hasNext()) {
                    Object next = it.next();
                    int i3 = next.getClass().getField("type").getInt(next);
                    String str = "type: " + i3;
                    if (i3 == 1) {
                        if (i2 >= 26) {
                            it = it;
                            j4 = d(context, (String) next.getClass().getDeclaredMethod("getFsUuid", new Class[0]).invoke(next, new Object[0]));
                        } else {
                            it = it;
                            j4 = i2 >= 25 ? ((Long) StorageManager.class.getMethod("getPrimaryStorageSize", new Class[0]).invoke(storageManager, new Object[0])).longValue() : 0L;
                        }
                        if (((Boolean) next.getClass().getDeclaredMethod("isMountedReadable", new Class[0]).invoke(next, new Object[0])).booleanValue()) {
                            File file2 = (File) next.getClass().getDeclaredMethod("getPath", new Class[0]).invoke(next, new Object[0]);
                            if (j4 == 0) {
                                j4 = file2.getTotalSpace();
                            }
                            file2.getTotalSpace();
                            j7Used += j4 - file2.getFreeSpace();
                            j8Total += j4;
                        }
                    } else {
                        it = it;
                        if (i3 == 0 && ((Boolean) next.getClass().getDeclaredMethod("isMountedReadable", new Class[0]).invoke(next, new Object[0])).booleanValue()) {
                            File file3 = (File) next.getClass().getDeclaredMethod("getPath", new Class[0]).invoke(next, new Object[0]);
                            j7Used += file3.getTotalSpace() - file3.getFreeSpace();
                            j8Total += file3.getTotalSpace();
                        }
                    }
                }
//    Typeface createFromAsset = Typeface.createFromAsset(context.getAssets(), "roboto_bold.ttf");
//    StringBuilder sb = new StringBuilder();
//    sb.append(context.getString(R.string.storage_tip));
//    sb.append(" ");
//    sb.append(e((float) j7, 1000.0f, context));
//    sb.append("/");
                float f2Total = (float) j8Total;
//    sb.append(e(f2, 1000.0f, context));
//    SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(sb.toString());
//    StringBuilder sb2 = new StringBuilder();
                long j9Free = j8Total - j7Used;
//    sb2.append(j9 / j8);
//    sb2.append("");
//    sb2.toString();
//    spannableStringBuilder.setSpan(new CustomTypefaceSpan("", createFromAsset), context.getString(R.string.storage_tip).length() + 1, spannableStringBuilder.length() - 1, 34);
                return j9Free;
            } catch (SecurityException unused) {
                Log.e("storage", "缺少权限：permission.PACKAGE_USAGE_STATS");
                return 0;
            } catch (Exception e5) {
                e5.printStackTrace();
                h();
                return 0;
            }
        }
    }

    public static long total(Context context) {
        long j2Free;
        long j3Total;
        long j4;
        StorageManager storageManager = (StorageManager) context.getSystemService("storage");
        int i2 = Build.VERSION.SDK_INT;
        if (i2 < 23) {
            try {
                StorageVolume[] storageVolumeArr = (StorageVolume[]) StorageManager.class.getDeclaredMethod("getVolumeList", new Class[0]).invoke(storageManager, new Object[0]);
                if (storageVolumeArr != null) {
                    Method method = null;
                    long j5Free = 0;
                    long j6Total = 0;
                    for (StorageVolume storageVolume : storageVolumeArr) {
                        if (method == null) {
                            method = storageVolume.getClass().getDeclaredMethod("getPathFile", new Class[0]);
                        }
                        File file = (File) method.invoke(storageVolume, new Object[0]);
                        j6Total += file.getTotalSpace();
                        j5Free += file.getUsableSpace();
                    }
                    j3Total = j6Total;
                    j2Free = j5Free;
                } else {
                    j3Total = 0;
                    j2Free = 0;
                }
                return j3Total;
            } catch (IllegalAccessException e2) {
                e2.printStackTrace();
                return 0;
            } catch (NoSuchMethodException e3) {
                e3.printStackTrace();
                return 0;
            } catch (InvocationTargetException e4) {
                e4.printStackTrace();
                return 0;
            }
        } else {
            try {
                Iterator it = ((List) StorageManager.class.getDeclaredMethod("getVolumes", new Class[0]).invoke(storageManager, new Object[0])).iterator();
                long j7Used = 0;
                long j8Total = 0;
                while (it.hasNext()) {
                    Object next = it.next();
                    int i3 = next.getClass().getField("type").getInt(next);
                    String str = "type: " + i3;
                    if (i3 == 1) {
                        if (i2 >= 26) {
                            it = it;
                            j4 = d(context, (String) next.getClass().getDeclaredMethod("getFsUuid", new Class[0]).invoke(next, new Object[0]));
                        } else {
                            it = it;
                            j4 = i2 >= 25 ? ((Long) StorageManager.class.getMethod("getPrimaryStorageSize", new Class[0]).invoke(storageManager, new Object[0])).longValue() : 0L;
                        }
                        if (((Boolean) next.getClass().getDeclaredMethod("isMountedReadable", new Class[0]).invoke(next, new Object[0])).booleanValue()) {
                            File file2 = (File) next.getClass().getDeclaredMethod("getPath", new Class[0]).invoke(next, new Object[0]);
                            if (j4 == 0) {
                                j4 = file2.getTotalSpace();
                            }
                            file2.getTotalSpace();
                            j7Used += j4 - file2.getFreeSpace();
                            j8Total += j4;
                        }
                    } else {
                        it = it;
                        if (i3 == 0 && ((Boolean) next.getClass().getDeclaredMethod("isMountedReadable", new Class[0]).invoke(next, new Object[0])).booleanValue()) {
                            File file3 = (File) next.getClass().getDeclaredMethod("getPath", new Class[0]).invoke(next, new Object[0]);
                            j7Used += file3.getTotalSpace() - file3.getFreeSpace();
                            j8Total += file3.getTotalSpace();
                        }
                    }
                }
//    Typeface createFromAsset = Typeface.createFromAsset(context.getAssets(), "roboto_bold.ttf");
//    StringBuilder sb = new StringBuilder();
//    sb.append(context.getString(R.string.storage_tip));
//    sb.append(" ");
//    sb.append(e((float) j7, 1000.0f, context));
//    sb.append("/");
                float f2Total = (float) j8Total;
//    sb.append(e(f2, 1000.0f, context));
//    SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(sb.toString());
//    StringBuilder sb2 = new StringBuilder();
                long j9Free = j8Total - j7Used;
//    sb2.append(j9 / j8);
//    sb2.append("");
//    sb2.toString();
//    spannableStringBuilder.setSpan(new CustomTypefaceSpan("", createFromAsset), context.getString(R.string.storage_tip).length() + 1, spannableStringBuilder.length() - 1, 34);
                Log.e("xxx", "xxx total " + j8Total);
                return j8Total;
            } catch (SecurityException unused) {
                Log.e("storage", "缺少权限：permission.PACKAGE_USAGE_STATS");
                return 0;
            } catch (Exception e5) {
                e5.printStackTrace();
                h();
                return 0;
            }
        }
    }

    public static float freePercent(Context context) {
        long j2Free;
        long j3Total;
        long j4;
        StorageManager storageManager = (StorageManager) context.getSystemService("storage");
        int i2 = Build.VERSION.SDK_INT;
        if (i2 < 23) {
            try {
                StorageVolume[] storageVolumeArr = (StorageVolume[]) StorageManager.class.getDeclaredMethod("getVolumeList", new Class[0]).invoke(storageManager, new Object[0]);
                if (storageVolumeArr != null) {
                    Method method = null;
                    long j5Free = 0;
                    long j6Total = 0;
                    for (StorageVolume storageVolume : storageVolumeArr) {
                        if (method == null) {
                            method = storageVolume.getClass().getDeclaredMethod("getPathFile", new Class[0]);
                        }
                        File file = (File) method.invoke(storageVolume, new Object[0]);
                        j6Total += file.getTotalSpace();
                        j5Free += file.getUsableSpace();
                    }
                    j3Total = j6Total;
                    j2Free = j5Free;
                } else {
                    j3Total = 0;
                    j2Free = 0;
                }
                return (((float) j2Free) * 1.0f) / ((float) j3Total);
            } catch (IllegalAccessException e2) {
                e2.printStackTrace();
                return 0.0f;
            } catch (NoSuchMethodException e3) {
                e3.printStackTrace();
                return 0.0f;
            } catch (InvocationTargetException e4) {
                e4.printStackTrace();
                return 0.0f;
            }
        } else {
            try {
                Iterator it = ((List) StorageManager.class.getDeclaredMethod("getVolumes", new Class[0]).invoke(storageManager, new Object[0])).iterator();
                long j7Used = 0;
                long j8Total = 0;
                while (it.hasNext()) {
                    Object next = it.next();
                    int i3 = next.getClass().getField("type").getInt(next);
                    String str = "type: " + i3;
                    if (i3 == 1) {
                        if (i2 >= 26) {
                            it = it;
                            j4 = d(context, (String) next.getClass().getDeclaredMethod("getFsUuid", new Class[0]).invoke(next, new Object[0]));
                        } else {
                            it = it;
                            j4 = i2 >= 25 ? ((Long) StorageManager.class.getMethod("getPrimaryStorageSize", new Class[0]).invoke(storageManager, new Object[0])).longValue() : 0L;
                        }
                        if (((Boolean) next.getClass().getDeclaredMethod("isMountedReadable", new Class[0]).invoke(next, new Object[0])).booleanValue()) {
                            File file2 = (File) next.getClass().getDeclaredMethod("getPath", new Class[0]).invoke(next, new Object[0]);
                            if (j4 == 0) {
                                j4 = file2.getTotalSpace();
                            }
                            file2.getTotalSpace();
                            j7Used += j4 - file2.getFreeSpace();
                            j8Total += j4;
                        }
                    } else {
                        it = it;
                        if (i3 == 0 && ((Boolean) next.getClass().getDeclaredMethod("isMountedReadable", new Class[0]).invoke(next, new Object[0])).booleanValue()) {
                            File file3 = (File) next.getClass().getDeclaredMethod("getPath", new Class[0]).invoke(next, new Object[0]);
                            j7Used += file3.getTotalSpace() - file3.getFreeSpace();
                            j8Total += file3.getTotalSpace();
                        }
                    }
                }
//    Typeface createFromAsset = Typeface.createFromAsset(context.getAssets(), "roboto_bold.ttf");
//    StringBuilder sb = new StringBuilder();
//    sb.append(context.getString(R.string.storage_tip));
//    sb.append(" ");
//    sb.append(e((float) j7, 1000.0f, context));
//    sb.append("/");
                float f2Total = (float) j8Total;
//    sb.append(e(f2, 1000.0f, context));
//    SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(sb.toString());
//    StringBuilder sb2 = new StringBuilder();
                long j9Free = j8Total - j7Used;
//    sb2.append(j9 / j8);
//    sb2.append("");
//    sb2.toString();
//    spannableStringBuilder.setSpan(new CustomTypefaceSpan("", createFromAsset), context.getString(R.string.storage_tip).length() + 1, spannableStringBuilder.length() - 1, 34);
                return (((float) j9Free) * 1.0f) / f2Total;
            } catch (SecurityException unused) {
                Log.e("storage", "缺少权限：permission.PACKAGE_USAGE_STATS");
                return 0.0f;
            } catch (Exception e5) {
                e5.printStackTrace();
                h();
                return 0.0f;
            }
        }
    }

    public static void h() {
        StatFs statFs = new StatFs(Environment.getExternalStorageDirectory().getPath());
        statFs.getBlockCount();
        statFs.getBlockSize();
        statFs.getAvailableBlocks();
        statFs.getFreeBlocks();
    }

    @RequiresApi(26)
    public static long d(Context context, String str) {
        UUID uuid;
        try {
            if (str == null) {
                uuid = StorageManager.UUID_DEFAULT;
            } else {
                uuid = UUID.fromString(str);
            }
            return ((StorageStatsManager) context.getSystemService(StorageStatsManager.class)).getTotalBytes(uuid);
        } catch (IOException | NoClassDefFoundError | NoSuchFieldError | NullPointerException e2) {
            e2.printStackTrace();
            return -1L;
        }
    }
}
