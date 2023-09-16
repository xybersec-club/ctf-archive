package io.flutter.view;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Insets;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.os.Build;
import android.os.Handler;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.DisplayCutout;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.PointerIcon;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewStructure;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeProvider;
import android.view.autofill.AutofillValue;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import io.flutter.Log;
import io.flutter.app.FlutterPluginRegistry;
import io.flutter.embedding.android.AndroidTouchProcessor;
import io.flutter.embedding.android.KeyboardManager;
import io.flutter.embedding.engine.dart.DartExecutor;
import io.flutter.embedding.engine.renderer.FlutterRenderer;
import io.flutter.embedding.engine.renderer.SurfaceTextureWrapper;
import io.flutter.embedding.engine.systemchannels.AccessibilityChannel;
import io.flutter.embedding.engine.systemchannels.LifecycleChannel;
import io.flutter.embedding.engine.systemchannels.LocalizationChannel;
import io.flutter.embedding.engine.systemchannels.MouseCursorChannel;
import io.flutter.embedding.engine.systemchannels.NavigationChannel;
import io.flutter.embedding.engine.systemchannels.PlatformChannel;
import io.flutter.embedding.engine.systemchannels.SettingsChannel;
import io.flutter.embedding.engine.systemchannels.SystemChannel;
import io.flutter.embedding.engine.systemchannels.TextInputChannel;
import io.flutter.plugin.common.ActivityLifecycleListener;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.editing.TextInputPlugin;
import io.flutter.plugin.localization.LocalizationPlugin;
import io.flutter.plugin.mouse.MouseCursorPlugin;
import io.flutter.plugin.platform.PlatformPlugin;
import io.flutter.plugin.platform.PlatformViewsController;
import io.flutter.util.ViewUtils;
import io.flutter.view.AccessibilityBridge;
import io.flutter.view.TextureRegistry;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
@Deprecated
/* loaded from: classes.dex */
public class FlutterView extends SurfaceView implements BinaryMessenger, TextureRegistry, MouseCursorPlugin.MouseCursorViewDelegate, KeyboardManager.ViewDelegate {
    private static final String TAG = "FlutterView";
    private final AndroidTouchProcessor androidTouchProcessor;
    private final DartExecutor dartExecutor;
    private boolean didRenderFirstFrame;
    private final FlutterRenderer flutterRenderer;
    private final LifecycleChannel lifecycleChannel;
    private final LocalizationChannel localizationChannel;
    private AccessibilityBridge mAccessibilityNodeProvider;
    private final List<ActivityLifecycleListener> mActivityLifecycleListeners;
    private final List<FirstFrameListener> mFirstFrameListeners;
    private final InputMethodManager mImm;
    private boolean mIsSoftwareRenderingEnabled;
    private final KeyboardManager mKeyboardManager;
    private final LocalizationPlugin mLocalizationPlugin;
    private final ViewportMetrics mMetrics;
    private final MouseCursorPlugin mMouseCursorPlugin;
    private FlutterNativeView mNativeView;
    private final SurfaceHolder.Callback mSurfaceCallback;
    private final TextInputPlugin mTextInputPlugin;
    private final NavigationChannel navigationChannel;
    private final AtomicLong nextTextureId;
    private final AccessibilityBridge.OnAccessibilityChangeListener onAccessibilityChangeListener;
    private final PlatformChannel platformChannel;
    private final SettingsChannel settingsChannel;
    private final SystemChannel systemChannel;

    /* loaded from: classes.dex */
    public interface FirstFrameListener {
        void onFirstFrame();
    }

    /* loaded from: classes.dex */
    public interface Provider {
        FlutterView getFlutterView();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public enum ZeroSides {
        NONE,
        LEFT,
        RIGHT,
        BOTH
    }

    @Override // io.flutter.plugin.common.BinaryMessenger
    public /* synthetic */ BinaryMessenger.TaskQueue makeBackgroundTaskQueue() {
        BinaryMessenger.TaskQueue makeBackgroundTaskQueue;
        makeBackgroundTaskQueue = makeBackgroundTaskQueue(new BinaryMessenger.TaskQueueOptions());
        return makeBackgroundTaskQueue;
    }

