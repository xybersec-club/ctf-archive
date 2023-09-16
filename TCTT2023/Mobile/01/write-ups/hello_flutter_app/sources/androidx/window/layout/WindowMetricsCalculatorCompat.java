package androidx.window.layout;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.util.Log;
import android.view.Display;
import android.view.DisplayCutout;
import android.view.WindowManager;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
/* compiled from: WindowMetricsCalculatorCompat.kt */
@Metadata(d1 = {"\u0000P\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\bÀ\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u0010\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\bH\u0016J\u0010\u0010\t\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\bH\u0016J\u0015\u0010\n\u001a\u00020\u000b2\u0006\u0010\u0007\u001a\u00020\bH\u0001¢\u0006\u0002\b\fJ\u0015\u0010\r\u001a\u00020\u000b2\u0006\u0010\u0007\u001a\u00020\bH\u0001¢\u0006\u0002\b\u000eJ\u0015\u0010\u000f\u001a\u00020\u000b2\u0006\u0010\u0007\u001a\u00020\bH\u0001¢\u0006\u0002\b\u0010J\u0015\u0010\u0011\u001a\u00020\u000b2\u0006\u0010\u0007\u001a\u00020\bH\u0001¢\u0006\u0002\b\u0012J\u0012\u0010\u0013\u001a\u0004\u0018\u00010\u00142\u0006\u0010\u0015\u001a\u00020\u0016H\u0003J\u0010\u0010\u0017\u001a\u00020\u00182\u0006\u0010\u0019\u001a\u00020\u001aH\u0002J\u0015\u0010\u001b\u001a\u00020\u001c2\u0006\u0010\u0015\u001a\u00020\u0016H\u0001¢\u0006\u0002\b\u001dJ\u0018\u0010\u001e\u001a\u00020\u001f2\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010 \u001a\u00020\u000bH\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006!"}, d2 = {"Landroidx/window/layout/WindowMetricsCalculatorCompat;", "Landroidx/window/layout/WindowMetricsCalculator;", "()V", "TAG", "", "computeCurrentWindowMetrics", "Landroidx/window/layout/WindowMetrics;", "activity", "Landroid/app/Activity;", "computeMaximumWindowMetrics", "computeWindowBoundsIceCreamSandwich", "Landroid/graphics/Rect;", "computeWindowBoundsIceCreamSandwich$window_release", "computeWindowBoundsN", "computeWindowBoundsN$window_release", "computeWindowBoundsP", "computeWindowBoundsP$window_release", "computeWindowBoundsQ", "computeWindowBoundsQ$window_release", "getCutoutForDisplay", "Landroid/view/DisplayCutout;", "display", "Landroid/view/Display;", "getNavigationBarHeight", "", "context", "Landroid/content/Context;", "getRealSizeForDisplay", "Landroid/graphics/Point;", "getRealSizeForDisplay$window_release", "getRectSizeFromDisplay", "", "bounds", "window_release"}, k = 1, mv = {1, 5, 1}, xi = 48)
/* loaded from: classes.dex */
public final class WindowMetricsCalculatorCompat implements WindowMetricsCalculator {
    public static final WindowMetricsCalculatorCompat INSTANCE = new WindowMetricsCalculatorCompat();
    private static final String TAG;

    private WindowMetricsCalculatorCompat() {
    }

    static {
        String simpleName = WindowMetricsCalculatorCompat.class.getSimpleName();
        Intrinsics.checkNotNullExpressionValue(simpleName, "WindowMetricsCalculatorC…at::class.java.simpleName");
        TAG = simpleName;
    }

    @Override // androidx.window.layout.WindowMetricsCalculator
    public WindowMetrics computeCurrentWindowMetrics(Activity activity) {
        Rect bounds;
        Intrinsics.checkNotNullParameter(activity, "activity");
        if (Build.VERSION.SDK_INT >= 30) {
            bounds = ActivityCompatHelperApi30.INSTANCE.currentWindowBounds(activity);
        } else if (Build.VERSION.SDK_INT >= 29) {
            bounds = computeWindowBoundsQ$window_release(activity);
        } else if (Build.VERSION.SDK_INT >= 28) {
            bounds = computeWindowBoundsP$window_release(activity);
        } else if (Build.VERSION.SDK_INT >= 24) {
            bounds = computeWindowBoundsN$window_release(activity);
        } else {
            bounds = computeWindowBoundsIceCreamSandwich$window_release(activity);
        }
        return new WindowMetrics(bounds);
    }

    @Override // androidx.window.layout.WindowMetricsCalculator
    public WindowMetrics computeMaximumWindowMetrics(Activity activity) {
        Rect bounds;
        Intrinsics.checkNotNullParameter(activity, "activity");
        if (Build.VERSION.SDK_INT >= 30) {
            bounds = ActivityCompatHelperApi30.INSTANCE.maximumWindowBounds(activity);
        } else {
            Display display = activity.getWindowManager().getDefaultDisplay();
            Intrinsics.checkNotNullExpressionValue(display, "display");
            Point displaySize = getRealSizeForDisplay$window_release(display);
            bounds = new Rect(0, 0, displaySize.x, displaySize.y);
        }
        return new WindowMetrics(bounds);
    }

