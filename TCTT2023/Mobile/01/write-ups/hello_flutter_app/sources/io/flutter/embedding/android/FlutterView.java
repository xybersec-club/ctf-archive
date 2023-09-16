package io.flutter.embedding.android;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.database.ContentObserver;
import android.graphics.Insets;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.DisplayCutout;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.PointerIcon;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewStructure;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeProvider;
import android.view.autofill.AutofillValue;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.textservice.SpellCheckerInfo;
import android.view.textservice.TextServicesManager;
import android.widget.FrameLayout;
import androidx.core.content.ContextCompat;
import androidx.core.util.Consumer;
import androidx.window.java.layout.WindowInfoTrackerCallbackAdapter;
import androidx.window.layout.DisplayFeature;
import androidx.window.layout.FoldingFeature;
import androidx.window.layout.WindowInfoTracker;
import androidx.window.layout.WindowLayoutInfo;
import io.flutter.Log;
import io.flutter.embedding.android.FlutterImageView;
import io.flutter.embedding.android.KeyboardManager;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.embedding.engine.renderer.FlutterRenderer;
import io.flutter.embedding.engine.renderer.FlutterUiDisplayListener;
import io.flutter.embedding.engine.renderer.RenderSurface;
import io.flutter.embedding.engine.systemchannels.SettingsChannel;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.editing.SpellCheckPlugin;
import io.flutter.plugin.editing.TextInputPlugin;
import io.flutter.plugin.localization.LocalizationPlugin;
import io.flutter.plugin.mouse.MouseCursorPlugin;
import io.flutter.util.ViewUtils;
import io.flutter.view.AccessibilityBridge;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
/* loaded from: classes.dex */
public class FlutterView extends FrameLayout implements MouseCursorPlugin.MouseCursorViewDelegate, KeyboardManager.ViewDelegate {
    private static final String TAG = "FlutterView";
    private AccessibilityBridge accessibilityBridge;
    private AndroidTouchProcessor androidTouchProcessor;
    private FlutterEngine flutterEngine;
    private final Set<FlutterEngineAttachmentListener> flutterEngineAttachmentListeners;
    private FlutterImageView flutterImageView;
    private FlutterSurfaceView flutterSurfaceView;
    private FlutterTextureView flutterTextureView;
    private final FlutterUiDisplayListener flutterUiDisplayListener;
    private final Set<FlutterUiDisplayListener> flutterUiDisplayListeners;
    private boolean isFlutterUiDisplayed;
    private KeyboardManager keyboardManager;
    private LocalizationPlugin localizationPlugin;
    private MouseCursorPlugin mouseCursorPlugin;
    private final AccessibilityBridge.OnAccessibilityChangeListener onAccessibilityChangeListener;
    private RenderSurface previousRenderSurface;
    RenderSurface renderSurface;
    private SpellCheckPlugin spellCheckPlugin;
    private final ContentObserver systemSettingsObserver;
    private TextInputPlugin textInputPlugin;
    private TextServicesManager textServicesManager;
    private final FlutterRenderer.ViewportMetrics viewportMetrics;
    private final Consumer<WindowLayoutInfo> windowInfoListener;
    private WindowInfoRepositoryCallbackAdapterWrapper windowInfoRepo;

    /* loaded from: classes.dex */
    public interface FlutterEngineAttachmentListener {
        void onFlutterEngineAttachedToFlutterView(FlutterEngine flutterEngine);

        void onFlutterEngineDetachedFromFlutterView();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public enum ZeroSides {
        NONE,
        LEFT,
        RIGHT,
        BOTH
    }

    public FlutterView(Context context) {
        this(context, (AttributeSet) null, new FlutterSurfaceView(context));
    }

    @Deprecated
    public FlutterView(Context context, RenderMode renderMode) {
        super(context, null);
        this.flutterUiDisplayListeners = new HashSet();
        this.flutterEngineAttachmentListeners = new HashSet();
        this.viewportMetrics = new FlutterRenderer.ViewportMetrics();
        this.onAccessibilityChangeListener = new AccessibilityBridge.OnAccessibilityChangeListener() { // from class: io.flutter.embedding.android.FlutterView.1
            @Override // io.flutter.view.AccessibilityBridge.OnAccessibilityChangeListener
            public void onAccessibilityChanged(boolean isAccessibilityEnabled, boolean isTouchExplorationEnabled) {
                FlutterView.this.resetWillNotDraw(isAccessibilityEnabled, isTouchExplorationEnabled);
            }
        };
        this.systemSettingsObserver = new ContentObserver(new Handler(Looper.getMainLooper())) { // from class: io.flutter.embedding.android.FlutterView.2
            @Override // android.database.ContentObserver
            public void onChange(boolean selfChange) {
                super.onChange(selfChange);
                if (FlutterView.this.flutterEngine == null) {
                    return;
                }
                Log.v(FlutterView.TAG, "System settings changed. Sending user settings to Flutter.");
                FlutterView.this.sendUserSettingsToFlutter();
            }

            @Override // android.database.ContentObserver
            public boolean deliverSelfNotifications() {
                return true;
            }
        };
        this.flutterUiDisplayListener = new FlutterUiDisplayListener() { // from class: io.flutter.embedding.android.FlutterView.3
            @Override // io.flutter.embedding.engine.renderer.FlutterUiDisplayListener
            public void onFlutterUiDisplayed() {
                FlutterView.this.isFlutterUiDisplayed = true;
                for (FlutterUiDisplayListener listener : FlutterView.this.flutterUiDisplayListeners) {
                    listener.onFlutterUiDisplayed();
                }
            }

            @Override // io.flutter.embedding.engine.renderer.FlutterUiDisplayListener
            public void onFlutterUiNoLongerDisplayed() {
                FlutterView.this.isFlutterUiDisplayed = false;
                for (FlutterUiDisplayListener listener : FlutterView.this.flutterUiDisplayListeners) {
                    listener.onFlutterUiNoLongerDisplayed();
                }
            }
        };
        this.windowInfoListener = new Consumer<WindowLayoutInfo>() { // from class: io.flutter.embedding.android.FlutterView.4
            @Override // androidx.core.util.Consumer
            public void accept(WindowLayoutInfo layoutInfo) {
                FlutterView.this.setWindowInfoListenerDisplayFeatures(layoutInfo);
            }
        };
        if (renderMode == RenderMode.surface) {
            FlutterSurfaceView flutterSurfaceView = new FlutterSurfaceView(context);
            this.flutterSurfaceView = flutterSurfaceView;
            this.renderSurface = flutterSurfaceView;
        } else if (renderMode == RenderMode.texture) {
            FlutterTextureView flutterTextureView = new FlutterTextureView(context);
            this.flutterTextureView = flutterTextureView;
            this.renderSurface = flutterTextureView;
        } else {
            throw new IllegalArgumentException(String.format("RenderMode not supported with this constructor: %s", renderMode));
        }
        init();
    }

