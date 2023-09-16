package io.flutter.embedding.engine;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.ColorSpace;
import android.graphics.ImageDecoder;
import android.os.Build;
import android.os.Looper;
import android.util.Size;
import android.view.Surface;
import io.flutter.Log;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.embedding.engine.dart.PlatformMessageHandler;
import io.flutter.embedding.engine.deferredcomponents.DeferredComponentManager;
import io.flutter.embedding.engine.mutatorsstack.FlutterMutatorsStack;
import io.flutter.embedding.engine.renderer.FlutterUiDisplayListener;
import io.flutter.embedding.engine.renderer.SurfaceTextureWrapper;
import io.flutter.plugin.common.StandardMessageCodec;
import io.flutter.plugin.localization.LocalizationPlugin;
import io.flutter.plugin.platform.PlatformViewsController;
import io.flutter.util.Preconditions;
import io.flutter.view.AccessibilityBridge;
import io.flutter.view.FlutterCallbackInformation;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.locks.ReentrantReadWriteLock;
/* loaded from: classes.dex */
public class FlutterJNI {
    private static final String TAG = "FlutterJNI";
    private static AsyncWaitForVsyncDelegate asyncWaitForVsyncDelegate;
    private static String vmServiceUri;
    private AccessibilityDelegate accessibilityDelegate;
    private DeferredComponentManager deferredComponentManager;
    private LocalizationPlugin localizationPlugin;
    private Long nativeShellHolderId;
    private PlatformMessageHandler platformMessageHandler;
    private PlatformViewsController platformViewsController;
    private static boolean loadLibraryCalled = false;
    private static boolean prefetchDefaultFontManagerCalled = false;
    private static boolean initCalled = false;
    private static float refreshRateFPS = 60.0f;
    private ReentrantReadWriteLock shellHolderLock = new ReentrantReadWriteLock();
    private final Set<FlutterEngine.EngineLifecycleListener> engineLifecycleListeners = new CopyOnWriteArraySet();
    private final Set<FlutterUiDisplayListener> flutterUiDisplayListeners = new CopyOnWriteArraySet();
    private final Looper mainLooper = Looper.getMainLooper();

    /* loaded from: classes.dex */
    public interface AccessibilityDelegate {
        void updateCustomAccessibilityActions(ByteBuffer byteBuffer, String[] strArr);

        void updateSemantics(ByteBuffer byteBuffer, String[] strArr, ByteBuffer[] byteBufferArr);
    }

    /* loaded from: classes.dex */
    public interface AsyncWaitForVsyncDelegate {
        void asyncWaitForVsync(long j);
    }

    private native long nativeAttach(FlutterJNI flutterJNI);

    private native void nativeCleanupMessageData(long j);

    private native void nativeDeferredComponentInstallFailure(int i, String str, boolean z);

    private native void nativeDestroy(long j);

    private native void nativeDispatchEmptyPlatformMessage(long j, String str, int i);

    private native void nativeDispatchPlatformMessage(long j, String str, ByteBuffer byteBuffer, int i, int i2);

    private native void nativeDispatchPointerDataPacket(long j, ByteBuffer byteBuffer, int i);

    private native void nativeDispatchSemanticsAction(long j, int i, int i2, ByteBuffer byteBuffer, int i3);

    private native boolean nativeFlutterTextUtilsIsEmoji(int i);

    private native boolean nativeFlutterTextUtilsIsEmojiModifier(int i);

    private native boolean nativeFlutterTextUtilsIsEmojiModifierBase(int i);

    private native boolean nativeFlutterTextUtilsIsRegionalIndicator(int i);

    private native boolean nativeFlutterTextUtilsIsVariationSelector(int i);

    private native Bitmap nativeGetBitmap(long j);

    private native boolean nativeGetIsSoftwareRenderingEnabled();

    public static native void nativeImageHeaderCallback(long j, int i, int i2);

    private static native void nativeInit(Context context, String[] strArr, String str, String str2, String str3, long j);

    private native void nativeInvokePlatformMessageEmptyResponseCallback(long j, int i);

    private native void nativeInvokePlatformMessageResponseCallback(long j, int i, ByteBuffer byteBuffer, int i2);

