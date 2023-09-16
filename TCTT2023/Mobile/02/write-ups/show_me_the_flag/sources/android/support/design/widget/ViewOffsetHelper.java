package android.support.design.widget;

import android.support.v4.view.ViewCompat;
import android.view.View;
/* loaded from: classes.dex */
class ViewOffsetHelper {
    private int mLayoutLeft;
    private int mLayoutTop;
    private int mOffsetLeft;
    private int mOffsetTop;
    private final View mView;

    public ViewOffsetHelper(View view) {
        this.mView = view;
    }

    public void onViewLayout() {
        this.mLayoutTop = this.mView.getTop();
        this.mLayoutLeft = this.mView.getLeft();
        updateOffsets();
    }

    private void updateOffsets() {
        View view = this.mView;
        ViewCompat.offsetTopAndBottom(view, this.mOffsetTop - (view.getTop() - this.mLayoutTop));
        View view2 = this.mView;
        ViewCompat.offsetLeftAndRight(view2, this.mOffsetLeft - (view2.getLeft() - this.mLayoutLeft));
    }

    public boolean setTopAndBottomOffset(int i) {
        if (this.mOffsetTop != i) {
            this.mOffsetTop = i;
            updateOffsets();
            return true;
        }
        return false;
    }

    public boolean setLeftAndRightOffset(int i) {
        if (this.mOffsetLeft != i) {
            this.mOffsetLeft = i;
            updateOffsets();
            return true;
        }
        return false;
    }

    public int getTopAndBottomOffset() {
        return this.mOffsetTop;
    }

    public int getLeftAndRightOffset() {
        return this.mOffsetLeft;
    }

    public int getLayoutTop() {
        return this.mLayoutTop;
    }

    public int getLayoutLeft() {
        return this.mLayoutLeft;
    }
}
