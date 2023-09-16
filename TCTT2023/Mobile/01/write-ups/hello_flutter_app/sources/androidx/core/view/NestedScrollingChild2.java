package androidx.core.view;
/* loaded from: classes.dex */
public interface NestedScrollingChild2 extends NestedScrollingChild {
    boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow, int type);

    boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int[] offsetInWindow, int type);

    boolean hasNestedScrollingParent(int type);

    boolean startNestedScroll(int axes, int type);

    void stopNestedScroll(int type);
}