    @Override // io.flutter.view.TextureRegistry
    public /* synthetic */ void onTrimMemory(int i) {
        TextureRegistry.CC.$default$onTrimMemory(this, i);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class ViewportMetrics {
        float devicePixelRatio = 1.0f;
        int physicalWidth = 0;
        int physicalHeight = 0;
        int physicalViewPaddingTop = 0;
        int physicalViewPaddingRight = 0;
        int physicalViewPaddingBottom = 0;
        int physicalViewPaddingLeft = 0;
        int physicalViewInsetTop = 0;
        int physicalViewInsetRight = 0;
        int physicalViewInsetBottom = 0;
        int physicalViewInsetLeft = 0;
        int systemGestureInsetTop = 0;
        int systemGestureInsetRight = 0;
        int systemGestureInsetBottom = 0;
        int systemGestureInsetLeft = 0;
        int physicalTouchSlop = -1;

        ViewportMetrics() {
        }
    }

    public FlutterView(Context context) {
        this(context, null);
    }

    public FlutterView(Context context, AttributeSet attrs) {
        this(context, attrs, null);
    }

    public FlutterView(Context context, AttributeSet attrs, FlutterNativeView nativeView) {
        super(context, attrs);
        this.nextTextureId = new AtomicLong(0L);
        this.mIsSoftwareRenderingEnabled = false;
        this.didRenderFirstFrame = false;
        this.onAccessibilityChangeListener = new AccessibilityBridge.OnAccessibilityChangeListener() { // from class: io.flutter.view.FlutterView.1
            @Override // io.flutter.view.AccessibilityBridge.OnAccessibilityChangeListener
            public void onAccessibilityChanged(boolean isAccessibilityEnabled, boolean isTouchExplorationEnabled) {
                FlutterView.this.resetWillNotDraw(isAccessibilityEnabled, isTouchExplorationEnabled);
            }
        };
        Activity activity = ViewUtils.getActivity(getContext());
        if (activity == null) {
            throw new IllegalArgumentException("Bad context");
        }
        if (nativeView == null) {
            this.mNativeView = new FlutterNativeView(activity.getApplicationContext());
        } else {
            this.mNativeView = nativeView;
        }
        DartExecutor dartExecutor = this.mNativeView.getDartExecutor();
        this.dartExecutor = dartExecutor;
        FlutterRenderer flutterRenderer = new FlutterRenderer(this.mNativeView.getFlutterJNI());
        this.flutterRenderer = flutterRenderer;
        this.mIsSoftwareRenderingEnabled = this.mNativeView.getFlutterJNI().getIsSoftwareRenderingEnabled();
        ViewportMetrics viewportMetrics = new ViewportMetrics();
        this.mMetrics = viewportMetrics;
        viewportMetrics.devicePixelRatio = context.getResources().getDisplayMetrics().density;
        viewportMetrics.physicalTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        setFocusable(true);
        setFocusableInTouchMode(true);
        this.mNativeView.attachViewAndActivity(this, activity);
        SurfaceHolder.Callback callback = new SurfaceHolder.Callback() { // from class: io.flutter.view.FlutterView.2
            @Override // android.view.SurfaceHolder.Callback
            public void surfaceCreated(SurfaceHolder holder) {
                FlutterView.this.assertAttached();
                FlutterView.this.mNativeView.getFlutterJNI().onSurfaceCreated(holder.getSurface());
            }

            @Override // android.view.SurfaceHolder.Callback
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                FlutterView.this.assertAttached();
                FlutterView.this.mNativeView.getFlutterJNI().onSurfaceChanged(width, height);
            }

            @Override // android.view.SurfaceHolder.Callback
            public void surfaceDestroyed(SurfaceHolder holder) {
                FlutterView.this.assertAttached();
                FlutterView.this.mNativeView.getFlutterJNI().onSurfaceDestroyed();
            }
        };
        this.mSurfaceCallback = callback;
        getHolder().addCallback(callback);
        this.mActivityLifecycleListeners = new ArrayList();
        this.mFirstFrameListeners = new ArrayList();
        this.navigationChannel = new NavigationChannel(dartExecutor);
        this.lifecycleChannel = new LifecycleChannel(dartExecutor);
        LocalizationChannel localizationChannel = new LocalizationChannel(dartExecutor);
        this.localizationChannel = localizationChannel;
        PlatformChannel platformChannel = new PlatformChannel(dartExecutor);
        this.platformChannel = platformChannel;
        this.systemChannel = new SystemChannel(dartExecutor);
        this.settingsChannel = new SettingsChannel(dartExecutor);
        final PlatformPlugin platformPlugin = new PlatformPlugin(activity, platformChannel);
        addActivityLifecycleListener(new ActivityLifecycleListener() { // from class: io.flutter.view.FlutterView.3
            @Override // io.flutter.plugin.common.ActivityLifecycleListener
            public void onPostResume() {
                platformPlugin.updateSystemUiOverlays();
            }
        });
        this.mImm = (InputMethodManager) getContext().getSystemService("input_method");
        PlatformViewsController platformViewsController = this.mNativeView.getPluginRegistry().getPlatformViewsController();
        TextInputPlugin textInputPlugin = new TextInputPlugin(this, new TextInputChannel(dartExecutor), platformViewsController);
        this.mTextInputPlugin = textInputPlugin;
        this.mKeyboardManager = new KeyboardManager(this);
        if (Build.VERSION.SDK_INT >= 24) {
            this.mMouseCursorPlugin = new MouseCursorPlugin(this, new MouseCursorChannel(dartExecutor));
        } else {
            this.mMouseCursorPlugin = null;
        }
        LocalizationPlugin localizationPlugin = new LocalizationPlugin(context, localizationChannel);
        this.mLocalizationPlugin = localizationPlugin;
        this.androidTouchProcessor = new AndroidTouchProcessor(flutterRenderer, false);
        platformViewsController.attachToFlutterRenderer(flutterRenderer);
        this.mNativeView.getPluginRegistry().getPlatformViewsController().attachTextInputPlugin(textInputPlugin);
        this.mNativeView.getFlutterJNI().setLocalizationPlugin(localizationPlugin);
        localizationPlugin.sendLocalesToFlutter(getResources().getConfiguration());
        sendUserPlatformSettingsToDart();
    }

