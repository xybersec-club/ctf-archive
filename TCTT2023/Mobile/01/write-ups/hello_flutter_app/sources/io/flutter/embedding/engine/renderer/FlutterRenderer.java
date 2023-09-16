package io.flutter.embedding.engine.renderer;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.os.Build;
import android.os.Handler;
import android.view.Surface;
import io.flutter.Log;
import io.flutter.embedding.engine.FlutterJNI;
import io.flutter.view.TextureRegistry;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
/* loaded from: classes.dex */
public class FlutterRenderer implements TextureRegistry {
    private static final String TAG = "FlutterRenderer";
    private final FlutterJNI flutterJNI;
    private final FlutterUiDisplayListener flutterUiDisplayListener;
    private Surface surface;
    private final AtomicLong nextTextureId = new AtomicLong(0);
    private boolean isDisplayingFlutterUi = false;
    private Handler handler = new Handler();
    private final Set<WeakReference<TextureRegistry.OnTrimMemoryListener>> onTrimMemoryListeners = new HashSet();

    public FlutterRenderer(FlutterJNI flutterJNI) {
        FlutterUiDisplayListener flutterUiDisplayListener = new FlutterUiDisplayListener() { // from class: io.flutter.embedding.engine.renderer.FlutterRenderer.1
            @Override // io.flutter.embedding.engine.renderer.FlutterUiDisplayListener
            public void onFlutterUiDisplayed() {
                FlutterRenderer.this.isDisplayingFlutterUi = true;
            }

            @Override // io.flutter.embedding.engine.renderer.FlutterUiDisplayListener
            public void onFlutterUiNoLongerDisplayed() {
                FlutterRenderer.this.isDisplayingFlutterUi = false;
            }
        };
        this.flutterUiDisplayListener = flutterUiDisplayListener;
        this.flutterJNI = flutterJNI;
        flutterJNI.addIsDisplayingFlutterUiListener(flutterUiDisplayListener);
    }

    public boolean isDisplayingFlutterUi() {
        return this.isDisplayingFlutterUi;
    }

    public void addIsDisplayingFlutterUiListener(FlutterUiDisplayListener listener) {
        this.flutterJNI.addIsDisplayingFlutterUiListener(listener);
        if (this.isDisplayingFlutterUi) {
            listener.onFlutterUiDisplayed();
        }
    }

    public void removeIsDisplayingFlutterUiListener(FlutterUiDisplayListener listener) {
        this.flutterJNI.removeIsDisplayingFlutterUiListener(listener);
    }

    private void clearDeadListeners() {
        Iterator<WeakReference<TextureRegistry.OnTrimMemoryListener>> iterator = this.onTrimMemoryListeners.iterator();
        while (iterator.hasNext()) {
            WeakReference<TextureRegistry.OnTrimMemoryListener> listenerRef = iterator.next();
            TextureRegistry.OnTrimMemoryListener listener = listenerRef.get();
            if (listener == null) {
                iterator.remove();
            }
        }
    }

    void addOnTrimMemoryListener(TextureRegistry.OnTrimMemoryListener listener) {
        clearDeadListeners();
        this.onTrimMemoryListeners.add(new WeakReference<>(listener));
    }

    void removeOnTrimMemoryListener(TextureRegistry.OnTrimMemoryListener listener) {
        for (WeakReference<TextureRegistry.OnTrimMemoryListener> listenerRef : this.onTrimMemoryListeners) {
            if (listenerRef.get() == listener) {
                this.onTrimMemoryListeners.remove(listenerRef);
                return;
            }
        }
    }

    @Override // io.flutter.view.TextureRegistry
    public TextureRegistry.SurfaceTextureEntry createSurfaceTexture() {
        Log.v(TAG, "Creating a SurfaceTexture.");
        SurfaceTexture surfaceTexture = new SurfaceTexture(0);
        return registerSurfaceTexture(surfaceTexture);
    }

    @Override // io.flutter.view.TextureRegistry
    public TextureRegistry.SurfaceTextureEntry registerSurfaceTexture(SurfaceTexture surfaceTexture) {
        surfaceTexture.detachFromGLContext();
        SurfaceTextureRegistryEntry entry = new SurfaceTextureRegistryEntry(this.nextTextureId.getAndIncrement(), surfaceTexture);
        Log.v(TAG, "New SurfaceTexture ID: " + entry.id());
        registerTexture(entry.id(), entry.textureWrapper());
        addOnTrimMemoryListener(entry);
        return entry;
    }

