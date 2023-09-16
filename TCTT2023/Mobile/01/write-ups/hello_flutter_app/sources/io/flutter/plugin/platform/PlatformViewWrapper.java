package io.flutter.plugin.platform;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.os.Build;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.view.accessibility.AccessibilityEvent;
import android.widget.FrameLayout;
import io.flutter.Log;
import io.flutter.embedding.android.AndroidTouchProcessor;
import io.flutter.util.ViewUtils;
import io.flutter.view.TextureRegistry;
import java.util.concurrent.atomic.AtomicLong;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class PlatformViewWrapper extends FrameLayout {
    private static final String TAG = "PlatformViewWrapper";
    ViewTreeObserver.OnGlobalFocusChangeListener activeFocusListener;
    private int bufferHeight;
    private int bufferWidth;
    private final TextureRegistry.OnFrameConsumedListener frameConsumedListener;
    private int left;
    private final AtomicLong pendingFramesCount;
    private int prevLeft;
    private int prevTop;
    private boolean shouldRecreateSurfaceForLowMemory;
    private Surface surface;
    private int top;
    private AndroidTouchProcessor touchProcessor;
    private final TextureRegistry.OnTrimMemoryListener trimMemoryListener;
    private SurfaceTexture tx;

    private void onFrameProduced() {
        if (Build.VERSION.SDK_INT == 29) {
            this.pendingFramesCount.incrementAndGet();
        }
    }

    private void recreateSurfaceIfNeeded() {
        if (this.shouldRecreateSurfaceForLowMemory) {
            Surface surface = this.surface;
            if (surface != null) {
                surface.release();
            }
            this.surface = createSurface(this.tx);
            this.shouldRecreateSurfaceForLowMemory = false;
        }
    }

    private boolean shouldDrawToSurfaceNow() {
        return Build.VERSION.SDK_INT != 29 || this.pendingFramesCount.get() <= 0;
    }

    public PlatformViewWrapper(Context context) {
        super(context);
        this.pendingFramesCount = new AtomicLong(0L);
        this.frameConsumedListener = new TextureRegistry.OnFrameConsumedListener() { // from class: io.flutter.plugin.platform.PlatformViewWrapper.1
            @Override // io.flutter.view.TextureRegistry.OnFrameConsumedListener
            public void onFrameConsumed() {
                if (Build.VERSION.SDK_INT == 29) {
                    PlatformViewWrapper.this.pendingFramesCount.decrementAndGet();
                }
            }
        };
        this.shouldRecreateSurfaceForLowMemory = false;
        this.trimMemoryListener = new TextureRegistry.OnTrimMemoryListener() { // from class: io.flutter.plugin.platform.PlatformViewWrapper.2
            @Override // io.flutter.view.TextureRegistry.OnTrimMemoryListener
            public void onTrimMemory(int level) {
                if (level == 80 && Build.VERSION.SDK_INT >= 29) {
                    PlatformViewWrapper.this.shouldRecreateSurfaceForLowMemory = true;
                }
            }
        };
        setWillNotDraw(false);
    }

    public PlatformViewWrapper(Context context, TextureRegistry.SurfaceTextureEntry textureEntry) {
        this(context);
        textureEntry.setOnFrameConsumedListener(this.frameConsumedListener);
        textureEntry.setOnTrimMemoryListener(this.trimMemoryListener);
        setTexture(textureEntry.surfaceTexture());
    }

    public void setTouchProcessor(AndroidTouchProcessor newTouchProcessor) {
        this.touchProcessor = newTouchProcessor;
    }

    public void setTexture(SurfaceTexture newTx) {
        int i;
        if (Build.VERSION.SDK_INT < 23) {
            Log.e(TAG, "Platform views cannot be displayed below API level 23. You can prevent this issue by setting `minSdkVersion: 23` in build.gradle.");
            return;
        }
        this.tx = newTx;
        int i2 = this.bufferWidth;
        if (i2 > 0 && (i = this.bufferHeight) > 0) {
            newTx.setDefaultBufferSize(i2, i);
        }
        Surface surface = this.surface;
        if (surface != null) {
            surface.release();
        }
        Surface createSurface = createSurface(newTx);
        this.surface = createSurface;
        Canvas canvas = createSurface.lockHardwareCanvas();
        try {
            canvas.drawColor(0, PorterDuff.Mode.CLEAR);
            onFrameProduced();
        } finally {
            this.surface.unlockCanvasAndPost(canvas);
        }
    }

    protected Surface createSurface(SurfaceTexture tx) {
        return new Surface(tx);
    }

    public SurfaceTexture getTexture() {
        return this.tx;
    }

    public void setLayoutParams(FrameLayout.LayoutParams params) {
        super.setLayoutParams((ViewGroup.LayoutParams) params);
        this.left = params.leftMargin;
        this.top = params.topMargin;
    }

    public void setBufferSize(int width, int height) {
        this.bufferWidth = width;
        this.bufferHeight = height;
        SurfaceTexture surfaceTexture = this.tx;
        if (surfaceTexture != null) {
            surfaceTexture.setDefaultBufferSize(width, height);
        }
    }

    public int getBufferWidth() {
        return this.bufferWidth;
    }

    public int getBufferHeight() {
        return this.bufferHeight;
    }

    public void release() {
        this.tx = null;
        Surface surface = this.surface;
        if (surface != null) {
            surface.release();
            this.surface = null;
        }
    }

    @Override // android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return true;
    }

    @Override // android.view.ViewGroup, android.view.ViewParent
    public boolean requestSendAccessibilityEvent(View child, AccessibilityEvent event) {
        View embeddedView = getChildAt(0);
        if (embeddedView != null && embeddedView.getImportantForAccessibility() == 4) {
            return false;
        }
        return super.requestSendAccessibilityEvent(child, event);
    }

    @Override // android.view.ViewGroup, android.view.ViewParent
    public void onDescendantInvalidated(View child, View target) {
        super.onDescendantInvalidated(child, target);
        invalidate();
    }

    @Override // android.view.ViewGroup, android.view.ViewParent
    public ViewParent invalidateChildInParent(int[] location, Rect dirty) {
        invalidate();
        return super.invalidateChildInParent(location, dirty);
    }

    @Override // android.view.View
    public void draw(Canvas canvas) {
        Surface surface = this.surface;
        if (surface == null) {
            super.draw(canvas);
            Log.e(TAG, "Platform view cannot be composed without a surface.");
        } else if (!surface.isValid()) {
            Log.e(TAG, "Invalid surface. The platform view cannot be displayed.");
        } else {
            SurfaceTexture surfaceTexture = this.tx;
            if (surfaceTexture == null || surfaceTexture.isReleased()) {
                Log.e(TAG, "Invalid texture. The platform view cannot be displayed.");
            } else if (!shouldDrawToSurfaceNow()) {
                invalidate();
            } else {
                recreateSurfaceIfNeeded();
                Canvas surfaceCanvas = this.surface.lockHardwareCanvas();
                try {
                    surfaceCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
                    super.draw(surfaceCanvas);
                    onFrameProduced();
                } finally {
                    this.surface.unlockCanvasAndPost(surfaceCanvas);
                }
            }
        }
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        if (this.touchProcessor == null) {
            return super.onTouchEvent(event);
        }
        Matrix screenMatrix = new Matrix();
        switch (event.getAction()) {
            case 0:
                int i = this.left;
                this.prevLeft = i;
                int i2 = this.top;
                this.prevTop = i2;
                screenMatrix.postTranslate(i, i2);
                break;
            case 1:
            default:
                screenMatrix.postTranslate(this.left, this.top);
                break;
            case 2:
                screenMatrix.postTranslate(this.prevLeft, this.prevTop);
                this.prevLeft = this.left;
                this.prevTop = this.top;
                break;
        }
        return this.touchProcessor.onTouchEvent(event, screenMatrix);
    }

    public void setOnDescendantFocusChangeListener(final View.OnFocusChangeListener userFocusListener) {
        unsetOnDescendantFocusChangeListener();
        ViewTreeObserver observer = getViewTreeObserver();
        if (observer.isAlive() && this.activeFocusListener == null) {
            ViewTreeObserver.OnGlobalFocusChangeListener onGlobalFocusChangeListener = new ViewTreeObserver.OnGlobalFocusChangeListener() { // from class: io.flutter.plugin.platform.PlatformViewWrapper.3
                @Override // android.view.ViewTreeObserver.OnGlobalFocusChangeListener
                public void onGlobalFocusChanged(View oldFocus, View newFocus) {
                    View.OnFocusChangeListener onFocusChangeListener = userFocusListener;
                    PlatformViewWrapper platformViewWrapper = PlatformViewWrapper.this;
                    onFocusChangeListener.onFocusChange(platformViewWrapper, ViewUtils.childHasFocus(platformViewWrapper));
                }
            };
            this.activeFocusListener = onGlobalFocusChangeListener;
            observer.addOnGlobalFocusChangeListener(onGlobalFocusChangeListener);
        }
    }

    public void unsetOnDescendantFocusChangeListener() {
        ViewTreeObserver observer = getViewTreeObserver();
        if (observer.isAlive() && this.activeFocusListener != null) {
            ViewTreeObserver.OnGlobalFocusChangeListener currFocusListener = this.activeFocusListener;
            this.activeFocusListener = null;
            observer.removeOnGlobalFocusChangeListener(currFocusListener);
        }
    }
}
