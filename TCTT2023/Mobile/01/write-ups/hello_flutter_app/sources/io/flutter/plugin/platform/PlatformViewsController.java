package io.flutter.plugin.platform;

import android.content.Context;
import android.content.MutableContextWrapper;
import android.os.Build;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import io.flutter.Log;
import io.flutter.embedding.android.AndroidTouchProcessor;
import io.flutter.embedding.android.FlutterView;
import io.flutter.embedding.android.MotionEventTracker;
import io.flutter.embedding.engine.FlutterOverlaySurface;
import io.flutter.embedding.engine.dart.DartExecutor;
import io.flutter.embedding.engine.mutatorsstack.FlutterMutatorView;
import io.flutter.embedding.engine.mutatorsstack.FlutterMutatorsStack;
import io.flutter.embedding.engine.renderer.FlutterRenderer;
import io.flutter.embedding.engine.systemchannels.PlatformViewsChannel;
import io.flutter.plugin.editing.TextInputPlugin;
import io.flutter.plugin.platform.PlatformViewsController;
import io.flutter.util.ViewUtils;
import io.flutter.view.AccessibilityBridge;
import io.flutter.view.TextureRegistry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
/* loaded from: classes.dex */
public class PlatformViewsController implements PlatformViewsAccessibilityDelegate {
    private static final String TAG = "PlatformViewsController";
    private static Class[] VIEW_TYPES_REQUIRE_VIRTUAL_DISPLAY = {SurfaceView.class};
    private AndroidTouchProcessor androidTouchProcessor;
    private Context context;
    private FlutterView flutterView;
    private PlatformViewsChannel platformViewsChannel;
    private TextInputPlugin textInputPlugin;
    private TextureRegistry textureRegistry;
    private int nextOverlayLayerId = 0;
    private boolean flutterViewConvertedToImageView = false;
    private boolean synchronizeToNativeViewHierarchy = true;
    private boolean usesSoftwareRendering = false;
    private final PlatformViewsChannel.PlatformViewsHandler channelHandler = new AnonymousClass1();
    private final PlatformViewRegistryImpl registry = new PlatformViewRegistryImpl();
    final HashMap<Integer, VirtualDisplayController> vdControllers = new HashMap<>();
    private final AccessibilityEventsDelegate accessibilityEventsDelegate = new AccessibilityEventsDelegate();
    final HashMap<Context, View> contextToEmbeddedView = new HashMap<>();
    private final SparseArray<PlatformOverlayView> overlayLayerViews = new SparseArray<>();
    private final HashSet<Integer> currentFrameUsedOverlayLayerIds = new HashSet<>();
    private final HashSet<Integer> currentFrameUsedPlatformViewIds = new HashSet<>();
    private final SparseArray<PlatformViewWrapper> viewWrappers = new SparseArray<>();
    private final SparseArray<PlatformView> platformViews = new SparseArray<>();
    private final SparseArray<FlutterMutatorView> platformViewParent = new SparseArray<>();
    private final MotionEventTracker motionEventTracker = MotionEventTracker.getInstance();

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: io.flutter.plugin.platform.PlatformViewsController$1  reason: invalid class name */
    /* loaded from: classes.dex */
    public class AnonymousClass1 implements PlatformViewsChannel.PlatformViewsHandler {
        AnonymousClass1() {
        }

        @Override // io.flutter.embedding.engine.systemchannels.PlatformViewsChannel.PlatformViewsHandler
        public void createForPlatformViewLayer(PlatformViewsChannel.PlatformViewCreationRequest request) {
            ensureValidAndroidVersion(19);
            ensureValidRequest(request);
            PlatformView platformView = createPlatformView(request, false);
            configureForHybridComposition(platformView, request);
        }

        @Override // io.flutter.embedding.engine.systemchannels.PlatformViewsChannel.PlatformViewsHandler
        public long createForTextureLayer(PlatformViewsChannel.PlatformViewCreationRequest request) {
            ensureValidRequest(request);
            int viewId = request.viewId;
            if (PlatformViewsController.this.viewWrappers.get(viewId) == null) {
                if (PlatformViewsController.this.textureRegistry != null) {
                    if (PlatformViewsController.this.flutterView == null) {
                        throw new IllegalStateException("Flutter view is null. This means the platform views controller doesn't have an attached view, view id: " + viewId);
                    }
                    boolean supportsTextureLayerMode = true;
                    PlatformView platformView = createPlatformView(request, true);
                    View embeddedView = platformView.getView();
                    if (embeddedView.getParent() != null) {
                        throw new IllegalStateException("The Android view returned from PlatformView#getView() was already added to a parent view.");
                    }
                    supportsTextureLayerMode = (Build.VERSION.SDK_INT < 23 || ViewUtils.hasChildViewOfType(embeddedView, PlatformViewsController.VIEW_TYPES_REQUIRE_VIRTUAL_DISPLAY)) ? false : false;
                    if (!supportsTextureLayerMode) {
                        if (request.displayMode != PlatformViewsChannel.PlatformViewCreationRequest.RequestedDisplayMode.TEXTURE_WITH_HYBRID_FALLBACK) {
                            if (!PlatformViewsController.this.usesSoftwareRendering) {
                                return configureForVirtualDisplay(platformView, request);
                            }
                        } else {
                            configureForHybridComposition(platformView, request);
                            return -2L;
                        }
                    }
                    return configureForTextureLayerComposition(platformView, request);
                }
                throw new IllegalStateException("Texture registry is null. This means that platform views controller was detached, view id: " + viewId);
            }
            throw new IllegalStateException("Trying to create an already created platform view, view id: " + viewId);
        }

