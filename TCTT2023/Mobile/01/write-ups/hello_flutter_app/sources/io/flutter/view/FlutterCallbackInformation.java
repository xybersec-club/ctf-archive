package io.flutter.view;

import io.flutter.embedding.engine.FlutterJNI;
/* loaded from: classes.dex */
public final class FlutterCallbackInformation {
    public final String callbackClassName;
    public final String callbackLibraryPath;
    public final String callbackName;

    public static FlutterCallbackInformation lookupCallbackInformation(long handle) {
        return FlutterJNI.nativeLookupCallbackInformation(handle);
    }

    private FlutterCallbackInformation(String callbackName, String callbackClassName, String callbackLibraryPath) {
        this.callbackName = callbackName;
        this.callbackClassName = callbackClassName;
        this.callbackLibraryPath = callbackLibraryPath;
    }
}