    private native void nativeLoadDartDeferredLibrary(long j, int i, String[] strArr);

    @Deprecated
    public static native FlutterCallbackInformation nativeLookupCallbackInformation(long j);

    private native void nativeMarkTextureFrameAvailable(long j, long j2);

    private native void nativeNotifyLowMemoryWarning(long j);

    private native void nativeOnVsync(long j, long j2, long j3);

    private static native void nativePrefetchDefaultFontManager();

    private native void nativeRegisterTexture(long j, long j2, WeakReference<SurfaceTextureWrapper> weakReference);

    private native void nativeRunBundleAndSnapshotFromLibrary(long j, String str, String str2, String str3, AssetManager assetManager, List<String> list);

    private native void nativeSetAccessibilityFeatures(long j, int i);

    private native void nativeSetSemanticsEnabled(long j, boolean z);

    private native void nativeSetViewportMetrics(long j, float f, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9, int i10, int i11, int i12, int i13, int i14, int i15, int[] iArr, int[] iArr2, int[] iArr3);

    private native FlutterJNI nativeSpawn(long j, String str, String str2, String str3, List<String> list);

    private native void nativeSurfaceChanged(long j, int i, int i2);

    private native void nativeSurfaceCreated(long j, Surface surface);

    private native void nativeSurfaceDestroyed(long j);

    private native void nativeSurfaceWindowChanged(long j, Surface surface);

    private native void nativeUnregisterTexture(long j, long j2);

    private native void nativeUpdateJavaAssetManager(long j, AssetManager assetManager, String str);

    private native void nativeUpdateRefreshRate(float f);

    /* loaded from: classes.dex */
    public static class Factory {
        public FlutterJNI provideFlutterJNI() {
            return new FlutterJNI();
        }
    }

    public void loadLibrary() {
        if (loadLibraryCalled) {
            Log.w(TAG, "FlutterJNI.loadLibrary called more than once");
        }
        System.loadLibrary("flutter");
        loadLibraryCalled = true;
    }

    public void prefetchDefaultFontManager() {
        if (prefetchDefaultFontManagerCalled) {
            Log.w(TAG, "FlutterJNI.prefetchDefaultFontManager called more than once");
        }
        nativePrefetchDefaultFontManager();
        prefetchDefaultFontManagerCalled = true;
    }

    public void init(Context context, String[] args, String bundlePath, String appStoragePath, String engineCachesPath, long initTimeMillis) {
        if (initCalled) {
            Log.w(TAG, "FlutterJNI.init called more than once");
        }
        nativeInit(context, args, bundlePath, appStoragePath, engineCachesPath, initTimeMillis);
        initCalled = true;
    }

    public boolean getIsSoftwareRenderingEnabled() {
        return nativeGetIsSoftwareRenderingEnabled();
    }

    public static String getVMServiceUri() {
        return vmServiceUri;
    }

    @Deprecated
    public static String getObservatoryUri() {
        return vmServiceUri;
    }

    public void setRefreshRateFPS(float refreshRateFPS2) {
        refreshRateFPS = refreshRateFPS2;
        updateRefreshRate();
    }

    public void updateRefreshRate() {
        if (!loadLibraryCalled) {
            return;
        }
        nativeUpdateRefreshRate(refreshRateFPS);
    }

    public void setAsyncWaitForVsyncDelegate(AsyncWaitForVsyncDelegate delegate) {
        asyncWaitForVsyncDelegate = delegate;
    }

    private static void asyncWaitForVsync(long cookie) {
        AsyncWaitForVsyncDelegate asyncWaitForVsyncDelegate2 = asyncWaitForVsyncDelegate;
        if (asyncWaitForVsyncDelegate2 != null) {
            asyncWaitForVsyncDelegate2.asyncWaitForVsync(cookie);
            return;
        }
        throw new IllegalStateException("An AsyncWaitForVsyncDelegate must be registered with FlutterJNI before asyncWaitForVsync() is invoked.");
    }

    public void onVsync(long frameDelayNanos, long refreshPeriodNanos, long cookie) {
        nativeOnVsync(frameDelayNanos, refreshPeriodNanos, cookie);
    }

