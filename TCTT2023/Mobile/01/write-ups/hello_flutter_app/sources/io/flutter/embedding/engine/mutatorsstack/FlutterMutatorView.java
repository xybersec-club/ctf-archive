package io.flutter.embedding.engine.mutatorsstack;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Path;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.accessibility.AccessibilityEvent;
import android.widget.FrameLayout;
import io.flutter.embedding.android.AndroidTouchProcessor;
import io.flutter.util.ViewUtils;
/* loaded from: classes.dex */
public class FlutterMutatorView extends FrameLayout {
    ViewTreeObserver.OnGlobalFocusChangeListener activeFocusListener;
    private final AndroidTouchProcessor androidTouchProcessor;
    private int left;
    private FlutterMutatorsStack mutatorsStack;
    private int prevLeft;
    private int prevTop;
    private float screenDensity;
    private int top;

    public FlutterMutatorView(Context context, float screenDensity, AndroidTouchProcessor androidTouchProcessor) {
        super(context, null);
        this.screenDensity = screenDensity;
        this.androidTouchProcessor = androidTouchProcessor;
    }

    public FlutterMutatorView(Context context) {
        this(context, 1.0f, null);
    }

    public void setOnDescendantFocusChangeListener(final View.OnFocusChangeListener userFocusListener) {
        unsetOnDescendantFocusChangeListener();
        ViewTreeObserver observer = getViewTreeObserver();
        if (observer.isAlive() && this.activeFocusListener == null) {
            ViewTreeObserver.OnGlobalFocusChangeListener onGlobalFocusChangeListener = new ViewTreeObserver.OnGlobalFocusChangeListener() { // from class: io.flutter.embedding.engine.mutatorsstack.FlutterMutatorView.1
                @Override // android.view.ViewTreeObserver.OnGlobalFocusChangeListener
                public void onGlobalFocusChanged(View oldFocus, View newFocus) {
                    View.OnFocusChangeListener onFocusChangeListener = userFocusListener;
                    View view = this;
                    onFocusChangeListener.onFocusChange(view, ViewUtils.childHasFocus(view));
                }
            };
            this.activeFocusListener = onGlobalFocusChangeListener;
            observer.addOnGlobalFocusChangeListener(onGlobalFocusChangeListener);
        }
    }

    public void unsetOnDescendantFocusChangeListener() {
        ViewTreeObserver observer = getViewTreeObserver();
        if (observer.isAlive() && this.activeFocusListener != null) {
            ViewTreeObserver.OnGlobalFocusChangeListener currFocusListener = this.activeFocusListener;
            this.activeFocusListener = null;
            observer.removeOnGlobalFocusChangeListener(currFocusListener);
        }
    }

    public void readyToDisplay(FlutterMutatorsStack mutatorsStack, int left, int top, int width, int height) {
        this.mutatorsStack = mutatorsStack;
        this.left = left;
        this.top = top;
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(width, height);
        layoutParams.leftMargin = left;
        layoutParams.topMargin = top;
        setLayoutParams(layoutParams);
        setWillNotDraw(false);
    }

    @Override // android.view.View
    public void draw(Canvas canvas) {
        canvas.save();
        for (Path path : this.mutatorsStack.getFinalClippingPaths()) {
            Path pathCopy = new Path(path);
            pathCopy.offset(-this.left, -this.top);
            canvas.clipPath(pathCopy);
        }
        super.draw(canvas);
        canvas.restore();
    }

    @Override // android.view.ViewGroup, android.view.View
    public void dispatchDraw(Canvas canvas) {
        canvas.save();
        canvas.concat(getPlatformViewMatrix());
        super.dispatchDraw(canvas);
        canvas.restore();
    }

    private Matrix getPlatformViewMatrix() {
        Matrix finalMatrix = new Matrix(this.mutatorsStack.getFinalMatrix());
        float f = this.screenDensity;
        finalMatrix.preScale(1.0f / f, 1.0f / f);
        finalMatrix.postTranslate(-this.left, -this.top);
        return finalMatrix;
    }

    @Override // android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return true;
    }

    @Override // android.view.ViewGroup, android.view.ViewParent
    public boolean requestSendAccessibilityEvent(View child, AccessibilityEvent event) {
        View embeddedView = getChildAt(0);
        if (embeddedView != null && embeddedView.getImportantForAccessibility() == 4) {
            return false;
        }
        return super.requestSendAccessibilityEvent(child, event);
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        if (this.androidTouchProcessor == null) {
            return super.onTouchEvent(event);
        }
        Matrix screenMatrix = new Matrix();
        switch (event.getAction()) {
            case 0:
                int i = this.left;
                this.prevLeft = i;
                int i2 = this.top;
                this.prevTop = i2;
                screenMatrix.postTranslate(i, i2);
                break;
            case 1:
            default:
                screenMatrix.postTranslate(this.left, this.top);
                break;
            case 2:
                screenMatrix.postTranslate(this.prevLeft, this.prevTop);
                this.prevLeft = this.left;
                this.prevTop = this.top;
                break;
        }
        return this.androidTouchProcessor.onTouchEvent(event, screenMatrix);
    }
}
