package android.support.transition;

import android.util.Log;
import android.view.ViewGroup;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
/* loaded from: classes.dex */
class ViewGroupUtilsApi18 extends ViewGroupUtilsApi14 {
    private static final String TAG = "ViewUtilsApi18";
    private static Method sSuppressLayoutMethod;
    private static boolean sSuppressLayoutMethodFetched;

    @Override // android.support.transition.ViewGroupUtilsApi14, android.support.transition.ViewGroupUtilsImpl
    public ViewGroupOverlayImpl getOverlay(ViewGroup viewGroup) {
        return new ViewGroupOverlayApi18(viewGroup);
    }

    @Override // android.support.transition.ViewGroupUtilsApi14, android.support.transition.ViewGroupUtilsImpl
    public void suppressLayout(ViewGroup viewGroup, boolean z) {
        fetchSuppressLayoutMethod();
        Method method = sSuppressLayoutMethod;
        if (method != null) {
            try {
                method.invoke(viewGroup, Boolean.valueOf(z));
            } catch (IllegalAccessException e) {
                Log.i(TAG, "Failed to invoke suppressLayout method", e);
            } catch (InvocationTargetException e2) {
                Log.i(TAG, "Error invoking suppressLayout method", e2);
            }
        }
    }

    private void fetchSuppressLayoutMethod() {
        if (sSuppressLayoutMethodFetched) {
            return;
        }
        try {
            sSuppressLayoutMethod = ViewGroup.class.getDeclaredMethod("suppressLayout", Boolean.TYPE);
            sSuppressLayoutMethod.setAccessible(true);
        } catch (NoSuchMethodException e) {
            Log.i(TAG, "Failed to retrieve suppressLayout method", e);
        }
        sSuppressLayoutMethodFetched = true;
    }
}
