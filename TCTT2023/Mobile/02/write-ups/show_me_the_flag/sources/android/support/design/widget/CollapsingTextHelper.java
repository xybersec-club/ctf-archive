package android.support.design.widget;

import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v4.math.MathUtils;
import android.support.v4.text.TextDirectionHeuristicsCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.appcompat.R;
import android.support.v7.widget.TintTypedArray;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Interpolator;
/* loaded from: classes.dex */
final class CollapsingTextHelper {
    private static final boolean DEBUG_DRAW = false;
    private static final Paint DEBUG_DRAW_PAINT;
    private static final boolean USE_SCALING_TEXTURE;
    private boolean mBoundsChanged;
    private float mCollapsedDrawX;
    private float mCollapsedDrawY;
    private int mCollapsedShadowColor;
    private float mCollapsedShadowDx;
    private float mCollapsedShadowDy;
    private float mCollapsedShadowRadius;
    private ColorStateList mCollapsedTextColor;
    private Typeface mCollapsedTypeface;
    private float mCurrentDrawX;
    private float mCurrentDrawY;
    private float mCurrentTextSize;
    private Typeface mCurrentTypeface;
    private boolean mDrawTitle;
    private float mExpandedDrawX;
    private float mExpandedDrawY;
    private float mExpandedFraction;
    private int mExpandedShadowColor;
    private float mExpandedShadowDx;
    private float mExpandedShadowDy;
    private float mExpandedShadowRadius;
    private ColorStateList mExpandedTextColor;
    private Bitmap mExpandedTitleTexture;
    private Typeface mExpandedTypeface;
    private boolean mIsRtl;
    private Interpolator mPositionInterpolator;
    private float mScale;
    private int[] mState;
    private CharSequence mText;
    private Interpolator mTextSizeInterpolator;
    private CharSequence mTextToDraw;
    private float mTextureAscent;
    private float mTextureDescent;
    private Paint mTexturePaint;
    private boolean mUseTexture;
    private final View mView;
    private int mExpandedTextGravity = 16;
    private int mCollapsedTextGravity = 16;
    private float mExpandedTextSize = 15.0f;
    private float mCollapsedTextSize = 15.0f;
    private final TextPaint mTextPaint = new TextPaint(129);
    private final Rect mCollapsedBounds = new Rect();
    private final Rect mExpandedBounds = new Rect();
    private final RectF mCurrentBounds = new RectF();

    static {
        USE_SCALING_TEXTURE = Build.VERSION.SDK_INT < 18;
        DEBUG_DRAW_PAINT = null;
        Paint paint = DEBUG_DRAW_PAINT;
        if (paint != null) {
            paint.setAntiAlias(true);
            DEBUG_DRAW_PAINT.setColor(-65281);
        }
    }

