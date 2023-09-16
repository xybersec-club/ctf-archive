package io.flutter.embedding.engine.systemchannels;

import io.flutter.Log;
import io.flutter.embedding.engine.dart.DartExecutor;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.StandardMethodCodec;
import java.util.HashMap;
/* loaded from: classes.dex */
public class MouseCursorChannel {
    private static final String TAG = "MouseCursorChannel";
    public final MethodChannel channel;
    private MouseCursorMethodHandler mouseCursorMethodHandler;
    private final MethodChannel.MethodCallHandler parsingMethodCallHandler;

    /* loaded from: classes.dex */
    public interface MouseCursorMethodHandler {
        void activateSystemCursor(String str);
    }

    public MouseCursorChannel(DartExecutor dartExecutor) {
        MethodChannel.MethodCallHandler methodCallHandler = new MethodChannel.MethodCallHandler() { // from class: io.flutter.embedding.engine.systemchannels.MouseCursorChannel.1
            @Override // io.flutter.plugin.common.MethodChannel.MethodCallHandler
            public void onMethodCall(MethodCall call, MethodChannel.Result result) {
                if (MouseCursorChannel.this.mouseCursorMethodHandler == null) {
                    return;
                }
                String method = call.method;
                Log.v(MouseCursorChannel.TAG, "Received '" + method + "' message.");
                char c = 65535;
                try {
                    switch (method.hashCode()) {
                        case -1307105544:
                            if (method.equals("activateSystemCursor")) {
                                c = 0;
                                break;
                            }
                    }
                    switch (c) {
                        case 0:
                            HashMap<String, Object> data = (HashMap) call.arguments;
                            String kind = (String) data.get("kind");
                            try {
                                MouseCursorChannel.this.mouseCursorMethodHandler.activateSystemCursor(kind);
                                result.success(true);
                                return;
                            } catch (Exception e) {
                                result.error("error", "Error when setting cursors: " + e.getMessage(), null);
                                return;
                            }
                        default:
                            return;
                    }
                } catch (Exception e2) {
                    result.error("error", "Unhandled error: " + e2.getMessage(), null);
                }
            }
        };
        this.parsingMethodCallHandler = methodCallHandler;
        MethodChannel methodChannel = new MethodChannel(dartExecutor, "flutter/mousecursor", StandardMethodCodec.INSTANCE);
        this.channel = methodChannel;
        methodChannel.setMethodCallHandler(methodCallHandler);
    }

    public void setMethodHandler(MouseCursorMethodHandler mouseCursorMethodHandler) {
        this.mouseCursorMethodHandler = mouseCursorMethodHandler;
    }

    public void synthesizeMethodCall(MethodCall call, MethodChannel.Result result) {
        this.parsingMethodCallHandler.onMethodCall(call, result);
    }
}
