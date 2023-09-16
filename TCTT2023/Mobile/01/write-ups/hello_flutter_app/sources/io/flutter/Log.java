package io.flutter;
/* loaded from: classes.dex */
public class Log {
    private static int logLevel = 3;
    public static int ASSERT = 7;
    public static int DEBUG = 3;
    public static int ERROR = 6;
    public static int INFO = 4;
    public static int VERBOSE = 2;
    public static int WARN = 5;

    public static void setLogLevel(int logLevel2) {
        logLevel = logLevel2;
    }

    public static void println(int level, String tag, String message) {
        if (logLevel <= level) {
            android.util.Log.println(level, tag, message);
        }
    }

    public static void v(String tag, String message) {
        if (logLevel <= 2) {
            android.util.Log.v(tag, message);
        }
    }

    public static void v(String tag, String message, Throwable tr) {
        if (logLevel <= 2) {
            android.util.Log.v(tag, message, tr);
        }
    }

    public static void i(String tag, String message) {
        if (logLevel <= 4) {
            android.util.Log.i(tag, message);
        }
    }

    public static void i(String tag, String message, Throwable tr) {
        if (logLevel <= 4) {
            android.util.Log.i(tag, message, tr);
        }
    }

    public static void d(String tag, String message) {
        if (logLevel <= 3) {
            android.util.Log.d(tag, message);
        }
    }

    public static void d(String tag, String message, Throwable tr) {
        if (logLevel <= 3) {
            android.util.Log.d(tag, message, tr);
        }
    }

    public static void w(String tag, String message) {
        android.util.Log.w(tag, message);
    }

    public static void w(String tag, String message, Throwable tr) {
        android.util.Log.w(tag, message, tr);
    }

    public static void e(String tag, String message) {
        android.util.Log.e(tag, message);
    }

    public static void e(String tag, String message, Throwable tr) {
        android.util.Log.e(tag, message, tr);
    }

    public static void wtf(String tag, String message) {
        android.util.Log.wtf(tag, message);
    }

    public static void wtf(String tag, String message, Throwable tr) {
        android.util.Log.wtf(tag, message, tr);
    }

    public static String getStackTraceString(Throwable tr) {
        return android.util.Log.getStackTraceString(tr);
    }
}