    @Deprecated
    public FlutterView(Context context, TransparencyMode transparencyMode) {
        this(context, (AttributeSet) null, new FlutterSurfaceView(context, transparencyMode == TransparencyMode.transparent));
    }

    public FlutterView(Context context, FlutterSurfaceView flutterSurfaceView) {
        this(context, (AttributeSet) null, flutterSurfaceView);
    }

    public FlutterView(Context context, FlutterTextureView flutterTextureView) {
        this(context, (AttributeSet) null, flutterTextureView);
    }

    public FlutterView(Context context, FlutterImageView flutterImageView) {
        this(context, (AttributeSet) null, flutterImageView);
    }

    public FlutterView(Context context, AttributeSet attrs) {
        this(context, attrs, new FlutterSurfaceView(context));
    }

    @Deprecated
    public FlutterView(Context context, RenderMode renderMode, TransparencyMode transparencyMode) {
        super(context, null);
        this.flutterUiDisplayListeners = new HashSet();
        this.flutterEngineAttachmentListeners = new HashSet();
        this.viewportMetrics = new FlutterRenderer.ViewportMetrics();
        this.onAccessibilityChangeListener = new AccessibilityBridge.OnAccessibilityChangeListener() { // from class: io.flutter.embedding.android.FlutterView.1
            @Override // io.flutter.view.AccessibilityBridge.OnAccessibilityChangeListener
            public void onAccessibilityChanged(boolean isAccessibilityEnabled, boolean isTouchExplorationEnabled) {
                FlutterView.this.resetWillNotDraw(isAccessibilityEnabled, isTouchExplorationEnabled);
            }
        };
        this.systemSettingsObserver = new ContentObserver(new Handler(Looper.getMainLooper())) { // from class: io.flutter.embedding.android.FlutterView.2
            @Override // android.database.ContentObserver
            public void onChange(boolean selfChange) {
                super.onChange(selfChange);
                if (FlutterView.this.flutterEngine == null) {
                    return;
                }
                Log.v(FlutterView.TAG, "System settings changed. Sending user settings to Flutter.");
                FlutterView.this.sendUserSettingsToFlutter();
            }

            @Override // android.database.ContentObserver
            public boolean deliverSelfNotifications() {
                return true;
            }
        };
        this.flutterUiDisplayListener = new FlutterUiDisplayListener() { // from class: io.flutter.embedding.android.FlutterView.3
            @Override // io.flutter.embedding.engine.renderer.FlutterUiDisplayListener
            public void onFlutterUiDisplayed() {
                FlutterView.this.isFlutterUiDisplayed = true;
                for (FlutterUiDisplayListener listener : FlutterView.this.flutterUiDisplayListeners) {
                    listener.onFlutterUiDisplayed();
                }
            }

            @Override // io.flutter.embedding.engine.renderer.FlutterUiDisplayListener
            public void onFlutterUiNoLongerDisplayed() {
                FlutterView.this.isFlutterUiDisplayed = false;
                for (FlutterUiDisplayListener listener : FlutterView.this.flutterUiDisplayListeners) {
                    listener.onFlutterUiNoLongerDisplayed();
                }
            }
        };
        this.windowInfoListener = new Consumer<WindowLayoutInfo>() { // from class: io.flutter.embedding.android.FlutterView.4
            @Override // androidx.core.util.Consumer
            public void accept(WindowLayoutInfo layoutInfo) {
                FlutterView.this.setWindowInfoListenerDisplayFeatures(layoutInfo);
            }
        };
        if (renderMode == RenderMode.surface) {
            FlutterSurfaceView flutterSurfaceView = new FlutterSurfaceView(context, transparencyMode == TransparencyMode.transparent);
            this.flutterSurfaceView = flutterSurfaceView;
            this.renderSurface = flutterSurfaceView;
        } else if (renderMode == RenderMode.texture) {
            FlutterTextureView flutterTextureView = new FlutterTextureView(context);
            this.flutterTextureView = flutterTextureView;
            this.renderSurface = flutterTextureView;
        } else {
            throw new IllegalArgumentException(String.format("RenderMode not supported with this constructor: %s", renderMode));
        }
        init();
    }

