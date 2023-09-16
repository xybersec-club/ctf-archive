package android.support.transition;

import android.animation.Animator;
import android.graphics.Matrix;
import android.widget.ImageView;
/* loaded from: classes.dex */
interface ImageViewUtilsImpl {
    void animateTransform(ImageView imageView, Matrix matrix);

    void reserveEndAnimateTransform(ImageView imageView, Animator animator);

    void startAnimateTransform(ImageView imageView);
}
