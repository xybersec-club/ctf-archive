package android.support.transition;

import android.os.Build;
import android.view.ViewGroup;
/* loaded from: classes.dex */
class ViewGroupUtils {
    private static final ViewGroupUtilsImpl IMPL;

    ViewGroupUtils() {
    }

    static {
        if (Build.VERSION.SDK_INT >= 18) {
            IMPL = new ViewGroupUtilsApi18();
        } else {
            IMPL = new ViewGroupUtilsApi14();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static ViewGroupOverlayImpl getOverlay(ViewGroup viewGroup) {
        return IMPL.getOverlay(viewGroup);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void suppressLayout(ViewGroup viewGroup, boolean z) {
        IMPL.suppressLayout(viewGroup, z);
    }
}
