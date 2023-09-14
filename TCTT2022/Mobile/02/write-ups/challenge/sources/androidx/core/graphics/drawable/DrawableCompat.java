package androidx.core.graphics.drawable;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableContainer;
import android.graphics.drawable.InsetDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import java.io.IOException;
import java.lang.reflect.Method;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public final class DrawableCompat {
    private static final String TAG = "DrawableCompat";
    private static Method sGetLayoutDirectionMethod;
    private static boolean sGetLayoutDirectionMethodFetched;
    private static Method sSetLayoutDirectionMethod;
    private static boolean sSetLayoutDirectionMethodFetched;

    @Deprecated
    public static void jumpToCurrentState(Drawable drawable) {
        drawable.jumpToCurrentState();
    }

    public static void setAutoMirrored(Drawable drawable, boolean z) {
        if (Build.VERSION.SDK_INT >= 19) {
            Api19Impl.setAutoMirrored(drawable, z);
        }
    }

    public static boolean isAutoMirrored(Drawable drawable) {
        if (Build.VERSION.SDK_INT >= 19) {
            return Api19Impl.isAutoMirrored(drawable);
        }
        return false;
    }

    public static void setHotspot(Drawable drawable, float f, float f2) {
        if (Build.VERSION.SDK_INT >= 21) {
            Api21Impl.setHotspot(drawable, f, f2);
        }
    }

    public static void setHotspotBounds(Drawable drawable, int i, int i2, int i3, int i4) {
        if (Build.VERSION.SDK_INT >= 21) {
            Api21Impl.setHotspotBounds(drawable, i, i2, i3, i4);
        }
    }

    public static void setTint(Drawable drawable, int i) {
        if (Build.VERSION.SDK_INT >= 21) {
            Api21Impl.setTint(drawable, i);
        } else if (drawable instanceof TintAwareDrawable) {
            ((TintAwareDrawable) drawable).setTint(i);
        }
    }

    public static void setTintList(Drawable drawable, ColorStateList colorStateList) {
        if (Build.VERSION.SDK_INT >= 21) {
            Api21Impl.setTintList(drawable, colorStateList);
        } else if (drawable instanceof TintAwareDrawable) {
            ((TintAwareDrawable) drawable).setTintList(colorStateList);
        }
    }

    public static void setTintMode(Drawable drawable, PorterDuff.Mode mode) {
        if (Build.VERSION.SDK_INT >= 21) {
            Api21Impl.setTintMode(drawable, mode);
        } else if (drawable instanceof TintAwareDrawable) {
            ((TintAwareDrawable) drawable).setTintMode(mode);
        }
    }

    public static int getAlpha(Drawable drawable) {
        if (Build.VERSION.SDK_INT >= 19) {
            return Api19Impl.getAlpha(drawable);
        }
        return 0;
    }

    public static void applyTheme(Drawable drawable, Resources.Theme theme) {
        if (Build.VERSION.SDK_INT >= 21) {
            Api21Impl.applyTheme(drawable, theme);
        }
    }

    public static boolean canApplyTheme(Drawable drawable) {
        if (Build.VERSION.SDK_INT >= 21) {
            return Api21Impl.canApplyTheme(drawable);
        }
        return false;
    }

    public static ColorFilter getColorFilter(Drawable drawable) {
        if (Build.VERSION.SDK_INT >= 21) {
            return Api21Impl.getColorFilter(drawable);
        }
        return null;
    }

    public static void clearColorFilter(Drawable drawable) {
        DrawableContainer.DrawableContainerState drawableContainerState;
        if (Build.VERSION.SDK_INT >= 23) {
            drawable.clearColorFilter();
        } else if (Build.VERSION.SDK_INT >= 21) {
            drawable.clearColorFilter();
            if (drawable instanceof InsetDrawable) {
                clearColorFilter(Api19Impl.getDrawable((InsetDrawable) drawable));
            } else if (drawable instanceof WrappedDrawable) {
                clearColorFilter(((WrappedDrawable) drawable).getWrappedDrawable());
            } else if ((drawable instanceof DrawableContainer) && (drawableContainerState = (DrawableContainer.DrawableContainerState) ((DrawableContainer) drawable).getConstantState()) != null) {
                int childCount = drawableContainerState.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    Drawable child = Api19Impl.getChild(drawableContainerState, i);
                    if (child != null) {
                        clearColorFilter(child);
                    }
                }
            }
        } else {
            drawable.clearColorFilter();
        }
    }

    public static void inflate(Drawable drawable, Resources resources, XmlPullParser xmlPullParser, AttributeSet attributeSet, Resources.Theme theme) throws XmlPullParserException, IOException {
        if (Build.VERSION.SDK_INT >= 21) {
            Api21Impl.inflate(drawable, resources, xmlPullParser, attributeSet, theme);
        } else {
            drawable.inflate(resources, xmlPullParser, attributeSet);
        }
    }

    public static Drawable wrap(Drawable drawable) {
        return Build.VERSION.SDK_INT >= 23 ? drawable : Build.VERSION.SDK_INT >= 21 ? !(drawable instanceof TintAwareDrawable) ? new WrappedDrawableApi21(drawable) : drawable : !(drawable instanceof TintAwareDrawable) ? new WrappedDrawableApi14(drawable) : drawable;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static <T extends Drawable> T unwrap(Drawable drawable) {
        return drawable instanceof WrappedDrawable ? (T) ((WrappedDrawable) drawable).getWrappedDrawable() : drawable;
    }

    public static boolean setLayoutDirection(Drawable drawable, int i) {
        if (Build.VERSION.SDK_INT >= 23) {
            return Api23Impl.setLayoutDirection(drawable, i);
        }
        if (Build.VERSION.SDK_INT >= 17) {
            if (!sSetLayoutDirectionMethodFetched) {
                try {
                    Method declaredMethod = Drawable.class.getDeclaredMethod("setLayoutDirection", Integer.TYPE);
                    sSetLayoutDirectionMethod = declaredMethod;
                    declaredMethod.setAccessible(true);
                } catch (NoSuchMethodException e) {
                    Log.i(TAG, "Failed to retrieve setLayoutDirection(int) method", e);
                }
                sSetLayoutDirectionMethodFetched = true;
            }
            Method method = sSetLayoutDirectionMethod;
            if (method != null) {
                try {
                    method.invoke(drawable, Integer.valueOf(i));
                    return true;
                } catch (Exception e2) {
                    Log.i(TAG, "Failed to invoke setLayoutDirection(int) via reflection", e2);
                    sSetLayoutDirectionMethod = null;
                }
            }
        }
        return false;
    }

    public static int getLayoutDirection(Drawable drawable) {
        if (Build.VERSION.SDK_INT >= 23) {
            return Api23Impl.getLayoutDirection(drawable);
        }
        if (Build.VERSION.SDK_INT >= 17) {
            if (!sGetLayoutDirectionMethodFetched) {
                try {
                    Method declaredMethod = Drawable.class.getDeclaredMethod("getLayoutDirection", new Class[0]);
                    sGetLayoutDirectionMethod = declaredMethod;
                    declaredMethod.setAccessible(true);
                } catch (NoSuchMethodException e) {
                    Log.i(TAG, "Failed to retrieve getLayoutDirection() method", e);
                }
                sGetLayoutDirectionMethodFetched = true;
            }
            Method method = sGetLayoutDirectionMethod;
            if (method != null) {
                try {
                    return ((Integer) method.invoke(drawable, new Object[0])).intValue();
                } catch (Exception e2) {
                    Log.i(TAG, "Failed to invoke getLayoutDirection() via reflection", e2);
                    sGetLayoutDirectionMethod = null;
                }
            }
        }
        return 0;
    }

    private DrawableCompat() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class Api19Impl {
        private Api19Impl() {
        }

        static void setAutoMirrored(Drawable drawable, boolean z) {
            drawable.setAutoMirrored(z);
        }

        static boolean isAutoMirrored(Drawable drawable) {
            return drawable.isAutoMirrored();
        }

        static int getAlpha(Drawable drawable) {
            return drawable.getAlpha();
        }

        static Drawable getChild(DrawableContainer.DrawableContainerState drawableContainerState, int i) {
            return drawableContainerState.getChild(i);
        }

        static Drawable getDrawable(InsetDrawable insetDrawable) {
            return insetDrawable.getDrawable();
        }
    }

    /* loaded from: classes.dex */
    static class Api21Impl {
        private Api21Impl() {
        }

        static void setHotspot(Drawable drawable, float f, float f2) {
            drawable.setHotspot(f, f2);
        }

        static void setTint(Drawable drawable, int i) {
            drawable.setTint(i);
        }

        static void setTintList(Drawable drawable, ColorStateList colorStateList) {
            drawable.setTintList(colorStateList);
        }

        static void setTintMode(Drawable drawable, PorterDuff.Mode mode) {
            drawable.setTintMode(mode);
        }

        static void applyTheme(Drawable drawable, Resources.Theme theme) {
            drawable.applyTheme(theme);
        }

        static boolean canApplyTheme(Drawable drawable) {
            return drawable.canApplyTheme();
        }

        static ColorFilter getColorFilter(Drawable drawable) {
            return drawable.getColorFilter();
        }

        static void inflate(Drawable drawable, Resources resources, XmlPullParser xmlPullParser, AttributeSet attributeSet, Resources.Theme theme) throws XmlPullParserException, IOException {
            drawable.inflate(resources, xmlPullParser, attributeSet, theme);
        }

        static void setHotspotBounds(Drawable drawable, int i, int i2, int i3, int i4) {
            drawable.setHotspotBounds(i, i2, i3, i4);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class Api23Impl {
        private Api23Impl() {
        }

        static boolean setLayoutDirection(Drawable drawable, int i) {
            return drawable.setLayoutDirection(i);
        }

        static int getLayoutDirection(Drawable drawable) {
            return drawable.getLayoutDirection();
        }
    }
}
