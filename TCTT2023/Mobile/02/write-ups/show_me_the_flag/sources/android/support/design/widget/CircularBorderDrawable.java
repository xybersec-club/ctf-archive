package android.support.design.widget;

import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.ColorUtils;
/* loaded from: classes.dex */
class CircularBorderDrawable extends Drawable {
    private static final float DRAW_STROKE_WIDTH_MULTIPLE = 1.3333f;
    private ColorStateList mBorderTint;
    float mBorderWidth;
    private int mBottomInnerStrokeColor;
    private int mBottomOuterStrokeColor;
    private int mCurrentBorderTintColor;
    private float mRotation;
    private int mTopInnerStrokeColor;
    private int mTopOuterStrokeColor;
    final Rect mRect = new Rect();
    final RectF mRectF = new RectF();
    private boolean mInvalidateShader = true;
    final Paint mPaint = new Paint(1);

    public CircularBorderDrawable() {
        this.mPaint.setStyle(Paint.Style.STROKE);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setGradientColors(int i, int i2, int i3, int i4) {
        this.mTopOuterStrokeColor = i;
        this.mTopInnerStrokeColor = i2;
        this.mBottomOuterStrokeColor = i3;
        this.mBottomInnerStrokeColor = i4;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setBorderWidth(float f) {
        if (this.mBorderWidth != f) {
            this.mBorderWidth = f;
            this.mPaint.setStrokeWidth(f * DRAW_STROKE_WIDTH_MULTIPLE);
            this.mInvalidateShader = true;
            invalidateSelf();
        }
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        if (this.mInvalidateShader) {
            this.mPaint.setShader(createGradientShader());
            this.mInvalidateShader = false;
        }
        float strokeWidth = this.mPaint.getStrokeWidth() / 2.0f;
        RectF rectF = this.mRectF;
        copyBounds(this.mRect);
        rectF.set(this.mRect);
        rectF.left += strokeWidth;
        rectF.top += strokeWidth;
        rectF.right -= strokeWidth;
        rectF.bottom -= strokeWidth;
        canvas.save();
        canvas.rotate(this.mRotation, rectF.centerX(), rectF.centerY());
        canvas.drawOval(rectF, this.mPaint);
        canvas.restore();
    }

    @Override // android.graphics.drawable.Drawable
    public boolean getPadding(Rect rect) {
        int round = Math.round(this.mBorderWidth);
        rect.set(round, round, round, round);
        return true;
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int i) {
        this.mPaint.setAlpha(i);
        invalidateSelf();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setBorderTint(ColorStateList colorStateList) {
        if (colorStateList != null) {
            this.mCurrentBorderTintColor = colorStateList.getColorForState(getState(), this.mCurrentBorderTintColor);
        }
        this.mBorderTint = colorStateList;
        this.mInvalidateShader = true;
        invalidateSelf();
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter colorFilter) {
        this.mPaint.setColorFilter(colorFilter);
        invalidateSelf();
    }

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return this.mBorderWidth > 0.0f ? -3 : -2;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void setRotation(float f) {
        if (f != this.mRotation) {
            this.mRotation = f;
            invalidateSelf();
        }
    }

    @Override // android.graphics.drawable.Drawable
    protected void onBoundsChange(Rect rect) {
        this.mInvalidateShader = true;
    }

    @Override // android.graphics.drawable.Drawable
    public boolean isStateful() {
        ColorStateList colorStateList = this.mBorderTint;
        return (colorStateList != null && colorStateList.isStateful()) || super.isStateful();
    }

    @Override // android.graphics.drawable.Drawable
    protected boolean onStateChange(int[] iArr) {
        int colorForState;
        ColorStateList colorStateList = this.mBorderTint;
        if (colorStateList != null && (colorForState = colorStateList.getColorForState(iArr, this.mCurrentBorderTintColor)) != this.mCurrentBorderTintColor) {
            this.mInvalidateShader = true;
            this.mCurrentBorderTintColor = colorForState;
        }
        if (this.mInvalidateShader) {
            invalidateSelf();
        }
        return this.mInvalidateShader;
    }

    private Shader createGradientShader() {
        Rect rect = this.mRect;
        copyBounds(rect);
        float height = this.mBorderWidth / rect.height();
        return new LinearGradient(0.0f, rect.top, 0.0f, rect.bottom, new int[]{ColorUtils.compositeColors(this.mTopOuterStrokeColor, this.mCurrentBorderTintColor), ColorUtils.compositeColors(this.mTopInnerStrokeColor, this.mCurrentBorderTintColor), ColorUtils.compositeColors(ColorUtils.setAlphaComponent(this.mTopInnerStrokeColor, 0), this.mCurrentBorderTintColor), ColorUtils.compositeColors(ColorUtils.setAlphaComponent(this.mBottomInnerStrokeColor, 0), this.mCurrentBorderTintColor), ColorUtils.compositeColors(this.mBottomInnerStrokeColor, this.mCurrentBorderTintColor), ColorUtils.compositeColors(this.mBottomOuterStrokeColor, this.mCurrentBorderTintColor)}, new float[]{0.0f, height, 0.5f, 0.5f, 1.0f - height, 1.0f}, Shader.TileMode.CLAMP);
    }
}
