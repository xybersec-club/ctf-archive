package androidx.core.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.FocusFinder;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityEvent;
import android.view.animation.AnimationUtils;
import android.widget.EdgeEffect;
import android.widget.FrameLayout;
import android.widget.OverScroller;
import android.widget.ScrollView;
import androidx.constraintlayout.core.widgets.analyzer.BasicMeasure;
import androidx.core.R;
import androidx.core.view.AccessibilityDelegateCompat;
import androidx.core.view.MotionEventCompat;
import androidx.core.view.NestedScrollingChild3;
import androidx.core.view.NestedScrollingChildHelper;
import androidx.core.view.NestedScrollingParent3;
import androidx.core.view.NestedScrollingParentHelper;
import androidx.core.view.ScrollingView;
import androidx.core.view.ViewCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.core.view.accessibility.AccessibilityRecordCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import java.util.ArrayList;
/* loaded from: classes.dex */
public class NestedScrollView extends FrameLayout implements NestedScrollingParent3, NestedScrollingChild3, ScrollingView {
    static final int ANIMATED_SCROLL_GAP = 250;
    private static final int DEFAULT_SMOOTH_SCROLL_DURATION = 250;
    private static final int INVALID_POINTER = -1;
    static final float MAX_SCROLL_FACTOR = 0.5f;
    private static final String TAG = "NestedScrollView";
    private int mActivePointerId;
    private final NestedScrollingChildHelper mChildHelper;
    private View mChildToScrollTo;
    public EdgeEffect mEdgeGlowBottom;
    public EdgeEffect mEdgeGlowTop;
    private boolean mFillViewport;
    private boolean mIsBeingDragged;
    private boolean mIsLaidOut;
    private boolean mIsLayoutDirty;
    private int mLastMotionY;
    private long mLastScroll;
    private int mLastScrollerY;
    private int mMaximumVelocity;
    private int mMinimumVelocity;
    private int mNestedYOffset;
    private OnScrollChangeListener mOnScrollChangeListener;
    private final NestedScrollingParentHelper mParentHelper;
    private SavedState mSavedState;
    private final int[] mScrollConsumed;
    private final int[] mScrollOffset;
    private OverScroller mScroller;
    private boolean mSmoothScrollingEnabled;
    private final Rect mTempRect;
    private int mTouchSlop;
    private VelocityTracker mVelocityTracker;
    private float mVerticalScrollFactor;
    private static final AccessibilityDelegate ACCESSIBILITY_DELEGATE = new AccessibilityDelegate();
    private static final int[] SCROLLVIEW_STYLEABLE = {16843130};

    /* loaded from: classes.dex */
    public interface OnScrollChangeListener {
        void onScrollChange(NestedScrollView nestedScrollView, int i, int i2, int i3, int i4);
    }

    private static int clamp(int i, int i2, int i3) {
        if (i2 >= i3 || i < 0) {
            return 0;
        }
        return i2 + i > i3 ? i3 - i2 : i;
    }

    @Override // androidx.core.view.NestedScrollingParent2
    public boolean onStartNestedScroll(View view, View view2, int i, int i2) {
        return (i & 2) != 0;
    }

    @Override // android.widget.FrameLayout, android.view.ViewGroup
    public boolean shouldDelayChildPressedState() {
        return true;
    }

    public NestedScrollView(Context context) {
        this(context, null);
    }

