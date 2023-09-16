package io.flutter.embedding.engine.dart;

import android.content.res.AssetManager;
import io.flutter.FlutterInjector;
import io.flutter.Log;
import io.flutter.embedding.engine.FlutterJNI;
import io.flutter.embedding.engine.loader.FlutterLoader;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.StringCodec;
import io.flutter.util.TraceSection;
import io.flutter.view.FlutterCallbackInformation;
import java.nio.ByteBuffer;
import java.util.List;
/* loaded from: classes.dex */
public class DartExecutor implements BinaryMessenger {
    private static final String TAG = "DartExecutor";
    private final AssetManager assetManager;
    private final BinaryMessenger binaryMessenger;
    private final DartMessenger dartMessenger;
    private final FlutterJNI flutterJNI;
    private boolean isApplicationRunning;
    private final BinaryMessenger.BinaryMessageHandler isolateChannelMessageHandler;
    private String isolateServiceId;
    private IsolateServiceIdListener isolateServiceIdListener;

    /* loaded from: classes.dex */
    public interface IsolateServiceIdListener {
        void onIsolateServiceIdAvailable(String str);
    }

    @Override // io.flutter.plugin.common.BinaryMessenger
    public /* synthetic */ BinaryMessenger.TaskQueue makeBackgroundTaskQueue() {
        BinaryMessenger.TaskQueue makeBackgroundTaskQueue;
        makeBackgroundTaskQueue = makeBackgroundTaskQueue(new BinaryMessenger.TaskQueueOptions());
        return makeBackgroundTaskQueue;
    }

    public DartExecutor(FlutterJNI flutterJNI, AssetManager assetManager) {
        this.isApplicationRunning = false;
        BinaryMessenger.BinaryMessageHandler binaryMessageHandler = new BinaryMessenger.BinaryMessageHandler() { // from class: io.flutter.embedding.engine.dart.DartExecutor.1
            @Override // io.flutter.plugin.common.BinaryMessenger.BinaryMessageHandler
            public void onMessage(ByteBuffer message, BinaryMessenger.BinaryReply callback) {
                DartExecutor.this.isolateServiceId = StringCodec.INSTANCE.decodeMessage(message);
                if (DartExecutor.this.isolateServiceIdListener != null) {
                    DartExecutor.this.isolateServiceIdListener.onIsolateServiceIdAvailable(DartExecutor.this.isolateServiceId);
                }
            }
        };
        this.isolateChannelMessageHandler = binaryMessageHandler;
        this.flutterJNI = flutterJNI;
        this.assetManager = assetManager;
        DartMessenger dartMessenger = new DartMessenger(flutterJNI);
        this.dartMessenger = dartMessenger;
        dartMessenger.setMessageHandler("flutter/isolate", binaryMessageHandler);
        this.binaryMessenger = new DefaultBinaryMessenger(dartMessenger);
        if (flutterJNI.isAttached()) {
            this.isApplicationRunning = true;
        }
    }

    public void onAttachedToJNI() {
        Log.v(TAG, "Attached to JNI. Registering the platform message handler for this Dart execution context.");
        this.flutterJNI.setPlatformMessageHandler(this.dartMessenger);
    }

    public void onDetachedFromJNI() {
        Log.v(TAG, "Detached from JNI. De-registering the platform message handler for this Dart execution context.");
        this.flutterJNI.setPlatformMessageHandler(null);
    }

    public boolean isExecutingDart() {
        return this.isApplicationRunning;
    }

    public void executeDartEntrypoint(DartEntrypoint dartEntrypoint) {
        executeDartEntrypoint(dartEntrypoint, null);
    }

    public void executeDartEntrypoint(DartEntrypoint dartEntrypoint, List<String> dartEntrypointArgs) {
        if (this.isApplicationRunning) {
            Log.w(TAG, "Attempted to run a DartExecutor that is already running.");
            return;
        }
        TraceSection.begin("DartExecutor#executeDartEntrypoint");
        try {
            Log.v(TAG, "Executing Dart entrypoint: " + dartEntrypoint);
            this.flutterJNI.runBundleAndSnapshotFromLibrary(dartEntrypoint.pathToBundle, dartEntrypoint.dartEntrypointFunctionName, dartEntrypoint.dartEntrypointLibrary, this.assetManager, dartEntrypointArgs);
            this.isApplicationRunning = true;
        } finally {
            TraceSection.end();
        }
    }

