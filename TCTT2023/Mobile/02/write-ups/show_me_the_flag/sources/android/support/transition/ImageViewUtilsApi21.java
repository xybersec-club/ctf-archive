package android.support.transition;

import android.animation.Animator;
import android.graphics.Matrix;
import android.util.Log;
import android.widget.ImageView;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
/* loaded from: classes.dex */
class ImageViewUtilsApi21 implements ImageViewUtilsImpl {
    private static final String TAG = "ImageViewUtilsApi21";
    private static Method sAnimateTransformMethod;
    private static boolean sAnimateTransformMethodFetched;

    @Override // android.support.transition.ImageViewUtilsImpl
    public void reserveEndAnimateTransform(ImageView imageView, Animator animator) {
    }

    @Override // android.support.transition.ImageViewUtilsImpl
    public void startAnimateTransform(ImageView imageView) {
    }

    @Override // android.support.transition.ImageViewUtilsImpl
    public void animateTransform(ImageView imageView, Matrix matrix) {
        fetchAnimateTransformMethod();
        Method method = sAnimateTransformMethod;
        if (method != null) {
            try {
                method.invoke(imageView, matrix);
            } catch (IllegalAccessException unused) {
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e.getCause());
            }
        }
    }

    private void fetchAnimateTransformMethod() {
        if (sAnimateTransformMethodFetched) {
            return;
        }
        try {
            sAnimateTransformMethod = ImageView.class.getDeclaredMethod("animateTransform", Matrix.class);
            sAnimateTransformMethod.setAccessible(true);
        } catch (NoSuchMethodException e) {
            Log.i(TAG, "Failed to retrieve animateTransform method", e);
        }
        sAnimateTransformMethodFetched = true;
    }
}