    public final Rect computeWindowBoundsQ$window_release(Activity activity) {
        Intrinsics.checkNotNullParameter(activity, "activity");
        Configuration config = activity.getResources().getConfiguration();
        try {
            Field windowConfigField = Configuration.class.getDeclaredField("windowConfiguration");
            windowConfigField.setAccessible(true);
            Object windowConfig = windowConfigField.get(config);
            Method getBoundsMethod = windowConfig.getClass().getDeclaredMethod("getBounds", new Class[0]);
            Object invoke = getBoundsMethod.invoke(windowConfig, new Object[0]);
            if (invoke != null) {
                Rect bounds = new Rect((Rect) invoke);
                return bounds;
            }
            throw new NullPointerException("null cannot be cast to non-null type android.graphics.Rect");
        } catch (IllegalAccessException e) {
            Log.w(TAG, e);
            Rect bounds2 = computeWindowBoundsP$window_release(activity);
            return bounds2;
        } catch (NoSuchFieldException e2) {
            Log.w(TAG, e2);
            Rect bounds3 = computeWindowBoundsP$window_release(activity);
            return bounds3;
        } catch (NoSuchMethodException e3) {
            Log.w(TAG, e3);
            Rect bounds4 = computeWindowBoundsP$window_release(activity);
            return bounds4;
        } catch (InvocationTargetException e4) {
            Log.w(TAG, e4);
            Rect bounds5 = computeWindowBoundsP$window_release(activity);
            return bounds5;
        }
    }

    public final Rect computeWindowBoundsP$window_release(Activity activity) {
        DisplayCutout displayCutout;
        Intrinsics.checkNotNullParameter(activity, "activity");
        Rect bounds = new Rect();
        Configuration config = activity.getResources().getConfiguration();
        try {
            Field windowConfigField = Configuration.class.getDeclaredField("windowConfiguration");
            windowConfigField.setAccessible(true);
            Object windowConfig = windowConfigField.get(config);
            if (ActivityCompatHelperApi24.INSTANCE.isInMultiWindowMode(activity)) {
                Method getAppBounds = windowConfig.getClass().getDeclaredMethod("getBounds", new Class[0]);
                Object invoke = getAppBounds.invoke(windowConfig, new Object[0]);
                if (invoke == null) {
                    throw new NullPointerException("null cannot be cast to non-null type android.graphics.Rect");
                }
                bounds.set((Rect) invoke);
            } else {
                Method getAppBounds2 = windowConfig.getClass().getDeclaredMethod("getAppBounds", new Class[0]);
                Object invoke2 = getAppBounds2.invoke(windowConfig, new Object[0]);
                if (invoke2 == null) {
                    throw new NullPointerException("null cannot be cast to non-null type android.graphics.Rect");
                }
                bounds.set((Rect) invoke2);
            }
        } catch (IllegalAccessException e) {
            Log.w(TAG, e);
            getRectSizeFromDisplay(activity, bounds);
        } catch (NoSuchFieldException e2) {
            Log.w(TAG, e2);
            getRectSizeFromDisplay(activity, bounds);
        } catch (NoSuchMethodException e3) {
            Log.w(TAG, e3);
            getRectSizeFromDisplay(activity, bounds);
        } catch (InvocationTargetException e4) {
            Log.w(TAG, e4);
            getRectSizeFromDisplay(activity, bounds);
        }
        WindowManager platformWindowManager = activity.getWindowManager();
        Display currentDisplay = platformWindowManager.getDefaultDisplay();
        Point realDisplaySize = new Point();
        DisplayCompatHelperApi17 displayCompatHelperApi17 = DisplayCompatHelperApi17.INSTANCE;
        Intrinsics.checkNotNullExpressionValue(currentDisplay, "currentDisplay");
        displayCompatHelperApi17.getRealSize(currentDisplay, realDisplaySize);
        if (!ActivityCompatHelperApi24.INSTANCE.isInMultiWindowMode(activity)) {
            int navigationBarHeight = getNavigationBarHeight(activity);
            if (bounds.bottom + navigationBarHeight == realDisplaySize.y) {
                bounds.bottom += navigationBarHeight;
            } else if (bounds.right + navigationBarHeight == realDisplaySize.x) {
                bounds.right += navigationBarHeight;
            } else if (bounds.left == navigationBarHeight) {
                bounds.left = 0;
            }
        }
        if ((bounds.width() < realDisplaySize.x || bounds.height() < realDisplaySize.y) && !ActivityCompatHelperApi24.INSTANCE.isInMultiWindowMode(activity) && (displayCutout = getCutoutForDisplay(currentDisplay)) != null) {
            if (bounds.left == DisplayCompatHelperApi28.INSTANCE.safeInsetLeft(displayCutout)) {
                bounds.left = 0;
            }
            if (realDisplaySize.x - bounds.right == DisplayCompatHelperApi28.INSTANCE.safeInsetRight(displayCutout)) {
                bounds.right += DisplayCompatHelperApi28.INSTANCE.safeInsetRight(displayCutout);
            }
            if (bounds.top == DisplayCompatHelperApi28.INSTANCE.safeInsetTop(displayCutout)) {
                bounds.top = 0;
            }
            if (realDisplaySize.y - bounds.bottom == DisplayCompatHelperApi28.INSTANCE.safeInsetBottom(displayCutout)) {
                bounds.bottom += DisplayCompatHelperApi28.INSTANCE.safeInsetBottom(displayCutout);
            }
        }
        return bounds;
    }

