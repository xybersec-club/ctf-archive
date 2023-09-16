package android.support.transition;

import android.graphics.Matrix;
import android.util.Log;
import android.view.View;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
/* loaded from: classes.dex */
class ViewUtilsApi21 extends ViewUtilsApi19 {
    private static final String TAG = "ViewUtilsApi21";
    private static Method sSetAnimationMatrixMethod;
    private static boolean sSetAnimationMatrixMethodFetched;
    private static Method sTransformMatrixToGlobalMethod;
    private static boolean sTransformMatrixToGlobalMethodFetched;
    private static Method sTransformMatrixToLocalMethod;
    private static boolean sTransformMatrixToLocalMethodFetched;

    @Override // android.support.transition.ViewUtilsApi14, android.support.transition.ViewUtilsImpl
    public void transformMatrixToGlobal(View view, Matrix matrix) {
        fetchTransformMatrixToGlobalMethod();
        Method method = sTransformMatrixToGlobalMethod;
        if (method != null) {
            try {
                method.invoke(view, matrix);
            } catch (IllegalAccessException unused) {
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e.getCause());
            }
        }
    }

    @Override // android.support.transition.ViewUtilsApi14, android.support.transition.ViewUtilsImpl
    public void transformMatrixToLocal(View view, Matrix matrix) {
        fetchTransformMatrixToLocalMethod();
        Method method = sTransformMatrixToLocalMethod;
        if (method != null) {
            try {
                method.invoke(view, matrix);
            } catch (IllegalAccessException unused) {
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e.getCause());
            }
        }
    }

    @Override // android.support.transition.ViewUtilsApi14, android.support.transition.ViewUtilsImpl
    public void setAnimationMatrix(View view, Matrix matrix) {
        fetchSetAnimationMatrix();
        Method method = sSetAnimationMatrixMethod;
        if (method != null) {
            try {
                method.invoke(view, matrix);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e.getCause());
            } catch (InvocationTargetException unused) {
            }
        }
    }

    private void fetchTransformMatrixToGlobalMethod() {
        if (sTransformMatrixToGlobalMethodFetched) {
            return;
        }
        try {
            sTransformMatrixToGlobalMethod = View.class.getDeclaredMethod("transformMatrixToGlobal", Matrix.class);
            sTransformMatrixToGlobalMethod.setAccessible(true);
        } catch (NoSuchMethodException e) {
            Log.i(TAG, "Failed to retrieve transformMatrixToGlobal method", e);
        }
        sTransformMatrixToGlobalMethodFetched = true;
    }

    private void fetchTransformMatrixToLocalMethod() {
        if (sTransformMatrixToLocalMethodFetched) {
            return;
        }
        try {
            sTransformMatrixToLocalMethod = View.class.getDeclaredMethod("transformMatrixToLocal", Matrix.class);
            sTransformMatrixToLocalMethod.setAccessible(true);
        } catch (NoSuchMethodException e) {
            Log.i(TAG, "Failed to retrieve transformMatrixToLocal method", e);
        }
        sTransformMatrixToLocalMethodFetched = true;
    }

    private void fetchSetAnimationMatrix() {
        if (sSetAnimationMatrixMethodFetched) {
            return;
        }
        try {
            sSetAnimationMatrixMethod = View.class.getDeclaredMethod("setAnimationMatrix", Matrix.class);
            sSetAnimationMatrixMethod.setAccessible(true);
        } catch (NoSuchMethodException e) {
            Log.i(TAG, "Failed to retrieve setAnimationMatrix method", e);
        }
        sSetAnimationMatrixMethodFetched = true;
    }
}