    private FlutterView(Context context, AttributeSet attrs, FlutterSurfaceView flutterSurfaceView) {
        super(context, attrs);
        this.flutterUiDisplayListeners = new HashSet();
        this.flutterEngineAttachmentListeners = new HashSet();
        this.viewportMetrics = new FlutterRenderer.ViewportMetrics();
        this.onAccessibilityChangeListener = new AccessibilityBridge.OnAccessibilityChangeListener() { // from class: io.flutter.embedding.android.FlutterView.1
            @Override // io.flutter.view.AccessibilityBridge.OnAccessibilityChangeListener
            public void onAccessibilityChanged(boolean isAccessibilityEnabled, boolean isTouchExplorationEnabled) {
                FlutterView.this.resetWillNotDraw(isAccessibilityEnabled, isTouchExplorationEnabled);
            }
        };
        this.systemSettingsObserver = new ContentObserver(new Handler(Looper.getMainLooper())) { // from class: io.flutter.embedding.android.FlutterView.2
            @Override // android.database.ContentObserver
            public void onChange(boolean selfChange) {
                super.onChange(selfChange);
                if (FlutterView.this.flutterEngine == null) {
                    return;
                }
                Log.v(FlutterView.TAG, "System settings changed. Sending user settings to Flutter.");
                FlutterView.this.sendUserSettingsToFlutter();
            }

            @Override // android.database.ContentObserver
            public boolean deliverSelfNotifications() {
                return true;
            }
        };
        this.flutterUiDisplayListener = new FlutterUiDisplayListener() { // from class: io.flutter.embedding.android.FlutterView.3
            @Override // io.flutter.embedding.engine.renderer.FlutterUiDisplayListener
            public void onFlutterUiDisplayed() {
                FlutterView.this.isFlutterUiDisplayed = true;
                for (FlutterUiDisplayListener listener : FlutterView.this.flutterUiDisplayListeners) {
                    listener.onFlutterUiDisplayed();
                }
            }

            @Override // io.flutter.embedding.engine.renderer.FlutterUiDisplayListener
            public void onFlutterUiNoLongerDisplayed() {
                FlutterView.this.isFlutterUiDisplayed = false;
                for (FlutterUiDisplayListener listener : FlutterView.this.flutterUiDisplayListeners) {
                    listener.onFlutterUiNoLongerDisplayed();
                }
            }
        };
        this.windowInfoListener = new Consumer<WindowLayoutInfo>() { // from class: io.flutter.embedding.android.FlutterView.4
            @Override // androidx.core.util.Consumer
            public void accept(WindowLayoutInfo layoutInfo) {
                FlutterView.this.setWindowInfoListenerDisplayFeatures(layoutInfo);
            }
        };
        this.flutterSurfaceView = flutterSurfaceView;
        this.renderSurface = flutterSurfaceView;
        init();
    }

    private FlutterView(Context context, AttributeSet attrs, FlutterTextureView flutterTextureView) {
        super(context, attrs);
        this.flutterUiDisplayListeners = new HashSet();
        this.flutterEngineAttachmentListeners = new HashSet();
        this.viewportMetrics = new FlutterRenderer.ViewportMetrics();
        this.onAccessibilityChangeListener = new AccessibilityBridge.OnAccessibilityChangeListener() { // from class: io.flutter.embedding.android.FlutterView.1
            @Override // io.flutter.view.AccessibilityBridge.OnAccessibilityChangeListener
            public void onAccessibilityChanged(boolean isAccessibilityEnabled, boolean isTouchExplorationEnabled) {
                FlutterView.this.resetWillNotDraw(isAccessibilityEnabled, isTouchExplorationEnabled);
            }
        };
        this.systemSettingsObserver = new ContentObserver(new Handler(Looper.getMainLooper())) { // from class: io.flutter.embedding.android.FlutterView.2
            @Override // android.database.ContentObserver
            public void onChange(boolean selfChange) {
                super.onChange(selfChange);
                if (FlutterView.this.flutterEngine == null) {
                    return;
                }
                Log.v(FlutterView.TAG, "System settings changed. Sending user settings to Flutter.");
                FlutterView.this.sendUserSettingsToFlutter();
            }

            @Override // android.database.ContentObserver
            public boolean deliverSelfNotifications() {
                return true;
            }
        };
        this.flutterUiDisplayListener = new FlutterUiDisplayListener() { // from class: io.flutter.embedding.android.FlutterView.3
            @Override // io.flutter.embedding.engine.renderer.FlutterUiDisplayListener
            public void onFlutterUiDisplayed() {
                FlutterView.this.isFlutterUiDisplayed = true;
                for (FlutterUiDisplayListener listener : FlutterView.this.flutterUiDisplayListeners) {
                    listener.onFlutterUiDisplayed();
                }
            }

            @Override // io.flutter.embedding.engine.renderer.FlutterUiDisplayListener
            public void onFlutterUiNoLongerDisplayed() {
                FlutterView.this.isFlutterUiDisplayed = false;
                for (FlutterUiDisplayListener listener : FlutterView.this.flutterUiDisplayListeners) {
                    listener.onFlutterUiNoLongerDisplayed();
                }
            }
        };
        this.windowInfoListener = new Consumer<WindowLayoutInfo>() { // from class: io.flutter.embedding.android.FlutterView.4
            @Override // androidx.core.util.Consumer
            public void accept(WindowLayoutInfo layoutInfo) {
                FlutterView.this.setWindowInfoListenerDisplayFeatures(layoutInfo);
            }
        };
        this.flutterTextureView = flutterTextureView;
        this.renderSurface = flutterTextureView;
        init();
    }

    private FlutterView(Context context, AttributeSet attrs, FlutterImageView flutterImageView) {
        super(context, attrs);
        this.flutterUiDisplayListeners = new HashSet();
        this.flutterEngineAttachmentListeners = new HashSet();
        this.viewportMetrics = new FlutterRenderer.ViewportMetrics();
        this.onAccessibilityChangeListener = new AccessibilityBridge.OnAccessibilityChangeListener() { // from class: io.flutter.embedding.android.FlutterView.1
            @Override // io.flutter.view.AccessibilityBridge.OnAccessibilityChangeListener
            public void onAccessibilityChanged(boolean isAccessibilityEnabled, boolean isTouchExplorationEnabled) {
                FlutterView.this.resetWillNotDraw(isAccessibilityEnabled, isTouchExplorationEnabled);
            }
        };
        this.systemSettingsObserver = new ContentObserver(new Handler(Looper.getMainLooper())) { // from class: io.flutter.embedding.android.FlutterView.2
            @Override // android.database.ContentObserver
            public void onChange(boolean selfChange) {
                super.onChange(selfChange);
                if (FlutterView.this.flutterEngine == null) {
                    return;
                }
                Log.v(FlutterView.TAG, "System settings changed. Sending user settings to Flutter.");
                FlutterView.this.sendUserSettingsToFlutter();
            }

            @Override // android.database.ContentObserver
            public boolean deliverSelfNotifications() {
                return true;
            }
        };
        this.flutterUiDisplayListener = new FlutterUiDisplayListener() { // from class: io.flutter.embedding.android.FlutterView.3
            @Override // io.flutter.embedding.engine.renderer.FlutterUiDisplayListener
            public void onFlutterUiDisplayed() {
                FlutterView.this.isFlutterUiDisplayed = true;
                for (FlutterUiDisplayListener listener : FlutterView.this.flutterUiDisplayListeners) {
                    listener.onFlutterUiDisplayed();
                }
            }

            @Override // io.flutter.embedding.engine.renderer.FlutterUiDisplayListener
            public void onFlutterUiNoLongerDisplayed() {
                FlutterView.this.isFlutterUiDisplayed = false;
                for (FlutterUiDisplayListener listener : FlutterView.this.flutterUiDisplayListeners) {
                    listener.onFlutterUiNoLongerDisplayed();
                }
            }
        };
        this.windowInfoListener = new Consumer<WindowLayoutInfo>() { // from class: io.flutter.embedding.android.FlutterView.4
            @Override // androidx.core.util.Consumer
            public void accept(WindowLayoutInfo layoutInfo) {
                FlutterView.this.setWindowInfoListenerDisplayFeatures(layoutInfo);
            }
        };
        this.flutterImageView = flutterImageView;
        this.renderSurface = flutterImageView;
        init();
    }

