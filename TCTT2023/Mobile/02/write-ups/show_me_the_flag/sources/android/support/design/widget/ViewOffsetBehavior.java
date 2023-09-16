package android.support.design.widget;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;
/* loaded from: classes.dex */
class ViewOffsetBehavior<V extends View> extends CoordinatorLayout.Behavior<V> {
    private int mTempLeftRightOffset;
    private int mTempTopBottomOffset;
    private ViewOffsetHelper mViewOffsetHelper;

    public ViewOffsetBehavior() {
        this.mTempTopBottomOffset = 0;
        this.mTempLeftRightOffset = 0;
    }

    public ViewOffsetBehavior(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mTempTopBottomOffset = 0;
        this.mTempLeftRightOffset = 0;
    }

    @Override // android.support.design.widget.CoordinatorLayout.Behavior
    public boolean onLayoutChild(CoordinatorLayout coordinatorLayout, V v, int i) {
        layoutChild(coordinatorLayout, v, i);
        if (this.mViewOffsetHelper == null) {
            this.mViewOffsetHelper = new ViewOffsetHelper(v);
        }
        this.mViewOffsetHelper.onViewLayout();
        int i2 = this.mTempTopBottomOffset;
        if (i2 != 0) {
            this.mViewOffsetHelper.setTopAndBottomOffset(i2);
            this.mTempTopBottomOffset = 0;
        }
        int i3 = this.mTempLeftRightOffset;
        if (i3 != 0) {
            this.mViewOffsetHelper.setLeftAndRightOffset(i3);
            this.mTempLeftRightOffset = 0;
            return true;
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void layoutChild(CoordinatorLayout coordinatorLayout, V v, int i) {
        coordinatorLayout.onLayoutChild(v, i);
    }

    public boolean setTopAndBottomOffset(int i) {
        ViewOffsetHelper viewOffsetHelper = this.mViewOffsetHelper;
        if (viewOffsetHelper != null) {
            return viewOffsetHelper.setTopAndBottomOffset(i);
        }
        this.mTempTopBottomOffset = i;
        return false;
    }

    public boolean setLeftAndRightOffset(int i) {
        ViewOffsetHelper viewOffsetHelper = this.mViewOffsetHelper;
        if (viewOffsetHelper != null) {
            return viewOffsetHelper.setLeftAndRightOffset(i);
        }
        this.mTempLeftRightOffset = i;
        return false;
    }

    public int getTopAndBottomOffset() {
        ViewOffsetHelper viewOffsetHelper = this.mViewOffsetHelper;
        if (viewOffsetHelper != null) {
            return viewOffsetHelper.getTopAndBottomOffset();
        }
        return 0;
    }

    public int getLeftAndRightOffset() {
        ViewOffsetHelper viewOffsetHelper = this.mViewOffsetHelper;
        if (viewOffsetHelper != null) {
            return viewOffsetHelper.getLeftAndRightOffset();
        }
        return 0;
    }
}