    public DartExecutor getDartExecutor() {
        return this.dartExecutor;
    }

    @Override // android.view.View
    public boolean dispatchKeyEvent(KeyEvent event) {
        Log.e(TAG, "dispatchKeyEvent: " + event.toString());
        if (event.getAction() == 0 && event.getRepeatCount() == 0) {
            getKeyDispatcherState().startTracking(event, this);
        } else if (event.getAction() == 1) {
            getKeyDispatcherState().handleUpEvent(event);
        }
        return (isAttached() && this.mKeyboardManager.handleEvent(event)) || super.dispatchKeyEvent(event);
    }

    public FlutterNativeView getFlutterNativeView() {
        return this.mNativeView;
    }

    public FlutterPluginRegistry getPluginRegistry() {
        return this.mNativeView.getPluginRegistry();
    }

    public String getLookupKeyForAsset(String asset) {
        return FlutterMain.getLookupKeyForAsset(asset);
    }

    public String getLookupKeyForAsset(String asset, String packageName) {
        return FlutterMain.getLookupKeyForAsset(asset, packageName);
    }

    public void addActivityLifecycleListener(ActivityLifecycleListener listener) {
        this.mActivityLifecycleListeners.add(listener);
    }

    public void onStart() {
        this.lifecycleChannel.appIsInactive();
    }

    public void onPause() {
        this.lifecycleChannel.appIsInactive();
    }

    public void onPostResume() {
        for (ActivityLifecycleListener listener : this.mActivityLifecycleListeners) {
            listener.onPostResume();
        }
        this.lifecycleChannel.appIsResumed();
    }

    public void onStop() {
        this.lifecycleChannel.appIsPaused();
    }