    public CollapsingTextHelper(View view) {
        this.mView = view;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setTextSizeInterpolator(Interpolator interpolator) {
        this.mTextSizeInterpolator = interpolator;
        recalculate();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setPositionInterpolator(Interpolator interpolator) {
        this.mPositionInterpolator = interpolator;
        recalculate();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setExpandedTextSize(float f) {
        if (this.mExpandedTextSize != f) {
            this.mExpandedTextSize = f;
            recalculate();
        }
    }

    void setCollapsedTextSize(float f) {
        if (this.mCollapsedTextSize != f) {
            this.mCollapsedTextSize = f;
            recalculate();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setCollapsedTextColor(ColorStateList colorStateList) {
        if (this.mCollapsedTextColor != colorStateList) {
            this.mCollapsedTextColor = colorStateList;
            recalculate();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setExpandedTextColor(ColorStateList colorStateList) {
        if (this.mExpandedTextColor != colorStateList) {
            this.mExpandedTextColor = colorStateList;
            recalculate();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setExpandedBounds(int i, int i2, int i3, int i4) {
        if (rectEquals(this.mExpandedBounds, i, i2, i3, i4)) {
            return;
        }
        this.mExpandedBounds.set(i, i2, i3, i4);
        this.mBoundsChanged = true;
        onBoundsChanged();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setCollapsedBounds(int i, int i2, int i3, int i4) {
        if (rectEquals(this.mCollapsedBounds, i, i2, i3, i4)) {
            return;
        }
        this.mCollapsedBounds.set(i, i2, i3, i4);
        this.mBoundsChanged = true;
        onBoundsChanged();
    }

    void onBoundsChanged() {
        this.mDrawTitle = this.mCollapsedBounds.width() > 0 && this.mCollapsedBounds.height() > 0 && this.mExpandedBounds.width() > 0 && this.mExpandedBounds.height() > 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setExpandedTextGravity(int i) {
        if (this.mExpandedTextGravity != i) {
            this.mExpandedTextGravity = i;
            recalculate();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getExpandedTextGravity() {
        return this.mExpandedTextGravity;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setCollapsedTextGravity(int i) {
        if (this.mCollapsedTextGravity != i) {
            this.mCollapsedTextGravity = i;
            recalculate();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getCollapsedTextGravity() {
        return this.mCollapsedTextGravity;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setCollapsedTextAppearance(int i) {
        TintTypedArray obtainStyledAttributes = TintTypedArray.obtainStyledAttributes(this.mView.getContext(), i, R.styleable.TextAppearance);
        if (obtainStyledAttributes.hasValue(R.styleable.TextAppearance_android_textColor)) {
            this.mCollapsedTextColor = obtainStyledAttributes.getColorStateList(R.styleable.TextAppearance_android_textColor);
        }
        if (obtainStyledAttributes.hasValue(R.styleable.TextAppearance_android_textSize)) {
            this.mCollapsedTextSize = obtainStyledAttributes.getDimensionPixelSize(R.styleable.TextAppearance_android_textSize, (int) this.mCollapsedTextSize);
        }
        this.mCollapsedShadowColor = obtainStyledAttributes.getInt(R.styleable.TextAppearance_android_shadowColor, 0);
        this.mCollapsedShadowDx = obtainStyledAttributes.getFloat(R.styleable.TextAppearance_android_shadowDx, 0.0f);
        this.mCollapsedShadowDy = obtainStyledAttributes.getFloat(R.styleable.TextAppearance_android_shadowDy, 0.0f);
        this.mCollapsedShadowRadius = obtainStyledAttributes.getFloat(R.styleable.TextAppearance_android_shadowRadius, 0.0f);
        obtainStyledAttributes.recycle();
        if (Build.VERSION.SDK_INT >= 16) {
            this.mCollapsedTypeface = readFontFamilyTypeface(i);
        }
        recalculate();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setExpandedTextAppearance(int i) {
        TintTypedArray obtainStyledAttributes = TintTypedArray.obtainStyledAttributes(this.mView.getContext(), i, R.styleable.TextAppearance);
        if (obtainStyledAttributes.hasValue(R.styleable.TextAppearance_android_textColor)) {
            this.mExpandedTextColor = obtainStyledAttributes.getColorStateList(R.styleable.TextAppearance_android_textColor);
        }
        if (obtainStyledAttributes.hasValue(R.styleable.TextAppearance_android_textSize)) {
            this.mExpandedTextSize = obtainStyledAttributes.getDimensionPixelSize(R.styleable.TextAppearance_android_textSize, (int) this.mExpandedTextSize);
        }
        this.mExpandedShadowColor = obtainStyledAttributes.getInt(R.styleable.TextAppearance_android_shadowColor, 0);
        this.mExpandedShadowDx = obtainStyledAttributes.getFloat(R.styleable.TextAppearance_android_shadowDx, 0.0f);
        this.mExpandedShadowDy = obtainStyledAttributes.getFloat(R.styleable.TextAppearance_android_shadowDy, 0.0f);
        this.mExpandedShadowRadius = obtainStyledAttributes.getFloat(R.styleable.TextAppearance_android_shadowRadius, 0.0f);
        obtainStyledAttributes.recycle();
        if (Build.VERSION.SDK_INT >= 16) {
            this.mExpandedTypeface = readFontFamilyTypeface(i);
        }
        recalculate();
    }

    private Typeface readFontFamilyTypeface(int i) {
        TypedArray obtainStyledAttributes = this.mView.getContext().obtainStyledAttributes(i, new int[]{16843692});
        try {
            String string = obtainStyledAttributes.getString(0);
            if (string != null) {
                return Typeface.create(string, 0);
            }
            obtainStyledAttributes.recycle();
            return null;
        } finally {
            obtainStyledAttributes.recycle();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setCollapsedTypeface(Typeface typeface) {
        if (areTypefacesDifferent(this.mCollapsedTypeface, typeface)) {
            this.mCollapsedTypeface = typeface;
            recalculate();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setExpandedTypeface(Typeface typeface) {
        if (areTypefacesDifferent(this.mExpandedTypeface, typeface)) {
            this.mExpandedTypeface = typeface;
            recalculate();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setTypefaces(Typeface typeface) {
        this.mExpandedTypeface = typeface;
        this.mCollapsedTypeface = typeface;
        recalculate();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Typeface getCollapsedTypeface() {
        Typeface typeface = this.mCollapsedTypeface;
        return typeface != null ? typeface : Typeface.DEFAULT;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Typeface getExpandedTypeface() {
        Typeface typeface = this.mExpandedTypeface;
        return typeface != null ? typeface : Typeface.DEFAULT;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setExpansionFraction(float f) {
        float clamp = MathUtils.clamp(f, 0.0f, 1.0f);
        if (clamp != this.mExpandedFraction) {
            this.mExpandedFraction = clamp;
            calculateCurrentOffsets();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean setState(int[] iArr) {
        this.mState = iArr;
        if (isStateful()) {
            recalculate();
            return true;
        }
        return false;
    }

    final boolean isStateful() {
        ColorStateList colorStateList;
        ColorStateList colorStateList2 = this.mCollapsedTextColor;
        return (colorStateList2 != null && colorStateList2.isStateful()) || ((colorStateList = this.mExpandedTextColor) != null && colorStateList.isStateful());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public float getExpansionFraction() {
        return this.mExpandedFraction;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public float getCollapsedTextSize() {
        return this.mCollapsedTextSize;
    }

    float getExpandedTextSize() {
        return this.mExpandedTextSize;
    }

    private void calculateCurrentOffsets() {
        calculateOffsets(this.mExpandedFraction);
    }

    private void calculateOffsets(float f) {
        interpolateBounds(f);
        this.mCurrentDrawX = lerp(this.mExpandedDrawX, this.mCollapsedDrawX, f, this.mPositionInterpolator);
        this.mCurrentDrawY = lerp(this.mExpandedDrawY, this.mCollapsedDrawY, f, this.mPositionInterpolator);
        setInterpolatedTextSize(lerp(this.mExpandedTextSize, this.mCollapsedTextSize, f, this.mTextSizeInterpolator));
        if (this.mCollapsedTextColor != this.mExpandedTextColor) {
            this.mTextPaint.setColor(blendColors(getCurrentExpandedTextColor(), getCurrentCollapsedTextColor(), f));
        } else {
            this.mTextPaint.setColor(getCurrentCollapsedTextColor());
        }
        this.mTextPaint.setShadowLayer(lerp(this.mExpandedShadowRadius, this.mCollapsedShadowRadius, f, null), lerp(this.mExpandedShadowDx, this.mCollapsedShadowDx, f, null), lerp(this.mExpandedShadowDy, this.mCollapsedShadowDy, f, null), blendColors(this.mExpandedShadowColor, this.mCollapsedShadowColor, f));
        ViewCompat.postInvalidateOnAnimation(this.mView);
    }

    private int getCurrentExpandedTextColor() {
        int[] iArr = this.mState;
        if (iArr != null) {
            return this.mExpandedTextColor.getColorForState(iArr, 0);
        }
        return this.mExpandedTextColor.getDefaultColor();
    }

    private int getCurrentCollapsedTextColor() {
        int[] iArr = this.mState;
        if (iArr != null) {
            return this.mCollapsedTextColor.getColorForState(iArr, 0);
        }
        return this.mCollapsedTextColor.getDefaultColor();
    }

    private void calculateBaseOffsets() {
        float f = this.mCurrentTextSize;
        calculateUsingTextSize(this.mCollapsedTextSize);
        CharSequence charSequence = this.mTextToDraw;
        float measureText = charSequence != null ? this.mTextPaint.measureText(charSequence, 0, charSequence.length()) : 0.0f;
        int absoluteGravity = GravityCompat.getAbsoluteGravity(this.mCollapsedTextGravity, this.mIsRtl ? 1 : 0);
        int i = absoluteGravity & 112;
        if (i == 48) {
            this.mCollapsedDrawY = this.mCollapsedBounds.top - this.mTextPaint.ascent();
        } else if (i == 80) {
            this.mCollapsedDrawY = this.mCollapsedBounds.bottom;
        } else {
            this.mCollapsedDrawY = this.mCollapsedBounds.centerY() + (((this.mTextPaint.descent() - this.mTextPaint.ascent()) / 2.0f) - this.mTextPaint.descent());
        }
        int i2 = absoluteGravity & GravityCompat.RELATIVE_HORIZONTAL_GRAVITY_MASK;
        if (i2 == 1) {
            this.mCollapsedDrawX = this.mCollapsedBounds.centerX() - (measureText / 2.0f);
        } else if (i2 == 5) {
            this.mCollapsedDrawX = this.mCollapsedBounds.right - measureText;
        } else {
            this.mCollapsedDrawX = this.mCollapsedBounds.left;
        }
        calculateUsingTextSize(this.mExpandedTextSize);
        CharSequence charSequence2 = this.mTextToDraw;
        float measureText2 = charSequence2 != null ? this.mTextPaint.measureText(charSequence2, 0, charSequence2.length()) : 0.0f;
        int absoluteGravity2 = GravityCompat.getAbsoluteGravity(this.mExpandedTextGravity, this.mIsRtl ? 1 : 0);
        int i3 = absoluteGravity2 & 112;
        if (i3 == 48) {
            this.mExpandedDrawY = this.mExpandedBounds.top - this.mTextPaint.ascent();
        } else if (i3 == 80) {
            this.mExpandedDrawY = this.mExpandedBounds.bottom;
        } else {
            this.mExpandedDrawY = this.mExpandedBounds.centerY() + (((this.mTextPaint.descent() - this.mTextPaint.ascent()) / 2.0f) - this.mTextPaint.descent());
        }
        int i4 = absoluteGravity2 & GravityCompat.RELATIVE_HORIZONTAL_GRAVITY_MASK;
        if (i4 == 1) {
            this.mExpandedDrawX = this.mExpandedBounds.centerX() - (measureText2 / 2.0f);
        } else if (i4 == 5) {
            this.mExpandedDrawX = this.mExpandedBounds.right - measureText2;
        } else {
            this.mExpandedDrawX = this.mExpandedBounds.left;
        }
        clearTexture();
        setInterpolatedTextSize(f);
    }

    private void interpolateBounds(float f) {
        this.mCurrentBounds.left = lerp(this.mExpandedBounds.left, this.mCollapsedBounds.left, f, this.mPositionInterpolator);
        this.mCurrentBounds.top = lerp(this.mExpandedDrawY, this.mCollapsedDrawY, f, this.mPositionInterpolator);
        this.mCurrentBounds.right = lerp(this.mExpandedBounds.right, this.mCollapsedBounds.right, f, this.mPositionInterpolator);
        this.mCurrentBounds.bottom = lerp(this.mExpandedBounds.bottom, this.mCollapsedBounds.bottom, f, this.mPositionInterpolator);
    }

    public void draw(Canvas canvas) {
        float ascent;
        int save = canvas.save();
        if (this.mTextToDraw != null && this.mDrawTitle) {
            float f = this.mCurrentDrawX;
            float f2 = this.mCurrentDrawY;
            boolean z = this.mUseTexture && this.mExpandedTitleTexture != null;
            if (z) {
                ascent = this.mTextureAscent * this.mScale;
                float f3 = this.mTextureDescent;
            } else {
                ascent = this.mTextPaint.ascent() * this.mScale;
                this.mTextPaint.descent();
                float f4 = this.mScale;
            }
            if (z) {
                f2 += ascent;
            }
            float f5 = f2;
            float f6 = this.mScale;
            if (f6 != 1.0f) {
                canvas.scale(f6, f6, f, f5);
            }
            if (z) {
                canvas.drawBitmap(this.mExpandedTitleTexture, f, f5, this.mTexturePaint);
            } else {
                CharSequence charSequence = this.mTextToDraw;
                canvas.drawText(charSequence, 0, charSequence.length(), f, f5, this.mTextPaint);
            }
        }
        canvas.restoreToCount(save);
    }

    private boolean calculateIsRtl(CharSequence charSequence) {
        return (ViewCompat.getLayoutDirection(this.mView) == 1 ? TextDirectionHeuristicsCompat.FIRSTSTRONG_RTL : TextDirectionHeuristicsCompat.FIRSTSTRONG_LTR).isRtl(charSequence, 0, charSequence.length());
    }

    private void setInterpolatedTextSize(float f) {
        calculateUsingTextSize(f);
        this.mUseTexture = USE_SCALING_TEXTURE && this.mScale != 1.0f;
        if (this.mUseTexture) {
            ensureExpandedTexture();
        }
        ViewCompat.postInvalidateOnAnimation(this.mView);
    }

    private boolean areTypefacesDifferent(Typeface typeface, Typeface typeface2) {
        return !(typeface == null || typeface.equals(typeface2)) || (typeface == null && typeface2 != null);
    }

    private void calculateUsingTextSize(float f) {
        float f2;
        boolean z;
        boolean z2;
        if (this.mText == null) {
            return;
        }
        float width = this.mCollapsedBounds.width();
        float width2 = this.mExpandedBounds.width();
        if (isClose(f, this.mCollapsedTextSize)) {
            float f3 = this.mCollapsedTextSize;
            this.mScale = 1.0f;
            if (areTypefacesDifferent(this.mCurrentTypeface, this.mCollapsedTypeface)) {
                this.mCurrentTypeface = this.mCollapsedTypeface;
                z2 = true;
            } else {
                z2 = false;
            }
            f2 = f3;
            z = z2;
        } else {
            f2 = this.mExpandedTextSize;
            if (areTypefacesDifferent(this.mCurrentTypeface, this.mExpandedTypeface)) {
                this.mCurrentTypeface = this.mExpandedTypeface;
                z = true;
            } else {
                z = false;
            }
            if (isClose(f, this.mExpandedTextSize)) {
                this.mScale = 1.0f;
            } else {
                this.mScale = f / this.mExpandedTextSize;
            }
            float f4 = this.mCollapsedTextSize / this.mExpandedTextSize;
            width = width2 * f4 > width ? Math.min(width / f4, width2) : width2;
        }
        if (width > 0.0f) {
            z = this.mCurrentTextSize != f2 || this.mBoundsChanged || z;
            this.mCurrentTextSize = f2;
            this.mBoundsChanged = false;
        }
        if (this.mTextToDraw == null || z) {
            this.mTextPaint.setTextSize(this.mCurrentTextSize);
            this.mTextPaint.setTypeface(this.mCurrentTypeface);
            this.mTextPaint.setLinearText(this.mScale != 1.0f);
            CharSequence ellipsize = TextUtils.ellipsize(this.mText, this.mTextPaint, width, TextUtils.TruncateAt.END);
            if (TextUtils.equals(ellipsize, this.mTextToDraw)) {
                return;
            }
            this.mTextToDraw = ellipsize;
            this.mIsRtl = calculateIsRtl(this.mTextToDraw);
        }
    }

    private void ensureExpandedTexture() {
        if (this.mExpandedTitleTexture != null || this.mExpandedBounds.isEmpty() || TextUtils.isEmpty(this.mTextToDraw)) {
            return;
        }
        calculateOffsets(0.0f);
        this.mTextureAscent = this.mTextPaint.ascent();
        this.mTextureDescent = this.mTextPaint.descent();
        TextPaint textPaint = this.mTextPaint;
        CharSequence charSequence = this.mTextToDraw;
        int round = Math.round(textPaint.measureText(charSequence, 0, charSequence.length()));
        int round2 = Math.round(this.mTextureDescent - this.mTextureAscent);
        if (round <= 0 || round2 <= 0) {
            return;
        }
        this.mExpandedTitleTexture = Bitmap.createBitmap(round, round2, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(this.mExpandedTitleTexture);
        CharSequence charSequence2 = this.mTextToDraw;
        canvas.drawText(charSequence2, 0, charSequence2.length(), 0.0f, round2 - this.mTextPaint.descent(), this.mTextPaint);
        if (this.mTexturePaint == null) {
            this.mTexturePaint = new Paint(3);
        }
    }

    public void recalculate() {
        if (this.mView.getHeight() <= 0 || this.mView.getWidth() <= 0) {
            return;
        }
        calculateBaseOffsets();
        calculateCurrentOffsets();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setText(CharSequence charSequence) {
        if (charSequence == null || !charSequence.equals(this.mText)) {
            this.mText = charSequence;
            this.mTextToDraw = null;
            clearTexture();
            recalculate();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public CharSequence getText() {
        return this.mText;
    }

    private void clearTexture() {
        Bitmap bitmap = this.mExpandedTitleTexture;
        if (bitmap != null) {
            bitmap.recycle();
            this.mExpandedTitleTexture = null;
        }
    }

    private static boolean isClose(float f, float f2) {
        return Math.abs(f - f2) < 0.001f;
    }

    ColorStateList getExpandedTextColor() {
        return this.mExpandedTextColor;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ColorStateList getCollapsedTextColor() {
        return this.mCollapsedTextColor;
    }

    private static int blendColors(int i, int i2, float f) {
        float f2 = 1.0f - f;
        return Color.argb((int) ((Color.alpha(i) * f2) + (Color.alpha(i2) * f)), (int) ((Color.red(i) * f2) + (Color.red(i2) * f)), (int) ((Color.green(i) * f2) + (Color.green(i2) * f)), (int) ((Color.blue(i) * f2) + (Color.blue(i2) * f)));
    }

    private static float lerp(float f, float f2, float f3, Interpolator interpolator) {
        if (interpolator != null) {
            f3 = interpolator.getInterpolation(f3);
        }
        return AnimationUtils.lerp(f, f2, f3);
    }

    private static boolean rectEquals(Rect rect, int i, int i2, int i3, int i4) {
        return rect.left == i && rect.top == i2 && rect.right == i3 && rect.bottom == i4;
    }
}