        @Override // io.flutter.embedding.engine.systemchannels.PlatformViewsChannel.PlatformViewsHandler
        public void dispose(int viewId) {
            PlatformView platformView = (PlatformView) PlatformViewsController.this.platformViews.get(viewId);
            if (platformView != null) {
                PlatformViewsController.this.platformViews.remove(viewId);
                try {
                    platformView.dispose();
                } catch (RuntimeException exception) {
                    Log.e(PlatformViewsController.TAG, "Disposing platform view threw an exception", exception);
                }
                if (!PlatformViewsController.this.usesVirtualDisplay(viewId)) {
                    PlatformViewWrapper viewWrapper = (PlatformViewWrapper) PlatformViewsController.this.viewWrappers.get(viewId);
                    if (viewWrapper == null) {
                        FlutterMutatorView parentView = (FlutterMutatorView) PlatformViewsController.this.platformViewParent.get(viewId);
                        if (parentView != null) {
                            parentView.removeAllViews();
                            parentView.unsetOnDescendantFocusChangeListener();
                            ViewGroup mutatorViewParent = (ViewGroup) parentView.getParent();
                            if (mutatorViewParent != null) {
                                mutatorViewParent.removeView(parentView);
                            }
                            PlatformViewsController.this.platformViewParent.remove(viewId);
                            return;
                        }
                        return;
                    }
                    viewWrapper.removeAllViews();
                    viewWrapper.release();
                    viewWrapper.unsetOnDescendantFocusChangeListener();
                    ViewGroup wrapperParent = (ViewGroup) viewWrapper.getParent();
                    if (wrapperParent != null) {
                        wrapperParent.removeView(viewWrapper);
                    }
                    PlatformViewsController.this.viewWrappers.remove(viewId);
                    return;
                }
                VirtualDisplayController vdController = PlatformViewsController.this.vdControllers.get(Integer.valueOf(viewId));
                View embeddedView = vdController.getView();
                if (embeddedView != null) {
                    PlatformViewsController.this.contextToEmbeddedView.remove(embeddedView.getContext());
                }
                PlatformViewsController.this.vdControllers.remove(Integer.valueOf(viewId));
                return;
            }
            Log.e(PlatformViewsController.TAG, "Disposing unknown platform view with id: " + viewId);
        }

        @Override // io.flutter.embedding.engine.systemchannels.PlatformViewsChannel.PlatformViewsHandler
        public void offset(int viewId, double top, double left) {
            if (!PlatformViewsController.this.usesVirtualDisplay(viewId)) {
                PlatformViewWrapper viewWrapper = (PlatformViewWrapper) PlatformViewsController.this.viewWrappers.get(viewId);
                if (viewWrapper != null) {
                    int physicalTop = PlatformViewsController.this.toPhysicalPixels(top);
                    int physicalLeft = PlatformViewsController.this.toPhysicalPixels(left);
                    FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) viewWrapper.getLayoutParams();
                    layoutParams.topMargin = physicalTop;
                    layoutParams.leftMargin = physicalLeft;
                    viewWrapper.setLayoutParams(layoutParams);
                    return;
                }
                Log.e(PlatformViewsController.TAG, "Setting offset for unknown platform view with id: " + viewId);
            }
        }

