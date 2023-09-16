package io.flutter.plugin.platform;

import android.app.AlertDialog;
import android.app.Presentation;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.MutableContextWrapper;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import io.flutter.Log;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
/* loaded from: classes.dex */
class SingleViewPresentation extends Presentation {
    private static final String TAG = "PlatformViewsController";
    private final AccessibilityEventsDelegate accessibilityEventsDelegate;
    private FrameLayout container;
    private final View.OnFocusChangeListener focusChangeListener;
    private final Context outerContext;
    private AccessibilityDelegatingFrameLayout rootView;
    private boolean startFocused;
    private final PresentationState state;
    private int viewId;

    /* loaded from: classes.dex */
    static class PresentationState {
        private FakeWindowViewGroup fakeWindowViewGroup;
        private PlatformView platformView;
        private WindowManagerHandler windowManagerHandler;

        PresentationState() {
        }
    }

    public SingleViewPresentation(Context outerContext, Display display, PlatformView view, AccessibilityEventsDelegate accessibilityEventsDelegate, int viewId, View.OnFocusChangeListener focusChangeListener) {
        super(new ImmContext(outerContext), display);
        this.startFocused = false;
        this.accessibilityEventsDelegate = accessibilityEventsDelegate;
        this.viewId = viewId;
        this.focusChangeListener = focusChangeListener;
        this.outerContext = outerContext;
        PresentationState presentationState = new PresentationState();
        this.state = presentationState;
        presentationState.platformView = view;
        getWindow().setFlags(8, 8);
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().setType(2030);
        }
    }

    public SingleViewPresentation(Context outerContext, Display display, AccessibilityEventsDelegate accessibilityEventsDelegate, PresentationState state, View.OnFocusChangeListener focusChangeListener, boolean startFocused) {
        super(new ImmContext(outerContext), display);
        this.startFocused = false;
        this.accessibilityEventsDelegate = accessibilityEventsDelegate;
        this.state = state;
        this.focusChangeListener = focusChangeListener;
        this.outerContext = outerContext;
        getWindow().setFlags(8, 8);
        this.startFocused = startFocused;
    }

    @Override // android.app.Dialog
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(0));
        if (this.state.fakeWindowViewGroup == null) {
            this.state.fakeWindowViewGroup = new FakeWindowViewGroup(getContext());
        }
        if (this.state.windowManagerHandler == null) {
            WindowManager windowManagerDelegate = (WindowManager) getContext().getSystemService("window");
            PresentationState presentationState = this.state;
            presentationState.windowManagerHandler = new WindowManagerHandler(windowManagerDelegate, presentationState.fakeWindowViewGroup);
        }
        this.container = new FrameLayout(getContext());
        Context baseContext = new PresentationContext(getContext(), this.state.windowManagerHandler, this.outerContext);
        View embeddedView = this.state.platformView.getView();
        if (embeddedView.getContext() instanceof MutableContextWrapper) {
            MutableContextWrapper currentContext = (MutableContextWrapper) embeddedView.getContext();
            currentContext.setBaseContext(baseContext);
        } else {
            Log.w(TAG, "Unexpected platform view context for view ID " + this.viewId + "; some functionality may not work correctly. When constructing a platform view in the factory, ensure that the view returned from PlatformViewFactory#create returns the provided context from getContext(). If you are unable to associate the view with that context, consider using Hybrid Composition instead.");
        }
        this.container.addView(embeddedView);
        AccessibilityDelegatingFrameLayout accessibilityDelegatingFrameLayout = new AccessibilityDelegatingFrameLayout(getContext(), this.accessibilityEventsDelegate, embeddedView);
        this.rootView = accessibilityDelegatingFrameLayout;
        accessibilityDelegatingFrameLayout.addView(this.container);
        this.rootView.addView(this.state.fakeWindowViewGroup);
        embeddedView.setOnFocusChangeListener(this.focusChangeListener);
        this.rootView.setFocusableInTouchMode(true);
        if (this.startFocused) {
            embeddedView.requestFocus();
        } else {
            this.rootView.requestFocus();
        }
        setContentView(this.rootView);
    }

    public PresentationState detachState() {
        this.container.removeAllViews();
        this.rootView.removeAllViews();
        return this.state;
    }

    public PlatformView getView() {
        if (this.state.platformView == null) {
            return null;
        }
        return this.state.platformView;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class FakeWindowViewGroup extends ViewGroup {
        private final Rect childRect;
        private final Rect viewBounds;

        public FakeWindowViewGroup(Context context) {
            super(context);
            this.viewBounds = new Rect();
            this.childRect = new Rect();
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void onLayout(boolean changed, int l, int t, int r, int b) {
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                WindowManager.LayoutParams params = (WindowManager.LayoutParams) child.getLayoutParams();
                this.viewBounds.set(l, t, r, b);
                Gravity.apply(params.gravity, child.getMeasuredWidth(), child.getMeasuredHeight(), this.viewBounds, params.x, params.y, this.childRect);
                child.layout(this.childRect.left, this.childRect.top, this.childRect.right, this.childRect.bottom);
            }
        }

        @Override // android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                child.measure(atMost(widthMeasureSpec), atMost(heightMeasureSpec));
            }
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }

        private static int atMost(int measureSpec) {
            return View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(measureSpec), Integer.MIN_VALUE);
        }
    }

    /* loaded from: classes.dex */
    private static class ImmContext extends ContextWrapper {
        private final InputMethodManager inputMethodManager;

        ImmContext(Context base) {
            this(base, null);
        }

        private ImmContext(Context base, InputMethodManager inputMethodManager) {
            super(base);
            InputMethodManager inputMethodManager2;
            if (inputMethodManager != null) {
                inputMethodManager2 = inputMethodManager;
            } else {
                inputMethodManager2 = (InputMethodManager) base.getSystemService("input_method");
            }
            this.inputMethodManager = inputMethodManager2;
        }

        @Override // android.content.ContextWrapper, android.content.Context
        public Object getSystemService(String name) {
            if ("input_method".equals(name)) {
                return this.inputMethodManager;
            }
            return super.getSystemService(name);
        }

        @Override // android.content.ContextWrapper, android.content.Context
        public Context createDisplayContext(Display display) {
            Context displayContext = super.createDisplayContext(display);
            return new ImmContext(displayContext, this.inputMethodManager);
        }
    }

    /* loaded from: classes.dex */
    private static class PresentationContext extends ContextWrapper {
        private final Context flutterAppWindowContext;
        private WindowManager windowManager;
        private final WindowManagerHandler windowManagerHandler;

        PresentationContext(Context base, WindowManagerHandler windowManagerHandler, Context flutterAppWindowContext) {
            super(base);
            this.windowManagerHandler = windowManagerHandler;
            this.flutterAppWindowContext = flutterAppWindowContext;
        }

        @Override // android.content.ContextWrapper, android.content.Context
        public Object getSystemService(String name) {
            if ("window".equals(name)) {
                if (isCalledFromAlertDialog()) {
                    return this.flutterAppWindowContext.getSystemService(name);
                }
                return getWindowManager();
            }
            return super.getSystemService(name);
        }

        private WindowManager getWindowManager() {
            if (this.windowManager == null) {
                this.windowManager = this.windowManagerHandler.getWindowManager();
            }
            return this.windowManager;
        }

        private boolean isCalledFromAlertDialog() {
            StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
            for (int i = 0; i < stackTraceElements.length && i < 11; i++) {
                if (stackTraceElements[i].getClassName().equals(AlertDialog.class.getCanonicalName()) && stackTraceElements[i].getMethodName().equals("<init>")) {
                    return true;
                }
            }
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class WindowManagerHandler implements InvocationHandler {
        private static final String TAG = "PlatformViewsController";
        private final WindowManager delegate;
        FakeWindowViewGroup fakeWindowRootView;

        WindowManagerHandler(WindowManager delegate, FakeWindowViewGroup fakeWindowViewGroup) {
            this.delegate = delegate;
            this.fakeWindowRootView = fakeWindowViewGroup;
        }

        public WindowManager getWindowManager() {
            return (WindowManager) Proxy.newProxyInstance(WindowManager.class.getClassLoader(), new Class[]{WindowManager.class}, this);
        }

        /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
        @Override // java.lang.reflect.InvocationHandler
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            char c;
            String name = method.getName();
            switch (name.hashCode()) {
                case -1148522778:
                    if (name.equals("addView")) {
                        c = 0;
                        break;
                    }
                    c = 65535;
                    break;
                case 542766184:
                    if (name.equals("removeViewImmediate")) {
                        c = 2;
                        break;
                    }
                    c = 65535;
                    break;
                case 931413976:
                    if (name.equals("updateViewLayout")) {
                        c = 3;
                        break;
                    }
                    c = 65535;
                    break;
                case 1098630473:
                    if (name.equals("removeView")) {
                        c = 1;
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
                    addView(args);
                    return null;
                case 1:
                    removeView(args);
                    return null;
                case 2:
                    removeViewImmediate(args);
                    return null;
                case 3:
                    updateViewLayout(args);
                    return null;
                default:
                    try {
                        return method.invoke(this.delegate, args);
                    } catch (InvocationTargetException e) {
                        throw e.getCause();
                    }
            }
        }

        private void addView(Object[] args) {
            FakeWindowViewGroup fakeWindowViewGroup = this.fakeWindowRootView;
            if (fakeWindowViewGroup == null) {
                Log.w(TAG, "Embedded view called addView while detached from presentation");
                return;
            }
            View view = (View) args[0];
            WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) args[1];
            fakeWindowViewGroup.addView(view, layoutParams);
        }

        private void removeView(Object[] args) {
            FakeWindowViewGroup fakeWindowViewGroup = this.fakeWindowRootView;
            if (fakeWindowViewGroup == null) {
                Log.w(TAG, "Embedded view called removeView while detached from presentation");
                return;
            }
            View view = (View) args[0];
            fakeWindowViewGroup.removeView(view);
        }

        private void removeViewImmediate(Object[] args) {
            if (this.fakeWindowRootView == null) {
                Log.w(TAG, "Embedded view called removeViewImmediate while detached from presentation");
                return;
            }
            View view = (View) args[0];
            view.clearAnimation();
            this.fakeWindowRootView.removeView(view);
        }

        private void updateViewLayout(Object[] args) {
            FakeWindowViewGroup fakeWindowViewGroup = this.fakeWindowRootView;
            if (fakeWindowViewGroup == null) {
                Log.w(TAG, "Embedded view called updateViewLayout while detached from presentation");
                return;
            }
            View view = (View) args[0];
            WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) args[1];
            fakeWindowViewGroup.updateViewLayout(view, layoutParams);
        }
    }

    /* loaded from: classes.dex */
    private static class AccessibilityDelegatingFrameLayout extends FrameLayout {
        private final AccessibilityEventsDelegate accessibilityEventsDelegate;
        private final View embeddedView;

        public AccessibilityDelegatingFrameLayout(Context context, AccessibilityEventsDelegate accessibilityEventsDelegate, View embeddedView) {
            super(context);
            this.accessibilityEventsDelegate = accessibilityEventsDelegate;
            this.embeddedView = embeddedView;
        }

        @Override // android.view.ViewGroup, android.view.ViewParent
        public boolean requestSendAccessibilityEvent(View child, AccessibilityEvent event) {
            return this.accessibilityEventsDelegate.requestSendAccessibilityEvent(this.embeddedView, child, event);
        }
    }
}