    private final void getRectSizeFromDisplay(Activity activity, Rect bounds) {
        Display defaultDisplay = activity.getWindowManager().getDefaultDisplay();
        defaultDisplay.getRectSize(bounds);
    }

    public final Rect computeWindowBoundsN$window_release(Activity activity) {
        Intrinsics.checkNotNullParameter(activity, "activity");
        Rect bounds = new Rect();
        Display defaultDisplay = activity.getWindowManager().getDefaultDisplay();
        defaultDisplay.getRectSize(bounds);
        if (!ActivityCompatHelperApi24.INSTANCE.isInMultiWindowMode(activity)) {
            Intrinsics.checkNotNullExpressionValue(defaultDisplay, "defaultDisplay");
            Point realDisplaySize = getRealSizeForDisplay$window_release(defaultDisplay);
            int navigationBarHeight = getNavigationBarHeight(activity);
            if (bounds.bottom + navigationBarHeight == realDisplaySize.y) {
                bounds.bottom += navigationBarHeight;
            } else if (bounds.right + navigationBarHeight == realDisplaySize.x) {
                bounds.right += navigationBarHeight;
            }
        }
        return bounds;
    }

    public final Rect computeWindowBoundsIceCreamSandwich$window_release(Activity activity) {
        Intrinsics.checkNotNullParameter(activity, "activity");
        Display defaultDisplay = activity.getWindowManager().getDefaultDisplay();
        Intrinsics.checkNotNullExpressionValue(defaultDisplay, "defaultDisplay");
        Point realDisplaySize = getRealSizeForDisplay$window_release(defaultDisplay);
        Rect bounds = new Rect();
        if (realDisplaySize.x == 0 || realDisplaySize.y == 0) {
            defaultDisplay.getRectSize(bounds);
        } else {
            bounds.right = realDisplaySize.x;
            bounds.bottom = realDisplaySize.y;
        }
        return bounds;
    }

    public final Point getRealSizeForDisplay$window_release(Display display) {
        Intrinsics.checkNotNullParameter(display, "display");
        Point size = new Point();
        if (Build.VERSION.SDK_INT >= 17) {
            DisplayCompatHelperApi17.INSTANCE.getRealSize(display, size);
        } else {
            try {
                Method getRealSizeMethod = Display.class.getDeclaredMethod("getRealSize", Point.class);
                getRealSizeMethod.setAccessible(true);
                getRealSizeMethod.invoke(display, size);
            } catch (IllegalAccessException e) {
                Log.w(TAG, e);
            } catch (NoSuchMethodException e2) {
                Log.w(TAG, e2);
            } catch (InvocationTargetException e3) {
                Log.w(TAG, e3);
            }
        }
        return size;
    }

    private final int getNavigationBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    private final DisplayCutout getCutoutForDisplay(Display display) {
        try {
            Class displayInfoClass = Class.forName("android.view.DisplayInfo");
            Constructor displayInfoConstructor = displayInfoClass.getConstructor(new Class[0]);
            displayInfoConstructor.setAccessible(true);
            Object displayInfo = displayInfoConstructor.newInstance(new Object[0]);
            Method getDisplayInfoMethod = display.getClass().getDeclaredMethod("getDisplayInfo", displayInfo.getClass());
            getDisplayInfoMethod.setAccessible(true);
            getDisplayInfoMethod.invoke(display, displayInfo);
            Field displayCutoutField = displayInfo.getClass().getDeclaredField("displayCutout");
            displayCutoutField.setAccessible(true);
            Object cutout = displayCutoutField.get(displayInfo);
            if (!(cutout instanceof DisplayCutout)) {
                return null;
            }
            DisplayCutout displayCutout = (DisplayCutout) cutout;
            return displayCutout;
        } catch (ClassNotFoundException e) {
            Log.w(TAG, e);
            return null;
        } catch (IllegalAccessException e2) {
            Log.w(TAG, e2);
            return null;
        } catch (InstantiationException e3) {
            Log.w(TAG, e3);
            return null;
        } catch (NoSuchFieldException e4) {
            Log.w(TAG, e4);
            return null;
        } catch (NoSuchMethodException e5) {
            Log.w(TAG, e5);
            return null;
        } catch (InvocationTargetException e6) {
            Log.w(TAG, e6);
            return null;
        }
    }
}