    private void init() {
        Log.v(TAG, "Initializing FlutterView");
        if (this.flutterSurfaceView != null) {
            Log.v(TAG, "Internally using a FlutterSurfaceView.");
            addView(this.flutterSurfaceView);
        } else if (this.flutterTextureView != null) {
            Log.v(TAG, "Internally using a FlutterTextureView.");
            addView(this.flutterTextureView);
        } else {
            Log.v(TAG, "Internally using a FlutterImageView.");
            addView(this.flutterImageView);
        }
        setFocusable(true);
        setFocusableInTouchMode(true);
        if (Build.VERSION.SDK_INT >= 26) {
            setImportantForAutofill(1);
        }
    }

    public boolean hasRenderedFirstFrame() {
        return this.isFlutterUiDisplayed;
    }

    public void addOnFirstFrameRenderedListener(FlutterUiDisplayListener listener) {
        this.flutterUiDisplayListeners.add(listener);
    }

    public void removeOnFirstFrameRenderedListener(FlutterUiDisplayListener listener) {
        this.flutterUiDisplayListeners.remove(listener);
    }

    @Override // android.view.View
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (this.flutterEngine != null) {
            Log.v(TAG, "Configuration changed. Sending locales and user settings to Flutter.");
            this.localizationPlugin.sendLocalesToFlutter(newConfig);
            sendUserSettingsToFlutter();
        }
    }