        @Override // io.flutter.embedding.engine.systemchannels.PlatformViewsChannel.PlatformViewsHandler
        public void resize(PlatformViewsChannel.PlatformViewResizeRequest request, final PlatformViewsChannel.PlatformViewBufferResized onComplete) {
            int physicalWidth = PlatformViewsController.this.toPhysicalPixels(request.newLogicalWidth);
            int physicalHeight = PlatformViewsController.this.toPhysicalPixels(request.newLogicalHeight);
            int viewId = request.viewId;
            if (PlatformViewsController.this.usesVirtualDisplay(viewId)) {
                final float originalDisplayDensity = PlatformViewsController.this.getDisplayDensity();
                final VirtualDisplayController vdController = PlatformViewsController.this.vdControllers.get(Integer.valueOf(viewId));
                PlatformViewsController.this.lockInputConnection(vdController);
                vdController.resize(physicalWidth, physicalHeight, new Runnable() { // from class: io.flutter.plugin.platform.PlatformViewsController$1$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        PlatformViewsController.AnonymousClass1.this.m33x7b26d3(vdController, originalDisplayDensity, onComplete);
                    }
                });
                return;
            }
            PlatformView platformView = (PlatformView) PlatformViewsController.this.platformViews.get(viewId);
            PlatformViewWrapper viewWrapper = (PlatformViewWrapper) PlatformViewsController.this.viewWrappers.get(viewId);
            if (platformView == null || viewWrapper == null) {
                Log.e(PlatformViewsController.TAG, "Resizing unknown platform view with id: " + viewId);
                return;
            }
            if (physicalWidth > viewWrapper.getBufferWidth() || physicalHeight > viewWrapper.getBufferHeight()) {
                viewWrapper.setBufferSize(physicalWidth, physicalHeight);
            }
            ViewGroup.LayoutParams viewWrapperLayoutParams = viewWrapper.getLayoutParams();
            viewWrapperLayoutParams.width = physicalWidth;
            viewWrapperLayoutParams.height = physicalHeight;
            viewWrapper.setLayoutParams(viewWrapperLayoutParams);
            View embeddedView = platformView.getView();
            if (embeddedView != null) {
                ViewGroup.LayoutParams embeddedViewLayoutParams = embeddedView.getLayoutParams();
                embeddedViewLayoutParams.width = physicalWidth;
                embeddedViewLayoutParams.height = physicalHeight;
                embeddedView.setLayoutParams(embeddedViewLayoutParams);
            }
            onComplete.run(new PlatformViewsChannel.PlatformViewBufferSize(PlatformViewsController.this.toLogicalPixels(viewWrapper.getBufferWidth()), PlatformViewsController.this.toLogicalPixels(viewWrapper.getBufferHeight())));
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* renamed from: lambda$resize$0$io-flutter-plugin-platform-PlatformViewsController$1  reason: not valid java name */
        public /* synthetic */ void m33x7b26d3(VirtualDisplayController vdController, float originalDisplayDensity, PlatformViewsChannel.PlatformViewBufferResized onComplete) {
            PlatformViewsController.this.unlockInputConnection(vdController);
            float displayDensity = PlatformViewsController.this.context == null ? originalDisplayDensity : PlatformViewsController.this.getDisplayDensity();
            onComplete.run(new PlatformViewsChannel.PlatformViewBufferSize(PlatformViewsController.this.toLogicalPixels(vdController.getBufferWidth(), displayDensity), PlatformViewsController.this.toLogicalPixels(vdController.getBufferHeight(), displayDensity)));
        }

        @Override // io.flutter.embedding.engine.systemchannels.PlatformViewsChannel.PlatformViewsHandler
        public void onTouch(PlatformViewsChannel.PlatformViewTouch touch) {
            int viewId = touch.viewId;
            float density = PlatformViewsController.this.context.getResources().getDisplayMetrics().density;
            if (!PlatformViewsController.this.usesVirtualDisplay(viewId)) {
                PlatformView platformView = (PlatformView) PlatformViewsController.this.platformViews.get(viewId);
                if (platformView == null) {
                    Log.e(PlatformViewsController.TAG, "Sending touch to an unknown view with id: " + viewId);
                    return;
                }
                View view = platformView.getView();
                if (view == null) {
                    Log.e(PlatformViewsController.TAG, "Sending touch to a null view with id: " + viewId);
                    return;
                }
                MotionEvent event = PlatformViewsController.this.toMotionEvent(density, touch, false);
                view.dispatchTouchEvent(event);
                return;
            }
            VirtualDisplayController vdController = PlatformViewsController.this.vdControllers.get(Integer.valueOf(viewId));
            MotionEvent event2 = PlatformViewsController.this.toMotionEvent(density, touch, true);
            vdController.dispatchTouchEvent(event2);
        }

        @Override // io.flutter.embedding.engine.systemchannels.PlatformViewsChannel.PlatformViewsHandler
        public void setDirection(int viewId, int direction) {
            View embeddedView;
            if (!PlatformViewsController.validateDirection(direction)) {
                throw new IllegalStateException("Trying to set unknown direction value: " + direction + "(view id: " + viewId + ")");
            }
            if (!PlatformViewsController.this.usesVirtualDisplay(viewId)) {
                PlatformView platformView = (PlatformView) PlatformViewsController.this.platformViews.get(viewId);
                if (platformView == null) {
                    Log.e(PlatformViewsController.TAG, "Setting direction to an unknown view with id: " + viewId);
                    return;
                }
                embeddedView = platformView.getView();
            } else {
                VirtualDisplayController controller = PlatformViewsController.this.vdControllers.get(Integer.valueOf(viewId));
                embeddedView = controller.getView();
            }
            if (embeddedView == null) {
                Log.e(PlatformViewsController.TAG, "Setting direction to a null view with id: " + viewId);
            } else {
                embeddedView.setLayoutDirection(direction);
            }
        }

        @Override // io.flutter.embedding.engine.systemchannels.PlatformViewsChannel.PlatformViewsHandler
        public void clearFocus(int viewId) {
            View embeddedView;
            if (!PlatformViewsController.this.usesVirtualDisplay(viewId)) {
                PlatformView platformView = (PlatformView) PlatformViewsController.this.platformViews.get(viewId);
                if (platformView == null) {
                    Log.e(PlatformViewsController.TAG, "Clearing focus on an unknown view with id: " + viewId);
                    return;
                }
                embeddedView = platformView.getView();
            } else {
                VirtualDisplayController controller = PlatformViewsController.this.vdControllers.get(Integer.valueOf(viewId));
                embeddedView = controller.getView();
            }
            if (embeddedView == null) {
                Log.e(PlatformViewsController.TAG, "Clearing focus on a null view with id: " + viewId);
            } else {
                embeddedView.clearFocus();
            }
        }