    @Override // io.flutter.view.TextureRegistry
    public void onTrimMemory(int level) {
        Iterator<WeakReference<TextureRegistry.OnTrimMemoryListener>> iterator = this.onTrimMemoryListeners.iterator();
        while (iterator.hasNext()) {
            WeakReference<TextureRegistry.OnTrimMemoryListener> listenerRef = iterator.next();
            TextureRegistry.OnTrimMemoryListener listener = listenerRef.get();
            if (listener != null) {
                listener.onTrimMemory(level);
            } else {
                iterator.remove();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public final class SurfaceTextureRegistryEntry implements TextureRegistry.SurfaceTextureEntry, TextureRegistry.OnTrimMemoryListener {
        private TextureRegistry.OnFrameConsumedListener frameConsumedListener;
        private final long id;
        private final Runnable onFrameConsumed;
        private SurfaceTexture.OnFrameAvailableListener onFrameListener;
        private boolean released;
        private final SurfaceTextureWrapper textureWrapper;
        private TextureRegistry.OnTrimMemoryListener trimMemoryListener;

        SurfaceTextureRegistryEntry(long id, SurfaceTexture surfaceTexture) {
            Runnable runnable = new Runnable() { // from class: io.flutter.embedding.engine.renderer.FlutterRenderer.SurfaceTextureRegistryEntry.1
                @Override // java.lang.Runnable
                public void run() {
                    if (SurfaceTextureRegistryEntry.this.frameConsumedListener != null) {
                        SurfaceTextureRegistryEntry.this.frameConsumedListener.onFrameConsumed();
                    }
                }
            };
            this.onFrameConsumed = runnable;
            this.onFrameListener = new SurfaceTexture.OnFrameAvailableListener() { // from class: io.flutter.embedding.engine.renderer.FlutterRenderer.SurfaceTextureRegistryEntry.2
                @Override // android.graphics.SurfaceTexture.OnFrameAvailableListener
                public void onFrameAvailable(SurfaceTexture texture) {
                    if (!SurfaceTextureRegistryEntry.this.released && FlutterRenderer.this.flutterJNI.isAttached()) {
                        FlutterRenderer.this.markTextureFrameAvailable(SurfaceTextureRegistryEntry.this.id);
                    }
                }
            };
            this.id = id;
            this.textureWrapper = new SurfaceTextureWrapper(surfaceTexture, runnable);
            if (Build.VERSION.SDK_INT >= 21) {
                surfaceTexture().setOnFrameAvailableListener(this.onFrameListener, new Handler());
            } else {
                surfaceTexture().setOnFrameAvailableListener(this.onFrameListener);
            }
        }

        @Override // io.flutter.view.TextureRegistry.OnTrimMemoryListener
        public void onTrimMemory(int level) {
            TextureRegistry.OnTrimMemoryListener onTrimMemoryListener = this.trimMemoryListener;
            if (onTrimMemoryListener != null) {
                onTrimMemoryListener.onTrimMemory(level);
            }
        }

        private void removeListener() {
            FlutterRenderer.this.removeOnTrimMemoryListener(this);
        }

        public SurfaceTextureWrapper textureWrapper() {
            return this.textureWrapper;
        }

        @Override // io.flutter.view.TextureRegistry.SurfaceTextureEntry
        public SurfaceTexture surfaceTexture() {
            return this.textureWrapper.surfaceTexture();
        }

        @Override // io.flutter.view.TextureRegistry.SurfaceTextureEntry
        public long id() {
            return this.id;
        }

        @Override // io.flutter.view.TextureRegistry.SurfaceTextureEntry
        public void release() {
            if (this.released) {
                return;
            }
            Log.v(FlutterRenderer.TAG, "Releasing a SurfaceTexture (" + this.id + ").");
            this.textureWrapper.release();
            FlutterRenderer.this.unregisterTexture(this.id);
            removeListener();
            this.released = true;
        }

        protected void finalize() throws Throwable {
            try {
                if (!this.released) {
                    FlutterRenderer.this.handler.post(new SurfaceTextureFinalizerRunnable(this.id, FlutterRenderer.this.flutterJNI));
                }
            } finally {
                super.finalize();
            }
        }

        @Override // io.flutter.view.TextureRegistry.SurfaceTextureEntry
        public void setOnFrameConsumedListener(TextureRegistry.OnFrameConsumedListener listener) {
            this.frameConsumedListener = listener;
        }

        @Override // io.flutter.view.TextureRegistry.SurfaceTextureEntry
        public void setOnTrimMemoryListener(TextureRegistry.OnTrimMemoryListener listener) {
            this.trimMemoryListener = listener;
        }
    }

    /* loaded from: classes.dex */
    static final class SurfaceTextureFinalizerRunnable implements Runnable {
        private final FlutterJNI flutterJNI;
        private final long id;

        SurfaceTextureFinalizerRunnable(long id, FlutterJNI flutterJNI) {
            this.id = id;
            this.flutterJNI = flutterJNI;
        }

        @Override // java.lang.Runnable
        public void run() {
            if (!this.flutterJNI.isAttached()) {
                return;
            }
            Log.v(FlutterRenderer.TAG, "Releasing a SurfaceTexture (" + this.id + ").");
            this.flutterJNI.unregisterTexture(this.id);
        }
    }

    public void startRenderingToSurface(Surface surface, boolean keepCurrentSurface) {
        if (this.surface != null && !keepCurrentSurface) {
            stopRenderingToSurface();
        }
        this.surface = surface;
        this.flutterJNI.onSurfaceCreated(surface);
    }

    public void swapSurface(Surface surface) {
        this.surface = surface;
        this.flutterJNI.onSurfaceWindowChanged(surface);
    }

    public void surfaceChanged(int width, int height) {
        this.flutterJNI.onSurfaceChanged(width, height);
    }

    public void stopRenderingToSurface() {
        this.flutterJNI.onSurfaceDestroyed();
        this.surface = null;
        if (this.isDisplayingFlutterUi) {
            this.flutterUiDisplayListener.onFlutterUiNoLongerDisplayed();
        }
        this.isDisplayingFlutterUi = false;
    }

    public void setViewportMetrics(ViewportMetrics viewportMetrics) {
        if (!viewportMetrics.validate()) {
            return;
        }
        Log.v(TAG, "Setting viewport metrics\nSize: " + viewportMetrics.width + " x " + viewportMetrics.height + "\nPadding - L: " + viewportMetrics.viewPaddingLeft + ", T: " + viewportMetrics.viewPaddingTop + ", R: " + viewportMetrics.viewPaddingRight + ", B: " + viewportMetrics.viewPaddingBottom + "\nInsets - L: " + viewportMetrics.viewInsetLeft + ", T: " + viewportMetrics.viewInsetTop + ", R: " + viewportMetrics.viewInsetRight + ", B: " + viewportMetrics.viewInsetBottom + "\nSystem Gesture Insets - L: " + viewportMetrics.systemGestureInsetLeft + ", T: " + viewportMetrics.systemGestureInsetTop + ", R: " + viewportMetrics.systemGestureInsetRight + ", B: " + viewportMetrics.systemGestureInsetRight + "\nDisplay Features: " + viewportMetrics.displayFeatures.size());
        int[] displayFeaturesBounds = new int[viewportMetrics.displayFeatures.size() * 4];
        int[] displayFeaturesType = new int[viewportMetrics.displayFeatures.size()];
        int[] displayFeaturesState = new int[viewportMetrics.displayFeatures.size()];
        for (int i = 0; i < viewportMetrics.displayFeatures.size(); i++) {
            DisplayFeature displayFeature = viewportMetrics.displayFeatures.get(i);
            displayFeaturesBounds[i * 4] = displayFeature.bounds.left;
            displayFeaturesBounds[(i * 4) + 1] = displayFeature.bounds.top;
            displayFeaturesBounds[(i * 4) + 2] = displayFeature.bounds.right;
            displayFeaturesBounds[(i * 4) + 3] = displayFeature.bounds.bottom;
            displayFeaturesType[i] = displayFeature.type.encodedValue;
            displayFeaturesState[i] = displayFeature.state.encodedValue;
        }
        this.flutterJNI.setViewportMetrics(viewportMetrics.devicePixelRatio, viewportMetrics.width, viewportMetrics.height, viewportMetrics.viewPaddingTop, viewportMetrics.viewPaddingRight, viewportMetrics.viewPaddingBottom, viewportMetrics.viewPaddingLeft, viewportMetrics.viewInsetTop, viewportMetrics.viewInsetRight, viewportMetrics.viewInsetBottom, viewportMetrics.viewInsetLeft, viewportMetrics.systemGestureInsetTop, viewportMetrics.systemGestureInsetRight, viewportMetrics.systemGestureInsetBottom, viewportMetrics.systemGestureInsetLeft, viewportMetrics.physicalTouchSlop, displayFeaturesBounds, displayFeaturesType, displayFeaturesState);
    }

    public Bitmap getBitmap() {
        return this.flutterJNI.getBitmap();
    }

    public void dispatchPointerDataPacket(ByteBuffer buffer, int position) {
        this.flutterJNI.dispatchPointerDataPacket(buffer, position);
    }

    private void registerTexture(long textureId, SurfaceTextureWrapper textureWrapper) {
        this.flutterJNI.registerTexture(textureId, textureWrapper);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void markTextureFrameAvailable(long textureId) {
        this.flutterJNI.markTextureFrameAvailable(textureId);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void unregisterTexture(long textureId) {
        this.flutterJNI.unregisterTexture(textureId);
    }

    public boolean isSoftwareRenderingEnabled() {
        return this.flutterJNI.getIsSoftwareRenderingEnabled();
    }

    public void setAccessibilityFeatures(int flags) {
        this.flutterJNI.setAccessibilityFeatures(flags);
    }

    public void setSemanticsEnabled(boolean enabled) {
        this.flutterJNI.setSemanticsEnabled(enabled);
    }

    public void dispatchSemanticsAction(int nodeId, int action, ByteBuffer args, int argsPosition) {
        this.flutterJNI.dispatchSemanticsAction(nodeId, action, args, argsPosition);
    }

    /* loaded from: classes.dex */
    public static final class ViewportMetrics {
        public static final int unsetValue = -1;
        public float devicePixelRatio = 1.0f;
        public int width = 0;
        public int height = 0;
        public int viewPaddingTop = 0;
        public int viewPaddingRight = 0;
        public int viewPaddingBottom = 0;
        public int viewPaddingLeft = 0;
        public int viewInsetTop = 0;
        public int viewInsetRight = 0;
        public int viewInsetBottom = 0;
        public int viewInsetLeft = 0;
        public int systemGestureInsetTop = 0;
        public int systemGestureInsetRight = 0;
        public int systemGestureInsetBottom = 0;
        public int systemGestureInsetLeft = 0;
        public int physicalTouchSlop = -1;
        public List<DisplayFeature> displayFeatures = new ArrayList();

        boolean validate() {
            return this.width > 0 && this.height > 0 && this.devicePixelRatio > 0.0f;
        }
    }

    /* loaded from: classes.dex */
    public static final class DisplayFeature {
        public final Rect bounds;
        public final DisplayFeatureState state;
        public final DisplayFeatureType type;

        public DisplayFeature(Rect bounds, DisplayFeatureType type, DisplayFeatureState state) {
            this.bounds = bounds;
            this.type = type;
            this.state = state;
        }

        public DisplayFeature(Rect bounds, DisplayFeatureType type) {
            this.bounds = bounds;
            this.type = type;
            this.state = DisplayFeatureState.UNKNOWN;
        }
    }

    /* loaded from: classes.dex */
    public enum DisplayFeatureType {
        UNKNOWN(0),
        FOLD(1),
        HINGE(2),
        CUTOUT(3);
        
        public final int encodedValue;

        DisplayFeatureType(int encodedValue) {
            this.encodedValue = encodedValue;
        }
    }

    /* loaded from: classes.dex */
    public enum DisplayFeatureState {
        UNKNOWN(0),
        POSTURE_FLAT(1),
        POSTURE_HALF_OPENED(2);
        
        public final int encodedValue;

        DisplayFeatureState(int encodedValue) {
            this.encodedValue = encodedValue;
        }
    }
}