    public void onMemoryPressure() {
        this.mNativeView.getFlutterJNI().notifyLowMemoryWarning();
        this.systemChannel.sendMemoryPressureWarning();
    }

    public boolean hasRenderedFirstFrame() {
        return this.didRenderFirstFrame;
    }

    public void addFirstFrameListener(FirstFrameListener listener) {
        this.mFirstFrameListeners.add(listener);
    }

    public void removeFirstFrameListener(FirstFrameListener listener) {
        this.mFirstFrameListeners.remove(listener);
    }

    @Override // io.flutter.plugin.common.BinaryMessenger
    public void enableBufferingIncomingMessages() {
    }

    @Override // io.flutter.plugin.common.BinaryMessenger
    public void disableBufferingIncomingMessages() {
    }

    public void disableTransparentBackground() {
        setZOrderOnTop(false);
        getHolder().setFormat(-1);
    }

    public void setInitialRoute(String route) {
        this.navigationChannel.setInitialRoute(route);
    }

    public void pushRoute(String route) {
        this.navigationChannel.pushRoute(route);
    }

    public void popRoute() {
        this.navigationChannel.popRoute();
    }

    private void sendUserPlatformSettingsToDart() {
        SettingsChannel.PlatformBrightness brightness;
        boolean isNightModeOn = (getResources().getConfiguration().uiMode & 48) == 32;
        if (isNightModeOn) {
            brightness = SettingsChannel.PlatformBrightness.dark;
        } else {
            brightness = SettingsChannel.PlatformBrightness.light;
        }
        this.settingsChannel.startMessage().setTextScaleFactor(getResources().getConfiguration().fontScale).setUse24HourFormat(DateFormat.is24HourFormat(getContext())).setPlatformBrightness(brightness).send();
    }

    @Override // android.view.View
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        this.mLocalizationPlugin.sendLocalesToFlutter(newConfig);
        sendUserPlatformSettingsToDart();
    }

    float getDevicePixelRatio() {
        return this.mMetrics.devicePixelRatio;
    }

    public FlutterNativeView detach() {
        if (isAttached()) {
            getHolder().removeCallback(this.mSurfaceCallback);
            this.mNativeView.detachFromFlutterView();
            FlutterNativeView view = this.mNativeView;
            this.mNativeView = null;
            return view;
        }
        return null;
    }

    public void destroy() {
        if (isAttached()) {
            getHolder().removeCallback(this.mSurfaceCallback);
            releaseAccessibilityNodeProvider();
            this.mNativeView.destroy();
            this.mNativeView = null;
        }
    }