    @Override // android.view.View
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);
        Log.v(TAG, "Size changed. Sending Flutter new viewport metrics. FlutterView was " + oldWidth + " x " + oldHeight + ", it is now " + width + " x " + height);
        this.viewportMetrics.width = width;
        this.viewportMetrics.height = height;
        sendViewportMetricsToFlutter();
    }

    protected WindowInfoRepositoryCallbackAdapterWrapper createWindowInfoRepo() {
        try {
            return new WindowInfoRepositoryCallbackAdapterWrapper(new WindowInfoTrackerCallbackAdapter(WindowInfoTracker.Companion.getOrCreate(getContext())));
        } catch (NoClassDefFoundError e) {
            return null;
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.windowInfoRepo = createWindowInfoRepo();
        Activity activity = ViewUtils.getActivity(getContext());
        WindowInfoRepositoryCallbackAdapterWrapper windowInfoRepositoryCallbackAdapterWrapper = this.windowInfoRepo;
        if (windowInfoRepositoryCallbackAdapterWrapper != null && activity != null) {
            windowInfoRepositoryCallbackAdapterWrapper.addWindowLayoutInfoListener(activity, ContextCompat.getMainExecutor(getContext()), this.windowInfoListener);
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        WindowInfoRepositoryCallbackAdapterWrapper windowInfoRepositoryCallbackAdapterWrapper = this.windowInfoRepo;
        if (windowInfoRepositoryCallbackAdapterWrapper != null) {
            windowInfoRepositoryCallbackAdapterWrapper.removeWindowLayoutInfoListener(this.windowInfoListener);
        }
        this.windowInfoRepo = null;
        super.onDetachedFromWindow();
    }

    protected void setWindowInfoListenerDisplayFeatures(WindowLayoutInfo layoutInfo) {
        WindowInsets insets;
        DisplayCutout cutout;
        FlutterRenderer.DisplayFeatureType type;
        FlutterRenderer.DisplayFeatureState state;
        List<DisplayFeature> displayFeatures = layoutInfo.getDisplayFeatures();
        List<FlutterRenderer.DisplayFeature> result = new ArrayList<>();
        for (DisplayFeature displayFeature : displayFeatures) {
            Log.v(TAG, "WindowInfoTracker Display Feature reported with bounds = " + displayFeature.getBounds().toString() + " and type = " + displayFeature.getClass().getSimpleName());
            if (displayFeature instanceof FoldingFeature) {
                FoldingFeature feature = (FoldingFeature) displayFeature;
                if (feature.getOcclusionType() == FoldingFeature.OcclusionType.FULL) {
                    type = FlutterRenderer.DisplayFeatureType.HINGE;
                } else {
                    type = FlutterRenderer.DisplayFeatureType.FOLD;
                }
                if (feature.getState() == FoldingFeature.State.FLAT) {
                    state = FlutterRenderer.DisplayFeatureState.POSTURE_FLAT;
                } else if (feature.getState() == FoldingFeature.State.HALF_OPENED) {
                    state = FlutterRenderer.DisplayFeatureState.POSTURE_HALF_OPENED;
                } else {
                    state = FlutterRenderer.DisplayFeatureState.UNKNOWN;
                }
                result.add(new FlutterRenderer.DisplayFeature(displayFeature.getBounds(), type, state));
            } else {
                result.add(new FlutterRenderer.DisplayFeature(displayFeature.getBounds(), FlutterRenderer.DisplayFeatureType.UNKNOWN, FlutterRenderer.DisplayFeatureState.UNKNOWN));
            }
        }
        if (Build.VERSION.SDK_INT >= 28 && (insets = getRootWindowInsets()) != null && (cutout = insets.getDisplayCutout()) != null) {
            for (Rect bounds : cutout.getBoundingRects()) {
                Log.v(TAG, "DisplayCutout area reported with bounds = " + bounds.toString());
                result.add(new FlutterRenderer.DisplayFeature(bounds, FlutterRenderer.DisplayFeatureType.CUTOUT));
            }
        }
        this.viewportMetrics.displayFeatures = result;
        sendViewportMetricsToFlutter();
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
        WindowInsets newInsets = super.onApplyWindowInsets(insets);
        if (Build.VERSION.SDK_INT == 29) {
            Insets systemGestureInsets = insets.getSystemGestureInsets();
            this.viewportMetrics.systemGestureInsetTop = systemGestureInsets.top;
            this.viewportMetrics.systemGestureInsetRight = systemGestureInsets.right;
            this.viewportMetrics.systemGestureInsetBottom = systemGestureInsets.bottom;
            this.viewportMetrics.systemGestureInsetLeft = systemGestureInsets.left;
        }
        boolean statusBarVisible = (getWindowSystemUiVisibility() & 4) == 0;
        boolean navigationBarVisible = (getWindowSystemUiVisibility() & 2) == 0;
        if (Build.VERSION.SDK_INT >= 30) {
            int mask = navigationBarVisible ? 0 | WindowInsets.Type.navigationBars() : 0;
            if (statusBarVisible) {
                mask |= WindowInsets.Type.statusBars();
            }
            Insets uiInsets = insets.getInsets(mask);
            this.viewportMetrics.viewPaddingTop = uiInsets.top;
            this.viewportMetrics.viewPaddingRight = uiInsets.right;
            this.viewportMetrics.viewPaddingBottom = uiInsets.bottom;
            this.viewportMetrics.viewPaddingLeft = uiInsets.left;
            Insets imeInsets = insets.getInsets(WindowInsets.Type.ime());
            this.viewportMetrics.viewInsetTop = imeInsets.top;
            this.viewportMetrics.viewInsetRight = imeInsets.right;
            this.viewportMetrics.viewInsetBottom = imeInsets.bottom;
            this.viewportMetrics.viewInsetLeft = imeInsets.left;
            Insets systemGestureInsets2 = insets.getInsets(WindowInsets.Type.systemGestures());
            this.viewportMetrics.systemGestureInsetTop = systemGestureInsets2.top;
            this.viewportMetrics.systemGestureInsetRight = systemGestureInsets2.right;
            this.viewportMetrics.systemGestureInsetBottom = systemGestureInsets2.bottom;
            this.viewportMetrics.systemGestureInsetLeft = systemGestureInsets2.left;
            DisplayCutout cutout = insets.getDisplayCutout();
            if (cutout != null) {
                Insets waterfallInsets = cutout.getWaterfallInsets();
                FlutterRenderer.ViewportMetrics viewportMetrics = this.viewportMetrics;
                viewportMetrics.viewPaddingTop = Math.max(Math.max(viewportMetrics.viewPaddingTop, waterfallInsets.top), cutout.getSafeInsetTop());
                FlutterRenderer.ViewportMetrics viewportMetrics2 = this.viewportMetrics;
                viewportMetrics2.viewPaddingRight = Math.max(Math.max(viewportMetrics2.viewPaddingRight, waterfallInsets.right), cutout.getSafeInsetRight());
                FlutterRenderer.ViewportMetrics viewportMetrics3 = this.viewportMetrics;
                viewportMetrics3.viewPaddingBottom = Math.max(Math.max(viewportMetrics3.viewPaddingBottom, waterfallInsets.bottom), cutout.getSafeInsetBottom());
                FlutterRenderer.ViewportMetrics viewportMetrics4 = this.viewportMetrics;
                viewportMetrics4.viewPaddingLeft = Math.max(Math.max(viewportMetrics4.viewPaddingLeft, waterfallInsets.left), cutout.getSafeInsetLeft());
            }
        } else {
            ZeroSides zeroSides = ZeroSides.NONE;
            if (!navigationBarVisible) {
                zeroSides = calculateShouldZeroSides();
            }
            this.viewportMetrics.viewPaddingTop = statusBarVisible ? insets.getSystemWindowInsetTop() : 0;
            FlutterRenderer.ViewportMetrics viewportMetrics5 = this.viewportMetrics;
            if (zeroSides == ZeroSides.RIGHT || zeroSides == ZeroSides.BOTH) {
                i = 0;
            } else {
                i = insets.getSystemWindowInsetRight();
            }
            viewportMetrics5.viewPaddingRight = i;
            FlutterRenderer.ViewportMetrics viewportMetrics6 = this.viewportMetrics;
            if (navigationBarVisible && guessBottomKeyboardInset(insets) == 0) {
                i2 = insets.getSystemWindowInsetBottom();
            } else {
                i2 = 0;
            }
            viewportMetrics6.viewPaddingBottom = i2;
            FlutterRenderer.ViewportMetrics viewportMetrics7 = this.viewportMetrics;
            if (zeroSides == ZeroSides.LEFT || zeroSides == ZeroSides.BOTH) {
                i3 = 0;
            } else {
                i3 = insets.getSystemWindowInsetLeft();
            }
            viewportMetrics7.viewPaddingLeft = i3;
            this.viewportMetrics.viewInsetTop = 0;
            this.viewportMetrics.viewInsetRight = 0;
            this.viewportMetrics.viewInsetBottom = guessBottomKeyboardInset(insets);
            this.viewportMetrics.viewInsetLeft = 0;
        }
        Log.v(TAG, "Updating window insets (onApplyWindowInsets()):\nStatus bar insets: Top: " + this.viewportMetrics.viewPaddingTop + ", Left: " + this.viewportMetrics.viewPaddingLeft + ", Right: " + this.viewportMetrics.viewPaddingRight + "\nKeyboard insets: Bottom: " + this.viewportMetrics.viewInsetBottom + ", Left: " + this.viewportMetrics.viewInsetLeft + ", Right: " + this.viewportMetrics.viewInsetRight + "System Gesture Insets - Left: " + this.viewportMetrics.systemGestureInsetLeft + ", Top: " + this.viewportMetrics.systemGestureInsetTop + ", Right: " + this.viewportMetrics.systemGestureInsetRight + ", Bottom: " + this.viewportMetrics.viewInsetBottom);
        sendViewportMetricsToFlutter();
        return newInsets;
    }

    @Override // android.view.View
    protected boolean fitSystemWindows(Rect insets) {
        if (Build.VERSION.SDK_INT <= 19) {
            this.viewportMetrics.viewPaddingTop = insets.top;
            this.viewportMetrics.viewPaddingRight = insets.right;
            this.viewportMetrics.viewPaddingBottom = 0;
            this.viewportMetrics.viewPaddingLeft = insets.left;
            this.viewportMetrics.viewInsetTop = 0;
            this.viewportMetrics.viewInsetRight = 0;
            this.viewportMetrics.viewInsetBottom = insets.bottom;
            this.viewportMetrics.viewInsetLeft = 0;
            Log.v(TAG, "Updating window insets (fitSystemWindows()):\nStatus bar insets: Top: " + this.viewportMetrics.viewPaddingTop + ", Left: " + this.viewportMetrics.viewPaddingLeft + ", Right: " + this.viewportMetrics.viewPaddingRight + "\nKeyboard insets: Bottom: " + this.viewportMetrics.viewInsetBottom + ", Left: " + this.viewportMetrics.viewInsetLeft + ", Right: " + this.viewportMetrics.viewInsetRight);
            sendViewportMetricsToFlutter();
            return true;
        }
        return super.fitSystemWindows(insets);
    }

    @Override // android.view.View
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        if (!isAttachedToFlutterEngine()) {
            return super.onCreateInputConnection(outAttrs);
        }
        return this.textInputPlugin.createInputConnection(this, this.keyboardManager, outAttrs);
    }

    @Override // android.view.View
    public boolean checkInputConnectionProxy(View view) {
        FlutterEngine flutterEngine = this.flutterEngine;
        if (flutterEngine != null) {
            return flutterEngine.getPlatformViewsController().checkInputConnectionProxy(view);
        }
        return super.checkInputConnectionProxy(view);
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == 0 && event.getRepeatCount() == 0) {
            getKeyDispatcherState().startTracking(event, this);
        } else if (event.getAction() == 1) {
            getKeyDispatcherState().handleUpEvent(event);
        }
        return (isAttachedToFlutterEngine() && this.keyboardManager.handleEvent(event)) || super.dispatchKeyEvent(event);
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        if (!isAttachedToFlutterEngine()) {
            return super.onTouchEvent(event);
        }
        if (Build.VERSION.SDK_INT >= 21) {
            requestUnbufferedDispatch(event);
        }
        return this.androidTouchProcessor.onTouchEvent(event);
    }

    @Override // android.view.View
    public boolean onGenericMotionEvent(MotionEvent event) {
        boolean handled = isAttachedToFlutterEngine() && this.androidTouchProcessor.onGenericMotionEvent(event);
        if (handled) {
            return true;
        }
        return super.onGenericMotionEvent(event);
    }

    @Override // android.view.View
    public boolean onHoverEvent(MotionEvent event) {
        if (!isAttachedToFlutterEngine()) {
            return super.onHoverEvent(event);
        }
        boolean handled = this.accessibilityBridge.onAccessibilityHoverEvent(event);
        return handled;
    }

    @Override // android.view.View
    public AccessibilityNodeProvider getAccessibilityNodeProvider() {
        AccessibilityBridge accessibilityBridge = this.accessibilityBridge;
        if (accessibilityBridge != null && accessibilityBridge.isAccessibilityEnabled()) {
            return this.accessibilityBridge;
        }
        return null;
    }

    public View findViewByAccessibilityIdTraversal(int accessibilityId) {
        if (Build.VERSION.SDK_INT < 29) {
            return findViewByAccessibilityIdRootedAtCurrentView(accessibilityId, this);
        }
        try {
            Method findViewByAccessibilityIdTraversalMethod = View.class.getDeclaredMethod("findViewByAccessibilityIdTraversal", Integer.TYPE);
            findViewByAccessibilityIdTraversalMethod.setAccessible(true);
            try {
                return (View) findViewByAccessibilityIdTraversalMethod.invoke(this, Integer.valueOf(accessibilityId));
            } catch (IllegalAccessException e) {
                return null;
            } catch (InvocationTargetException e2) {
                return null;
            }
        } catch (NoSuchMethodException e3) {
            return null;
        }
    }

    private View findViewByAccessibilityIdRootedAtCurrentView(int accessibilityId, View currentView) {
        try {
            Method getAccessibilityViewIdMethod = View.class.getDeclaredMethod("getAccessibilityViewId", new Class[0]);
            getAccessibilityViewIdMethod.setAccessible(true);
            try {
                if (getAccessibilityViewIdMethod.invoke(currentView, new Object[0]).equals(Integer.valueOf(accessibilityId))) {
                    return currentView;
                }
                if (currentView instanceof ViewGroup) {
                    for (int i = 0; i < ((ViewGroup) currentView).getChildCount(); i++) {
                        View view = findViewByAccessibilityIdRootedAtCurrentView(accessibilityId, ((ViewGroup) currentView).getChildAt(i));
                        if (view != null) {
                            return view;
                        }
                    }
                }
                return null;
            } catch (IllegalAccessException e) {
                return null;
            } catch (InvocationTargetException e2) {
                return null;
            }
        } catch (NoSuchMethodException e3) {
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void resetWillNotDraw(boolean isAccessibilityEnabled, boolean isTouchExplorationEnabled) {
        boolean z = false;
        if (!this.flutterEngine.getRenderer().isSoftwareRenderingEnabled()) {
            if (!isAccessibilityEnabled && !isTouchExplorationEnabled) {
                z = true;
            }
            setWillNotDraw(z);
            return;
        }
        setWillNotDraw(false);
    }

    @Override // io.flutter.plugin.mouse.MouseCursorPlugin.MouseCursorViewDelegate
    public PointerIcon getSystemPointerIcon(int type) {
        return PointerIcon.getSystemIcon(getContext(), type);
    }

    @Override // io.flutter.embedding.android.KeyboardManager.ViewDelegate
    public BinaryMessenger getBinaryMessenger() {
        return this.flutterEngine.getDartExecutor();
    }

    @Override // io.flutter.embedding.android.KeyboardManager.ViewDelegate
    public boolean onTextInputKeyEvent(KeyEvent keyEvent) {
        return this.textInputPlugin.handleKeyEvent(keyEvent);
    }

    @Override // io.flutter.embedding.android.KeyboardManager.ViewDelegate
    public void redispatch(KeyEvent keyEvent) {
        getRootView().dispatchKeyEvent(keyEvent);
    }

    public void attachToFlutterEngine(FlutterEngine flutterEngine) {
        Log.v(TAG, "Attaching to a FlutterEngine: " + flutterEngine);
        if (isAttachedToFlutterEngine()) {
            if (flutterEngine == this.flutterEngine) {
                Log.v(TAG, "Already attached to this engine. Doing nothing.");
                return;
            } else {
                Log.v(TAG, "Currently attached to a different engine. Detaching and then attaching to new engine.");
                detachFromFlutterEngine();
            }
        }
        this.flutterEngine = flutterEngine;
        FlutterRenderer flutterRenderer = flutterEngine.getRenderer();
        this.isFlutterUiDisplayed = flutterRenderer.isDisplayingFlutterUi();
        this.renderSurface.attachToRenderer(flutterRenderer);
        flutterRenderer.addIsDisplayingFlutterUiListener(this.flutterUiDisplayListener);
        if (Build.VERSION.SDK_INT >= 24) {
            this.mouseCursorPlugin = new MouseCursorPlugin(this, this.flutterEngine.getMouseCursorChannel());
        }
        this.textInputPlugin = new TextInputPlugin(this, this.flutterEngine.getTextInputChannel(), this.flutterEngine.getPlatformViewsController());
        try {
            TextServicesManager textServicesManager = (TextServicesManager) getContext().getSystemService("textservices");
            this.textServicesManager = textServicesManager;
            this.spellCheckPlugin = new SpellCheckPlugin(textServicesManager, this.flutterEngine.getSpellCheckChannel());
        } catch (Exception e) {
            Log.e(TAG, "TextServicesManager not supported by device, spell check disabled.");
        }
        this.localizationPlugin = this.flutterEngine.getLocalizationPlugin();
        this.keyboardManager = new KeyboardManager(this);
        this.androidTouchProcessor = new AndroidTouchProcessor(this.flutterEngine.getRenderer(), false);
        AccessibilityBridge accessibilityBridge = new AccessibilityBridge(this, flutterEngine.getAccessibilityChannel(), (AccessibilityManager) getContext().getSystemService("accessibility"), getContext().getContentResolver(), this.flutterEngine.getPlatformViewsController());
        this.accessibilityBridge = accessibilityBridge;
        accessibilityBridge.setOnAccessibilityChangeListener(this.onAccessibilityChangeListener);
        resetWillNotDraw(this.accessibilityBridge.isAccessibilityEnabled(), this.accessibilityBridge.isTouchExplorationEnabled());
        this.flutterEngine.getPlatformViewsController().attachAccessibilityBridge(this.accessibilityBridge);
        this.flutterEngine.getPlatformViewsController().attachToFlutterRenderer(this.flutterEngine.getRenderer());
        this.textInputPlugin.getInputMethodManager().restartInput(this);
        sendUserSettingsToFlutter();
        getContext().getContentResolver().registerContentObserver(Settings.System.getUriFor("show_password"), false, this.systemSettingsObserver);
        sendViewportMetricsToFlutter();
        flutterEngine.getPlatformViewsController().attachToView(this);
        for (FlutterEngineAttachmentListener listener : this.flutterEngineAttachmentListeners) {
            listener.onFlutterEngineAttachedToFlutterView(flutterEngine);
        }
        if (this.isFlutterUiDisplayed) {
            this.flutterUiDisplayListener.onFlutterUiDisplayed();
        }
    }

    public void detachFromFlutterEngine() {
        Log.v(TAG, "Detaching from a FlutterEngine: " + this.flutterEngine);
        if (!isAttachedToFlutterEngine()) {
            Log.v(TAG, "FlutterView not attached to an engine. Not detaching.");
            return;
        }
        for (FlutterEngineAttachmentListener listener : this.flutterEngineAttachmentListeners) {
            listener.onFlutterEngineDetachedFromFlutterView();
        }
        getContext().getContentResolver().unregisterContentObserver(this.systemSettingsObserver);
        this.flutterEngine.getPlatformViewsController().detachFromView();
        this.flutterEngine.getPlatformViewsController().detachAccessibilityBridge();
        this.accessibilityBridge.release();
        this.accessibilityBridge = null;
        this.textInputPlugin.getInputMethodManager().restartInput(this);
        this.textInputPlugin.destroy();
        this.keyboardManager.destroy();
        SpellCheckPlugin spellCheckPlugin = this.spellCheckPlugin;
        if (spellCheckPlugin != null) {
            spellCheckPlugin.destroy();
        }
        MouseCursorPlugin mouseCursorPlugin = this.mouseCursorPlugin;
        if (mouseCursorPlugin != null) {
            mouseCursorPlugin.destroy();
        }
        FlutterRenderer flutterRenderer = this.flutterEngine.getRenderer();
        this.isFlutterUiDisplayed = false;
        flutterRenderer.removeIsDisplayingFlutterUiListener(this.flutterUiDisplayListener);
        flutterRenderer.stopRenderingToSurface();
        flutterRenderer.setSemanticsEnabled(false);
        RenderSurface renderSurface = this.previousRenderSurface;
        if (renderSurface != null && this.renderSurface == this.flutterImageView) {
            this.renderSurface = renderSurface;
        }
        this.renderSurface.detachFromRenderer();
        releaseImageView();
        this.previousRenderSurface = null;
        this.flutterEngine = null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void releaseImageView() {
        FlutterImageView flutterImageView = this.flutterImageView;
        if (flutterImageView != null) {
            flutterImageView.closeImageReader();
            removeView(this.flutterImageView);
            this.flutterImageView = null;
        }
    }

    public FlutterImageView createImageView() {
        return new FlutterImageView(getContext(), getWidth(), getHeight(), FlutterImageView.SurfaceKind.background);
    }

    public FlutterImageView getCurrentImageSurface() {
        return this.flutterImageView;
    }

    public void convertToImageView() {
        this.renderSurface.pause();
        FlutterImageView flutterImageView = this.flutterImageView;
        if (flutterImageView == null) {
            FlutterImageView createImageView = createImageView();
            this.flutterImageView = createImageView;
            addView(createImageView);
        } else {
            flutterImageView.resizeIfNeeded(getWidth(), getHeight());
        }
        this.previousRenderSurface = this.renderSurface;
        FlutterImageView flutterImageView2 = this.flutterImageView;
        this.renderSurface = flutterImageView2;
        FlutterEngine flutterEngine = this.flutterEngine;
        if (flutterEngine != null) {
            flutterImageView2.attachToRenderer(flutterEngine.getRenderer());
        }
    }

    public void revertImageView(final Runnable onDone) {
        if (this.flutterImageView == null) {
            Log.v(TAG, "Tried to revert the image view, but no image view is used.");
            return;
        }
        RenderSurface renderSurface = this.previousRenderSurface;
        if (renderSurface == null) {
            Log.v(TAG, "Tried to revert the image view, but no previous surface was used.");
            return;
        }
        this.renderSurface = renderSurface;
        this.previousRenderSurface = null;
        final FlutterRenderer renderer = this.flutterEngine.getRenderer();
        if (this.flutterEngine == null || renderer == null) {
            this.flutterImageView.detachFromRenderer();
            releaseImageView();
            onDone.run();
            return;
        }
        this.renderSurface.attachToRenderer(renderer);
        renderer.addIsDisplayingFlutterUiListener(new FlutterUiDisplayListener() { // from class: io.flutter.embedding.android.FlutterView.5
            @Override // io.flutter.embedding.engine.renderer.FlutterUiDisplayListener
            public void onFlutterUiDisplayed() {
                renderer.removeIsDisplayingFlutterUiListener(this);
                onDone.run();
                if (!(FlutterView.this.renderSurface instanceof FlutterImageView) && FlutterView.this.flutterImageView != null) {
                    FlutterView.this.flutterImageView.detachFromRenderer();
                    FlutterView.this.releaseImageView();
                }
            }

            @Override // io.flutter.embedding.engine.renderer.FlutterUiDisplayListener
            public void onFlutterUiNoLongerDisplayed() {
            }
        });
    }

    public void attachOverlaySurfaceToRender(FlutterImageView view) {
        FlutterEngine flutterEngine = this.flutterEngine;
        if (flutterEngine != null) {
            view.attachToRenderer(flutterEngine.getRenderer());
        }
    }

    public boolean acquireLatestImageViewFrame() {
        FlutterImageView flutterImageView = this.flutterImageView;
        if (flutterImageView != null) {
            return flutterImageView.acquireLatestImage();
        }
        return false;
    }

    public boolean isAttachedToFlutterEngine() {
        FlutterEngine flutterEngine = this.flutterEngine;
        return flutterEngine != null && flutterEngine.getRenderer() == this.renderSurface.getAttachedRenderer();
    }

    public FlutterEngine getAttachedFlutterEngine() {
        return this.flutterEngine;
    }

    public void addFlutterEngineAttachmentListener(FlutterEngineAttachmentListener listener) {
        this.flutterEngineAttachmentListeners.add(listener);
    }

    public void removeFlutterEngineAttachmentListener(FlutterEngineAttachmentListener listener) {
        this.flutterEngineAttachmentListeners.remove(listener);
    }

    void sendUserSettingsToFlutter() {
        SettingsChannel.PlatformBrightness brightness;
        boolean isNightModeOn = (getResources().getConfiguration().uiMode & 48) == 32;
        if (isNightModeOn) {
            brightness = SettingsChannel.PlatformBrightness.dark;
        } else {
            brightness = SettingsChannel.PlatformBrightness.light;
        }
        boolean isNativeSpellCheckServiceDefined = false;
        if (this.textServicesManager != null) {
            if (Build.VERSION.SDK_INT >= 31) {
                List<SpellCheckerInfo> enabledSpellCheckerInfos = this.textServicesManager.getEnabledSpellCheckerInfos();
                boolean gboardSpellCheckerEnabled = enabledSpellCheckerInfos.stream().anyMatch(new Predicate() { // from class: io.flutter.embedding.android.FlutterView$$ExternalSyntheticLambda0
                    @Override // java.util.function.Predicate
                    public final boolean test(Object obj) {
                        boolean equals;
                        equals = ((SpellCheckerInfo) obj).getPackageName().equals("com.google.android.inputmethod.latin");
                        return equals;
                    }
                });
                isNativeSpellCheckServiceDefined = this.textServicesManager.isSpellCheckerEnabled() && gboardSpellCheckerEnabled;
            } else {
                isNativeSpellCheckServiceDefined = true;
            }
        }
        this.flutterEngine.getSettingsChannel().startMessage().setTextScaleFactor(getResources().getConfiguration().fontScale).setNativeSpellCheckServiceDefined(isNativeSpellCheckServiceDefined).setBrieflyShowPassword(Settings.System.getInt(getContext().getContentResolver(), "show_password", 1) == 1).setUse24HourFormat(DateFormat.is24HourFormat(getContext())).setPlatformBrightness(brightness).send();
    }

    private void sendViewportMetricsToFlutter() {
        if (!isAttachedToFlutterEngine()) {
            Log.w(TAG, "Tried to send viewport metrics from Android to Flutter but this FlutterView was not attached to a FlutterEngine.");
            return;
        }
        this.viewportMetrics.devicePixelRatio = getResources().getDisplayMetrics().density;
        this.viewportMetrics.physicalTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        this.flutterEngine.getRenderer().setViewportMetrics(this.viewportMetrics);
    }

    @Override // android.view.View
    public void onProvideAutofillVirtualStructure(ViewStructure structure, int flags) {
        super.onProvideAutofillVirtualStructure(structure, flags);
        this.textInputPlugin.onProvideAutofillVirtualStructure(structure, flags);
    }

    @Override // android.view.View
    public void autofill(SparseArray<AutofillValue> values) {
        this.textInputPlugin.autofill(values);
    }

    @Override // android.view.View
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        RenderSurface renderSurface = this.renderSurface;
        if (renderSurface instanceof FlutterSurfaceView) {
            ((FlutterSurfaceView) renderSurface).setVisibility(visibility);
        }
    }
}
