package io.flutter.view;

import android.app.Activity;
import android.content.Context;
import io.flutter.Log;
import io.flutter.app.FlutterPluginRegistry;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.embedding.engine.FlutterJNI;
import io.flutter.embedding.engine.dart.DartExecutor;
import io.flutter.embedding.engine.renderer.FlutterUiDisplayListener;
import io.flutter.plugin.common.BinaryMessenger;
import java.nio.ByteBuffer;
@Deprecated
/* loaded from: classes.dex */
public class FlutterNativeView implements BinaryMessenger {
    private static final String TAG = "FlutterNativeView";
    private boolean applicationIsRunning;
    private final DartExecutor dartExecutor;
    private final FlutterUiDisplayListener flutterUiDisplayListener;
    private final Context mContext;
    private final FlutterJNI mFlutterJNI;
    private FlutterView mFlutterView;
    private final FlutterPluginRegistry mPluginRegistry;

    @Override // io.flutter.plugin.common.BinaryMessenger
    public /* synthetic */ BinaryMessenger.TaskQueue makeBackgroundTaskQueue() {
        BinaryMessenger.TaskQueue makeBackgroundTaskQueue;
        makeBackgroundTaskQueue = makeBackgroundTaskQueue(new BinaryMessenger.TaskQueueOptions());
        return makeBackgroundTaskQueue;
    }

    public FlutterNativeView(Context context) {
        this(context, false);
    }

    public FlutterNativeView(Context context, boolean isBackgroundView) {
        FlutterUiDisplayListener flutterUiDisplayListener = new FlutterUiDisplayListener() { // from class: io.flutter.view.FlutterNativeView.1
            @Override // io.flutter.embedding.engine.renderer.FlutterUiDisplayListener
            public void onFlutterUiDisplayed() {
                if (FlutterNativeView.this.mFlutterView != null) {
                    FlutterNativeView.this.mFlutterView.onFirstFrame();
                }
            }

            @Override // io.flutter.embedding.engine.renderer.FlutterUiDisplayListener
            public void onFlutterUiNoLongerDisplayed() {
            }
        };
        this.flutterUiDisplayListener = flutterUiDisplayListener;
        if (isBackgroundView) {
            Log.w(TAG, "'isBackgroundView' is no longer supported and will be ignored");
        }
        this.mContext = context;
        this.mPluginRegistry = new FlutterPluginRegistry(this, context);
        FlutterJNI flutterJNI = new FlutterJNI();
        this.mFlutterJNI = flutterJNI;
        flutterJNI.addIsDisplayingFlutterUiListener(flutterUiDisplayListener);
        this.dartExecutor = new DartExecutor(flutterJNI, context.getAssets());
        flutterJNI.addEngineLifecycleListener(new EngineLifecycleListenerImpl());
        attach(this);
        assertAttached();
    }

    public void detachFromFlutterView() {
        this.mPluginRegistry.detach();
        this.mFlutterView = null;
    }

    public void destroy() {
        this.mPluginRegistry.destroy();
        this.dartExecutor.onDetachedFromJNI();
        this.mFlutterView = null;
        this.mFlutterJNI.removeIsDisplayingFlutterUiListener(this.flutterUiDisplayListener);
        this.mFlutterJNI.detachFromNativeAndReleaseResources();
        this.applicationIsRunning = false;
    }

    public DartExecutor getDartExecutor() {
        return this.dartExecutor;
    }

    public FlutterPluginRegistry getPluginRegistry() {
        return this.mPluginRegistry;
    }

    public void attachViewAndActivity(FlutterView flutterView, Activity activity) {
        this.mFlutterView = flutterView;
        this.mPluginRegistry.attach(flutterView, activity);
    }

    public boolean isAttached() {
        return this.mFlutterJNI.isAttached();
    }

    public void assertAttached() {
        if (!isAttached()) {
            throw new AssertionError("Platform view is not attached");
        }
    }

    public void runFromBundle(FlutterRunArguments args) {
        if (args.entrypoint == null) {
            throw new AssertionError("An entrypoint must be specified");
        }
        assertAttached();
        if (this.applicationIsRunning) {
            throw new AssertionError("This Flutter engine instance is already running an application");
        }
        this.mFlutterJNI.runBundleAndSnapshotFromLibrary(args.bundlePath, args.entrypoint, args.libraryPath, this.mContext.getResources().getAssets(), null);
        this.applicationIsRunning = true;
    }

    public boolean isApplicationRunning() {
        return this.applicationIsRunning;
    }

    @Deprecated
    public static String getObservatoryUri() {
        return FlutterJNI.getVMServiceUri();
    }

    public static String getVMServiceUri() {
        return FlutterJNI.getVMServiceUri();
    }

    @Override // io.flutter.plugin.common.BinaryMessenger
    public BinaryMessenger.TaskQueue makeBackgroundTaskQueue(BinaryMessenger.TaskQueueOptions options) {
        return this.dartExecutor.getBinaryMessenger().makeBackgroundTaskQueue(options);
    }

    @Override // io.flutter.plugin.common.BinaryMessenger
    public void send(String channel, ByteBuffer message) {
        this.dartExecutor.getBinaryMessenger().send(channel, message);
    }

    @Override // io.flutter.plugin.common.BinaryMessenger
    public void send(String channel, ByteBuffer message, BinaryMessenger.BinaryReply callback) {
        if (!isAttached()) {
            Log.d(TAG, "FlutterView.send called on a detached view, channel=" + channel);
        } else {
            this.dartExecutor.getBinaryMessenger().send(channel, message, callback);
        }
    }

    @Override // io.flutter.plugin.common.BinaryMessenger
    public void setMessageHandler(String channel, BinaryMessenger.BinaryMessageHandler handler) {
        this.dartExecutor.getBinaryMessenger().setMessageHandler(channel, handler);
    }

    @Override // io.flutter.plugin.common.BinaryMessenger
    public void setMessageHandler(String channel, BinaryMessenger.BinaryMessageHandler handler, BinaryMessenger.TaskQueue taskQueue) {
        this.dartExecutor.getBinaryMessenger().setMessageHandler(channel, handler, taskQueue);
    }

    @Override // io.flutter.plugin.common.BinaryMessenger
    public void enableBufferingIncomingMessages() {
    }

    @Override // io.flutter.plugin.common.BinaryMessenger
    public void disableBufferingIncomingMessages() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public FlutterJNI getFlutterJNI() {
        return this.mFlutterJNI;
    }

    private void attach(FlutterNativeView view) {
        this.mFlutterJNI.attachToNative();
        this.dartExecutor.onAttachedToJNI();
    }

    /* loaded from: classes.dex */
    private final class EngineLifecycleListenerImpl implements FlutterEngine.EngineLifecycleListener {
        private EngineLifecycleListenerImpl() {
        }

        @Override // io.flutter.embedding.engine.FlutterEngine.EngineLifecycleListener
        public void onPreEngineRestart() {
            if (FlutterNativeView.this.mFlutterView != null) {
                FlutterNativeView.this.mFlutterView.resetAccessibilityTree();
            }
            if (FlutterNativeView.this.mPluginRegistry != null) {
                FlutterNativeView.this.mPluginRegistry.onPreEngineRestart();
            }
        }

        @Override // io.flutter.embedding.engine.FlutterEngine.EngineLifecycleListener
        public void onEngineWillDestroy() {
        }
    }
}
