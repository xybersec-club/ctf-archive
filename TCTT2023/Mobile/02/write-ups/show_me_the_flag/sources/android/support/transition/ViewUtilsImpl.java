package android.support.transition;

import android.graphics.Matrix;
import android.view.View;
/* loaded from: classes.dex */
interface ViewUtilsImpl {
    void clearNonTransitionAlpha(View view);

    ViewOverlayImpl getOverlay(View view);

    float getTransitionAlpha(View view);

    WindowIdImpl getWindowId(View view);

    void saveNonTransitionAlpha(View view);

    void setAnimationMatrix(View view, Matrix matrix);

    void setLeftTopRightBottom(View view, int i, int i2, int i3, int i4);

    void setTransitionAlpha(View view, float f);

    void transformMatrixToGlobal(View view, Matrix matrix);

    void transformMatrixToLocal(View view, Matrix matrix);
}