    public boolean isCodePointEmoji(int codePoint) {
        return nativeFlutterTextUtilsIsEmoji(codePoint);
    }

    public boolean isCodePointEmojiModifier(int codePoint) {
        return nativeFlutterTextUtilsIsEmojiModifier(codePoint);
    }

    public boolean isCodePointEmojiModifierBase(int codePoint) {
        return nativeFlutterTextUtilsIsEmojiModifierBase(codePoint);
    }

    public boolean isCodePointVariantSelector(int codePoint) {
        return nativeFlutterTextUtilsIsVariationSelector(codePoint);
    }

    public boolean isCodePointRegionalIndicator(int codePoint) {
        return nativeFlutterTextUtilsIsRegionalIndicator(codePoint);
    }

    public boolean isAttached() {
        return this.nativeShellHolderId != null;
    }

    public void attachToNative() {
        ensureRunningOnMainThread();
        ensureNotAttachedToNative();
        this.shellHolderLock.writeLock().lock();
        try {
            this.nativeShellHolderId = Long.valueOf(performNativeAttach(this));
        } finally {
            this.shellHolderLock.writeLock().unlock();
        }
    }

    public long performNativeAttach(FlutterJNI flutterJNI) {
        return nativeAttach(flutterJNI);
    }

    public FlutterJNI spawn(String entrypointFunctionName, String pathToEntrypointFunction, String initialRoute, List<String> entrypointArgs) {
        ensureRunningOnMainThread();
        ensureAttachedToNative();
        FlutterJNI spawnedJNI = nativeSpawn(this.nativeShellHolderId.longValue(), entrypointFunctionName, pathToEntrypointFunction, initialRoute, entrypointArgs);
        Long l = spawnedJNI.nativeShellHolderId;
        Preconditions.checkState((l == null || l.longValue() == 0) ? false : true, "Failed to spawn new JNI connected shell from existing shell.");
        return spawnedJNI;
    }

    public void detachFromNativeAndReleaseResources() {
        ensureRunningOnMainThread();
        ensureAttachedToNative();
        this.shellHolderLock.writeLock().lock();
        try {
            nativeDestroy(this.nativeShellHolderId.longValue());
            this.nativeShellHolderId = null;
        } finally {
            this.shellHolderLock.writeLock().unlock();
        }
    }

    private void ensureNotAttachedToNative() {
        if (this.nativeShellHolderId != null) {
            throw new RuntimeException("Cannot execute operation because FlutterJNI is attached to native.");
        }
    }

    private void ensureAttachedToNative() {
        if (this.nativeShellHolderId == null) {
            throw new RuntimeException("Cannot execute operation because FlutterJNI is not attached to native.");
        }
    }

    public void addIsDisplayingFlutterUiListener(FlutterUiDisplayListener listener) {
        ensureRunningOnMainThread();
        this.flutterUiDisplayListeners.add(listener);
    }

    public void removeIsDisplayingFlutterUiListener(FlutterUiDisplayListener listener) {
        ensureRunningOnMainThread();
        this.flutterUiDisplayListeners.remove(listener);
    }