    @Override // android.view.View
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        return this.mTextInputPlugin.createInputConnection(this, this.mKeyboardManager, outAttrs);
    }

    @Override // android.view.View
    public boolean checkInputConnectionProxy(View view) {
        return this.mNativeView.getPluginRegistry().getPlatformViewsController().checkInputConnectionProxy(view);
    }

    @Override // android.view.View
    public void onProvideAutofillVirtualStructure(ViewStructure structure, int flags) {
        super.onProvideAutofillVirtualStructure(structure, flags);
        this.mTextInputPlugin.onProvideAutofillVirtualStructure(structure, flags);
    }

    @Override // android.view.View
    public void autofill(SparseArray<AutofillValue> values) {
        this.mTextInputPlugin.autofill(values);
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        if (!isAttached()) {
            return super.onTouchEvent(event);
        }
        if (Build.VERSION.SDK_INT >= 21) {
            requestUnbufferedDispatch(event);
        }
        return this.androidTouchProcessor.onTouchEvent(event);
    }

    @Override // android.view.View
    public boolean onHoverEvent(MotionEvent event) {
        if (!isAttached()) {
            return super.onHoverEvent(event);
        }
        boolean handled = this.mAccessibilityNodeProvider.onAccessibilityHoverEvent(event);
        return handled;
    }

    @Override // android.view.View
    public boolean onGenericMotionEvent(MotionEvent event) {
        boolean handled = isAttached() && this.androidTouchProcessor.onGenericMotionEvent(event);
        if (handled) {
            return true;
        }
        return super.onGenericMotionEvent(event);
    }

    @Override // android.view.View
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        this.mMetrics.physicalWidth = width;
        this.mMetrics.physicalHeight = height;
        updateViewportMetrics();
        super.onSizeChanged(width, height, oldWidth, oldHeight);
    }

    private ZeroSides calculateShouldZeroSides() {
        Context context = getContext();
        int orientation = context.getResources().getConfiguration().orientation;
        int rotation = ((WindowManager) context.getSystemService("window")).getDefaultDisplay().getRotation();
        if (orientation == 2) {
            if (rotation == 1) {
                return ZeroSides.RIGHT;
            }
            if (rotation == 3) {
                return Build.VERSION.SDK_INT >= 23 ? ZeroSides.LEFT : ZeroSides.RIGHT;
            } else if (rotation == 0 || rotation == 2) {
                return ZeroSides.BOTH;
            }
        }
        return ZeroSides.NONE;
    }

    private int guessBottomKeyboardInset(WindowInsets insets) {
        int screenHeight = getRootView().getHeight();
        double d = screenHeight;
        Double.isNaN(d);
        if (insets.getSystemWindowInsetBottom() < d * 0.18d) {
            return 0;
        }
        return insets.getSystemWindowInsetBottom();
    }

    @Override // android.view.View
    public final WindowInsets onApplyWindowInsets(WindowInsets insets) {
        int i;
        int i2;
        int i3;
        if (Build.VERSION.SDK_INT == 29) {
            Insets systemGestureInsets = insets.getSystemGestureInsets();
            this.mMetrics.systemGestureInsetTop = systemGestureInsets.top;
            this.mMetrics.systemGestureInsetRight = systemGestureInsets.right;
            this.mMetrics.systemGestureInsetBottom = systemGestureInsets.bottom;
            this.mMetrics.systemGestureInsetLeft = systemGestureInsets.left;
        }
        boolean statusBarVisible = (getWindowSystemUiVisibility() & 4) == 0;
        boolean navigationBarVisible = (getWindowSystemUiVisibility() & 2) == 0;
        if (Build.VERSION.SDK_INT >= 30) {
            int mask = navigationBarVisible ? 0 | WindowInsets.Type.navigationBars() : 0;
            if (statusBarVisible) {
                mask |= WindowInsets.Type.statusBars();
            }
            Insets uiInsets = insets.getInsets(mask);
            this.mMetrics.physicalViewPaddingTop = uiInsets.top;
            this.mMetrics.physicalViewPaddingRight = uiInsets.right;
            this.mMetrics.physicalViewPaddingBottom = uiInsets.bottom;
            this.mMetrics.physicalViewPaddingLeft = uiInsets.left;
            Insets imeInsets = insets.getInsets(WindowInsets.Type.ime());
            this.mMetrics.physicalViewInsetTop = imeInsets.top;
            this.mMetrics.physicalViewInsetRight = imeInsets.right;
            this.mMetrics.physicalViewInsetBottom = imeInsets.bottom;
            this.mMetrics.physicalViewInsetLeft = imeInsets.left;
            Insets systemGestureInsets2 = insets.getInsets(WindowInsets.Type.systemGestures());
            this.mMetrics.systemGestureInsetTop = systemGestureInsets2.top;
            this.mMetrics.systemGestureInsetRight = systemGestureInsets2.right;
            this.mMetrics.systemGestureInsetBottom = systemGestureInsets2.bottom;
            this.mMetrics.systemGestureInsetLeft = systemGestureInsets2.left;
            DisplayCutout cutout = insets.getDisplayCutout();
            if (cutout != null) {
                Insets waterfallInsets = cutout.getWaterfallInsets();
                ViewportMetrics viewportMetrics = this.mMetrics;
                viewportMetrics.physicalViewPaddingTop = Math.max(Math.max(viewportMetrics.physicalViewPaddingTop, waterfallInsets.top), cutout.getSafeInsetTop());
                ViewportMetrics viewportMetrics2 = this.mMetrics;
                viewportMetrics2.physicalViewPaddingRight = Math.max(Math.max(viewportMetrics2.physicalViewPaddingRight, waterfallInsets.right), cutout.getSafeInsetRight());
                ViewportMetrics viewportMetrics3 = this.mMetrics;
                viewportMetrics3.physicalViewPaddingBottom = Math.max(Math.max(viewportMetrics3.physicalViewPaddingBottom, waterfallInsets.bottom), cutout.getSafeInsetBottom());
                ViewportMetrics viewportMetrics4 = this.mMetrics;
                viewportMetrics4.physicalViewPaddingLeft = Math.max(Math.max(viewportMetrics4.physicalViewPaddingLeft, waterfallInsets.left), cutout.getSafeInsetLeft());
            }
        } else {
            ZeroSides zeroSides = ZeroSides.NONE;
            if (!navigationBarVisible) {
                zeroSides = calculateShouldZeroSides();
            }
            this.mMetrics.physicalViewPaddingTop = statusBarVisible ? insets.getSystemWindowInsetTop() : 0;
            ViewportMetrics viewportMetrics5 = this.mMetrics;
            if (zeroSides == ZeroSides.RIGHT || zeroSides == ZeroSides.BOTH) {
                i = 0;
            } else {
                i = insets.getSystemWindowInsetRight();
            }
            viewportMetrics5.physicalViewPaddingRight = i;
            ViewportMetrics viewportMetrics6 = this.mMetrics;
            if (navigationBarVisible && guessBottomKeyboardInset(insets) == 0) {
                i2 = insets.getSystemWindowInsetBottom();
            } else {
                i2 = 0;
            }
            viewportMetrics6.physicalViewPaddingBottom = i2;
            ViewportMetrics viewportMetrics7 = this.mMetrics;
            if (zeroSides == ZeroSides.LEFT || zeroSides == ZeroSides.BOTH) {
                i3 = 0;
            } else {
                i3 = insets.getSystemWindowInsetLeft();
            }
            viewportMetrics7.physicalViewPaddingLeft = i3;
            this.mMetrics.physicalViewInsetTop = 0;
            this.mMetrics.physicalViewInsetRight = 0;
            this.mMetrics.physicalViewInsetBottom = guessBottomKeyboardInset(insets);
            this.mMetrics.physicalViewInsetLeft = 0;
        }
        updateViewportMetrics();
        return super.onApplyWindowInsets(insets);
    }

    @Override // android.view.View
    protected boolean fitSystemWindows(Rect insets) {
        if (Build.VERSION.SDK_INT <= 19) {
            this.mMetrics.physicalViewPaddingTop = insets.top;
            this.mMetrics.physicalViewPaddingRight = insets.right;
            this.mMetrics.physicalViewPaddingBottom = 0;
            this.mMetrics.physicalViewPaddingLeft = insets.left;
            this.mMetrics.physicalViewInsetTop = 0;
            this.mMetrics.physicalViewInsetRight = 0;
            this.mMetrics.physicalViewInsetBottom = insets.bottom;
            this.mMetrics.physicalViewInsetLeft = 0;
            updateViewportMetrics();
            return true;
        }
        return super.fitSystemWindows(insets);
    }

    private boolean isAttached() {
        FlutterNativeView flutterNativeView = this.mNativeView;
        return flutterNativeView != null && flutterNativeView.isAttached();
    }

    void assertAttached() {
        if (!isAttached()) {
            throw new AssertionError("Platform view is not attached");
        }
    }

    private void preRun() {
        resetAccessibilityTree();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void resetAccessibilityTree() {
        AccessibilityBridge accessibilityBridge = this.mAccessibilityNodeProvider;
        if (accessibilityBridge != null) {
            accessibilityBridge.reset();
        }
    }

    private void postRun() {
    }

    public void runFromBundle(FlutterRunArguments args) {
        assertAttached();
        preRun();
        this.mNativeView.runFromBundle(args);
        postRun();
    }

    public Bitmap getBitmap() {
        assertAttached();
        return this.mNativeView.getFlutterJNI().getBitmap();
    }

    private void updateViewportMetrics() {
        if (isAttached()) {
            this.mNativeView.getFlutterJNI().setViewportMetrics(this.mMetrics.devicePixelRatio, this.mMetrics.physicalWidth, this.mMetrics.physicalHeight, this.mMetrics.physicalViewPaddingTop, this.mMetrics.physicalViewPaddingRight, this.mMetrics.physicalViewPaddingBottom, this.mMetrics.physicalViewPaddingLeft, this.mMetrics.physicalViewInsetTop, this.mMetrics.physicalViewInsetRight, this.mMetrics.physicalViewInsetBottom, this.mMetrics.physicalViewInsetLeft, this.mMetrics.systemGestureInsetTop, this.mMetrics.systemGestureInsetRight, this.mMetrics.systemGestureInsetBottom, this.mMetrics.systemGestureInsetLeft, this.mMetrics.physicalTouchSlop, new int[0], new int[0], new int[0]);
        }
    }

    public void onFirstFrame() {
        this.didRenderFirstFrame = true;
        List<FirstFrameListener> listeners = new ArrayList<>(this.mFirstFrameListeners);
        for (FirstFrameListener listener : listeners) {
            listener.onFirstFrame();
        }
    }

    @Override // android.view.SurfaceView, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        PlatformViewsController platformViewsController = getPluginRegistry().getPlatformViewsController();
        AccessibilityBridge accessibilityBridge = new AccessibilityBridge(this, new AccessibilityChannel(this.dartExecutor, getFlutterNativeView().getFlutterJNI()), (AccessibilityManager) getContext().getSystemService("accessibility"), getContext().getContentResolver(), platformViewsController);
        this.mAccessibilityNodeProvider = accessibilityBridge;
        accessibilityBridge.setOnAccessibilityChangeListener(this.onAccessibilityChangeListener);
        resetWillNotDraw(this.mAccessibilityNodeProvider.isAccessibilityEnabled(), this.mAccessibilityNodeProvider.isTouchExplorationEnabled());
    }

    @Override // android.view.SurfaceView, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        releaseAccessibilityNodeProvider();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void resetWillNotDraw(boolean isAccessibilityEnabled, boolean isTouchExplorationEnabled) {
        boolean z = false;
        if (!this.mIsSoftwareRenderingEnabled) {
            if (!isAccessibilityEnabled && !isTouchExplorationEnabled) {
                z = true;
            }
            setWillNotDraw(z);
            return;
        }
        setWillNotDraw(false);
    }

    @Override // android.view.View
    public AccessibilityNodeProvider getAccessibilityNodeProvider() {
        AccessibilityBridge accessibilityBridge = this.mAccessibilityNodeProvider;
        if (accessibilityBridge != null && accessibilityBridge.isAccessibilityEnabled()) {
            return this.mAccessibilityNodeProvider;
        }
        return null;
    }

    private void releaseAccessibilityNodeProvider() {
        AccessibilityBridge accessibilityBridge = this.mAccessibilityNodeProvider;
        if (accessibilityBridge != null) {
            accessibilityBridge.release();
            this.mAccessibilityNodeProvider = null;
        }
    }

    @Override // io.flutter.plugin.mouse.MouseCursorPlugin.MouseCursorViewDelegate
    public PointerIcon getSystemPointerIcon(int type) {
        return PointerIcon.getSystemIcon(getContext(), type);
    }

    @Override // io.flutter.embedding.android.KeyboardManager.ViewDelegate
    public BinaryMessenger getBinaryMessenger() {
        return this;
    }

    @Override // io.flutter.embedding.android.KeyboardManager.ViewDelegate
    public boolean onTextInputKeyEvent(KeyEvent keyEvent) {
        return this.mTextInputPlugin.handleKeyEvent(keyEvent);
    }

    @Override // io.flutter.embedding.android.KeyboardManager.ViewDelegate
    public void redispatch(KeyEvent keyEvent) {
        getRootView().dispatchKeyEvent(keyEvent);
    }

    @Override // io.flutter.plugin.common.BinaryMessenger
    public BinaryMessenger.TaskQueue makeBackgroundTaskQueue(BinaryMessenger.TaskQueueOptions options) {
        return null;
    }

    @Override // io.flutter.plugin.common.BinaryMessenger
    public void send(String channel, ByteBuffer message) {
        send(channel, message, null);
    }

    @Override // io.flutter.plugin.common.BinaryMessenger
    public void send(String channel, ByteBuffer message, BinaryMessenger.BinaryReply callback) {
        if (!isAttached()) {
            Log.d(TAG, "FlutterView.send called on a detached view, channel=" + channel);
        } else {
            this.mNativeView.send(channel, message, callback);
        }
    }

    @Override // io.flutter.plugin.common.BinaryMessenger
    public void setMessageHandler(String channel, BinaryMessenger.BinaryMessageHandler handler) {
        this.mNativeView.setMessageHandler(channel, handler);
    }

    @Override // io.flutter.plugin.common.BinaryMessenger
    public void setMessageHandler(String channel, BinaryMessenger.BinaryMessageHandler handler, BinaryMessenger.TaskQueue taskQueue) {
        this.mNativeView.setMessageHandler(channel, handler, taskQueue);
    }

    @Override // io.flutter.view.TextureRegistry
    public TextureRegistry.SurfaceTextureEntry createSurfaceTexture() {
        SurfaceTexture surfaceTexture = new SurfaceTexture(0);
        return registerSurfaceTexture(surfaceTexture);
    }

    @Override // io.flutter.view.TextureRegistry
    public TextureRegistry.SurfaceTextureEntry registerSurfaceTexture(SurfaceTexture surfaceTexture) {
        surfaceTexture.detachFromGLContext();
        SurfaceTextureRegistryEntry entry = new SurfaceTextureRegistryEntry(this.nextTextureId.getAndIncrement(), surfaceTexture);
        this.mNativeView.getFlutterJNI().registerTexture(entry.id(), entry.textureWrapper());
        return entry;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public final class SurfaceTextureRegistryEntry implements TextureRegistry.SurfaceTextureEntry {
        private final long id;
        private SurfaceTexture.OnFrameAvailableListener onFrameListener = new SurfaceTexture.OnFrameAvailableListener() { // from class: io.flutter.view.FlutterView.SurfaceTextureRegistryEntry.1
            @Override // android.graphics.SurfaceTexture.OnFrameAvailableListener
            public void onFrameAvailable(SurfaceTexture texture) {
                if (!SurfaceTextureRegistryEntry.this.released && FlutterView.this.mNativeView != null) {
                    FlutterView.this.mNativeView.getFlutterJNI().markTextureFrameAvailable(SurfaceTextureRegistryEntry.this.id);
                }
            }
        };
        private boolean released;
        private final SurfaceTextureWrapper textureWrapper;

        @Override // io.flutter.view.TextureRegistry.SurfaceTextureEntry
        public /* synthetic */ void setOnFrameConsumedListener(TextureRegistry.OnFrameConsumedListener onFrameConsumedListener) {
            TextureRegistry.SurfaceTextureEntry.CC.$default$setOnFrameConsumedListener(this, onFrameConsumedListener);
        }

        @Override // io.flutter.view.TextureRegistry.SurfaceTextureEntry
        public /* synthetic */ void setOnTrimMemoryListener(TextureRegistry.OnTrimMemoryListener onTrimMemoryListener) {
            TextureRegistry.SurfaceTextureEntry.CC.$default$setOnTrimMemoryListener(this, onTrimMemoryListener);
        }

        SurfaceTextureRegistryEntry(long id, SurfaceTexture surfaceTexture) {
            this.id = id;
            this.textureWrapper = new SurfaceTextureWrapper(surfaceTexture);
            if (Build.VERSION.SDK_INT >= 21) {
                surfaceTexture().setOnFrameAvailableListener(this.onFrameListener, new Handler());
            } else {
                surfaceTexture().setOnFrameAvailableListener(this.onFrameListener);
            }
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
            this.released = true;
            surfaceTexture().setOnFrameAvailableListener(null);
            this.textureWrapper.release();
            FlutterView.this.mNativeView.getFlutterJNI().unregisterTexture(this.id);
        }
    }
}
