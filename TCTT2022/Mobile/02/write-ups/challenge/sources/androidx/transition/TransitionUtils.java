package androidx.transition;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.TypeEvaluator;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Picture;
import android.graphics.RectF;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.constraintlayout.core.widgets.analyzer.BasicMeasure;
/* loaded from: classes.dex */
class TransitionUtils {
    private static final boolean HAS_IS_ATTACHED_TO_WINDOW;
    private static final boolean HAS_OVERLAY;
    private static final boolean HAS_PICTURE_BITMAP;
    private static final int MAX_IMAGE_SIZE = 1048576;

    static {
        HAS_IS_ATTACHED_TO_WINDOW = Build.VERSION.SDK_INT >= 19;
        HAS_OVERLAY = Build.VERSION.SDK_INT >= 18;
        HAS_PICTURE_BITMAP = Build.VERSION.SDK_INT >= 28;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static View copyViewImage(ViewGroup viewGroup, View view, View view2) {
        Matrix matrix = new Matrix();
        matrix.setTranslate(-view2.getScrollX(), -view2.getScrollY());
        ViewUtils.transformMatrixToGlobal(view, matrix);
        ViewUtils.transformMatrixToLocal(viewGroup, matrix);
        RectF rectF = new RectF(0.0f, 0.0f, view.getWidth(), view.getHeight());
        matrix.mapRect(rectF);
        int round = Math.round(rectF.left);
        int round2 = Math.round(rectF.top);
        int round3 = Math.round(rectF.right);
        int round4 = Math.round(rectF.bottom);
        ImageView imageView = new ImageView(view.getContext());
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Bitmap createViewBitmap = createViewBitmap(view, matrix, rectF, viewGroup);
        if (createViewBitmap != null) {
            imageView.setImageBitmap(createViewBitmap);
        }
        imageView.measure(View.MeasureSpec.makeMeasureSpec(round3 - round, BasicMeasure.EXACTLY), View.MeasureSpec.makeMeasureSpec(round4 - round2, BasicMeasure.EXACTLY));
        imageView.layout(round, round2, round3, round4);
        return imageView;
    }

    private static Bitmap createViewBitmap(View view, Matrix matrix, RectF rectF, ViewGroup viewGroup) {
        boolean z;
        boolean z2;
        int i;
        ViewGroup viewGroup2;
        if (HAS_IS_ATTACHED_TO_WINDOW) {
            z = !view.isAttachedToWindow();
            z2 = viewGroup == null ? false : viewGroup.isAttachedToWindow();
        } else {
            z = false;
            z2 = false;
        }
        boolean z3 = HAS_OVERLAY;
        Bitmap bitmap = null;
        if (!z3 || !z) {
            i = 0;
            viewGroup2 = null;
        } else if (!z2) {
            return null;
        } else {
            viewGroup2 = (ViewGroup) view.getParent();
            i = viewGroup2.indexOfChild(view);
            viewGroup.getOverlay().add(view);
        }
        int round = Math.round(rectF.width());
        int round2 = Math.round(rectF.height());
        if (round > 0 && round2 > 0) {
            float min = Math.min(1.0f, 1048576.0f / (round * round2));
            int round3 = Math.round(round * min);
            int round4 = Math.round(round2 * min);
            matrix.postTranslate(-rectF.left, -rectF.top);
            matrix.postScale(min, min);
            if (HAS_PICTURE_BITMAP) {
                Picture picture = new Picture();
                Canvas beginRecording = picture.beginRecording(round3, round4);
                beginRecording.concat(matrix);
                view.draw(beginRecording);
                picture.endRecording();
                bitmap = Bitmap.createBitmap(picture);
            } else {
                bitmap = Bitmap.createBitmap(round3, round4, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                canvas.concat(matrix);
                view.draw(canvas);
            }
        }
        if (z3 && z) {
            viewGroup.getOverlay().remove(view);
            viewGroup2.addView(view, i);
        }
        return bitmap;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Animator mergeAnimators(Animator animator, Animator animator2) {
        if (animator == null) {
            return animator2;
        }
        if (animator2 == null) {
            return animator;
        }
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animator, animator2);
        return animatorSet;
    }

    /* loaded from: classes.dex */
    static class MatrixEvaluator implements TypeEvaluator<Matrix> {
        final float[] mTempStartValues = new float[9];
        final float[] mTempEndValues = new float[9];
        final Matrix mTempMatrix = new Matrix();

        @Override // android.animation.TypeEvaluator
        public Matrix evaluate(float f, Matrix matrix, Matrix matrix2) {
            matrix.getValues(this.mTempStartValues);
            matrix2.getValues(this.mTempEndValues);
            for (int i = 0; i < 9; i++) {
                float[] fArr = this.mTempEndValues;
                float f2 = fArr[i];
                float[] fArr2 = this.mTempStartValues;
                fArr[i] = fArr2[i] + ((f2 - fArr2[i]) * f);
            }
            this.mTempMatrix.setValues(this.mTempEndValues);
            return this.mTempMatrix;
        }
    }

    private TransitionUtils() {
    }
}
