package io.flutter.embedding.engine.systemchannels;

import io.flutter.Log;
import io.flutter.embedding.engine.dart.DartExecutor;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.StandardMethodCodec;
import java.util.HashMap;
import java.util.Map;
/* loaded from: classes.dex */
public class RestorationChannel {
    private static final String TAG = "RestorationChannel";
    private MethodChannel channel;
    private boolean engineHasProvidedData;
    private boolean frameworkHasRequestedData;
    private final MethodChannel.MethodCallHandler handler;
    private MethodChannel.Result pendingFrameworkRestorationChannelRequest;
    private byte[] restorationData;
    public final boolean waitForRestorationData;

    public RestorationChannel(DartExecutor dartExecutor, boolean waitForRestorationData) {
        this(new MethodChannel(dartExecutor, "flutter/restoration", StandardMethodCodec.INSTANCE), waitForRestorationData);
    }

    RestorationChannel(MethodChannel channel, boolean waitForRestorationData) {
        this.engineHasProvidedData = false;
        this.frameworkHasRequestedData = false;
        MethodChannel.MethodCallHandler methodCallHandler = new MethodChannel.MethodCallHandler() { // from class: io.flutter.embedding.engine.systemchannels.RestorationChannel.2
            /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
            @Override // io.flutter.plugin.common.MethodChannel.MethodCallHandler
            public void onMethodCall(MethodCall call, MethodChannel.Result result) {
                char c;
                String method = call.method;
                Object args = call.arguments;
                switch (method.hashCode()) {
                    case 102230:
                        if (method.equals("get")) {
                            c = 1;
                            break;
                        }
                        c = 65535;
                        break;
                    case 111375:
                        if (method.equals("put")) {
                            c = 0;
                            break;
                        }
                        c = 65535;
                        break;
                    default:
                        c = 65535;
                        break;
                }
                switch (c) {
                    case 0:
                        RestorationChannel.this.restorationData = (byte[]) args;
                        result.success(null);
                        return;
                    case 1:
                        RestorationChannel.this.frameworkHasRequestedData = true;
                        if (!RestorationChannel.this.engineHasProvidedData && RestorationChannel.this.waitForRestorationData) {
                            RestorationChannel.this.pendingFrameworkRestorationChannelRequest = result;
                            return;
                        }
                        RestorationChannel restorationChannel = RestorationChannel.this;
                        result.success(restorationChannel.packageData(restorationChannel.restorationData));
                        return;
                    default:
                        result.notImplemented();
                        return;
                }
            }
        };
        this.handler = methodCallHandler;
        this.channel = channel;
        this.waitForRestorationData = waitForRestorationData;
        channel.setMethodCallHandler(methodCallHandler);
    }

    public byte[] getRestorationData() {
        return this.restorationData;
    }

    public void setRestorationData(final byte[] data) {
        this.engineHasProvidedData = true;
        MethodChannel.Result result = this.pendingFrameworkRestorationChannelRequest;
        if (result != null) {
            result.success(packageData(data));
            this.pendingFrameworkRestorationChannelRequest = null;
            this.restorationData = data;
        } else if (this.frameworkHasRequestedData) {
            this.channel.invokeMethod("push", packageData(data), new MethodChannel.Result() { // from class: io.flutter.embedding.engine.systemchannels.RestorationChannel.1
                @Override // io.flutter.plugin.common.MethodChannel.Result
                public void success(Object result2) {
                    RestorationChannel.this.restorationData = data;
                }

                @Override // io.flutter.plugin.common.MethodChannel.Result
                public void error(String errorCode, String errorMessage, Object errorDetails) {
                    Log.e(RestorationChannel.TAG, "Error " + errorCode + " while sending restoration data to framework: " + errorMessage);
                }

                @Override // io.flutter.plugin.common.MethodChannel.Result
                public void notImplemented() {
                }
            });
        } else {
            this.restorationData = data;
        }
    }

    public void clearData() {
        this.restorationData = null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Map<String, Object> packageData(byte[] data) {
        Map<String, Object> packaged = new HashMap<>();
        packaged.put("enabled", true);
        packaged.put("data", data);
        return packaged;
    }
}