    public static Bitmap decodeImage(ByteBuffer buffer, final long imageGeneratorAddress) {
        if (Build.VERSION.SDK_INT >= 28) {
            ImageDecoder.Source source = ImageDecoder.createSource(buffer);
            try {
                return ImageDecoder.decodeBitmap(source, new ImageDecoder.OnHeaderDecodedListener() { // from class: io.flutter.embedding.engine.FlutterJNI$$ExternalSyntheticLambda0
                    @Override // android.graphics.ImageDecoder.OnHeaderDecodedListener
                    public final void onHeaderDecoded(ImageDecoder imageDecoder, ImageDecoder.ImageInfo imageInfo, ImageDecoder.Source source2) {
                        FlutterJNI.lambda$decodeImage$0(imageGeneratorAddress, imageDecoder, imageInfo, source2);
                    }
                });
            } catch (IOException e) {
                Log.e(TAG, "Failed to decode image", e);
                return null;
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$decodeImage$0(long imageGeneratorAddress, ImageDecoder decoder, ImageDecoder.ImageInfo info, ImageDecoder.Source src) {
        decoder.setTargetColorSpace(ColorSpace.get(ColorSpace.Named.SRGB));
        decoder.setAllocator(1);
        Size size = info.getSize();
        nativeImageHeaderCallback(imageGeneratorAddress, size.getWidth(), size.getHeight());
    }

    public void onFirstFrame() {
        ensureRunningOnMainThread();
        for (FlutterUiDisplayListener listener : this.flutterUiDisplayListeners) {
            listener.onFlutterUiDisplayed();
        }
    }

    void onRenderingStopped() {
        ensureRunningOnMainThread();
        for (FlutterUiDisplayListener listener : this.flutterUiDisplayListeners) {
            listener.onFlutterUiNoLongerDisplayed();
        }
    }

    public void onSurfaceCreated(Surface surface) {
        ensureRunningOnMainThread();
        ensureAttachedToNative();
        nativeSurfaceCreated(this.nativeShellHolderId.longValue(), surface);
    }

    public void onSurfaceWindowChanged(Surface surface) {
        ensureRunningOnMainThread();
        ensureAttachedToNative();
        nativeSurfaceWindowChanged(this.nativeShellHolderId.longValue(), surface);
    }

    public void onSurfaceChanged(int width, int height) {
        ensureRunningOnMainThread();
        ensureAttachedToNative();
        nativeSurfaceChanged(this.nativeShellHolderId.longValue(), width, height);
    }

    public void onSurfaceDestroyed() {
        ensureRunningOnMainThread();
        ensureAttachedToNative();
        onRenderingStopped();
        nativeSurfaceDestroyed(this.nativeShellHolderId.longValue());
    }

    public void setViewportMetrics(float devicePixelRatio, int physicalWidth, int physicalHeight, int physicalPaddingTop, int physicalPaddingRight, int physicalPaddingBottom, int physicalPaddingLeft, int physicalViewInsetTop, int physicalViewInsetRight, int physicalViewInsetBottom, int physicalViewInsetLeft, int systemGestureInsetTop, int systemGestureInsetRight, int systemGestureInsetBottom, int systemGestureInsetLeft, int physicalTouchSlop, int[] displayFeaturesBounds, int[] displayFeaturesType, int[] displayFeaturesState) {
        ensureRunningOnMainThread();
        ensureAttachedToNative();
        nativeSetViewportMetrics(this.nativeShellHolderId.longValue(), devicePixelRatio, physicalWidth, physicalHeight, physicalPaddingTop, physicalPaddingRight, physicalPaddingBottom, physicalPaddingLeft, physicalViewInsetTop, physicalViewInsetRight, physicalViewInsetBottom, physicalViewInsetLeft, systemGestureInsetTop, systemGestureInsetRight, systemGestureInsetBottom, systemGestureInsetLeft, physicalTouchSlop, displayFeaturesBounds, displayFeaturesType, displayFeaturesState);
    }

    public void dispatchPointerDataPacket(ByteBuffer buffer, int position) {
        ensureRunningOnMainThread();
        ensureAttachedToNative();
        nativeDispatchPointerDataPacket(this.nativeShellHolderId.longValue(), buffer, position);
    }

    public void setPlatformViewsController(PlatformViewsController platformViewsController) {
        ensureRunningOnMainThread();
        this.platformViewsController = platformViewsController;
    }

    public void setAccessibilityDelegate(AccessibilityDelegate accessibilityDelegate) {
        ensureRunningOnMainThread();
        this.accessibilityDelegate = accessibilityDelegate;
    }

    private void updateSemantics(ByteBuffer buffer, String[] strings, ByteBuffer[] stringAttributeArgs) {
        ensureRunningOnMainThread();
        AccessibilityDelegate accessibilityDelegate = this.accessibilityDelegate;
        if (accessibilityDelegate != null) {
            accessibilityDelegate.updateSemantics(buffer, strings, stringAttributeArgs);
        }
    }

    private void updateCustomAccessibilityActions(ByteBuffer buffer, String[] strings) {
        ensureRunningOnMainThread();
        AccessibilityDelegate accessibilityDelegate = this.accessibilityDelegate;
        if (accessibilityDelegate != null) {
            accessibilityDelegate.updateCustomAccessibilityActions(buffer, strings);
        }
    }

    public void dispatchSemanticsAction(int nodeId, AccessibilityBridge.Action action) {
        dispatchSemanticsAction(nodeId, action, null);
    }

    public void dispatchSemanticsAction(int nodeId, AccessibilityBridge.Action action, Object args) {
        ensureAttachedToNative();
        ByteBuffer encodedArgs = null;
        int position = 0;
        if (args != null) {
            encodedArgs = StandardMessageCodec.INSTANCE.encodeMessage(args);
            position = encodedArgs.position();
        }
        dispatchSemanticsAction(nodeId, action.value, encodedArgs, position);
    }

    public void dispatchSemanticsAction(int nodeId, int action, ByteBuffer args, int argsPosition) {
        ensureRunningOnMainThread();
        ensureAttachedToNative();
        nativeDispatchSemanticsAction(this.nativeShellHolderId.longValue(), nodeId, action, args, argsPosition);
    }

    public void setSemanticsEnabled(boolean enabled) {
        ensureRunningOnMainThread();
        ensureAttachedToNative();
        nativeSetSemanticsEnabled(this.nativeShellHolderId.longValue(), enabled);
    }

    public void setAccessibilityFeatures(int flags) {
        ensureRunningOnMainThread();
        ensureAttachedToNative();
        nativeSetAccessibilityFeatures(this.nativeShellHolderId.longValue(), flags);
    }

    public void registerTexture(long textureId, SurfaceTextureWrapper textureWrapper) {
        ensureRunningOnMainThread();
        ensureAttachedToNative();
        nativeRegisterTexture(this.nativeShellHolderId.longValue(), textureId, new WeakReference<>(textureWrapper));
    }

    public void markTextureFrameAvailable(long textureId) {
        ensureRunningOnMainThread();
        ensureAttachedToNative();
        nativeMarkTextureFrameAvailable(this.nativeShellHolderId.longValue(), textureId);
    }

    public void unregisterTexture(long textureId) {
        ensureRunningOnMainThread();
        ensureAttachedToNative();
        nativeUnregisterTexture(this.nativeShellHolderId.longValue(), textureId);
    }

    public void runBundleAndSnapshotFromLibrary(String bundlePath, String entrypointFunctionName, String pathToEntrypointFunction, AssetManager assetManager, List<String> entrypointArgs) {
        ensureRunningOnMainThread();
        ensureAttachedToNative();
        nativeRunBundleAndSnapshotFromLibrary(this.nativeShellHolderId.longValue(), bundlePath, entrypointFunctionName, pathToEntrypointFunction, assetManager, entrypointArgs);
    }

    public void setPlatformMessageHandler(PlatformMessageHandler platformMessageHandler) {
        ensureRunningOnMainThread();
        this.platformMessageHandler = platformMessageHandler;
    }

    public void cleanupMessageData(long messageData) {
        nativeCleanupMessageData(messageData);
    }

    public void handlePlatformMessage(String channel, ByteBuffer message, int replyId, long messageData) {
        PlatformMessageHandler platformMessageHandler = this.platformMessageHandler;
        if (platformMessageHandler != null) {
            platformMessageHandler.handleMessageFromDart(channel, message, replyId, messageData);
        } else {
            nativeCleanupMessageData(messageData);
        }
    }

    private void handlePlatformMessageResponse(int replyId, ByteBuffer reply) {
        PlatformMessageHandler platformMessageHandler = this.platformMessageHandler;
        if (platformMessageHandler != null) {
            platformMessageHandler.handlePlatformMessageResponse(replyId, reply);
        }
    }

    public void dispatchEmptyPlatformMessage(String channel, int responseId) {
        ensureRunningOnMainThread();
        if (isAttached()) {
            nativeDispatchEmptyPlatformMessage(this.nativeShellHolderId.longValue(), channel, responseId);
        } else {
            Log.w(TAG, "Tried to send a platform message to Flutter, but FlutterJNI was detached from native C++. Could not send. Channel: " + channel + ". Response ID: " + responseId);
        }
    }

    public void dispatchPlatformMessage(String channel, ByteBuffer message, int position, int responseId) {
        ensureRunningOnMainThread();
        if (isAttached()) {
            nativeDispatchPlatformMessage(this.nativeShellHolderId.longValue(), channel, message, position, responseId);
        } else {
            Log.w(TAG, "Tried to send a platform message to Flutter, but FlutterJNI was detached from native C++. Could not send. Channel: " + channel + ". Response ID: " + responseId);
        }
    }

    public void invokePlatformMessageEmptyResponseCallback(int responseId) {
        this.shellHolderLock.readLock().lock();
        try {
            if (isAttached()) {
                nativeInvokePlatformMessageEmptyResponseCallback(this.nativeShellHolderId.longValue(), responseId);
            } else {
                Log.w(TAG, "Tried to send a platform message response, but FlutterJNI was detached from native C++. Could not send. Response ID: " + responseId);
            }
        } finally {
            this.shellHolderLock.readLock().unlock();
        }
    }

    public void invokePlatformMessageResponseCallback(int responseId, ByteBuffer message, int position) {
        if (!message.isDirect()) {
            throw new IllegalArgumentException("Expected a direct ByteBuffer.");
        }
        this.shellHolderLock.readLock().lock();
        try {
            if (isAttached()) {
                nativeInvokePlatformMessageResponseCallback(this.nativeShellHolderId.longValue(), responseId, message, position);
            } else {
                Log.w(TAG, "Tried to send a platform message response, but FlutterJNI was detached from native C++. Could not send. Response ID: " + responseId);
            }
        } finally {
            this.shellHolderLock.readLock().unlock();
        }
    }

    public void addEngineLifecycleListener(FlutterEngine.EngineLifecycleListener engineLifecycleListener) {
        ensureRunningOnMainThread();
        this.engineLifecycleListeners.add(engineLifecycleListener);
    }

    public void removeEngineLifecycleListener(FlutterEngine.EngineLifecycleListener engineLifecycleListener) {
        ensureRunningOnMainThread();
        this.engineLifecycleListeners.remove(engineLifecycleListener);
    }

    private void onPreEngineRestart() {
        for (FlutterEngine.EngineLifecycleListener listener : this.engineLifecycleListeners) {
            listener.onPreEngineRestart();
        }
    }

    public void onDisplayOverlaySurface(int id, int x, int y, int width, int height) {
        ensureRunningOnMainThread();
        PlatformViewsController platformViewsController = this.platformViewsController;
        if (platformViewsController == null) {
            throw new RuntimeException("platformViewsController must be set before attempting to position an overlay surface");
        }
        platformViewsController.onDisplayOverlaySurface(id, x, y, width, height);
    }

    public void onBeginFrame() {
        ensureRunningOnMainThread();
        PlatformViewsController platformViewsController = this.platformViewsController;
        if (platformViewsController == null) {
            throw new RuntimeException("platformViewsController must be set before attempting to begin the frame");
        }
        platformViewsController.onBeginFrame();
    }

    public void onEndFrame() {
        ensureRunningOnMainThread();
        PlatformViewsController platformViewsController = this.platformViewsController;
        if (platformViewsController == null) {
            throw new RuntimeException("platformViewsController must be set before attempting to end the frame");
        }
        platformViewsController.onEndFrame();
    }

    public FlutterOverlaySurface createOverlaySurface() {
        ensureRunningOnMainThread();
        PlatformViewsController platformViewsController = this.platformViewsController;
        if (platformViewsController == null) {
            throw new RuntimeException("platformViewsController must be set before attempting to position an overlay surface");
        }
        return platformViewsController.createOverlaySurface();
    }

    public void destroyOverlaySurfaces() {
        ensureRunningOnMainThread();
        PlatformViewsController platformViewsController = this.platformViewsController;
        if (platformViewsController == null) {
            throw new RuntimeException("platformViewsController must be set before attempting to destroy an overlay surface");
        }
        platformViewsController.destroyOverlaySurfaces();
    }

    public void setLocalizationPlugin(LocalizationPlugin localizationPlugin) {
        ensureRunningOnMainThread();
        this.localizationPlugin = localizationPlugin;
    }

    public String[] computePlatformResolvedLocale(String[] strings) {
        if (this.localizationPlugin == null) {
            return new String[0];
        }
        List<Locale> supportedLocales = new ArrayList<>();
        for (int i = 0; i < strings.length; i += 3) {
            String languageCode = strings[i + 0];
            String countryCode = strings[i + 1];
            String scriptCode = strings[i + 2];
            if (Build.VERSION.SDK_INT >= 21) {
                Locale.Builder localeBuilder = new Locale.Builder();
                if (!languageCode.isEmpty()) {
                    localeBuilder.setLanguage(languageCode);
                }
                if (!countryCode.isEmpty()) {
                    localeBuilder.setRegion(countryCode);
                }
                if (!scriptCode.isEmpty()) {
                    localeBuilder.setScript(scriptCode);
                }
                supportedLocales.add(localeBuilder.build());
            } else {
                supportedLocales.add(new Locale(languageCode, countryCode));
            }
        }
        Locale result = this.localizationPlugin.resolveNativeLocale(supportedLocales);
        if (result == null) {
            return new String[0];
        }
        String[] output = new String[3];
        output[0] = result.getLanguage();
        output[1] = result.getCountry();
        if (Build.VERSION.SDK_INT >= 21) {
            output[2] = result.getScript();
        } else {
            output[2] = "";
        }
        return output;
    }

    public void setDeferredComponentManager(DeferredComponentManager deferredComponentManager) {
        ensureRunningOnMainThread();
        this.deferredComponentManager = deferredComponentManager;
        if (deferredComponentManager != null) {
            deferredComponentManager.setJNI(this);
        }
    }

    public void requestDartDeferredLibrary(int loadingUnitId) {
        DeferredComponentManager deferredComponentManager = this.deferredComponentManager;
        if (deferredComponentManager != null) {
            deferredComponentManager.installDeferredComponent(loadingUnitId, null);
        } else {
            Log.e(TAG, "No DeferredComponentManager found. Android setup must be completed before using split AOT deferred components.");
        }
    }

    public void loadDartDeferredLibrary(int loadingUnitId, String[] searchPaths) {
        ensureRunningOnMainThread();
        ensureAttachedToNative();
        nativeLoadDartDeferredLibrary(this.nativeShellHolderId.longValue(), loadingUnitId, searchPaths);
    }

    public void updateJavaAssetManager(AssetManager assetManager, String assetBundlePath) {
        ensureRunningOnMainThread();
        ensureAttachedToNative();
        nativeUpdateJavaAssetManager(this.nativeShellHolderId.longValue(), assetManager, assetBundlePath);
    }

    public void deferredComponentInstallFailure(int loadingUnitId, String error, boolean isTransient) {
        ensureRunningOnMainThread();
        nativeDeferredComponentInstallFailure(loadingUnitId, error, isTransient);
    }

    public void onDisplayPlatformView(int viewId, int x, int y, int width, int height, int viewWidth, int viewHeight, FlutterMutatorsStack mutatorsStack) {
        ensureRunningOnMainThread();
        PlatformViewsController platformViewsController = this.platformViewsController;
        if (platformViewsController == null) {
            throw new RuntimeException("platformViewsController must be set before attempting to position a platform view");
        }
        platformViewsController.onDisplayPlatformView(viewId, x, y, width, height, viewWidth, viewHeight, mutatorsStack);
    }

    public Bitmap getBitmap() {
        ensureRunningOnMainThread();
        ensureAttachedToNative();
        return nativeGetBitmap(this.nativeShellHolderId.longValue());
    }

    public void notifyLowMemoryWarning() {
        ensureRunningOnMainThread();
        ensureAttachedToNative();
        nativeNotifyLowMemoryWarning(this.nativeShellHolderId.longValue());
    }

    private void ensureRunningOnMainThread() {
        if (Looper.myLooper() != this.mainLooper) {
            throw new RuntimeException("Methods marked with @UiThread must be executed on the main thread. Current thread: " + Thread.currentThread().getName());
        }
    }
}
