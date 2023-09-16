package io.flutter.plugin.common;

import io.flutter.Log;
import io.flutter.plugin.common.MethodChannel;
/* loaded from: classes.dex */
public class ErrorLogResult implements MethodChannel.Result {
    private int level;
    private String tag;

    public ErrorLogResult(String tag) {
        this(tag, Log.WARN);
    }

    public ErrorLogResult(String tag, int level) {
        this.tag = tag;
        this.level = level;
    }

    @Override // io.flutter.plugin.common.MethodChannel.Result
    public void success(Object result) {
    }

    @Override // io.flutter.plugin.common.MethodChannel.Result
    public void error(String errorCode, String errorMessage, Object errorDetails) {
        String details = errorDetails != null ? " details: " + errorDetails : "";
        int i = Log.WARN;
        Log.println(this.level, this.tag, errorMessage + details);
    }

    @Override // io.flutter.plugin.common.MethodChannel.Result
    public void notImplemented() {
        int i = Log.WARN;
        Log.println(this.level, this.tag, "method not implemented");
    }
}