    public void executeDartCallback(DartCallback dartCallback) {
        if (this.isApplicationRunning) {
            Log.w(TAG, "Attempted to run a DartExecutor that is already running.");
            return;
        }
        TraceSection.begin("DartExecutor#executeDartCallback");
        try {
            Log.v(TAG, "Executing Dart callback: " + dartCallback);
            this.flutterJNI.runBundleAndSnapshotFromLibrary(dartCallback.pathToBundle, dartCallback.callbackHandle.callbackName, dartCallback.callbackHandle.callbackLibraryPath, dartCallback.androidAssetManager, null);
            this.isApplicationRunning = true;
        } finally {
            TraceSection.end();
        }
    }

    public BinaryMessenger getBinaryMessenger() {
        return this.binaryMessenger;
    }

    @Override // io.flutter.plugin.common.BinaryMessenger
    @Deprecated
    public BinaryMessenger.TaskQueue makeBackgroundTaskQueue(BinaryMessenger.TaskQueueOptions options) {
        return this.binaryMessenger.makeBackgroundTaskQueue(options);
    }

    @Override // io.flutter.plugin.common.BinaryMessenger
    @Deprecated
    public void send(String channel, ByteBuffer message) {
        this.binaryMessenger.send(channel, message);
    }

    @Override // io.flutter.plugin.common.BinaryMessenger
    @Deprecated
    public void send(String channel, ByteBuffer message, BinaryMessenger.BinaryReply callback) {
        this.binaryMessenger.send(channel, message, callback);
    }

    @Override // io.flutter.plugin.common.BinaryMessenger
    @Deprecated
    public void setMessageHandler(String channel, BinaryMessenger.BinaryMessageHandler handler) {
        this.binaryMessenger.setMessageHandler(channel, handler);
    }

    @Override // io.flutter.plugin.common.BinaryMessenger
    @Deprecated
    public void setMessageHandler(String channel, BinaryMessenger.BinaryMessageHandler handler, BinaryMessenger.TaskQueue taskQueue) {
        this.binaryMessenger.setMessageHandler(channel, handler, taskQueue);
    }

    @Override // io.flutter.plugin.common.BinaryMessenger
    @Deprecated
    public void enableBufferingIncomingMessages() {
        this.dartMessenger.enableBufferingIncomingMessages();
    }

    @Override // io.flutter.plugin.common.BinaryMessenger
    @Deprecated
    public void disableBufferingIncomingMessages() {
        this.dartMessenger.disableBufferingIncomingMessages();
    }

    public int getPendingChannelResponseCount() {
        return this.dartMessenger.getPendingChannelResponseCount();
    }

    public String getIsolateServiceId() {
        return this.isolateServiceId;
    }

    public void setIsolateServiceIdListener(IsolateServiceIdListener listener) {
        String str;
        this.isolateServiceIdListener = listener;
        if (listener != null && (str = this.isolateServiceId) != null) {
            listener.onIsolateServiceIdAvailable(str);
        }
    }

    public void notifyLowMemoryWarning() {
        if (this.flutterJNI.isAttached()) {
            this.flutterJNI.notifyLowMemoryWarning();
        }
    }

    /* loaded from: classes.dex */
    public static class DartEntrypoint {
        public final String dartEntrypointFunctionName;
        public final String dartEntrypointLibrary;
        public final String pathToBundle;

        public static DartEntrypoint createDefault() {
            FlutterLoader flutterLoader = FlutterInjector.instance().flutterLoader();
            if (!flutterLoader.initialized()) {
                throw new AssertionError("DartEntrypoints can only be created once a FlutterEngine is created.");
            }
            return new DartEntrypoint(flutterLoader.findAppBundlePath(), "main");
        }