        private void ensureValidAndroidVersion(int minSdkVersion) {
            if (Build.VERSION.SDK_INT < minSdkVersion) {
                throw new IllegalStateException("Trying to use platform views with API " + Build.VERSION.SDK_INT + ", required API level is: " + minSdkVersion);
            }
        }

        private void ensureValidRequest(PlatformViewsChannel.PlatformViewCreationRequest request) {
            if (!PlatformViewsController.validateDirection(request.direction)) {
                throw new IllegalStateException("Trying to create a view with unknown direction value: " + request.direction + "(view id: " + request.viewId + ")");
            }
        }

        private PlatformView createPlatformView(PlatformViewsChannel.PlatformViewCreationRequest request, boolean wrapContext) {
            PlatformViewFactory viewFactory = PlatformViewsController.this.registry.getFactory(request.viewType);
            if (viewFactory == null) {
                throw new IllegalStateException("Trying to create a platform view of unregistered type: " + request.viewType);
            }
            Object createParams = null;
            if (request.params != null) {
                createParams = viewFactory.getCreateArgsCodec().decodeMessage(request.params);
            }
            Context mutableContext = wrapContext ? new MutableContextWrapper(PlatformViewsController.this.context) : PlatformViewsController.this.context;
            PlatformView platformView = viewFactory.create(mutableContext, request.viewId, createParams);
            View embeddedView = platformView.getView();
            if (embeddedView == null) {
                throw new IllegalStateException("PlatformView#getView() returned null, but an Android view reference was expected.");
            }
            embeddedView.setLayoutDirection(request.direction);
            PlatformViewsController.this.platformViews.put(request.viewId, platformView);
            return platformView;
        }

        private void configureForHybridComposition(PlatformView platformView, PlatformViewsChannel.PlatformViewCreationRequest request) {
            Log.i(PlatformViewsController.TAG, "Using hybrid composition for platform view: " + request.viewId);
        }

