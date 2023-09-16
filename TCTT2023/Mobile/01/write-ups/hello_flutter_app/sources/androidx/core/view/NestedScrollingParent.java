package androidx.core.view;

import android.view.View;
/* loaded from: classes.dex */
public interface NestedScrollingParent {
    int getNestedScrollAxes();

    boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed);

    boolean onNestedPreFling(View target, float velocityX, float velocityY);

    void onNestedPreScroll(View target, int dx, int dy, int[] consumed);

    void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed);

    void onNestedScrollAccepted(View child, View target, int axes);

    boolean onStartNestedScroll(View child, View target, int axes);

    void onStopNestedScroll(View target);
}
