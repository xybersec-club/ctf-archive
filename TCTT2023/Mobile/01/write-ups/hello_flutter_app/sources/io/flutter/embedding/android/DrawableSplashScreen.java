package io.flutter.embedding.android;

import android.animation.Animator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import io.flutter.embedding.android.SplashScreen;
@Deprecated
/* loaded from: classes.dex */
public final class DrawableSplashScreen implements SplashScreen {
    private final long crossfadeDurationInMillis;
    private final Drawable drawable;
    private final ImageView.ScaleType scaleType;
    private DrawableSplashScreenView splashView;

    @Override // io.flutter.embedding.android.SplashScreen
    public /* synthetic */ boolean doesSplashViewRememberItsTransition() {
        return SplashScreen.CC.$default$doesSplashViewRememberItsTransition(this);
    }

    @Override // io.flutter.embedding.android.SplashScreen
    public /* synthetic */ Bundle saveSplashScreenState() {
        return SplashScreen.CC.$default$saveSplashScreenState(this);
    }

    public DrawableSplashScreen(Drawable drawable) {
        this(drawable, ImageView.ScaleType.FIT_XY, 500L);
    }

    public DrawableSplashScreen(Drawable drawable, ImageView.ScaleType scaleType, long crossfadeDurationInMillis) {
        this.drawable = drawable;
        this.scaleType = scaleType;
        this.crossfadeDurationInMillis = crossfadeDurationInMillis;
    }

    @Override // io.flutter.embedding.android.SplashScreen
    public View createSplashView(Context context, Bundle savedInstanceState) {
        DrawableSplashScreenView drawableSplashScreenView = new DrawableSplashScreenView(context);
        this.splashView = drawableSplashScreenView;
        drawableSplashScreenView.setSplashDrawable(this.drawable, this.scaleType);
        return this.splashView;
    }

    @Override // io.flutter.embedding.android.SplashScreen
    public void transitionToFlutter(final Runnable onTransitionComplete) {
        DrawableSplashScreenView drawableSplashScreenView = this.splashView;
        if (drawableSplashScreenView == null) {
            onTransitionComplete.run();
        } else {
            drawableSplashScreenView.animate().alpha(0.0f).setDuration(this.crossfadeDurationInMillis).setListener(new Animator.AnimatorListener() { // from class: io.flutter.embedding.android.DrawableSplashScreen.1
                @Override // android.animation.Animator.AnimatorListener
                public void onAnimationStart(Animator animation) {
                }

                @Override // android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    onTransitionComplete.run();
                }

                @Override // android.animation.Animator.AnimatorListener
                public void onAnimationCancel(Animator animation) {
                    onTransitionComplete.run();
                }

                @Override // android.animation.Animator.AnimatorListener
                public void onAnimationRepeat(Animator animation) {
                }
            });
        }
    }

    /* loaded from: classes.dex */
    public static class DrawableSplashScreenView extends ImageView {
        public DrawableSplashScreenView(Context context) {
            this(context, null, 0);
        }

        public DrawableSplashScreenView(Context context, AttributeSet attrs) {
            this(context, attrs, 0);
        }

        public DrawableSplashScreenView(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }

        public void setSplashDrawable(Drawable drawable) {
            setSplashDrawable(drawable, ImageView.ScaleType.FIT_XY);
        }

        public void setSplashDrawable(Drawable drawable, ImageView.ScaleType scaleType) {
            setScaleType(scaleType);
            setImageDrawable(drawable);
        }
    }
}
