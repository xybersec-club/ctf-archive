package androidx.core.view;
/* loaded from: classes.dex */
public interface NestedScrollingChild {
    boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed);

    boolean dispatchNestedPreFling(float velocityX, float velocityY);

    boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow);

    boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int[] offsetInWindow);

    boolean hasNestedScrollingParent();

    boolean isNestedScrollingEnabled();

    void setNestedScrollingEnabled(boolean enabled);

    boolean startNestedScroll(int axes);

    void stopNestedScroll();
}