        private long configureForVirtualDisplay(PlatformView platformView, final PlatformViewsChannel.PlatformViewCreationRequest request) {
            ensureValidAndroidVersion(20);
            Log.i(PlatformViewsController.TAG, "Hosting view in a virtual display for platform view: " + request.viewId);
            TextureRegistry.SurfaceTextureEntry textureEntry = PlatformViewsController.this.textureRegistry.createSurfaceTexture();
            int physicalWidth = PlatformViewsController.this.toPhysicalPixels(request.logicalWidth);
            int physicalHeight = PlatformViewsController.this.toPhysicalPixels(request.logicalHeight);
            VirtualDisplayController vdController = VirtualDisplayController.create(PlatformViewsController.this.context, PlatformViewsController.this.accessibilityEventsDelegate, platformView, textureEntry, physicalWidth, physicalHeight, request.viewId, null, new View.OnFocusChangeListener() { // from class: io.flutter.plugin.platform.PlatformViewsController$1$$ExternalSyntheticLambda2
                @Override // android.view.View.OnFocusChangeListener
                public final void onFocusChange(View view, boolean z) {
                    PlatformViewsController.AnonymousClass1.this.m32xbd462dcc(request, view, z);
                }
            });
            if (vdController != null) {
                if (PlatformViewsController.this.flutterView != null) {
                    vdController.onFlutterViewAttached(PlatformViewsController.this.flutterView);
                }
                PlatformViewsController.this.vdControllers.put(Integer.valueOf(request.viewId), vdController);
                View embeddedView = platformView.getView();
                PlatformViewsController.this.contextToEmbeddedView.put(embeddedView.getContext(), embeddedView);
                return textureEntry.id();
            }
            throw new IllegalStateException("Failed creating virtual display for a " + request.viewType + " with id: " + request.viewId);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* renamed from: lambda$configureForVirtualDisplay$1$io-flutter-plugin-platform-PlatformViewsController$1  reason: not valid java name */
        public /* synthetic */ void m32xbd462dcc(PlatformViewsChannel.PlatformViewCreationRequest request, View view, boolean hasFocus) {
            if (hasFocus) {
                PlatformViewsController.this.platformViewsChannel.invokeViewFocused(request.viewId);
            }
        }

        private long configureForTextureLayerComposition(PlatformView platformView, final PlatformViewsChannel.PlatformViewCreationRequest request) {
            PlatformViewWrapper viewWrapper;
            long textureId;
            ensureValidAndroidVersion(23);
            Log.i(PlatformViewsController.TAG, "Hosting view in view hierarchy for platform view: " + request.viewId);
            int physicalWidth = PlatformViewsController.this.toPhysicalPixels(request.logicalWidth);
            int physicalHeight = PlatformViewsController.this.toPhysicalPixels(request.logicalHeight);
            if (!PlatformViewsController.this.usesSoftwareRendering) {
                TextureRegistry.SurfaceTextureEntry textureEntry = PlatformViewsController.this.textureRegistry.createSurfaceTexture();
                PlatformViewWrapper viewWrapper2 = new PlatformViewWrapper(PlatformViewsController.this.context, textureEntry);
                long id = textureEntry.id();
                viewWrapper = viewWrapper2;
                textureId = id;
            } else {
                viewWrapper = new PlatformViewWrapper(PlatformViewsController.this.context);
                textureId = -1;
            }
            viewWrapper.setTouchProcessor(PlatformViewsController.this.androidTouchProcessor);
            viewWrapper.setBufferSize(physicalWidth, physicalHeight);
            FrameLayout.LayoutParams viewWrapperLayoutParams = new FrameLayout.LayoutParams(physicalWidth, physicalHeight);
            int physicalTop = PlatformViewsController.this.toPhysicalPixels(request.logicalTop);
            int physicalLeft = PlatformViewsController.this.toPhysicalPixels(request.logicalLeft);
            viewWrapperLayoutParams.topMargin = physicalTop;
            viewWrapperLayoutParams.leftMargin = physicalLeft;
            viewWrapper.setLayoutParams(viewWrapperLayoutParams);
            View embeddedView = platformView.getView();
            embeddedView.setLayoutParams(new FrameLayout.LayoutParams(physicalWidth, physicalHeight));
            embeddedView.setImportantForAccessibility(4);
            viewWrapper.addView(embeddedView);
            viewWrapper.setOnDescendantFocusChangeListener(new View.OnFocusChangeListener() { // from class: io.flutter.plugin.platform.PlatformViewsController$1$$ExternalSyntheticLambda0
                @Override // android.view.View.OnFocusChangeListener
                public final void onFocusChange(View view, boolean z) {
                    PlatformViewsController.AnonymousClass1.this.m31xfdfb23ee(request, view, z);
                }
            });
            PlatformViewsController.this.flutterView.addView(viewWrapper);
            PlatformViewsController.this.viewWrappers.append(request.viewId, viewWrapper);
            return textureId;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* renamed from: lambda$configureForTextureLayerComposition$2$io-flutter-plugin-platform-PlatformViewsController$1  reason: not valid java name */
        public /* synthetic */ void m31xfdfb23ee(PlatformViewsChannel.PlatformViewCreationRequest request, View v, boolean hasFocus) {
            if (hasFocus) {
                PlatformViewsController.this.platformViewsChannel.invokeViewFocused(request.viewId);
            } else if (PlatformViewsController.this.textInputPlugin != null) {
                PlatformViewsController.this.textInputPlugin.clearPlatformViewClient(request.viewId);
            }
        }

        @Override // io.flutter.embedding.engine.systemchannels.PlatformViewsChannel.PlatformViewsHandler
        public void synchronizeToNativeViewHierarchy(boolean yes) {
            PlatformViewsController.this.synchronizeToNativeViewHierarchy = yes;
        }
    }

    public MotionEvent toMotionEvent(float density, PlatformViewsChannel.PlatformViewTouch touch, boolean usingVirtualDiplay) {
        MotionEventTracker.MotionEventId motionEventId = MotionEventTracker.MotionEventId.from(touch.motionEventId);
        MotionEvent trackedEvent = this.motionEventTracker.pop(motionEventId);
        MotionEvent.PointerProperties[] pointerProperties = (MotionEvent.PointerProperties[]) parsePointerPropertiesList(touch.rawPointerPropertiesList).toArray(new MotionEvent.PointerProperties[touch.pointerCount]);
        MotionEvent.PointerCoords[] pointerCoords = (MotionEvent.PointerCoords[]) parsePointerCoordsList(touch.rawPointerCoords, density).toArray(new MotionEvent.PointerCoords[touch.pointerCount]);
        if (!usingVirtualDiplay && trackedEvent != null) {
            return MotionEvent.obtain(trackedEvent.getDownTime(), trackedEvent.getEventTime(), touch.action, touch.pointerCount, pointerProperties, pointerCoords, trackedEvent.getMetaState(), trackedEvent.getButtonState(), trackedEvent.getXPrecision(), trackedEvent.getYPrecision(), trackedEvent.getDeviceId(), trackedEvent.getEdgeFlags(), trackedEvent.getSource(), trackedEvent.getFlags());
        }
        return MotionEvent.obtain(touch.downTime.longValue(), touch.eventTime.longValue(), touch.action, touch.pointerCount, pointerProperties, pointerCoords, touch.metaState, touch.buttonState, touch.xPrecision, touch.yPrecision, touch.deviceId, touch.edgeFlags, touch.source, touch.flags);
    }

    public void attach(Context context, TextureRegistry textureRegistry, DartExecutor dartExecutor) {
        if (this.context != null) {
            throw new AssertionError("A PlatformViewsController can only be attached to a single output target.\nattach was called while the PlatformViewsController was already attached.");
        }
        this.context = context;
        this.textureRegistry = textureRegistry;
        PlatformViewsChannel platformViewsChannel = new PlatformViewsChannel(dartExecutor);
        this.platformViewsChannel = platformViewsChannel;
        platformViewsChannel.setPlatformViewsHandler(this.channelHandler);
    }

    public void setSoftwareRendering(boolean useSoftwareRendering) {
        this.usesSoftwareRendering = useSoftwareRendering;
    }

    public void detach() {
        PlatformViewsChannel platformViewsChannel = this.platformViewsChannel;
        if (platformViewsChannel != null) {
            platformViewsChannel.setPlatformViewsHandler(null);
        }
        destroyOverlaySurfaces();
        this.platformViewsChannel = null;
        this.context = null;
        this.textureRegistry = null;
    }

    public void attachToView(FlutterView newFlutterView) {
        this.flutterView = newFlutterView;
        for (int index = 0; index < this.viewWrappers.size(); index++) {
            PlatformViewWrapper view = this.viewWrappers.valueAt(index);
            this.flutterView.addView(view);
        }
        for (int index2 = 0; index2 < this.platformViewParent.size(); index2++) {
            FlutterMutatorView view2 = this.platformViewParent.valueAt(index2);
            this.flutterView.addView(view2);
        }
        for (int index3 = 0; index3 < this.platformViews.size(); index3++) {
            PlatformView view3 = this.platformViews.valueAt(index3);
            view3.onFlutterViewAttached(this.flutterView);
        }
    }

    public void detachFromView() {
        for (int index = 0; index < this.viewWrappers.size(); index++) {
            PlatformViewWrapper view = this.viewWrappers.valueAt(index);
            this.flutterView.removeView(view);
        }
        for (int index2 = 0; index2 < this.platformViewParent.size(); index2++) {
            FlutterMutatorView view2 = this.platformViewParent.valueAt(index2);
            this.flutterView.removeView(view2);
        }
        destroyOverlaySurfaces();
        removeOverlaySurfaces();
        this.flutterView = null;
        this.flutterViewConvertedToImageView = false;
        for (int index3 = 0; index3 < this.platformViews.size(); index3++) {
            PlatformView view3 = this.platformViews.valueAt(index3);
            view3.onFlutterViewDetached();
        }
    }

    @Override // io.flutter.plugin.platform.PlatformViewsAccessibilityDelegate
    public void attachAccessibilityBridge(AccessibilityBridge accessibilityBridge) {
        this.accessibilityEventsDelegate.setAccessibilityBridge(accessibilityBridge);
    }

    @Override // io.flutter.plugin.platform.PlatformViewsAccessibilityDelegate
    public void detachAccessibilityBridge() {
        this.accessibilityEventsDelegate.setAccessibilityBridge(null);
    }

    public void attachTextInputPlugin(TextInputPlugin textInputPlugin) {
        this.textInputPlugin = textInputPlugin;
    }

    public void detachTextInputPlugin() {
        this.textInputPlugin = null;
    }

    public boolean checkInputConnectionProxy(View view) {
        if (view == null || !this.contextToEmbeddedView.containsKey(view.getContext())) {
            return false;
        }
        View platformView = this.contextToEmbeddedView.get(view.getContext());
        if (platformView == view) {
            return true;
        }
        return platformView.checkInputConnectionProxy(view);
    }

    public PlatformViewRegistry getRegistry() {
        return this.registry;
    }

    public void onAttachedToJNI() {
    }

    public void onDetachedFromJNI() {
        diposeAllViews();
    }

    public void onPreEngineRestart() {
        diposeAllViews();
    }

    @Override // io.flutter.plugin.platform.PlatformViewsAccessibilityDelegate
    public View getPlatformViewById(int viewId) {
        if (usesVirtualDisplay(viewId)) {
            VirtualDisplayController controller = this.vdControllers.get(Integer.valueOf(viewId));
            return controller.getView();
        }
        PlatformView platformView = this.platformViews.get(viewId);
        if (platformView == null) {
            return null;
        }
        return platformView.getView();
    }

    @Override // io.flutter.plugin.platform.PlatformViewsAccessibilityDelegate
    public boolean usesVirtualDisplay(int id) {
        return this.vdControllers.containsKey(Integer.valueOf(id));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void lockInputConnection(VirtualDisplayController controller) {
        TextInputPlugin textInputPlugin = this.textInputPlugin;
        if (textInputPlugin == null) {
            return;
        }
        textInputPlugin.lockPlatformViewInputConnection();
        controller.onInputConnectionLocked();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void unlockInputConnection(VirtualDisplayController controller) {
        TextInputPlugin textInputPlugin = this.textInputPlugin;
        if (textInputPlugin == null) {
            return;
        }
        textInputPlugin.unlockPlatformViewInputConnection();
        controller.onInputConnectionUnlocked();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean validateDirection(int direction) {
        return direction == 0 || direction == 1;
    }

    private static List<MotionEvent.PointerProperties> parsePointerPropertiesList(Object rawPropertiesList) {
        List<Object> rawProperties = (List) rawPropertiesList;
        List<MotionEvent.PointerProperties> pointerProperties = new ArrayList<>();
        for (Object o : rawProperties) {
            pointerProperties.add(parsePointerProperties(o));
        }
        return pointerProperties;
    }

    private static MotionEvent.PointerProperties parsePointerProperties(Object rawProperties) {
        List<Object> propertiesList = (List) rawProperties;
        MotionEvent.PointerProperties properties = new MotionEvent.PointerProperties();
        properties.id = ((Integer) propertiesList.get(0)).intValue();
        properties.toolType = ((Integer) propertiesList.get(1)).intValue();
        return properties;
    }

    private static List<MotionEvent.PointerCoords> parsePointerCoordsList(Object rawCoordsList, float density) {
        List<Object> rawCoords = (List) rawCoordsList;
        List<MotionEvent.PointerCoords> pointerCoords = new ArrayList<>();
        for (Object o : rawCoords) {
            pointerCoords.add(parsePointerCoords(o, density));
        }
        return pointerCoords;
    }

    private static MotionEvent.PointerCoords parsePointerCoords(Object rawCoords, float density) {
        List<Object> coordsList = (List) rawCoords;
        MotionEvent.PointerCoords coords = new MotionEvent.PointerCoords();
        coords.orientation = (float) ((Double) coordsList.get(0)).doubleValue();
        coords.pressure = (float) ((Double) coordsList.get(1)).doubleValue();
        coords.size = (float) ((Double) coordsList.get(2)).doubleValue();
        coords.toolMajor = ((float) ((Double) coordsList.get(3)).doubleValue()) * density;
        coords.toolMinor = ((float) ((Double) coordsList.get(4)).doubleValue()) * density;
        coords.touchMajor = ((float) ((Double) coordsList.get(5)).doubleValue()) * density;
        coords.touchMinor = ((float) ((Double) coordsList.get(6)).doubleValue()) * density;
        coords.x = ((float) ((Double) coordsList.get(7)).doubleValue()) * density;
        coords.y = ((float) ((Double) coordsList.get(8)).doubleValue()) * density;
        return coords;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public float getDisplayDensity() {
        return this.context.getResources().getDisplayMetrics().density;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int toPhysicalPixels(double logicalPixels) {
        double displayDensity = getDisplayDensity();
        Double.isNaN(displayDensity);
        return (int) Math.round(displayDensity * logicalPixels);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int toLogicalPixels(double physicalPixels, float displayDensity) {
        double d = displayDensity;
        Double.isNaN(d);
        return (int) Math.round(physicalPixels / d);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int toLogicalPixels(double physicalPixels) {
        return toLogicalPixels(physicalPixels, getDisplayDensity());
    }

    private void diposeAllViews() {
        while (this.platformViews.size() > 0) {
            int viewId = this.platformViews.keyAt(0);
            this.channelHandler.dispose(viewId);
        }
    }

    private void initializeRootImageViewIfNeeded() {
        if (this.synchronizeToNativeViewHierarchy && !this.flutterViewConvertedToImageView) {
            this.flutterView.convertToImageView();
            this.flutterViewConvertedToImageView = true;
        }
    }

    void initializePlatformViewIfNeeded(final int viewId) {
        PlatformView platformView = this.platformViews.get(viewId);
        if (platformView == null) {
            throw new IllegalStateException("Platform view hasn't been initialized from the platform view channel.");
        }
        if (this.platformViewParent.get(viewId) != null) {
            return;
        }
        View embeddedView = platformView.getView();
        if (embeddedView == null) {
            throw new IllegalStateException("PlatformView#getView() returned null, but an Android view reference was expected.");
        }
        if (embeddedView.getParent() != null) {
            throw new IllegalStateException("The Android view returned from PlatformView#getView() was already added to a parent view.");
        }
        Context context = this.context;
        FlutterMutatorView parentView = new FlutterMutatorView(context, context.getResources().getDisplayMetrics().density, this.androidTouchProcessor);
        parentView.setOnDescendantFocusChangeListener(new View.OnFocusChangeListener() { // from class: io.flutter.plugin.platform.PlatformViewsController$$ExternalSyntheticLambda0
            @Override // android.view.View.OnFocusChangeListener
            public final void onFocusChange(View view, boolean z) {
                PlatformViewsController.this.m29x825f680(viewId, view, z);
            }
        });
        this.platformViewParent.put(viewId, parentView);
        embeddedView.setImportantForAccessibility(4);
        parentView.addView(embeddedView);
        this.flutterView.addView(parentView);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$initializePlatformViewIfNeeded$0$io-flutter-plugin-platform-PlatformViewsController  reason: not valid java name */
    public /* synthetic */ void m29x825f680(int viewId, View view, boolean hasFocus) {
        if (hasFocus) {
            this.platformViewsChannel.invokeViewFocused(viewId);
            return;
        }
        TextInputPlugin textInputPlugin = this.textInputPlugin;
        if (textInputPlugin != null) {
            textInputPlugin.clearPlatformViewClient(viewId);
        }
    }

    public void attachToFlutterRenderer(FlutterRenderer flutterRenderer) {
        this.androidTouchProcessor = new AndroidTouchProcessor(flutterRenderer, true);
    }

    public void onDisplayPlatformView(int viewId, int x, int y, int width, int height, int viewWidth, int viewHeight, FlutterMutatorsStack mutatorsStack) {
        initializeRootImageViewIfNeeded();
        initializePlatformViewIfNeeded(viewId);
        FlutterMutatorView parentView = this.platformViewParent.get(viewId);
        parentView.readyToDisplay(mutatorsStack, x, y, width, height);
        parentView.setVisibility(0);
        parentView.bringToFront();
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(viewWidth, viewHeight);
        View view = this.platformViews.get(viewId).getView();
        if (view != null) {
            view.setLayoutParams(layoutParams);
            view.bringToFront();
        }
        this.currentFrameUsedPlatformViewIds.add(Integer.valueOf(viewId));
    }

    public void onDisplayOverlaySurface(int id, int x, int y, int width, int height) {
        if (this.overlayLayerViews.get(id) == null) {
            throw new IllegalStateException("The overlay surface (id:" + id + ") doesn't exist");
        }
        initializeRootImageViewIfNeeded();
        PlatformOverlayView overlayView = this.overlayLayerViews.get(id);
        if (overlayView.getParent() == null) {
            this.flutterView.addView(overlayView);
        }
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(width, height);
        layoutParams.leftMargin = x;
        layoutParams.topMargin = y;
        overlayView.setLayoutParams(layoutParams);
        overlayView.setVisibility(0);
        overlayView.bringToFront();
        this.currentFrameUsedOverlayLayerIds.add(Integer.valueOf(id));
    }

    public void onBeginFrame() {
        this.currentFrameUsedOverlayLayerIds.clear();
        this.currentFrameUsedPlatformViewIds.clear();
    }

    public void onEndFrame() {
        boolean z = false;
        if (this.flutterViewConvertedToImageView && this.currentFrameUsedPlatformViewIds.isEmpty()) {
            this.flutterViewConvertedToImageView = false;
            this.flutterView.revertImageView(new Runnable() { // from class: io.flutter.plugin.platform.PlatformViewsController$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    PlatformViewsController.this.m30xe1328d28();
                }
            });
            return;
        }
        if (this.flutterViewConvertedToImageView && this.flutterView.acquireLatestImageViewFrame()) {
            z = true;
        }
        boolean isFrameRenderedUsingImageReaders = z;
        finishFrame(isFrameRenderedUsingImageReaders);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$onEndFrame$1$io-flutter-plugin-platform-PlatformViewsController  reason: not valid java name */
    public /* synthetic */ void m30xe1328d28() {
        finishFrame(false);
    }

    private void finishFrame(boolean isFrameRenderedUsingImageReaders) {
        for (int i = 0; i < this.overlayLayerViews.size(); i++) {
            int overlayId = this.overlayLayerViews.keyAt(i);
            PlatformOverlayView overlayView = this.overlayLayerViews.valueAt(i);
            if (this.currentFrameUsedOverlayLayerIds.contains(Integer.valueOf(overlayId))) {
                this.flutterView.attachOverlaySurfaceToRender(overlayView);
                boolean didAcquireOverlaySurfaceImage = overlayView.acquireLatestImage();
                isFrameRenderedUsingImageReaders &= didAcquireOverlaySurfaceImage;
            } else {
                if (!this.flutterViewConvertedToImageView) {
                    overlayView.detachFromRenderer();
                }
                overlayView.setVisibility(8);
                this.flutterView.removeView(overlayView);
            }
        }
        for (int i2 = 0; i2 < this.platformViewParent.size(); i2++) {
            int viewId = this.platformViewParent.keyAt(i2);
            View parentView = this.platformViewParent.get(viewId);
            if (this.currentFrameUsedPlatformViewIds.contains(Integer.valueOf(viewId)) && (isFrameRenderedUsingImageReaders || !this.synchronizeToNativeViewHierarchy)) {
                parentView.setVisibility(0);
            } else {
                parentView.setVisibility(8);
            }
        }
    }

    public FlutterOverlaySurface createOverlaySurface(PlatformOverlayView imageView) {
        int id = this.nextOverlayLayerId;
        this.nextOverlayLayerId = id + 1;
        this.overlayLayerViews.put(id, imageView);
        return new FlutterOverlaySurface(id, imageView.getSurface());
    }

    public FlutterOverlaySurface createOverlaySurface() {
        return createOverlaySurface(new PlatformOverlayView(this.flutterView.getContext(), this.flutterView.getWidth(), this.flutterView.getHeight(), this.accessibilityEventsDelegate));
    }

    public void destroyOverlaySurfaces() {
        for (int viewId = 0; viewId < this.overlayLayerViews.size(); viewId++) {
            PlatformOverlayView overlayView = this.overlayLayerViews.valueAt(viewId);
            overlayView.detachFromRenderer();
            overlayView.closeImageReader();
        }
    }

    private void removeOverlaySurfaces() {
        if (this.flutterView == null) {
            Log.e(TAG, "removeOverlaySurfaces called while flutter view is null");
            return;
        }
        for (int viewId = 0; viewId < this.overlayLayerViews.size(); viewId++) {
            this.flutterView.removeView(this.overlayLayerViews.valueAt(viewId));
        }
        this.overlayLayerViews.clear();
    }

    public SparseArray<PlatformOverlayView> getOverlayLayerViews() {
        return this.overlayLayerViews;
    }
}
