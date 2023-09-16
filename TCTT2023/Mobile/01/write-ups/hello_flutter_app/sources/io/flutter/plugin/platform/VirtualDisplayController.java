package io.flutter.plugin.platform;

import android.content.Context;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.ViewTreeObserver;
import io.flutter.plugin.platform.SingleViewPresentation;
import io.flutter.view.TextureRegistry;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class VirtualDisplayController {
    private static String TAG = "VirtualDisplayController";
    private final AccessibilityEventsDelegate accessibilityEventsDelegate;
    private int bufferHeight;
    private int bufferWidth;
    private final Context context;
    private final int densityDpi;
    private final View.OnFocusChangeListener focusChangeListener;
    SingleViewPresentation presentation;
    private final Surface surface;
    private final TextureRegistry.SurfaceTextureEntry textureEntry;
    private VirtualDisplay virtualDisplay;

    public static VirtualDisplayController create(Context context, AccessibilityEventsDelegate accessibilityEventsDelegate, PlatformView view, TextureRegistry.SurfaceTextureEntry textureEntry, int width, int height, int viewId, Object createParams, View.OnFocusChangeListener focusChangeListener) {
        context.getResources().getDisplayMetrics();
        if (width != 0 && height != 0) {
            textureEntry.surfaceTexture().setDefaultBufferSize(width, height);
            Surface surface = new Surface(textureEntry.surfaceTexture());
            DisplayManager displayManager = (DisplayManager) context.getSystemService("display");
            int densityDpi = context.getResources().getDisplayMetrics().densityDpi;
            VirtualDisplay virtualDisplay = displayManager.createVirtualDisplay("flutter-vd", width, height, densityDpi, surface, 0);
            if (virtualDisplay == null) {
                return null;
            }
            VirtualDisplayController controller = new VirtualDisplayController(context, accessibilityEventsDelegate, virtualDisplay, view, surface, textureEntry, focusChangeListener, viewId, createParams);
            controller.bufferWidth = width;
            controller.bufferHeight = height;
            return controller;
        }
        return null;
    }

    private VirtualDisplayController(Context context, AccessibilityEventsDelegate accessibilityEventsDelegate, VirtualDisplay virtualDisplay, PlatformView view, Surface surface, TextureRegistry.SurfaceTextureEntry textureEntry, View.OnFocusChangeListener focusChangeListener, int viewId, Object createParams) {
        this.context = context;
        this.accessibilityEventsDelegate = accessibilityEventsDelegate;
        this.textureEntry = textureEntry;
        this.focusChangeListener = focusChangeListener;
        this.surface = surface;
        this.virtualDisplay = virtualDisplay;
        this.densityDpi = context.getResources().getDisplayMetrics().densityDpi;
        SingleViewPresentation singleViewPresentation = new SingleViewPresentation(context, this.virtualDisplay.getDisplay(), view, accessibilityEventsDelegate, viewId, focusChangeListener);
        this.presentation = singleViewPresentation;
        singleViewPresentation.show();
    }

    public int getBufferWidth() {
        return this.bufferWidth;
    }

    public int getBufferHeight() {
        return this.bufferHeight;
    }

    public void resize(int width, int height, final Runnable onNewSizeFrameAvailable) {
        boolean isFocused = getView().isFocused();
        SingleViewPresentation.PresentationState presentationState = this.presentation.detachState();
        this.virtualDisplay.setSurface(null);
        this.virtualDisplay.release();
        this.bufferWidth = width;
        this.bufferHeight = height;
        this.textureEntry.surfaceTexture().setDefaultBufferSize(width, height);
        DisplayManager displayManager = (DisplayManager) this.context.getSystemService("display");
        this.virtualDisplay = displayManager.createVirtualDisplay("flutter-vd", width, height, this.densityDpi, this.surface, 0);
        final View embeddedView = getView();
        embeddedView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() { // from class: io.flutter.plugin.platform.VirtualDisplayController.1
            @Override // android.view.View.OnAttachStateChangeListener
            public void onViewAttachedToWindow(View v) {
                OneTimeOnDrawListener.schedule(embeddedView, new Runnable() { // from class: io.flutter.plugin.platform.VirtualDisplayController.1.1
                    @Override // java.lang.Runnable
                    public void run() {
                        embeddedView.postDelayed(onNewSizeFrameAvailable, 128L);
                    }
                });
                embeddedView.removeOnAttachStateChangeListener(this);
            }

            @Override // android.view.View.OnAttachStateChangeListener
            public void onViewDetachedFromWindow(View v) {
            }
        });
        SingleViewPresentation newPresentation = new SingleViewPresentation(this.context, this.virtualDisplay.getDisplay(), this.accessibilityEventsDelegate, presentationState, this.focusChangeListener, isFocused);
        newPresentation.show();
        this.presentation.cancel();
        this.presentation = newPresentation;
    }

    public void dispose() {
        this.presentation.cancel();
        this.presentation.detachState();
        this.virtualDisplay.release();
        this.textureEntry.release();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onFlutterViewAttached(View flutterView) {
        SingleViewPresentation singleViewPresentation = this.presentation;
        if (singleViewPresentation == null || singleViewPresentation.getView() == null) {
            return;
        }
        this.presentation.getView().onFlutterViewAttached(flutterView);
    }

    void onFlutterViewDetached() {
        SingleViewPresentation singleViewPresentation = this.presentation;
        if (singleViewPresentation == null || singleViewPresentation.getView() == null) {
            return;
        }
        this.presentation.getView().onFlutterViewDetached();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onInputConnectionLocked() {
        SingleViewPresentation singleViewPresentation = this.presentation;
        if (singleViewPresentation == null || singleViewPresentation.getView() == null) {
            return;
        }
        this.presentation.getView().onInputConnectionLocked();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onInputConnectionUnlocked() {
        SingleViewPresentation singleViewPresentation = this.presentation;
        if (singleViewPresentation == null || singleViewPresentation.getView() == null) {
            return;
        }
        this.presentation.getView().onInputConnectionUnlocked();
    }

    public View getView() {
        SingleViewPresentation singleViewPresentation = this.presentation;
        if (singleViewPresentation == null) {
            return null;
        }
        PlatformView platformView = singleViewPresentation.getView();
        return platformView.getView();
    }

    public void dispatchTouchEvent(MotionEvent event) {
        SingleViewPresentation singleViewPresentation = this.presentation;
        if (singleViewPresentation == null) {
            return;
        }
        singleViewPresentation.dispatchTouchEvent(event);
    }

    /* loaded from: classes.dex */
    static class OneTimeOnDrawListener implements ViewTreeObserver.OnDrawListener {
        Runnable mOnDrawRunnable;
        final View mView;

        static void schedule(View view, Runnable runnable) {
            OneTimeOnDrawListener listener = new OneTimeOnDrawListener(view, runnable);
            view.getViewTreeObserver().addOnDrawListener(listener);
        }

        OneTimeOnDrawListener(View view, Runnable onDrawRunnable) {
            this.mView = view;
            this.mOnDrawRunnable = onDrawRunnable;
        }

        @Override // android.view.ViewTreeObserver.OnDrawListener
        public void onDraw() {
            Runnable runnable = this.mOnDrawRunnable;
            if (runnable == null) {
                return;
            }
            runnable.run();
            this.mOnDrawRunnable = null;
            this.mView.post(new Runnable() { // from class: io.flutter.plugin.platform.VirtualDisplayController.OneTimeOnDrawListener.1
                @Override // java.lang.Runnable
                public void run() {
                    OneTimeOnDrawListener.this.mView.getViewTreeObserver().removeOnDrawListener(OneTimeOnDrawListener.this);
                }
            });
        }
    }
}
