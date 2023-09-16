package io.flutter.embedding.engine.systemchannels;

import io.flutter.Log;
import io.flutter.embedding.engine.dart.DartExecutor;
import io.flutter.plugin.common.JSONMethodCodec;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import java.util.HashMap;
import java.util.Map;
/* loaded from: classes.dex */
public class NavigationChannel {
    private static final String TAG = "NavigationChannel";
    public final MethodChannel channel;
    private final MethodChannel.MethodCallHandler defaultHandler;

    public NavigationChannel(DartExecutor dartExecutor) {
        MethodChannel.MethodCallHandler methodCallHandler = new MethodChannel.MethodCallHandler() { // from class: io.flutter.embedding.engine.systemchannels.NavigationChannel.1
            @Override // io.flutter.plugin.common.MethodChannel.MethodCallHandler
            public void onMethodCall(MethodCall call, MethodChannel.Result result) {
                result.success(null);
            }
        };
        this.defaultHandler = methodCallHandler;
        MethodChannel methodChannel = new MethodChannel(dartExecutor, "flutter/navigation", JSONMethodCodec.INSTANCE);
        this.channel = methodChannel;
        methodChannel.setMethodCallHandler(methodCallHandler);
    }

    public void setInitialRoute(String initialRoute) {
        Log.v(TAG, "Sending message to set initial route to '" + initialRoute + "'");
        this.channel.invokeMethod("setInitialRoute", initialRoute);
    }

    public void pushRoute(String route) {
        Log.v(TAG, "Sending message to push route '" + route + "'");
        this.channel.invokeMethod("pushRoute", route);
    }

    public void pushRouteInformation(String route) {
        Log.v(TAG, "Sending message to push route information '" + route + "'");
        Map<String, String> message = new HashMap<>();
        message.put("location", route);
        this.channel.invokeMethod("pushRouteInformation", message);
    }

    public void popRoute() {
        Log.v(TAG, "Sending message to pop route.");
        this.channel.invokeMethod("popRoute", null);
    }

    public void setMethodCallHandler(MethodChannel.MethodCallHandler handler) {
        this.channel.setMethodCallHandler(handler);
    }
}
