package androidx.core.view;

import android.os.Build;
import android.view.ViewGroup;
/* loaded from: classes.dex */
public final class MarginLayoutParamsCompat {
    public static int getMarginStart(ViewGroup.MarginLayoutParams marginLayoutParams) {
        if (Build.VERSION.SDK_INT >= 17) {
            return Api17Impl.getMarginStart(marginLayoutParams);
        }
        return marginLayoutParams.leftMargin;
    }

    public static int getMarginEnd(ViewGroup.MarginLayoutParams marginLayoutParams) {
        if (Build.VERSION.SDK_INT >= 17) {
            return Api17Impl.getMarginEnd(marginLayoutParams);
        }
        return marginLayoutParams.rightMargin;
    }

    public static void setMarginStart(ViewGroup.MarginLayoutParams marginLayoutParams, int i) {
        if (Build.VERSION.SDK_INT >= 17) {
            Api17Impl.setMarginStart(marginLayoutParams, i);
        } else {
            marginLayoutParams.leftMargin = i;
        }
    }

    public static void setMarginEnd(ViewGroup.MarginLayoutParams marginLayoutParams, int i) {
        if (Build.VERSION.SDK_INT >= 17) {
            Api17Impl.setMarginEnd(marginLayoutParams, i);
        } else {
            marginLayoutParams.rightMargin = i;
        }
    }

    public static boolean isMarginRelative(ViewGroup.MarginLayoutParams marginLayoutParams) {
        if (Build.VERSION.SDK_INT >= 17) {
            return Api17Impl.isMarginRelative(marginLayoutParams);
        }
        return false;
    }

    public static int getLayoutDirection(ViewGroup.MarginLayoutParams marginLayoutParams) {
        int layoutDirection = Build.VERSION.SDK_INT >= 17 ? Api17Impl.getLayoutDirection(marginLayoutParams) : 0;
        if (layoutDirection == 0 || layoutDirection == 1) {
            return layoutDirection;
        }
        return 0;
    }

    public static void setLayoutDirection(ViewGroup.MarginLayoutParams marginLayoutParams, int i) {
        if (Build.VERSION.SDK_INT >= 17) {
            Api17Impl.setLayoutDirection(marginLayoutParams, i);
        }
    }

    public static void resolveLayoutDirection(ViewGroup.MarginLayoutParams marginLayoutParams, int i) {
        if (Build.VERSION.SDK_INT >= 17) {
            Api17Impl.resolveLayoutDirection(marginLayoutParams, i);
        }
    }

    private MarginLayoutParamsCompat() {
    }

    /* loaded from: classes.dex */
    static class Api17Impl {
        private Api17Impl() {
        }

        static int getMarginStart(ViewGroup.MarginLayoutParams marginLayoutParams) {
            return marginLayoutParams.getMarginStart();
        }

        static int getMarginEnd(ViewGroup.MarginLayoutParams marginLayoutParams) {
            return marginLayoutParams.getMarginEnd();
        }

        static void setMarginStart(ViewGroup.MarginLayoutParams marginLayoutParams, int i) {
            marginLayoutParams.setMarginStart(i);
        }

        static void setMarginEnd(ViewGroup.MarginLayoutParams marginLayoutParams, int i) {
            marginLayoutParams.setMarginEnd(i);
        }

        static boolean isMarginRelative(ViewGroup.MarginLayoutParams marginLayoutParams) {
            return marginLayoutParams.isMarginRelative();
        }

        static int getLayoutDirection(ViewGroup.MarginLayoutParams marginLayoutParams) {
            return marginLayoutParams.getLayoutDirection();
        }

        static void setLayoutDirection(ViewGroup.MarginLayoutParams marginLayoutParams, int i) {
            marginLayoutParams.setLayoutDirection(i);
        }

        static void resolveLayoutDirection(ViewGroup.MarginLayoutParams marginLayoutParams, int i) {
            marginLayoutParams.resolveLayoutDirection(i);
        }
    }
}
