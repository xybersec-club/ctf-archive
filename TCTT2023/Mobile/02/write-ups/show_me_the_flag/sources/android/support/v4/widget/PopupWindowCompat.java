package android.support.v4.widget;

import android.os.Build;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.View;
import android.widget.PopupWindow;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
/* loaded from: classes.dex */
public final class PopupWindowCompat {
    static final PopupWindowCompatBaseImpl IMPL;

    /* loaded from: classes.dex */
    static class PopupWindowCompatBaseImpl {
        private static Method sGetWindowLayoutTypeMethod;
        private static boolean sGetWindowLayoutTypeMethodAttempted;
        private static Method sSetWindowLayoutTypeMethod;
        private static boolean sSetWindowLayoutTypeMethodAttempted;

        public boolean getOverlapAnchor(PopupWindow popupWindow) {
            return false;
        }

        public void setOverlapAnchor(PopupWindow popupWindow, boolean z) {
        }

        PopupWindowCompatBaseImpl() {
        }

        public void showAsDropDown(PopupWindow popupWindow, View view, int i, int i2, int i3) {
            if ((GravityCompat.getAbsoluteGravity(i3, ViewCompat.getLayoutDirection(view)) & 7) == 5) {
                i -= popupWindow.getWidth() - view.getWidth();
            }
            popupWindow.showAsDropDown(view, i, i2);
        }

        public void setWindowLayoutType(PopupWindow popupWindow, int i) {
            if (!sSetWindowLayoutTypeMethodAttempted) {
                try {
                    sSetWindowLayoutTypeMethod = PopupWindow.class.getDeclaredMethod("setWindowLayoutType", Integer.TYPE);
                    sSetWindowLayoutTypeMethod.setAccessible(true);
                } catch (Exception unused) {
                }
                sSetWindowLayoutTypeMethodAttempted = true;
            }
            Method method = sSetWindowLayoutTypeMethod;
            if (method != null) {
                try {
                    method.invoke(popupWindow, Integer.valueOf(i));
                } catch (Exception unused2) {
                }
            }
        }

        public int getWindowLayoutType(PopupWindow popupWindow) {
            if (!sGetWindowLayoutTypeMethodAttempted) {
                try {
                    sGetWindowLayoutTypeMethod = PopupWindow.class.getDeclaredMethod("getWindowLayoutType", new Class[0]);
                    sGetWindowLayoutTypeMethod.setAccessible(true);
                } catch (Exception unused) {
                }
                sGetWindowLayoutTypeMethodAttempted = true;
            }
            Method method = sGetWindowLayoutTypeMethod;
            if (method != null) {
                try {
                    return ((Integer) method.invoke(popupWindow, new Object[0])).intValue();
                } catch (Exception unused2) {
                }
            }
            return 0;
        }
    }

    /* loaded from: classes.dex */
    static class PopupWindowCompatApi19Impl extends PopupWindowCompatBaseImpl {
        PopupWindowCompatApi19Impl() {
        }

        @Override // android.support.v4.widget.PopupWindowCompat.PopupWindowCompatBaseImpl
        public void showAsDropDown(PopupWindow popupWindow, View view, int i, int i2, int i3) {
            popupWindow.showAsDropDown(view, i, i2, i3);
        }
    }

    /* loaded from: classes.dex */
    static class PopupWindowCompatApi21Impl extends PopupWindowCompatApi19Impl {
        private static final String TAG = "PopupWindowCompatApi21";
        private static Field sOverlapAnchorField;

        PopupWindowCompatApi21Impl() {
        }

        static {
            try {
                sOverlapAnchorField = PopupWindow.class.getDeclaredField("mOverlapAnchor");
                sOverlapAnchorField.setAccessible(true);
            } catch (NoSuchFieldException e) {
                Log.i(TAG, "Could not fetch mOverlapAnchor field from PopupWindow", e);
            }
        }

        @Override // android.support.v4.widget.PopupWindowCompat.PopupWindowCompatBaseImpl
        public void setOverlapAnchor(PopupWindow popupWindow, boolean z) {
            Field field = sOverlapAnchorField;
            if (field != null) {
                try {
                    field.set(popupWindow, Boolean.valueOf(z));
                } catch (IllegalAccessException e) {
                    Log.i(TAG, "Could not set overlap anchor field in PopupWindow", e);
                }
            }
        }

        @Override // android.support.v4.widget.PopupWindowCompat.PopupWindowCompatBaseImpl
        public boolean getOverlapAnchor(PopupWindow popupWindow) {
            Field field = sOverlapAnchorField;
            if (field != null) {
                try {
                    return ((Boolean) field.get(popupWindow)).booleanValue();
                } catch (IllegalAccessException e) {
                    Log.i(TAG, "Could not get overlap anchor field in PopupWindow", e);
                    return false;
                }
            }
            return false;
        }
    }

    /* loaded from: classes.dex */
    static class PopupWindowCompatApi23Impl extends PopupWindowCompatApi21Impl {
        PopupWindowCompatApi23Impl() {
        }

        @Override // android.support.v4.widget.PopupWindowCompat.PopupWindowCompatApi21Impl, android.support.v4.widget.PopupWindowCompat.PopupWindowCompatBaseImpl
        public void setOverlapAnchor(PopupWindow popupWindow, boolean z) {
            popupWindow.setOverlapAnchor(z);
        }

        @Override // android.support.v4.widget.PopupWindowCompat.PopupWindowCompatApi21Impl, android.support.v4.widget.PopupWindowCompat.PopupWindowCompatBaseImpl
        public boolean getOverlapAnchor(PopupWindow popupWindow) {
            return popupWindow.getOverlapAnchor();
        }

        @Override // android.support.v4.widget.PopupWindowCompat.PopupWindowCompatBaseImpl
        public void setWindowLayoutType(PopupWindow popupWindow, int i) {
            popupWindow.setWindowLayoutType(i);
        }

        @Override // android.support.v4.widget.PopupWindowCompat.PopupWindowCompatBaseImpl
        public int getWindowLayoutType(PopupWindow popupWindow) {
            return popupWindow.getWindowLayoutType();
        }
    }

    static {
        if (Build.VERSION.SDK_INT >= 23) {
            IMPL = new PopupWindowCompatApi23Impl();
        } else if (Build.VERSION.SDK_INT >= 21) {
            IMPL = new PopupWindowCompatApi21Impl();
        } else if (Build.VERSION.SDK_INT >= 19) {
            IMPL = new PopupWindowCompatApi19Impl();
        } else {
            IMPL = new PopupWindowCompatBaseImpl();
        }
    }

    private PopupWindowCompat() {
    }

    public static void showAsDropDown(PopupWindow popupWindow, View view, int i, int i2, int i3) {
        IMPL.showAsDropDown(popupWindow, view, i, i2, i3);
    }

    public static void setOverlapAnchor(PopupWindow popupWindow, boolean z) {
        IMPL.setOverlapAnchor(popupWindow, z);
    }

    public static boolean getOverlapAnchor(PopupWindow popupWindow) {
        return IMPL.getOverlapAnchor(popupWindow);
    }

    public static void setWindowLayoutType(PopupWindow popupWindow, int i) {
        IMPL.setWindowLayoutType(popupWindow, i);
    }

    public static int getWindowLayoutType(PopupWindow popupWindow) {
        return IMPL.getWindowLayoutType(popupWindow);
    }
}
