package io.flutter.util;

import android.content.Context;
import android.os.Build;
import java.io.File;
/* loaded from: classes.dex */
public final class PathUtils {
    public static String getFilesDir(Context applicationContext) {
        File filesDir = applicationContext.getFilesDir();
        if (filesDir == null) {
            filesDir = new File(getDataDirPath(applicationContext), "files");
        }
        return filesDir.getPath();
    }

    public static String getDataDirectory(Context applicationContext) {
        File flutterDir = applicationContext.getDir("flutter", 0);
        if (flutterDir == null) {
            flutterDir = new File(getDataDirPath(applicationContext), "app_flutter");
        }
        return flutterDir.getPath();
    }

    public static String getCacheDirectory(Context applicationContext) {
        File cacheDir;
        if (Build.VERSION.SDK_INT >= 21) {
            cacheDir = applicationContext.getCodeCacheDir();
            if (cacheDir == null) {
                cacheDir = applicationContext.getCacheDir();
            }
        } else {
            cacheDir = applicationContext.getCacheDir();
        }
        if (cacheDir == null) {
            cacheDir = new File(getDataDirPath(applicationContext), "cache");
        }
        return cacheDir.getPath();
    }

    private static String getDataDirPath(Context applicationContext) {
        if (Build.VERSION.SDK_INT >= 24) {
            return applicationContext.getDataDir().getPath();
        }
        return applicationContext.getApplicationInfo().dataDir;
    }
}