    public NestedScrollView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R.attr.nestedScrollViewStyle);
    }

    public NestedScrollView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mTempRect = new Rect();
        this.mIsLayoutDirty = true;
        this.mIsLaidOut = false;
        this.mChildToScrollTo = null;
        this.mIsBeingDragged = false;
        this.mSmoothScrollingEnabled = true;
        this.mActivePointerId = -1;
        this.mScrollOffset = new int[2];
        this.mScrollConsumed = new int[2];
        this.mEdgeGlowTop = EdgeEffectCompat.create(context, attributeSet);
        this.mEdgeGlowBottom = EdgeEffectCompat.create(context, attributeSet);
        initScrollView();
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, SCROLLVIEW_STYLEABLE, i, 0);
        setFillViewport(obtainStyledAttributes.getBoolean(0, false));
        obtainStyledAttributes.recycle();
        this.mParentHelper = new NestedScrollingParentHelper(this);
        this.mChildHelper = new NestedScrollingChildHelper(this);
        setNestedScrollingEnabled(true);
        ViewCompat.setAccessibilityDelegate(this, ACCESSIBILITY_DELEGATE);
    }

    @Override // androidx.core.view.NestedScrollingChild3
    public void dispatchNestedScroll(int i, int i2, int i3, int i4, int[] iArr, int i5, int[] iArr2) {
        this.mChildHelper.dispatchNestedScroll(i, i2, i3, i4, iArr, i5, iArr2);
    }

    @Override // androidx.core.view.NestedScrollingChild2
    public boolean startNestedScroll(int i, int i2) {
        return this.mChildHelper.startNestedScroll(i, i2);
    }

    @Override // androidx.core.view.NestedScrollingChild2
    public void stopNestedScroll(int i) {
        this.mChildHelper.stopNestedScroll(i);
    }

    @Override // androidx.core.view.NestedScrollingChild2
    public boolean hasNestedScrollingParent(int i) {
        return this.mChildHelper.hasNestedScrollingParent(i);
    }

    @Override // androidx.core.view.NestedScrollingChild2
    public boolean dispatchNestedScroll(int i, int i2, int i3, int i4, int[] iArr, int i5) {
        return this.mChildHelper.dispatchNestedScroll(i, i2, i3, i4, iArr, i5);
    }

    @Override // androidx.core.view.NestedScrollingChild2
    public boolean dispatchNestedPreScroll(int i, int i2, int[] iArr, int[] iArr2, int i3) {
        return this.mChildHelper.dispatchNestedPreScroll(i, i2, iArr, iArr2, i3);
    }

    @Override // android.view.View, androidx.core.view.NestedScrollingChild
    public void setNestedScrollingEnabled(boolean z) {
        this.mChildHelper.setNestedScrollingEnabled(z);
    }

    @Override // android.view.View, androidx.core.view.NestedScrollingChild
    public boolean isNestedScrollingEnabled() {
        return this.mChildHelper.isNestedScrollingEnabled();
    }

    @Override // android.view.View, androidx.core.view.NestedScrollingChild
    public boolean startNestedScroll(int i) {
        return startNestedScroll(i, 0);
    }

    @Override // android.view.View, androidx.core.view.NestedScrollingChild
    public void stopNestedScroll() {
        stopNestedScroll(0);
    }

    @Override // android.view.View, androidx.core.view.NestedScrollingChild
    public boolean hasNestedScrollingParent() {
        return hasNestedScrollingParent(0);
    }

    @Override // android.view.View, androidx.core.view.NestedScrollingChild
    public boolean dispatchNestedScroll(int i, int i2, int i3, int i4, int[] iArr) {
        return this.mChildHelper.dispatchNestedScroll(i, i2, i3, i4, iArr);
    }

    @Override // android.view.View, androidx.core.view.NestedScrollingChild
    public boolean dispatchNestedPreScroll(int i, int i2, int[] iArr, int[] iArr2) {
        return dispatchNestedPreScroll(i, i2, iArr, iArr2, 0);
    }

    @Override // android.view.View, androidx.core.view.NestedScrollingChild
    public boolean dispatchNestedFling(float f, float f2, boolean z) {
        return this.mChildHelper.dispatchNestedFling(f, f2, z);
    }

    @Override // android.view.View, androidx.core.view.NestedScrollingChild
    public boolean dispatchNestedPreFling(float f, float f2) {
        return this.mChildHelper.dispatchNestedPreFling(f, f2);
    }

    @Override // androidx.core.view.NestedScrollingParent3
    public void onNestedScroll(View view, int i, int i2, int i3, int i4, int i5, int[] iArr) {
        onNestedScrollInternal(i4, i5, iArr);
    }

    private void onNestedScrollInternal(int i, int i2, int[] iArr) {
        int scrollY = getScrollY();
        scrollBy(0, i);
        int scrollY2 = getScrollY() - scrollY;
        if (iArr != null) {
            iArr[1] = iArr[1] + scrollY2;
        }
        this.mChildHelper.dispatchNestedScroll(0, scrollY2, 0, i - scrollY2, null, i2, iArr);
    }

    @Override // androidx.core.view.NestedScrollingParent2
    public void onNestedScrollAccepted(View view, View view2, int i, int i2) {
        this.mParentHelper.onNestedScrollAccepted(view, view2, i, i2);
        startNestedScroll(2, i2);
    }

    @Override // androidx.core.view.NestedScrollingParent2
    public void onStopNestedScroll(View view, int i) {
        this.mParentHelper.onStopNestedScroll(view, i);
        stopNestedScroll(i);
    }

    @Override // androidx.core.view.NestedScrollingParent2
    public void onNestedScroll(View view, int i, int i2, int i3, int i4, int i5) {
        onNestedScrollInternal(i4, i5, null);
    }

    @Override // androidx.core.view.NestedScrollingParent2
    public void onNestedPreScroll(View view, int i, int i2, int[] iArr, int i3) {
        dispatchNestedPreScroll(i, i2, iArr, null, i3);
    }

    @Override // android.view.ViewGroup, android.view.ViewParent, androidx.core.view.NestedScrollingParent
    public boolean onStartNestedScroll(View view, View view2, int i) {
        return onStartNestedScroll(view, view2, i, 0);
    }

    @Override // android.view.ViewGroup, android.view.ViewParent, androidx.core.view.NestedScrollingParent
    public void onNestedScrollAccepted(View view, View view2, int i) {
        onNestedScrollAccepted(view, view2, i, 0);
    }

    @Override // android.view.ViewGroup, android.view.ViewParent, androidx.core.view.NestedScrollingParent
    public void onStopNestedScroll(View view) {
        onStopNestedScroll(view, 0);
    }

    @Override // android.view.ViewGroup, android.view.ViewParent, androidx.core.view.NestedScrollingParent
    public void onNestedScroll(View view, int i, int i2, int i3, int i4) {
        onNestedScrollInternal(i4, 0, null);
    }

    @Override // android.view.ViewGroup, android.view.ViewParent, androidx.core.view.NestedScrollingParent
    public void onNestedPreScroll(View view, int i, int i2, int[] iArr) {
        onNestedPreScroll(view, i, i2, iArr, 0);
    }

    @Override // android.view.ViewGroup, android.view.ViewParent, androidx.core.view.NestedScrollingParent
    public boolean onNestedFling(View view, float f, float f2, boolean z) {
        if (z) {
            return false;
        }
        dispatchNestedFling(0.0f, f2, true);
        fling((int) f2);
        return true;
    }

    @Override // android.view.ViewGroup, android.view.ViewParent, androidx.core.view.NestedScrollingParent
    public boolean onNestedPreFling(View view, float f, float f2) {
        return dispatchNestedPreFling(f, f2);
    }

    @Override // android.view.ViewGroup, androidx.core.view.NestedScrollingParent
    public int getNestedScrollAxes() {
        return this.mParentHelper.getNestedScrollAxes();
    }

    @Override // android.view.View
    protected float getTopFadingEdgeStrength() {
        if (getChildCount() == 0) {
            return 0.0f;
        }
        int verticalFadingEdgeLength = getVerticalFadingEdgeLength();
        int scrollY = getScrollY();
        if (scrollY < verticalFadingEdgeLength) {
            return scrollY / verticalFadingEdgeLength;
        }
        return 1.0f;
    }

    @Override // android.view.View
    protected float getBottomFadingEdgeStrength() {
        if (getChildCount() == 0) {
            return 0.0f;
        }
        View childAt = getChildAt(0);
        int verticalFadingEdgeLength = getVerticalFadingEdgeLength();
        int bottom = ((childAt.getBottom() + ((FrameLayout.LayoutParams) childAt.getLayoutParams()).bottomMargin) - getScrollY()) - (getHeight() - getPaddingBottom());
        if (bottom < verticalFadingEdgeLength) {
            return bottom / verticalFadingEdgeLength;
        }
        return 1.0f;
    }

    public int getMaxScrollAmount() {
        return (int) (getHeight() * 0.5f);
    }

    private void initScrollView() {
        this.mScroller = new OverScroller(getContext());
        setFocusable(true);
        setDescendantFocusability(262144);
        setWillNotDraw(false);
        ViewConfiguration viewConfiguration = ViewConfiguration.get(getContext());
        this.mTouchSlop = viewConfiguration.getScaledTouchSlop();
        this.mMinimumVelocity = viewConfiguration.getScaledMinimumFlingVelocity();
        this.mMaximumVelocity = viewConfiguration.getScaledMaximumFlingVelocity();
    }

    @Override // android.view.ViewGroup
    public void addView(View view) {
        if (getChildCount() > 0) {
            throw new IllegalStateException("ScrollView can host only one direct child");
        }
        super.addView(view);
    }

    @Override // android.view.ViewGroup
    public void addView(View view, int i) {
        if (getChildCount() > 0) {
            throw new IllegalStateException("ScrollView can host only one direct child");
        }
        super.addView(view, i);
    }

    @Override // android.view.ViewGroup, android.view.ViewManager
    public void addView(View view, ViewGroup.LayoutParams layoutParams) {
        if (getChildCount() > 0) {
            throw new IllegalStateException("ScrollView can host only one direct child");
        }
        super.addView(view, layoutParams);
    }

    @Override // android.view.ViewGroup
    public void addView(View view, int i, ViewGroup.LayoutParams layoutParams) {
        if (getChildCount() > 0) {
            throw new IllegalStateException("ScrollView can host only one direct child");
        }
        super.addView(view, i, layoutParams);
    }

    public void setOnScrollChangeListener(OnScrollChangeListener onScrollChangeListener) {
        this.mOnScrollChangeListener = onScrollChangeListener;
    }

    private boolean canScroll() {
        if (getChildCount() > 0) {
            View childAt = getChildAt(0);
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) childAt.getLayoutParams();
            return (childAt.getHeight() + layoutParams.topMargin) + layoutParams.bottomMargin > (getHeight() - getPaddingTop()) - getPaddingBottom();
        }
        return false;
    }

    public boolean isFillViewport() {
        return this.mFillViewport;
    }

    public void setFillViewport(boolean z) {
        if (z != this.mFillViewport) {
            this.mFillViewport = z;
            requestLayout();
        }
    }

    public boolean isSmoothScrollingEnabled() {
        return this.mSmoothScrollingEnabled;
    }

    public void setSmoothScrollingEnabled(boolean z) {
        this.mSmoothScrollingEnabled = z;
    }

    @Override // android.view.View
    protected void onScrollChanged(int i, int i2, int i3, int i4) {
        super.onScrollChanged(i, i2, i3, i4);
        OnScrollChangeListener onScrollChangeListener = this.mOnScrollChangeListener;
        if (onScrollChangeListener != null) {
            onScrollChangeListener.onScrollChange(this, i, i2, i3, i4);
        }
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        if (this.mFillViewport && View.MeasureSpec.getMode(i2) != 0 && getChildCount() > 0) {
            View childAt = getChildAt(0);
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) childAt.getLayoutParams();
            int measuredHeight = childAt.getMeasuredHeight();
            int measuredHeight2 = (((getMeasuredHeight() - getPaddingTop()) - getPaddingBottom()) - layoutParams.topMargin) - layoutParams.bottomMargin;
            if (measuredHeight < measuredHeight2) {
                childAt.measure(getChildMeasureSpec(i, getPaddingLeft() + getPaddingRight() + layoutParams.leftMargin + layoutParams.rightMargin, layoutParams.width), View.MeasureSpec.makeMeasureSpec(measuredHeight2, BasicMeasure.EXACTLY));
            }
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        return super.dispatchKeyEvent(keyEvent) || executeKeyEvent(keyEvent);
    }

    public boolean executeKeyEvent(KeyEvent keyEvent) {
        this.mTempRect.setEmpty();
        if (!canScroll()) {
            if (!isFocused() || keyEvent.getKeyCode() == 4) {
                return false;
            }
            View findFocus = findFocus();
            if (findFocus == this) {
                findFocus = null;
            }
            View findNextFocus = FocusFinder.getInstance().findNextFocus(this, findFocus, 130);
            return (findNextFocus == null || findNextFocus == this || !findNextFocus.requestFocus(130)) ? false : true;
        } else if (keyEvent.getAction() == 0) {
            int keyCode = keyEvent.getKeyCode();
            if (keyCode == 19) {
                if (!keyEvent.isAltPressed()) {
                    return arrowScroll(33);
                }
                return fullScroll(33);
            } else if (keyCode == 20) {
                if (!keyEvent.isAltPressed()) {
                    return arrowScroll(130);
                }
                return fullScroll(130);
            } else if (keyCode != 62) {
                return false;
            } else {
                pageScroll(keyEvent.isShiftPressed() ? 33 : 130);
                return false;
            }
        } else {
            return false;
        }
    }

    private boolean inChild(int i, int i2) {
        if (getChildCount() > 0) {
            int scrollY = getScrollY();
            View childAt = getChildAt(0);
            return i2 >= childAt.getTop() - scrollY && i2 < childAt.getBottom() - scrollY && i >= childAt.getLeft() && i < childAt.getRight();
        }
        return false;
    }

    private void initOrResetVelocityTracker() {
        VelocityTracker velocityTracker = this.mVelocityTracker;
        if (velocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        } else {
            velocityTracker.clear();
        }
    }

    private void initVelocityTrackerIfNotExists() {
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }
    }

    private void recycleVelocityTracker() {
        VelocityTracker velocityTracker = this.mVelocityTracker;
        if (velocityTracker != null) {
            velocityTracker.recycle();
            this.mVelocityTracker = null;
        }
    }

    @Override // android.view.ViewGroup, android.view.ViewParent
    public void requestDisallowInterceptTouchEvent(boolean z) {
        if (z) {
            recycleVelocityTracker();
        }
        super.requestDisallowInterceptTouchEvent(z);
    }

    @Override // android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        boolean z = true;
        if (action == 2 && this.mIsBeingDragged) {
            return true;
        }
        int i = action & 255;
        if (i == 0) {
            int y = (int) motionEvent.getY();
            if (!inChild((int) motionEvent.getX(), y)) {
                if (!stopGlowAnimations(motionEvent) && this.mScroller.isFinished()) {
                    z = false;
                }
                this.mIsBeingDragged = z;
                recycleVelocityTracker();
            } else {
                this.mLastMotionY = y;
                this.mActivePointerId = motionEvent.getPointerId(0);
                initOrResetVelocityTracker();
                this.mVelocityTracker.addMovement(motionEvent);
                this.mScroller.computeScrollOffset();
                if (!stopGlowAnimations(motionEvent) && this.mScroller.isFinished()) {
                    z = false;
                }
                this.mIsBeingDragged = z;
                startNestedScroll(2, 0);
            }
        } else {
            if (i != 1) {
                if (i == 2) {
                    int i2 = this.mActivePointerId;
                    if (i2 != -1) {
                        int findPointerIndex = motionEvent.findPointerIndex(i2);
                        if (findPointerIndex == -1) {
                            Log.e(TAG, "Invalid pointerId=" + i2 + " in onInterceptTouchEvent");
                        } else {
                            int y2 = (int) motionEvent.getY(findPointerIndex);
                            if (Math.abs(y2 - this.mLastMotionY) > this.mTouchSlop && (2 & getNestedScrollAxes()) == 0) {
                                this.mIsBeingDragged = true;
                                this.mLastMotionY = y2;
                                initVelocityTrackerIfNotExists();
                                this.mVelocityTracker.addMovement(motionEvent);
                                this.mNestedYOffset = 0;
                                ViewParent parent = getParent();
                                if (parent != null) {
                                    parent.requestDisallowInterceptTouchEvent(true);
                                }
                            }
                        }
                    }
                } else if (i != 3) {
                    if (i == 6) {
                        onSecondaryPointerUp(motionEvent);
                    }
                }
            }
            this.mIsBeingDragged = false;
            this.mActivePointerId = -1;
            recycleVelocityTracker();
            if (this.mScroller.springBack(getScrollX(), getScrollY(), 0, 0, 0, getScrollRange())) {
                ViewCompat.postInvalidateOnAnimation(this);
            }
            stopNestedScroll(0);
        }
        return this.mIsBeingDragged;
    }

    /* JADX WARN: Removed duplicated region for block: B:78:0x01db  */
    @Override // android.view.View
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public boolean onTouchEvent(android.view.MotionEvent r24) {
        /*
            Method dump skipped, instructions count: 621
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.core.widget.NestedScrollView.onTouchEvent(android.view.MotionEvent):boolean");
    }

    private boolean edgeEffectFling(int i) {
        if (EdgeEffectCompat.getDistance(this.mEdgeGlowTop) != 0.0f) {
            this.mEdgeGlowTop.onAbsorb(i);
        } else if (EdgeEffectCompat.getDistance(this.mEdgeGlowBottom) == 0.0f) {
            return false;
        } else {
            this.mEdgeGlowBottom.onAbsorb(-i);
        }
        return true;
    }

    private boolean stopGlowAnimations(MotionEvent motionEvent) {
        boolean z;
        if (EdgeEffectCompat.getDistance(this.mEdgeGlowTop) != 0.0f) {
            EdgeEffectCompat.onPullDistance(this.mEdgeGlowTop, 0.0f, motionEvent.getX() / getWidth());
            z = true;
        } else {
            z = false;
        }
        if (EdgeEffectCompat.getDistance(this.mEdgeGlowBottom) != 0.0f) {
            EdgeEffectCompat.onPullDistance(this.mEdgeGlowBottom, 0.0f, 1.0f - (motionEvent.getX() / getWidth()));
            return true;
        }
        return z;
    }

    private void onSecondaryPointerUp(MotionEvent motionEvent) {
        int actionIndex = motionEvent.getActionIndex();
        if (motionEvent.getPointerId(actionIndex) == this.mActivePointerId) {
            int i = actionIndex == 0 ? 1 : 0;
            this.mLastMotionY = (int) motionEvent.getY(i);
            this.mActivePointerId = motionEvent.getPointerId(i);
            VelocityTracker velocityTracker = this.mVelocityTracker;
            if (velocityTracker != null) {
                velocityTracker.clear();
            }
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // android.view.View
    public boolean onGenericMotionEvent(MotionEvent motionEvent) {
        float axisValue;
        boolean z;
        int i = 0;
        if (motionEvent.getAction() == 8 && !this.mIsBeingDragged) {
            if (MotionEventCompat.isFromSource(motionEvent, 2)) {
                axisValue = motionEvent.getAxisValue(9);
            } else {
                axisValue = MotionEventCompat.isFromSource(motionEvent, 4194304) ? motionEvent.getAxisValue(26) : 0.0f;
            }
            if (axisValue != 0.0f) {
                int scrollRange = getScrollRange();
                int scrollY = getScrollY();
                int verticalScrollFactorCompat = scrollY - ((int) (axisValue * getVerticalScrollFactorCompat()));
                if (verticalScrollFactorCompat < 0) {
                    if (canOverScroll() && !MotionEventCompat.isFromSource(motionEvent, 8194)) {
                        EdgeEffectCompat.onPullDistance(this.mEdgeGlowTop, (-verticalScrollFactorCompat) / getHeight(), 0.5f);
                        this.mEdgeGlowTop.onRelease();
                        invalidate();
                        z = 1;
                    } else {
                        z = 0;
                    }
                } else if (verticalScrollFactorCompat > scrollRange) {
                    if (canOverScroll() && !MotionEventCompat.isFromSource(motionEvent, 8194)) {
                        EdgeEffectCompat.onPullDistance(this.mEdgeGlowBottom, (verticalScrollFactorCompat - scrollRange) / getHeight(), 0.5f);
                        this.mEdgeGlowBottom.onRelease();
                        invalidate();
                        i = 1;
                    }
                    z = i;
                    i = scrollRange;
                } else {
                    z = 0;
                    i = verticalScrollFactorCompat;
                }
                if (i != scrollY) {
                    super.scrollTo(getScrollX(), i);
                    return true;
                }
                return z;
            }
        }
        return false;
    }

    private boolean canOverScroll() {
        int overScrollMode = getOverScrollMode();
        if (overScrollMode != 0) {
            return overScrollMode == 1 && getScrollRange() > 0;
        }
        return true;
    }

    private float getVerticalScrollFactorCompat() {
        if (this.mVerticalScrollFactor == 0.0f) {
            TypedValue typedValue = new TypedValue();
            Context context = getContext();
            if (!context.getTheme().resolveAttribute(16842829, typedValue, true)) {
                throw new IllegalStateException("Expected theme to define listPreferredItemHeight.");
            }
            this.mVerticalScrollFactor = typedValue.getDimension(context.getResources().getDisplayMetrics());
        }
        return this.mVerticalScrollFactor;
    }

    @Override // android.view.View
    protected void onOverScrolled(int i, int i2, boolean z, boolean z2) {
        super.scrollTo(i, i2);
    }

    boolean overScrollByCompat(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, boolean z) {
        boolean z2;
        boolean z3;
        int overScrollMode = getOverScrollMode();
        boolean z4 = computeHorizontalScrollRange() > computeHorizontalScrollExtent();
        boolean z5 = computeVerticalScrollRange() > computeVerticalScrollExtent();
        boolean z6 = overScrollMode == 0 || (overScrollMode == 1 && z4);
        boolean z7 = overScrollMode == 0 || (overScrollMode == 1 && z5);
        int i9 = i3 + i;
        int i10 = !z6 ? 0 : i7;
        int i11 = i4 + i2;
        int i12 = !z7 ? 0 : i8;
        int i13 = -i10;
        int i14 = i10 + i5;
        int i15 = -i12;
        int i16 = i12 + i6;
        if (i9 > i14) {
            i9 = i14;
            z2 = true;
        } else if (i9 < i13) {
            z2 = true;
            i9 = i13;
        } else {
            z2 = false;
        }
        if (i11 > i16) {
            i11 = i16;
            z3 = true;
        } else if (i11 < i15) {
            z3 = true;
            i11 = i15;
        } else {
            z3 = false;
        }
        if (z3 && !hasNestedScrollingParent(1)) {
            this.mScroller.springBack(i9, i11, 0, 0, 0, getScrollRange());
        }
        onOverScrolled(i9, i11, z2, z3);
        return z2 || z3;
    }

    int getScrollRange() {
        if (getChildCount() > 0) {
            View childAt = getChildAt(0);
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) childAt.getLayoutParams();
            return Math.max(0, ((childAt.getHeight() + layoutParams.topMargin) + layoutParams.bottomMargin) - ((getHeight() - getPaddingTop()) - getPaddingBottom()));
        }
        return 0;
    }

    private View findFocusableViewInBounds(boolean z, int i, int i2) {
        ArrayList focusables = getFocusables(2);
        int size = focusables.size();
        View view = null;
        boolean z2 = false;
        for (int i3 = 0; i3 < size; i3++) {
            View view2 = (View) focusables.get(i3);
            int top = view2.getTop();
            int bottom = view2.getBottom();
            if (i < bottom && top < i2) {
                boolean z3 = i < top && bottom < i2;
                if (view == null) {
                    view = view2;
                    z2 = z3;
                } else {
                    boolean z4 = (z && top < view.getTop()) || (!z && bottom > view.getBottom());
                    if (z2) {
                        if (z3) {
                            if (!z4) {
                            }
                            view = view2;
                        }
                    } else if (z3) {
                        view = view2;
                        z2 = true;
                    } else {
                        if (!z4) {
                        }
                        view = view2;
                    }
                }
            }
        }
        return view;
    }

    public boolean pageScroll(int i) {
        boolean z = i == 130;
        int height = getHeight();
        if (z) {
            this.mTempRect.top = getScrollY() + height;
            int childCount = getChildCount();
            if (childCount > 0) {
                View childAt = getChildAt(childCount - 1);
                int bottom = childAt.getBottom() + ((FrameLayout.LayoutParams) childAt.getLayoutParams()).bottomMargin + getPaddingBottom();
                if (this.mTempRect.top + height > bottom) {
                    this.mTempRect.top = bottom - height;
                }
            }
        } else {
            this.mTempRect.top = getScrollY() - height;
            if (this.mTempRect.top < 0) {
                this.mTempRect.top = 0;
            }
        }
        Rect rect = this.mTempRect;
        rect.bottom = rect.top + height;
        return scrollAndFocus(i, this.mTempRect.top, this.mTempRect.bottom);
    }

    public boolean fullScroll(int i) {
        int childCount;
        boolean z = i == 130;
        int height = getHeight();
        this.mTempRect.top = 0;
        this.mTempRect.bottom = height;
        if (z && (childCount = getChildCount()) > 0) {
            View childAt = getChildAt(childCount - 1);
            this.mTempRect.bottom = childAt.getBottom() + ((FrameLayout.LayoutParams) childAt.getLayoutParams()).bottomMargin + getPaddingBottom();
            Rect rect = this.mTempRect;
            rect.top = rect.bottom - height;
        }
        return scrollAndFocus(i, this.mTempRect.top, this.mTempRect.bottom);
    }

    private boolean scrollAndFocus(int i, int i2, int i3) {
        int height = getHeight();
        int scrollY = getScrollY();
        int i4 = height + scrollY;
        boolean z = false;
        boolean z2 = i == 33;
        View findFocusableViewInBounds = findFocusableViewInBounds(z2, i2, i3);
        if (findFocusableViewInBounds == null) {
            findFocusableViewInBounds = this;
        }
        if (i2 < scrollY || i3 > i4) {
            doScrollY(z2 ? i2 - scrollY : i3 - i4);
            z = true;
        }
        if (findFocusableViewInBounds != findFocus()) {
            findFocusableViewInBounds.requestFocus(i);
        }
        return z;
    }

    public boolean arrowScroll(int i) {
        View findFocus = findFocus();
        if (findFocus == this) {
            findFocus = null;
        }
        View findNextFocus = FocusFinder.getInstance().findNextFocus(this, findFocus, i);
        int maxScrollAmount = getMaxScrollAmount();
        if (findNextFocus != null && isWithinDeltaOfScreen(findNextFocus, maxScrollAmount, getHeight())) {
            findNextFocus.getDrawingRect(this.mTempRect);
            offsetDescendantRectToMyCoords(findNextFocus, this.mTempRect);
            doScrollY(computeScrollDeltaToGetChildRectOnScreen(this.mTempRect));
            findNextFocus.requestFocus(i);
        } else {
            if (i == 33 && getScrollY() < maxScrollAmount) {
                maxScrollAmount = getScrollY();
            } else if (i == 130 && getChildCount() > 0) {
                View childAt = getChildAt(0);
                maxScrollAmount = Math.min((childAt.getBottom() + ((FrameLayout.LayoutParams) childAt.getLayoutParams()).bottomMargin) - ((getScrollY() + getHeight()) - getPaddingBottom()), maxScrollAmount);
            }
            if (maxScrollAmount == 0) {
                return false;
            }
            if (i != 130) {
                maxScrollAmount = -maxScrollAmount;
            }
            doScrollY(maxScrollAmount);
        }
        if (findFocus != null && findFocus.isFocused() && isOffScreen(findFocus)) {
            int descendantFocusability = getDescendantFocusability();
            setDescendantFocusability(131072);
            requestFocus();
            setDescendantFocusability(descendantFocusability);
            return true;
        }
        return true;
    }

    private boolean isOffScreen(View view) {
        return !isWithinDeltaOfScreen(view, 0, getHeight());
    }

    private boolean isWithinDeltaOfScreen(View view, int i, int i2) {
        view.getDrawingRect(this.mTempRect);
        offsetDescendantRectToMyCoords(view, this.mTempRect);
        return this.mTempRect.bottom + i >= getScrollY() && this.mTempRect.top - i <= getScrollY() + i2;
    }

    private void doScrollY(int i) {
        if (i != 0) {
            if (this.mSmoothScrollingEnabled) {
                smoothScrollBy(0, i);
            } else {
                scrollBy(0, i);
            }
        }
    }

    public final void smoothScrollBy(int i, int i2) {
        smoothScrollBy(i, i2, ItemTouchHelper.Callback.DEFAULT_SWIPE_ANIMATION_DURATION, false);
    }

    public final void smoothScrollBy(int i, int i2, int i3) {
        smoothScrollBy(i, i2, i3, false);
    }

    private void smoothScrollBy(int i, int i2, int i3, boolean z) {
        if (getChildCount() == 0) {
            return;
        }
        if (AnimationUtils.currentAnimationTimeMillis() - this.mLastScroll > 250) {
            View childAt = getChildAt(0);
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) childAt.getLayoutParams();
            int scrollY = getScrollY();
            OverScroller overScroller = this.mScroller;
            int scrollX = getScrollX();
            overScroller.startScroll(scrollX, scrollY, 0, Math.max(0, Math.min(i2 + scrollY, Math.max(0, ((childAt.getHeight() + layoutParams.topMargin) + layoutParams.bottomMargin) - ((getHeight() - getPaddingTop()) - getPaddingBottom())))) - scrollY, i3);
            runAnimatedScroll(z);
        } else {
            if (!this.mScroller.isFinished()) {
                abortAnimatedScroll();
            }
            scrollBy(i, i2);
        }
        this.mLastScroll = AnimationUtils.currentAnimationTimeMillis();
    }

    public final void smoothScrollTo(int i, int i2) {
        smoothScrollTo(i, i2, ItemTouchHelper.Callback.DEFAULT_SWIPE_ANIMATION_DURATION, false);
    }

    public final void smoothScrollTo(int i, int i2, int i3) {
        smoothScrollTo(i, i2, i3, false);
    }

    void smoothScrollTo(int i, int i2, boolean z) {
        smoothScrollTo(i, i2, ItemTouchHelper.Callback.DEFAULT_SWIPE_ANIMATION_DURATION, z);
    }

    void smoothScrollTo(int i, int i2, int i3, boolean z) {
        smoothScrollBy(i - getScrollX(), i2 - getScrollY(), i3, z);
    }

    @Override // android.view.View, androidx.core.view.ScrollingView
    public int computeVerticalScrollRange() {
        int childCount = getChildCount();
        int height = (getHeight() - getPaddingBottom()) - getPaddingTop();
        if (childCount == 0) {
            return height;
        }
        View childAt = getChildAt(0);
        int bottom = childAt.getBottom() + ((FrameLayout.LayoutParams) childAt.getLayoutParams()).bottomMargin;
        int scrollY = getScrollY();
        int max = Math.max(0, bottom - height);
        return scrollY < 0 ? bottom - scrollY : scrollY > max ? bottom + (scrollY - max) : bottom;
    }

    @Override // android.view.View, androidx.core.view.ScrollingView
    public int computeVerticalScrollOffset() {
        return Math.max(0, super.computeVerticalScrollOffset());
    }

    @Override // android.view.View, androidx.core.view.ScrollingView
    public int computeVerticalScrollExtent() {
        return super.computeVerticalScrollExtent();
    }

    @Override // android.view.View, androidx.core.view.ScrollingView
    public int computeHorizontalScrollRange() {
        return super.computeHorizontalScrollRange();
    }

    @Override // android.view.View, androidx.core.view.ScrollingView
    public int computeHorizontalScrollOffset() {
        return super.computeHorizontalScrollOffset();
    }

    @Override // android.view.View, androidx.core.view.ScrollingView
    public int computeHorizontalScrollExtent() {
        return super.computeHorizontalScrollExtent();
    }

    @Override // android.view.ViewGroup
    protected void measureChild(View view, int i, int i2) {
        view.measure(getChildMeasureSpec(i, getPaddingLeft() + getPaddingRight(), view.getLayoutParams().width), View.MeasureSpec.makeMeasureSpec(0, 0));
    }

    @Override // android.view.ViewGroup
    protected void measureChildWithMargins(View view, int i, int i2, int i3, int i4) {
        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        view.measure(getChildMeasureSpec(i, getPaddingLeft() + getPaddingRight() + marginLayoutParams.leftMargin + marginLayoutParams.rightMargin + i2, marginLayoutParams.width), View.MeasureSpec.makeMeasureSpec(marginLayoutParams.topMargin + marginLayoutParams.bottomMargin, 0));
    }

    @Override // android.view.View
    public void computeScroll() {
        if (this.mScroller.isFinished()) {
            return;
        }
        this.mScroller.computeScrollOffset();
        int currY = this.mScroller.getCurrY();
        int i = currY - this.mLastScrollerY;
        this.mLastScrollerY = currY;
        int[] iArr = this.mScrollConsumed;
        boolean z = false;
        iArr[1] = 0;
        dispatchNestedPreScroll(0, i, iArr, null, 1);
        int i2 = i - this.mScrollConsumed[1];
        int scrollRange = getScrollRange();
        if (i2 != 0) {
            int scrollY = getScrollY();
            overScrollByCompat(0, i2, getScrollX(), scrollY, 0, scrollRange, 0, 0, false);
            int scrollY2 = getScrollY() - scrollY;
            int i3 = i2 - scrollY2;
            int[] iArr2 = this.mScrollConsumed;
            iArr2[1] = 0;
            dispatchNestedScroll(0, scrollY2, 0, i3, this.mScrollOffset, 1, iArr2);
            i2 = i3 - this.mScrollConsumed[1];
        }
        if (i2 != 0) {
            int overScrollMode = getOverScrollMode();
            if (overScrollMode == 0 || (overScrollMode == 1 && scrollRange > 0)) {
                z = true;
            }
            if (z) {
                if (i2 < 0) {
                    if (this.mEdgeGlowTop.isFinished()) {
                        this.mEdgeGlowTop.onAbsorb((int) this.mScroller.getCurrVelocity());
                    }
                } else if (this.mEdgeGlowBottom.isFinished()) {
                    this.mEdgeGlowBottom.onAbsorb((int) this.mScroller.getCurrVelocity());
                }
            }
            abortAnimatedScroll();
        }
        if (!this.mScroller.isFinished()) {
            ViewCompat.postInvalidateOnAnimation(this);
        } else {
            stopNestedScroll(1);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:15:0x0060  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private int releaseVerticalGlow(int r4, float r5) {
        /*
            r3 = this;
            int r0 = r3.getWidth()
            float r0 = (float) r0
            float r5 = r5 / r0
            float r4 = (float) r4
            int r0 = r3.getHeight()
            float r0 = (float) r0
            float r4 = r4 / r0
            android.widget.EdgeEffect r0 = r3.mEdgeGlowTop
            float r0 = androidx.core.widget.EdgeEffectCompat.getDistance(r0)
            r1 = 0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 == 0) goto L31
            android.widget.EdgeEffect r0 = r3.mEdgeGlowTop
            float r4 = -r4
            float r4 = androidx.core.widget.EdgeEffectCompat.onPullDistance(r0, r4, r5)
            float r4 = -r4
            android.widget.EdgeEffect r5 = r3.mEdgeGlowTop
            float r5 = androidx.core.widget.EdgeEffectCompat.getDistance(r5)
            int r5 = (r5 > r1 ? 1 : (r5 == r1 ? 0 : -1))
            if (r5 != 0) goto L2f
            android.widget.EdgeEffect r5 = r3.mEdgeGlowTop
            r5.onRelease()
        L2f:
            r1 = r4
            goto L54
        L31:
            android.widget.EdgeEffect r0 = r3.mEdgeGlowBottom
            float r0 = androidx.core.widget.EdgeEffectCompat.getDistance(r0)
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 == 0) goto L54
            android.widget.EdgeEffect r0 = r3.mEdgeGlowBottom
            r2 = 1065353216(0x3f800000, float:1.0)
            float r2 = r2 - r5
            float r4 = androidx.core.widget.EdgeEffectCompat.onPullDistance(r0, r4, r2)
            android.widget.EdgeEffect r5 = r3.mEdgeGlowBottom
            float r5 = androidx.core.widget.EdgeEffectCompat.getDistance(r5)
            int r5 = (r5 > r1 ? 1 : (r5 == r1 ? 0 : -1))
            if (r5 != 0) goto L2f
            android.widget.EdgeEffect r5 = r3.mEdgeGlowBottom
            r5.onRelease()
            goto L2f
        L54:
            int r4 = r3.getHeight()
            float r4 = (float) r4
            float r1 = r1 * r4
            int r4 = java.lang.Math.round(r1)
            if (r4 == 0) goto L63
            r3.invalidate()
        L63:
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.core.widget.NestedScrollView.releaseVerticalGlow(int, float):int");
    }

    private void runAnimatedScroll(boolean z) {
        if (z) {
            startNestedScroll(2, 1);
        } else {
            stopNestedScroll(1);
        }
        this.mLastScrollerY = getScrollY();
        ViewCompat.postInvalidateOnAnimation(this);
    }

    private void abortAnimatedScroll() {
        this.mScroller.abortAnimation();
        stopNestedScroll(1);
    }

    private void scrollToChild(View view) {
        view.getDrawingRect(this.mTempRect);
        offsetDescendantRectToMyCoords(view, this.mTempRect);
        int computeScrollDeltaToGetChildRectOnScreen = computeScrollDeltaToGetChildRectOnScreen(this.mTempRect);
        if (computeScrollDeltaToGetChildRectOnScreen != 0) {
            scrollBy(0, computeScrollDeltaToGetChildRectOnScreen);
        }
    }

    private boolean scrollToChildRect(Rect rect, boolean z) {
        int computeScrollDeltaToGetChildRectOnScreen = computeScrollDeltaToGetChildRectOnScreen(rect);
        boolean z2 = computeScrollDeltaToGetChildRectOnScreen != 0;
        if (z2) {
            if (z) {
                scrollBy(0, computeScrollDeltaToGetChildRectOnScreen);
            } else {
                smoothScrollBy(0, computeScrollDeltaToGetChildRectOnScreen);
            }
        }
        return z2;
    }

    protected int computeScrollDeltaToGetChildRectOnScreen(Rect rect) {
        int i;
        int i2;
        if (getChildCount() == 0) {
            return 0;
        }
        int height = getHeight();
        int scrollY = getScrollY();
        int i3 = scrollY + height;
        int verticalFadingEdgeLength = getVerticalFadingEdgeLength();
        if (rect.top > 0) {
            scrollY += verticalFadingEdgeLength;
        }
        View childAt = getChildAt(0);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) childAt.getLayoutParams();
        int i4 = rect.bottom < (childAt.getHeight() + layoutParams.topMargin) + layoutParams.bottomMargin ? i3 - verticalFadingEdgeLength : i3;
        if (rect.bottom > i4 && rect.top > scrollY) {
            if (rect.height() > height) {
                i2 = rect.top - scrollY;
            } else {
                i2 = rect.bottom - i4;
            }
            return Math.min(i2 + 0, (childAt.getBottom() + layoutParams.bottomMargin) - i3);
        } else if (rect.top >= scrollY || rect.bottom >= i4) {
            return 0;
        } else {
            if (rect.height() > height) {
                i = 0 - (i4 - rect.bottom);
            } else {
                i = 0 - (scrollY - rect.top);
            }
            return Math.max(i, -getScrollY());
        }
    }

    @Override // android.view.ViewGroup, android.view.ViewParent
    public void requestChildFocus(View view, View view2) {
        if (!this.mIsLayoutDirty) {
            scrollToChild(view2);
        } else {
            this.mChildToScrollTo = view2;
        }
        super.requestChildFocus(view, view2);
    }

    @Override // android.view.ViewGroup
    protected boolean onRequestFocusInDescendants(int i, Rect rect) {
        View findNextFocusFromRect;
        if (i == 2) {
            i = 130;
        } else if (i == 1) {
            i = 33;
        }
        if (rect == null) {
            findNextFocusFromRect = FocusFinder.getInstance().findNextFocus(this, null, i);
        } else {
            findNextFocusFromRect = FocusFinder.getInstance().findNextFocusFromRect(this, rect, i);
        }
        if (findNextFocusFromRect == null || isOffScreen(findNextFocusFromRect)) {
            return false;
        }
        return findNextFocusFromRect.requestFocus(i, rect);
    }

    @Override // android.view.ViewGroup, android.view.ViewParent
    public boolean requestChildRectangleOnScreen(View view, Rect rect, boolean z) {
        rect.offset(view.getLeft() - view.getScrollX(), view.getTop() - view.getScrollY());
        return scrollToChildRect(rect, z);
    }

    @Override // android.view.View, android.view.ViewParent
    public void requestLayout() {
        this.mIsLayoutDirty = true;
        super.requestLayout();
    }

    @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        int i5 = 0;
        this.mIsLayoutDirty = false;
        View view = this.mChildToScrollTo;
        if (view != null && isViewDescendantOf(view, this)) {
            scrollToChild(this.mChildToScrollTo);
        }
        this.mChildToScrollTo = null;
        if (!this.mIsLaidOut) {
            if (this.mSavedState != null) {
                scrollTo(getScrollX(), this.mSavedState.scrollPosition);
                this.mSavedState = null;
            }
            if (getChildCount() > 0) {
                View childAt = getChildAt(0);
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) childAt.getLayoutParams();
                i5 = childAt.getMeasuredHeight() + layoutParams.topMargin + layoutParams.bottomMargin;
            }
            int paddingTop = ((i4 - i2) - getPaddingTop()) - getPaddingBottom();
            int scrollY = getScrollY();
            int clamp = clamp(scrollY, paddingTop, i5);
            if (clamp != scrollY) {
                scrollTo(getScrollX(), clamp);
            }
        }
        scrollTo(getScrollX(), getScrollY());
        this.mIsLaidOut = true;
    }

    @Override // android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mIsLaidOut = false;
    }

    @Override // android.view.View
    protected void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        View findFocus = findFocus();
        if (findFocus == null || this == findFocus || !isWithinDeltaOfScreen(findFocus, 0, i4)) {
            return;
        }
        findFocus.getDrawingRect(this.mTempRect);
        offsetDescendantRectToMyCoords(findFocus, this.mTempRect);
        doScrollY(computeScrollDeltaToGetChildRectOnScreen(this.mTempRect));
    }

    private static boolean isViewDescendantOf(View view, View view2) {
        if (view == view2) {
            return true;
        }
        ViewParent parent = view.getParent();
        return (parent instanceof ViewGroup) && isViewDescendantOf((View) parent, view2);
    }

    public void fling(int i) {
        if (getChildCount() > 0) {
            this.mScroller.fling(getScrollX(), getScrollY(), 0, i, 0, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, 0, 0);
            runAnimatedScroll(true);
        }
    }

    private void endDrag() {
        this.mIsBeingDragged = false;
        recycleVelocityTracker();
        stopNestedScroll(0);
        this.mEdgeGlowTop.onRelease();
        this.mEdgeGlowBottom.onRelease();
    }

    @Override // android.view.View
    public void scrollTo(int i, int i2) {
        if (getChildCount() > 0) {
            View childAt = getChildAt(0);
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) childAt.getLayoutParams();
            int clamp = clamp(i, (getWidth() - getPaddingLeft()) - getPaddingRight(), childAt.getWidth() + layoutParams.leftMargin + layoutParams.rightMargin);
            int clamp2 = clamp(i2, (getHeight() - getPaddingTop()) - getPaddingBottom(), childAt.getHeight() + layoutParams.topMargin + layoutParams.bottomMargin);
            if (clamp == getScrollX() && clamp2 == getScrollY()) {
                return;
            }
            super.scrollTo(clamp, clamp2);
        }
    }

    @Override // android.view.View
    public void draw(Canvas canvas) {
        int paddingLeft;
        super.draw(canvas);
        int scrollY = getScrollY();
        int i = 0;
        if (!this.mEdgeGlowTop.isFinished()) {
            int save = canvas.save();
            int width = getWidth();
            int height = getHeight();
            int min = Math.min(0, scrollY);
            if (Build.VERSION.SDK_INT < 21 || Api21Impl.getClipToPadding(this)) {
                width -= getPaddingLeft() + getPaddingRight();
                paddingLeft = getPaddingLeft() + 0;
            } else {
                paddingLeft = 0;
            }
            if (Build.VERSION.SDK_INT >= 21 && Api21Impl.getClipToPadding(this)) {
                height -= getPaddingTop() + getPaddingBottom();
                min += getPaddingTop();
            }
            canvas.translate(paddingLeft, min);
            this.mEdgeGlowTop.setSize(width, height);
            if (this.mEdgeGlowTop.draw(canvas)) {
                ViewCompat.postInvalidateOnAnimation(this);
            }
            canvas.restoreToCount(save);
        }
        if (this.mEdgeGlowBottom.isFinished()) {
            return;
        }
        int save2 = canvas.save();
        int width2 = getWidth();
        int height2 = getHeight();
        int max = Math.max(getScrollRange(), scrollY) + height2;
        if (Build.VERSION.SDK_INT < 21 || Api21Impl.getClipToPadding(this)) {
            width2 -= getPaddingLeft() + getPaddingRight();
            i = 0 + getPaddingLeft();
        }
        if (Build.VERSION.SDK_INT >= 21 && Api21Impl.getClipToPadding(this)) {
            height2 -= getPaddingTop() + getPaddingBottom();
            max -= getPaddingBottom();
        }
        canvas.translate(i - width2, max);
        canvas.rotate(180.0f, width2, 0.0f);
        this.mEdgeGlowBottom.setSize(width2, height2);
        if (this.mEdgeGlowBottom.draw(canvas)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
        canvas.restoreToCount(save2);
    }

    @Override // android.view.View
    protected void onRestoreInstanceState(Parcelable parcelable) {
        if (!(parcelable instanceof SavedState)) {
            super.onRestoreInstanceState(parcelable);
            return;
        }
        SavedState savedState = (SavedState) parcelable;
        super.onRestoreInstanceState(savedState.getSuperState());
        this.mSavedState = savedState;
        requestLayout();
    }

    @Override // android.view.View
    protected Parcelable onSaveInstanceState() {
        SavedState savedState = new SavedState(super.onSaveInstanceState());
        savedState.scrollPosition = getScrollY();
        return savedState;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class SavedState extends View.BaseSavedState {
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() { // from class: androidx.core.widget.NestedScrollView.SavedState.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public SavedState createFromParcel(Parcel parcel) {
                return new SavedState(parcel);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public SavedState[] newArray(int i) {
                return new SavedState[i];
            }
        };
        public int scrollPosition;

        SavedState(Parcelable parcelable) {
            super(parcelable);
        }

        SavedState(Parcel parcel) {
            super(parcel);
            this.scrollPosition = parcel.readInt();
        }

        @Override // android.view.View.BaseSavedState, android.view.AbsSavedState, android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            super.writeToParcel(parcel, i);
            parcel.writeInt(this.scrollPosition);
        }

        public String toString() {
            return "HorizontalScrollView.SavedState{" + Integer.toHexString(System.identityHashCode(this)) + " scrollPosition=" + this.scrollPosition + "}";
        }
    }

    /* loaded from: classes.dex */
    static class AccessibilityDelegate extends AccessibilityDelegateCompat {
        AccessibilityDelegate() {
        }

        @Override // androidx.core.view.AccessibilityDelegateCompat
        public boolean performAccessibilityAction(View view, int i, Bundle bundle) {
            if (super.performAccessibilityAction(view, i, bundle)) {
                return true;
            }
            NestedScrollView nestedScrollView = (NestedScrollView) view;
            if (nestedScrollView.isEnabled()) {
                int height = nestedScrollView.getHeight();
                Rect rect = new Rect();
                if (nestedScrollView.getMatrix().isIdentity() && nestedScrollView.getGlobalVisibleRect(rect)) {
                    height = rect.height();
                }
                if (i != 4096) {
                    if (i == 8192 || i == 16908344) {
                        int max = Math.max(nestedScrollView.getScrollY() - ((height - nestedScrollView.getPaddingBottom()) - nestedScrollView.getPaddingTop()), 0);
                        if (max != nestedScrollView.getScrollY()) {
                            nestedScrollView.smoothScrollTo(0, max, true);
                            return true;
                        }
                        return false;
                    } else if (i != 16908346) {
                        return false;
                    }
                }
                int min = Math.min(nestedScrollView.getScrollY() + ((height - nestedScrollView.getPaddingBottom()) - nestedScrollView.getPaddingTop()), nestedScrollView.getScrollRange());
                if (min != nestedScrollView.getScrollY()) {
                    nestedScrollView.smoothScrollTo(0, min, true);
                    return true;
                }
                return false;
            }
            return false;
        }

        @Override // androidx.core.view.AccessibilityDelegateCompat
        public void onInitializeAccessibilityNodeInfo(View view, AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
            int scrollRange;
            super.onInitializeAccessibilityNodeInfo(view, accessibilityNodeInfoCompat);
            NestedScrollView nestedScrollView = (NestedScrollView) view;
            accessibilityNodeInfoCompat.setClassName(ScrollView.class.getName());
            if (!nestedScrollView.isEnabled() || (scrollRange = nestedScrollView.getScrollRange()) <= 0) {
                return;
            }
            accessibilityNodeInfoCompat.setScrollable(true);
            if (nestedScrollView.getScrollY() > 0) {
                accessibilityNodeInfoCompat.addAction(AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_SCROLL_BACKWARD);
                accessibilityNodeInfoCompat.addAction(AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_SCROLL_UP);
            }
            if (nestedScrollView.getScrollY() < scrollRange) {
                accessibilityNodeInfoCompat.addAction(AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_SCROLL_FORWARD);
                accessibilityNodeInfoCompat.addAction(AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_SCROLL_DOWN);
            }
        }

        @Override // androidx.core.view.AccessibilityDelegateCompat
        public void onInitializeAccessibilityEvent(View view, AccessibilityEvent accessibilityEvent) {
            super.onInitializeAccessibilityEvent(view, accessibilityEvent);
            NestedScrollView nestedScrollView = (NestedScrollView) view;
            accessibilityEvent.setClassName(ScrollView.class.getName());
            accessibilityEvent.setScrollable(nestedScrollView.getScrollRange() > 0);
            accessibilityEvent.setScrollX(nestedScrollView.getScrollX());
            accessibilityEvent.setScrollY(nestedScrollView.getScrollY());
            AccessibilityRecordCompat.setMaxScrollX(accessibilityEvent, nestedScrollView.getScrollX());
            AccessibilityRecordCompat.setMaxScrollY(accessibilityEvent, nestedScrollView.getScrollRange());
        }
    }

    /* loaded from: classes.dex */
    static class Api21Impl {
        private Api21Impl() {
        }

        static boolean getClipToPadding(ViewGroup viewGroup) {
            return viewGroup.getClipToPadding();
        }
    }
}
