package io.flutter.util;

import android.os.Build;
import android.os.Handler;
import android.os.Looper;
/* loaded from: classes.dex */
public final class HandlerCompat {
    public static Handler createAsyncHandler(Looper looper) {
        if (Build.VERSION.SDK_INT >= 28) {
            return Handler.createAsync(looper);
        }
        return new Handler(looper);
    }
}