        public DartEntrypoint(String pathToBundle, String dartEntrypointFunctionName) {
            this.pathToBundle = pathToBundle;
            this.dartEntrypointLibrary = null;
            this.dartEntrypointFunctionName = dartEntrypointFunctionName;
        }

        public DartEntrypoint(String pathToBundle, String dartEntrypointLibrary, String dartEntrypointFunctionName) {
            this.pathToBundle = pathToBundle;
            this.dartEntrypointLibrary = dartEntrypointLibrary;
            this.dartEntrypointFunctionName = dartEntrypointFunctionName;
        }

        public String toString() {
            return "DartEntrypoint( bundle path: " + this.pathToBundle + ", function: " + this.dartEntrypointFunctionName + " )";
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            DartEntrypoint that = (DartEntrypoint) o;
            if (!this.pathToBundle.equals(that.pathToBundle)) {
                return false;
            }
            return this.dartEntrypointFunctionName.equals(that.dartEntrypointFunctionName);
        }

        public int hashCode() {
            int result = this.pathToBundle.hashCode();
            return (result * 31) + this.dartEntrypointFunctionName.hashCode();
        }
    }

    /* loaded from: classes.dex */
    public static class DartCallback {
        public final AssetManager androidAssetManager;
        public final FlutterCallbackInformation callbackHandle;
        public final String pathToBundle;

        public DartCallback(AssetManager androidAssetManager, String pathToBundle, FlutterCallbackInformation callbackHandle) {
            this.androidAssetManager = androidAssetManager;
            this.pathToBundle = pathToBundle;
            this.callbackHandle = callbackHandle;
        }

        public String toString() {
            return "DartCallback( bundle path: " + this.pathToBundle + ", library path: " + this.callbackHandle.callbackLibraryPath + ", function: " + this.callbackHandle.callbackName + " )";
        }
    }

    /* loaded from: classes.dex */
    private static class DefaultBinaryMessenger implements BinaryMessenger {
        private final DartMessenger messenger;

        @Override // io.flutter.plugin.common.BinaryMessenger
        public /* synthetic */ BinaryMessenger.TaskQueue makeBackgroundTaskQueue() {
            BinaryMessenger.TaskQueue makeBackgroundTaskQueue;
            makeBackgroundTaskQueue = makeBackgroundTaskQueue(new BinaryMessenger.TaskQueueOptions());
            return makeBackgroundTaskQueue;
        }

        private DefaultBinaryMessenger(DartMessenger messenger) {
            this.messenger = messenger;
        }

        @Override // io.flutter.plugin.common.BinaryMessenger
        public BinaryMessenger.TaskQueue makeBackgroundTaskQueue(BinaryMessenger.TaskQueueOptions options) {
            return this.messenger.makeBackgroundTaskQueue(options);
        }

        @Override // io.flutter.plugin.common.BinaryMessenger
        public void send(String channel, ByteBuffer message) {
            this.messenger.send(channel, message, null);
        }

        @Override // io.flutter.plugin.common.BinaryMessenger
        public void send(String channel, ByteBuffer message, BinaryMessenger.BinaryReply callback) {
            this.messenger.send(channel, message, callback);
        }

        @Override // io.flutter.plugin.common.BinaryMessenger
        public void setMessageHandler(String channel, BinaryMessenger.BinaryMessageHandler handler) {
            this.messenger.setMessageHandler(channel, handler);
        }

        @Override // io.flutter.plugin.common.BinaryMessenger
        public void setMessageHandler(String channel, BinaryMessenger.BinaryMessageHandler handler, BinaryMessenger.TaskQueue taskQueue) {
            this.messenger.setMessageHandler(channel, handler, taskQueue);
        }

        @Override // io.flutter.plugin.common.BinaryMessenger
        public void enableBufferingIncomingMessages() {
            this.messenger.enableBufferingIncomingMessages();
        }

        @Override // io.flutter.plugin.common.BinaryMessenger
        public void disableBufferingIncomingMessages() {
            this.messenger.disableBufferingIncomingMessages();
        }
    }
}
