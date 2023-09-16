package io.flutter.plugin.common;

import io.flutter.Log;
/* loaded from: classes.dex */
public class FlutterException extends RuntimeException {
    private static final String TAG = "FlutterException#";
    public final String code;
    public final Object details;

    /* JADX INFO: Access modifiers changed from: package-private */
    public FlutterException(String code, String message, Object details) {
        super(message);
        if (code == null) {
            Log.e(TAG, "Parameter code must not be null.");
        }
        this.code = code;
        this.details = details;
    }
}
