package androidx.core.view.accessibility;

import android.graphics.Rect;
import android.os.Build;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityWindowInfo;
/* loaded from: classes.dex */
public class AccessibilityWindowInfoCompat {
    public static final int TYPE_ACCESSIBILITY_OVERLAY = 4;
    public static final int TYPE_APPLICATION = 1;
    public static final int TYPE_INPUT_METHOD = 2;
    public static final int TYPE_SPLIT_SCREEN_DIVIDER = 5;
    public static final int TYPE_SYSTEM = 3;
    private static final int UNDEFINED = -1;
    private final Object mInfo;

    private static String typeToString(int i) {
        return i != 1 ? i != 2 ? i != 3 ? i != 4 ? "<UNKNOWN>" : "TYPE_ACCESSIBILITY_OVERLAY" : "TYPE_SYSTEM" : "TYPE_INPUT_METHOD" : "TYPE_APPLICATION";
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static AccessibilityWindowInfoCompat wrapNonNullInstance(Object obj) {
        if (obj != null) {
            return new AccessibilityWindowInfoCompat(obj);
        }
        return null;
    }

    private AccessibilityWindowInfoCompat(Object obj) {
        this.mInfo = obj;
    }

    public int getType() {
        if (Build.VERSION.SDK_INT >= 21) {
            return Api21Impl.getType((AccessibilityWindowInfo) this.mInfo);
        }
        return -1;
    }

    public int getLayer() {
        if (Build.VERSION.SDK_INT >= 21) {
            return Api21Impl.getLayer((AccessibilityWindowInfo) this.mInfo);
        }
        return -1;
    }

    public AccessibilityNodeInfoCompat getRoot() {
        if (Build.VERSION.SDK_INT >= 21) {
            return AccessibilityNodeInfoCompat.wrapNonNullInstance(Api21Impl.getRoot((AccessibilityWindowInfo) this.mInfo));
        }
        return null;
    }

    public AccessibilityWindowInfoCompat getParent() {
        if (Build.VERSION.SDK_INT >= 21) {
            return wrapNonNullInstance(Api21Impl.getParent((AccessibilityWindowInfo) this.mInfo));
        }
        return null;
    }

    public int getId() {
        if (Build.VERSION.SDK_INT >= 21) {
            return Api21Impl.getId((AccessibilityWindowInfo) this.mInfo);
        }
        return -1;
    }

    public void getBoundsInScreen(Rect rect) {
        if (Build.VERSION.SDK_INT >= 21) {
            Api21Impl.getBoundsInScreen((AccessibilityWindowInfo) this.mInfo, rect);
        }
    }

    public boolean isActive() {
        if (Build.VERSION.SDK_INT >= 21) {
            return Api21Impl.isActive((AccessibilityWindowInfo) this.mInfo);
        }
        return true;
    }

    public boolean isFocused() {
        if (Build.VERSION.SDK_INT >= 21) {
            return Api21Impl.isFocused((AccessibilityWindowInfo) this.mInfo);
        }
        return true;
    }

    public boolean isAccessibilityFocused() {
        if (Build.VERSION.SDK_INT >= 21) {
            return Api21Impl.isAccessibilityFocused((AccessibilityWindowInfo) this.mInfo);
        }
        return true;
    }

    public int getChildCount() {
        if (Build.VERSION.SDK_INT >= 21) {
            return Api21Impl.getChildCount((AccessibilityWindowInfo) this.mInfo);
        }
        return 0;
    }

    public AccessibilityWindowInfoCompat getChild(int i) {
        if (Build.VERSION.SDK_INT >= 21) {
            return wrapNonNullInstance(Api21Impl.getChild((AccessibilityWindowInfo) this.mInfo, i));
        }
        return null;
    }

    public CharSequence getTitle() {
        if (Build.VERSION.SDK_INT >= 24) {
            return Api24Impl.getTitle((AccessibilityWindowInfo) this.mInfo);
        }
        return null;
    }

    public AccessibilityNodeInfoCompat getAnchor() {
        if (Build.VERSION.SDK_INT >= 24) {
            return AccessibilityNodeInfoCompat.wrapNonNullInstance(Api24Impl.getAnchor((AccessibilityWindowInfo) this.mInfo));
        }
        return null;
    }

    public static AccessibilityWindowInfoCompat obtain() {
        if (Build.VERSION.SDK_INT >= 21) {
            return wrapNonNullInstance(Api21Impl.obtain());
        }
        return null;
    }

    public static AccessibilityWindowInfoCompat obtain(AccessibilityWindowInfoCompat accessibilityWindowInfoCompat) {
        if (Build.VERSION.SDK_INT < 21 || accessibilityWindowInfoCompat == null) {
            return null;
        }
        return wrapNonNullInstance(Api21Impl.obtain((AccessibilityWindowInfo) accessibilityWindowInfoCompat.mInfo));
    }

    public void recycle() {
        if (Build.VERSION.SDK_INT >= 21) {
            Api21Impl.recycle((AccessibilityWindowInfo) this.mInfo);
        }
    }

    public int hashCode() {
        Object obj = this.mInfo;
        if (obj == null) {
            return 0;
        }
        return obj.hashCode();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj != null && (obj instanceof AccessibilityWindowInfoCompat)) {
            AccessibilityWindowInfoCompat accessibilityWindowInfoCompat = (AccessibilityWindowInfoCompat) obj;
            Object obj2 = this.mInfo;
            if (obj2 == null) {
                return accessibilityWindowInfoCompat.mInfo == null;
            }
            return obj2.equals(accessibilityWindowInfoCompat.mInfo);
        }
        return false;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        Rect rect = new Rect();
        getBoundsInScreen(rect);
        sb.append("AccessibilityWindowInfo[");
        sb.append("id=").append(getId());
        sb.append(", type=").append(typeToString(getType()));
        sb.append(", layer=").append(getLayer());
        sb.append(", bounds=").append(rect);
        sb.append(", focused=").append(isFocused());
        sb.append(", active=").append(isActive());
        sb.append(", hasParent=").append(getParent() != null);
        sb.append(", hasChildren=").append(getChildCount() > 0);
        sb.append(']');
        return sb.toString();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class Api21Impl {
        private Api21Impl() {
        }

        static void getBoundsInScreen(AccessibilityWindowInfo accessibilityWindowInfo, Rect rect) {
            accessibilityWindowInfo.getBoundsInScreen(rect);
        }

        static AccessibilityWindowInfo getChild(AccessibilityWindowInfo accessibilityWindowInfo, int i) {
            return accessibilityWindowInfo.getChild(i);
        }

        static int getChildCount(AccessibilityWindowInfo accessibilityWindowInfo) {
            return accessibilityWindowInfo.getChildCount();
        }

        static int getId(AccessibilityWindowInfo accessibilityWindowInfo) {
            return accessibilityWindowInfo.getId();
        }

        static int getLayer(AccessibilityWindowInfo accessibilityWindowInfo) {
            return accessibilityWindowInfo.getLayer();
        }

        static AccessibilityWindowInfo getParent(AccessibilityWindowInfo accessibilityWindowInfo) {
            return accessibilityWindowInfo.getParent();
        }

        static AccessibilityNodeInfo getRoot(AccessibilityWindowInfo accessibilityWindowInfo) {
            return accessibilityWindowInfo.getRoot();
        }

        static int getType(AccessibilityWindowInfo accessibilityWindowInfo) {
            return accessibilityWindowInfo.getType();
        }

        static boolean isAccessibilityFocused(AccessibilityWindowInfo accessibilityWindowInfo) {
            return accessibilityWindowInfo.isAccessibilityFocused();
        }

        static boolean isActive(AccessibilityWindowInfo accessibilityWindowInfo) {
            return accessibilityWindowInfo.isActive();
        }

        static boolean isFocused(AccessibilityWindowInfo accessibilityWindowInfo) {
            return accessibilityWindowInfo.isFocused();
        }

        static AccessibilityWindowInfo obtain() {
            return AccessibilityWindowInfo.obtain();
        }

        static AccessibilityWindowInfo obtain(AccessibilityWindowInfo accessibilityWindowInfo) {
            return AccessibilityWindowInfo.obtain(accessibilityWindowInfo);
        }

        static void recycle(AccessibilityWindowInfo accessibilityWindowInfo) {
            accessibilityWindowInfo.recycle();
        }
    }

    /* loaded from: classes.dex */
    private static class Api24Impl {
        private Api24Impl() {
        }

        static AccessibilityNodeInfo getAnchor(AccessibilityWindowInfo accessibilityWindowInfo) {
            return accessibilityWindowInfo.getAnchor();
        }

        static CharSequence getTitle(AccessibilityWindowInfo accessibilityWindowInfo) {
            return accessibilityWindowInfo.getTitle();
        }
    }
}
