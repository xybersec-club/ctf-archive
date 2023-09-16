package android.support.constraint.solver.widgets;

import android.support.constraint.solver.LinearSystem;
import android.support.constraint.solver.widgets.ConstraintAnchor;
import android.support.constraint.solver.widgets.ConstraintWidget;
import java.util.ArrayList;
import java.util.Arrays;
/* loaded from: classes.dex */
public class ConstraintWidgetContainer extends WidgetContainer {
    static boolean ALLOW_ROOT_GROUP = true;
    private static final int CHAIN_FIRST = 0;
    private static final int CHAIN_FIRST_VISIBLE = 2;
    private static final int CHAIN_LAST = 1;
    private static final int CHAIN_LAST_VISIBLE = 3;
    private static final boolean DEBUG = false;
    private static final boolean DEBUG_LAYOUT = false;
    private static final boolean DEBUG_OPTIMIZE = false;
    private static final int FLAG_CHAIN_DANGLING = 1;
    private static final int FLAG_CHAIN_OPTIMIZE = 0;
    private static final int FLAG_RECOMPUTE_BOUNDS = 2;
    private static final int MAX_ITERATIONS = 8;
    public static final int OPTIMIZATION_ALL = 2;
    public static final int OPTIMIZATION_BASIC = 4;
    public static final int OPTIMIZATION_CHAIN = 8;
    public static final int OPTIMIZATION_NONE = 1;
    private static final boolean USE_SNAPSHOT = true;
    private static final boolean USE_THREAD = false;
    private boolean[] flags;
    protected LinearSystem mBackgroundSystem;
    private ConstraintWidget[] mChainEnds;
    private boolean mHeightMeasuredTooSmall;
    private ConstraintWidget[] mHorizontalChainsArray;
    private int mHorizontalChainsSize;
    private ConstraintWidget[] mMatchConstraintsChainedWidgets;
    private int mOptimizationLevel;
    int mPaddingBottom;
    int mPaddingLeft;
    int mPaddingRight;
    int mPaddingTop;
    private Snapshot mSnapshot;
    protected LinearSystem mSystem;
    private ConstraintWidget[] mVerticalChainsArray;
    private int mVerticalChainsSize;
    private boolean mWidthMeasuredTooSmall;
    int mWrapHeight;
    int mWrapWidth;

    @Override // android.support.constraint.solver.widgets.ConstraintWidget
    public String getType() {
        return "ConstraintLayout";
    }

    public boolean handlesInternalConstraints() {
        return false;
    }

    public ConstraintWidgetContainer() {
        this.mSystem = new LinearSystem();
        this.mBackgroundSystem = null;
        this.mHorizontalChainsSize = 0;
        this.mVerticalChainsSize = 0;
        this.mMatchConstraintsChainedWidgets = new ConstraintWidget[4];
        this.mVerticalChainsArray = new ConstraintWidget[4];
        this.mHorizontalChainsArray = new ConstraintWidget[4];
        this.mOptimizationLevel = 2;
        this.flags = new boolean[3];
        this.mChainEnds = new ConstraintWidget[4];
        this.mWidthMeasuredTooSmall = false;
        this.mHeightMeasuredTooSmall = false;
    }

    public ConstraintWidgetContainer(int i, int i2, int i3, int i4) {
        super(i, i2, i3, i4);
        this.mSystem = new LinearSystem();
        this.mBackgroundSystem = null;
        this.mHorizontalChainsSize = 0;
        this.mVerticalChainsSize = 0;
        this.mMatchConstraintsChainedWidgets = new ConstraintWidget[4];
        this.mVerticalChainsArray = new ConstraintWidget[4];
        this.mHorizontalChainsArray = new ConstraintWidget[4];
        this.mOptimizationLevel = 2;
        this.flags = new boolean[3];
        this.mChainEnds = new ConstraintWidget[4];
        this.mWidthMeasuredTooSmall = false;
        this.mHeightMeasuredTooSmall = false;
    }

    public ConstraintWidgetContainer(int i, int i2) {
        super(i, i2);
        this.mSystem = new LinearSystem();
        this.mBackgroundSystem = null;
        this.mHorizontalChainsSize = 0;
        this.mVerticalChainsSize = 0;
        this.mMatchConstraintsChainedWidgets = new ConstraintWidget[4];
        this.mVerticalChainsArray = new ConstraintWidget[4];
        this.mHorizontalChainsArray = new ConstraintWidget[4];
        this.mOptimizationLevel = 2;
        this.flags = new boolean[3];
        this.mChainEnds = new ConstraintWidget[4];
        this.mWidthMeasuredTooSmall = false;
        this.mHeightMeasuredTooSmall = false;
    }

    public void setOptimizationLevel(int i) {
        this.mOptimizationLevel = i;
    }

    @Override // android.support.constraint.solver.widgets.WidgetContainer, android.support.constraint.solver.widgets.ConstraintWidget
    public void reset() {
        this.mSystem.reset();
        this.mPaddingLeft = 0;
        this.mPaddingRight = 0;
        this.mPaddingTop = 0;
        this.mPaddingBottom = 0;
        super.reset();
    }

    public boolean isWidthMeasuredTooSmall() {
        return this.mWidthMeasuredTooSmall;
    }

    public boolean isHeightMeasuredTooSmall() {
        return this.mHeightMeasuredTooSmall;
    }

    public static ConstraintWidgetContainer createContainer(ConstraintWidgetContainer constraintWidgetContainer, String str, ArrayList<ConstraintWidget> arrayList, int i) {
        Rectangle bounds = getBounds(arrayList);
        if (bounds.width == 0 || bounds.height == 0) {
            return null;
        }
        if (i > 0) {
            int min = Math.min(bounds.x, bounds.y);
            if (i > min) {
                i = min;
            }
            bounds.grow(i, i);
        }
        constraintWidgetContainer.setOrigin(bounds.x, bounds.y);
        constraintWidgetContainer.setDimension(bounds.width, bounds.height);
        constraintWidgetContainer.setDebugName(str);
        ConstraintWidget parent = arrayList.get(0).getParent();
        int size = arrayList.size();
        for (int i2 = 0; i2 < size; i2++) {
            ConstraintWidget constraintWidget = arrayList.get(i2);
            if (constraintWidget.getParent() == parent) {
                constraintWidgetContainer.add(constraintWidget);
                constraintWidget.setX(constraintWidget.getX() - bounds.x);
                constraintWidget.setY(constraintWidget.getY() - bounds.y);
            }
        }
        return constraintWidgetContainer;
    }

    public boolean addChildrenToSolver(LinearSystem linearSystem, int i) {
        boolean z;
        addToSolver(linearSystem, i);
        int size = this.mChildren.size();
        int i2 = this.mOptimizationLevel;
        if (i2 != 2 && i2 != 4) {
            z = USE_SNAPSHOT;
        } else if (optimize(linearSystem)) {
            return false;
        } else {
            z = false;
        }
        for (int i3 = 0; i3 < size; i3++) {
            ConstraintWidget constraintWidget = this.mChildren.get(i3);
            if (constraintWidget instanceof ConstraintWidgetContainer) {
                ConstraintWidget.DimensionBehaviour dimensionBehaviour = constraintWidget.mHorizontalDimensionBehaviour;
                ConstraintWidget.DimensionBehaviour dimensionBehaviour2 = constraintWidget.mVerticalDimensionBehaviour;
                if (dimensionBehaviour == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT) {
                    constraintWidget.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.FIXED);
                }
                if (dimensionBehaviour2 == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT) {
                    constraintWidget.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.FIXED);
                }
                constraintWidget.addToSolver(linearSystem, i);
                if (dimensionBehaviour == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT) {
                    constraintWidget.setHorizontalDimensionBehaviour(dimensionBehaviour);
                }
                if (dimensionBehaviour2 == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT) {
                    constraintWidget.setVerticalDimensionBehaviour(dimensionBehaviour2);
                }
            } else {
                if (z) {
                    Optimizer.checkMatchParent(this, linearSystem, constraintWidget);
                }
                constraintWidget.addToSolver(linearSystem, i);
            }
        }
        if (this.mHorizontalChainsSize > 0) {
            applyHorizontalChain(linearSystem);
        }
        if (this.mVerticalChainsSize > 0) {
            applyVerticalChain(linearSystem);
        }
        return USE_SNAPSHOT;
    }

    private boolean optimize(LinearSystem linearSystem) {
        int size = this.mChildren.size();
        for (int i = 0; i < size; i++) {
            ConstraintWidget constraintWidget = this.mChildren.get(i);
            constraintWidget.mHorizontalResolution = -1;
            constraintWidget.mVerticalResolution = -1;
            if (constraintWidget.mHorizontalDimensionBehaviour == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT || constraintWidget.mVerticalDimensionBehaviour == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT) {
                constraintWidget.mHorizontalResolution = 1;
                constraintWidget.mVerticalResolution = 1;
            }
        }
        boolean z = false;
        int i2 = 0;
        int i3 = 0;
        while (!z) {
            int i4 = 0;
            int i5 = 0;
            for (int i6 = 0; i6 < size; i6++) {
                ConstraintWidget constraintWidget2 = this.mChildren.get(i6);
                if (constraintWidget2.mHorizontalResolution == -1) {
                    if (this.mHorizontalDimensionBehaviour == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT) {
                        constraintWidget2.mHorizontalResolution = 1;
                    } else {
                        Optimizer.checkHorizontalSimpleDependency(this, linearSystem, constraintWidget2);
                    }
                }
                if (constraintWidget2.mVerticalResolution == -1) {
                    if (this.mVerticalDimensionBehaviour == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT) {
                        constraintWidget2.mVerticalResolution = 1;
                    } else {
                        Optimizer.checkVerticalSimpleDependency(this, linearSystem, constraintWidget2);
                    }
                }
                if (constraintWidget2.mVerticalResolution == -1) {
                    i4++;
                }
                if (constraintWidget2.mHorizontalResolution == -1) {
                    i5++;
                }
            }
            if ((i4 == 0 && i5 == 0) || (i2 == i4 && i3 == i5)) {
                z = USE_SNAPSHOT;
            }
            i2 = i4;
            i3 = i5;
        }
        int i7 = 0;
        int i8 = 0;
        for (int i9 = 0; i9 < size; i9++) {
            ConstraintWidget constraintWidget3 = this.mChildren.get(i9);
            if (constraintWidget3.mHorizontalResolution == 1 || constraintWidget3.mHorizontalResolution == -1) {
                i7++;
            }
            if (constraintWidget3.mVerticalResolution == 1 || constraintWidget3.mVerticalResolution == -1) {
                i8++;
            }
        }
        if (i7 == 0 && i8 == 0) {
            return USE_SNAPSHOT;
        }
        return false;
    }

    /* JADX WARN: Removed duplicated region for block: B:196:0x04b1  */
    /* JADX WARN: Removed duplicated region for block: B:228:0x04b3 A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void applyHorizontalChain(android.support.constraint.solver.LinearSystem r31) {
        /*
            Method dump skipped, instructions count: 1302
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.constraint.solver.widgets.ConstraintWidgetContainer.applyHorizontalChain(android.support.constraint.solver.LinearSystem):void");
    }

    /* JADX WARN: Removed duplicated region for block: B:206:0x04d4  */
    /* JADX WARN: Removed duplicated region for block: B:238:0x04d6 A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void applyVerticalChain(android.support.constraint.solver.LinearSystem r31) {
        /*
            Method dump skipped, instructions count: 1337
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.constraint.solver.widgets.ConstraintWidgetContainer.applyVerticalChain(android.support.constraint.solver.LinearSystem):void");
    }

    public void updateChildrenFromSolver(LinearSystem linearSystem, int i, boolean[] zArr) {
        zArr[2] = false;
        updateFromSolver(linearSystem, i);
        int size = this.mChildren.size();
        for (int i2 = 0; i2 < size; i2++) {
            ConstraintWidget constraintWidget = this.mChildren.get(i2);
            constraintWidget.updateFromSolver(linearSystem, i);
            if (constraintWidget.mHorizontalDimensionBehaviour == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT && constraintWidget.getWidth() < constraintWidget.getWrapWidth()) {
                zArr[2] = USE_SNAPSHOT;
            }
            if (constraintWidget.mVerticalDimensionBehaviour == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT && constraintWidget.getHeight() < constraintWidget.getWrapHeight()) {
                zArr[2] = USE_SNAPSHOT;
            }
        }
    }

    public void setPadding(int i, int i2, int i3, int i4) {
        this.mPaddingLeft = i;
        this.mPaddingTop = i2;
        this.mPaddingRight = i3;
        this.mPaddingBottom = i4;
    }

    @Override // android.support.constraint.solver.widgets.WidgetContainer
    public void layout() {
        boolean z;
        boolean z2;
        int i = this.mX;
        int i2 = this.mY;
        int max = Math.max(0, getWidth());
        int max2 = Math.max(0, getHeight());
        this.mWidthMeasuredTooSmall = false;
        this.mHeightMeasuredTooSmall = false;
        if (this.mParent != null) {
            if (this.mSnapshot == null) {
                this.mSnapshot = new Snapshot(this);
            }
            this.mSnapshot.updateFrom(this);
            setX(this.mPaddingLeft);
            setY(this.mPaddingTop);
            resetAnchors();
            resetSolverVariables(this.mSystem.getCache());
        } else {
            this.mX = 0;
            this.mY = 0;
        }
        ConstraintWidget.DimensionBehaviour dimensionBehaviour = this.mVerticalDimensionBehaviour;
        ConstraintWidget.DimensionBehaviour dimensionBehaviour2 = this.mHorizontalDimensionBehaviour;
        char c = 2;
        if (this.mOptimizationLevel == 2 && (this.mVerticalDimensionBehaviour == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT || this.mHorizontalDimensionBehaviour == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT)) {
            findWrapSize(this.mChildren, this.flags);
            z = this.flags[0];
            if (max > 0 && max2 > 0 && (this.mWrapWidth > max || this.mWrapHeight > max2)) {
                z = false;
            }
            if (z) {
                if (this.mHorizontalDimensionBehaviour == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT) {
                    this.mHorizontalDimensionBehaviour = ConstraintWidget.DimensionBehaviour.FIXED;
                    if (max > 0 && max < this.mWrapWidth) {
                        this.mWidthMeasuredTooSmall = USE_SNAPSHOT;
                        setWidth(max);
                    } else {
                        setWidth(Math.max(this.mMinWidth, this.mWrapWidth));
                    }
                }
                if (this.mVerticalDimensionBehaviour == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT) {
                    this.mVerticalDimensionBehaviour = ConstraintWidget.DimensionBehaviour.FIXED;
                    if (max2 > 0 && max2 < this.mWrapHeight) {
                        this.mHeightMeasuredTooSmall = USE_SNAPSHOT;
                        setHeight(max2);
                    } else {
                        setHeight(Math.max(this.mMinHeight, this.mWrapHeight));
                    }
                }
            }
        } else {
            z = false;
        }
        resetChains();
        int size = this.mChildren.size();
        for (int i3 = 0; i3 < size; i3++) {
            ConstraintWidget constraintWidget = this.mChildren.get(i3);
            if (constraintWidget instanceof WidgetContainer) {
                ((WidgetContainer) constraintWidget).layout();
            }
        }
        boolean z3 = z;
        int i4 = 0;
        boolean z4 = USE_SNAPSHOT;
        while (z4) {
            int i5 = i4 + 1;
            try {
                this.mSystem.reset();
                z4 = addChildrenToSolver(this.mSystem, Integer.MAX_VALUE);
                if (z4) {
                    this.mSystem.minimize();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (z4) {
                updateChildrenFromSolver(this.mSystem, Integer.MAX_VALUE, this.flags);
            } else {
                updateFromSolver(this.mSystem, Integer.MAX_VALUE);
                int i6 = 0;
                while (true) {
                    if (i6 >= size) {
                        break;
                    }
                    ConstraintWidget constraintWidget2 = this.mChildren.get(i6);
                    if (constraintWidget2.mHorizontalDimensionBehaviour == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT && constraintWidget2.getWidth() < constraintWidget2.getWrapWidth()) {
                        this.flags[c] = USE_SNAPSHOT;
                        break;
                    } else if (constraintWidget2.mVerticalDimensionBehaviour == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT && constraintWidget2.getHeight() < constraintWidget2.getWrapHeight()) {
                        this.flags[c] = USE_SNAPSHOT;
                        break;
                    } else {
                        i6++;
                    }
                }
            }
            if (i5 >= 8 || !this.flags[c]) {
                z2 = false;
            } else {
                int i7 = 0;
                int i8 = 0;
                for (int i9 = 0; i9 < size; i9++) {
                    ConstraintWidget constraintWidget3 = this.mChildren.get(i9);
                    i7 = Math.max(i7, constraintWidget3.mX + constraintWidget3.getWidth());
                    i8 = Math.max(i8, constraintWidget3.mY + constraintWidget3.getHeight());
                }
                int max3 = Math.max(this.mMinWidth, i7);
                int max4 = Math.max(this.mMinHeight, i8);
                if (dimensionBehaviour2 != ConstraintWidget.DimensionBehaviour.WRAP_CONTENT || getWidth() >= max3) {
                    z2 = false;
                } else {
                    setWidth(max3);
                    this.mHorizontalDimensionBehaviour = ConstraintWidget.DimensionBehaviour.WRAP_CONTENT;
                    z2 = USE_SNAPSHOT;
                    z3 = USE_SNAPSHOT;
                }
                if (dimensionBehaviour == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT && getHeight() < max4) {
                    setHeight(max4);
                    this.mVerticalDimensionBehaviour = ConstraintWidget.DimensionBehaviour.WRAP_CONTENT;
                    z2 = USE_SNAPSHOT;
                    z3 = USE_SNAPSHOT;
                }
            }
            int max5 = Math.max(this.mMinWidth, getWidth());
            if (max5 > getWidth()) {
                setWidth(max5);
                this.mHorizontalDimensionBehaviour = ConstraintWidget.DimensionBehaviour.FIXED;
                z2 = USE_SNAPSHOT;
                z3 = USE_SNAPSHOT;
            }
            int max6 = Math.max(this.mMinHeight, getHeight());
            if (max6 > getHeight()) {
                setHeight(max6);
                this.mVerticalDimensionBehaviour = ConstraintWidget.DimensionBehaviour.FIXED;
                z2 = USE_SNAPSHOT;
                z3 = USE_SNAPSHOT;
            }
            if (!z3) {
                if (this.mHorizontalDimensionBehaviour == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT && max > 0 && getWidth() > max) {
                    this.mWidthMeasuredTooSmall = USE_SNAPSHOT;
                    this.mHorizontalDimensionBehaviour = ConstraintWidget.DimensionBehaviour.FIXED;
                    setWidth(max);
                    z2 = USE_SNAPSHOT;
                    z3 = USE_SNAPSHOT;
                }
                if (this.mVerticalDimensionBehaviour == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT && max2 > 0 && getHeight() > max2) {
                    this.mHeightMeasuredTooSmall = USE_SNAPSHOT;
                    this.mVerticalDimensionBehaviour = ConstraintWidget.DimensionBehaviour.FIXED;
                    setHeight(max2);
                    z4 = USE_SNAPSHOT;
                    z3 = USE_SNAPSHOT;
                    i4 = i5;
                    c = 2;
                }
            }
            z4 = z2;
            i4 = i5;
            c = 2;
        }
        if (this.mParent != null) {
            int max7 = Math.max(this.mMinWidth, getWidth());
            int max8 = Math.max(this.mMinHeight, getHeight());
            this.mSnapshot.applyTo(this);
            setWidth(max7 + this.mPaddingLeft + this.mPaddingRight);
            setHeight(max8 + this.mPaddingTop + this.mPaddingBottom);
        } else {
            this.mX = i;
            this.mY = i2;
        }
        if (z3) {
            this.mHorizontalDimensionBehaviour = dimensionBehaviour2;
            this.mVerticalDimensionBehaviour = dimensionBehaviour;
        }
        resetSolverVariables(this.mSystem.getCache());
        if (this == getRootConstraintContainer()) {
            updateDrawPosition();
        }
    }

    static int setGroup(ConstraintAnchor constraintAnchor, int i) {
        int i2 = constraintAnchor.mGroup;
        if (constraintAnchor.mOwner.getParent() == null) {
            return i;
        }
        if (i2 <= i) {
            return i2;
        }
        constraintAnchor.mGroup = i;
        ConstraintAnchor opposite = constraintAnchor.getOpposite();
        ConstraintAnchor constraintAnchor2 = constraintAnchor.mTarget;
        if (opposite != null) {
            i = setGroup(opposite, i);
        }
        if (constraintAnchor2 != null) {
            i = setGroup(constraintAnchor2, i);
        }
        if (opposite != null) {
            i = setGroup(opposite, i);
        }
        constraintAnchor.mGroup = i;
        return i;
    }

    public int layoutFindGroupsSimple() {
        int size = this.mChildren.size();
        for (int i = 0; i < size; i++) {
            ConstraintWidget constraintWidget = this.mChildren.get(i);
            constraintWidget.mLeft.mGroup = 0;
            constraintWidget.mRight.mGroup = 0;
            constraintWidget.mTop.mGroup = 1;
            constraintWidget.mBottom.mGroup = 1;
            constraintWidget.mBaseline.mGroup = 1;
        }
        return 2;
    }

    /* JADX WARN: Removed duplicated region for block: B:125:0x01ba  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void findHorizontalWrapRecursive(android.support.constraint.solver.widgets.ConstraintWidget r8, boolean[] r9) {
        /*
            Method dump skipped, instructions count: 453
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.constraint.solver.widgets.ConstraintWidgetContainer.findHorizontalWrapRecursive(android.support.constraint.solver.widgets.ConstraintWidget, boolean[]):void");
    }

    /* JADX WARN: Removed duplicated region for block: B:136:0x020a  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void findVerticalWrapRecursive(android.support.constraint.solver.widgets.ConstraintWidget r9, boolean[] r10) {
        /*
            Method dump skipped, instructions count: 533
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.constraint.solver.widgets.ConstraintWidgetContainer.findVerticalWrapRecursive(android.support.constraint.solver.widgets.ConstraintWidget, boolean[]):void");
    }

    public void findWrapSize(ArrayList<ConstraintWidget> arrayList, boolean[] zArr) {
        int size = arrayList.size();
        char c = 0;
        zArr[0] = USE_SNAPSHOT;
        int i = 0;
        int i2 = 0;
        int i3 = 0;
        int i4 = 0;
        int i5 = 0;
        int i6 = 0;
        int i7 = 0;
        while (i < size) {
            ConstraintWidget constraintWidget = arrayList.get(i);
            if (!constraintWidget.isRoot()) {
                if (!constraintWidget.mHorizontalWrapVisited) {
                    findHorizontalWrapRecursive(constraintWidget, zArr);
                }
                if (!constraintWidget.mVerticalWrapVisited) {
                    findVerticalWrapRecursive(constraintWidget, zArr);
                }
                if (!zArr[c]) {
                    return;
                }
                int width = (constraintWidget.mDistToLeft + constraintWidget.mDistToRight) - constraintWidget.getWidth();
                int height = (constraintWidget.mDistToTop + constraintWidget.mDistToBottom) - constraintWidget.getHeight();
                int width2 = constraintWidget.mHorizontalDimensionBehaviour == ConstraintWidget.DimensionBehaviour.MATCH_PARENT ? constraintWidget.getWidth() + constraintWidget.mLeft.mMargin + constraintWidget.mRight.mMargin : width;
                int height2 = constraintWidget.mVerticalDimensionBehaviour == ConstraintWidget.DimensionBehaviour.MATCH_PARENT ? constraintWidget.getHeight() + constraintWidget.mTop.mMargin + constraintWidget.mBottom.mMargin : height;
                if (constraintWidget.getVisibility() == 8) {
                    width2 = 0;
                    height2 = 0;
                }
                i2 = Math.max(i2, constraintWidget.mDistToLeft);
                i3 = Math.max(i3, constraintWidget.mDistToRight);
                i6 = Math.max(i6, constraintWidget.mDistToBottom);
                i5 = Math.max(i5, constraintWidget.mDistToTop);
                int max = Math.max(i4, width2);
                i7 = Math.max(i7, height2);
                i4 = max;
            }
            i++;
            c = 0;
        }
        this.mWrapWidth = Math.max(this.mMinWidth, Math.max(Math.max(i2, i3), i4));
        this.mWrapHeight = Math.max(this.mMinHeight, Math.max(Math.max(i5, i6), i7));
        for (int i8 = 0; i8 < size; i8++) {
            ConstraintWidget constraintWidget2 = arrayList.get(i8);
            constraintWidget2.mHorizontalWrapVisited = false;
            constraintWidget2.mVerticalWrapVisited = false;
            constraintWidget2.mLeftHasCentered = false;
            constraintWidget2.mRightHasCentered = false;
            constraintWidget2.mTopHasCentered = false;
            constraintWidget2.mBottomHasCentered = false;
        }
    }

    public int layoutFindGroups() {
        int i = 5;
        ConstraintAnchor.Type[] typeArr = {ConstraintAnchor.Type.LEFT, ConstraintAnchor.Type.RIGHT, ConstraintAnchor.Type.TOP, ConstraintAnchor.Type.BASELINE, ConstraintAnchor.Type.BOTTOM};
        int size = this.mChildren.size();
        int i2 = 1;
        for (int i3 = 0; i3 < size; i3++) {
            ConstraintWidget constraintWidget = this.mChildren.get(i3);
            ConstraintAnchor constraintAnchor = constraintWidget.mLeft;
            if (constraintAnchor.mTarget != null) {
                if (setGroup(constraintAnchor, i2) == i2) {
                    i2++;
                }
            } else {
                constraintAnchor.mGroup = Integer.MAX_VALUE;
            }
            ConstraintAnchor constraintAnchor2 = constraintWidget.mTop;
            if (constraintAnchor2.mTarget != null) {
                if (setGroup(constraintAnchor2, i2) == i2) {
                    i2++;
                }
            } else {
                constraintAnchor2.mGroup = Integer.MAX_VALUE;
            }
            ConstraintAnchor constraintAnchor3 = constraintWidget.mRight;
            if (constraintAnchor3.mTarget != null) {
                if (setGroup(constraintAnchor3, i2) == i2) {
                    i2++;
                }
            } else {
                constraintAnchor3.mGroup = Integer.MAX_VALUE;
            }
            ConstraintAnchor constraintAnchor4 = constraintWidget.mBottom;
            if (constraintAnchor4.mTarget != null) {
                if (setGroup(constraintAnchor4, i2) == i2) {
                    i2++;
                }
            } else {
                constraintAnchor4.mGroup = Integer.MAX_VALUE;
            }
            ConstraintAnchor constraintAnchor5 = constraintWidget.mBaseline;
            if (constraintAnchor5.mTarget != null) {
                if (setGroup(constraintAnchor5, i2) == i2) {
                    i2++;
                }
            } else {
                constraintAnchor5.mGroup = Integer.MAX_VALUE;
            }
        }
        boolean z = USE_SNAPSHOT;
        while (z) {
            int i4 = 0;
            boolean z2 = false;
            while (i4 < size) {
                ConstraintWidget constraintWidget2 = this.mChildren.get(i4);
                boolean z3 = z2;
                int i5 = 0;
                while (i5 < typeArr.length) {
                    ConstraintAnchor constraintAnchor6 = null;
                    int i6 = AnonymousClass2.$SwitchMap$android$support$constraint$solver$widgets$ConstraintAnchor$Type[typeArr[i5].ordinal()];
                    if (i6 == 1) {
                        constraintAnchor6 = constraintWidget2.mLeft;
                    } else if (i6 == 2) {
                        constraintAnchor6 = constraintWidget2.mTop;
                    } else if (i6 == 3) {
                        constraintAnchor6 = constraintWidget2.mRight;
                    } else if (i6 == 4) {
                        constraintAnchor6 = constraintWidget2.mBottom;
                    } else if (i6 == i) {
                        constraintAnchor6 = constraintWidget2.mBaseline;
                    }
                    ConstraintAnchor constraintAnchor7 = constraintAnchor6.mTarget;
                    if (constraintAnchor7 != null) {
                        if (constraintAnchor7.mOwner.getParent() != null && constraintAnchor7.mGroup != constraintAnchor6.mGroup) {
                            int i7 = constraintAnchor6.mGroup > constraintAnchor7.mGroup ? constraintAnchor7.mGroup : constraintAnchor6.mGroup;
                            constraintAnchor6.mGroup = i7;
                            constraintAnchor7.mGroup = i7;
                            z3 = USE_SNAPSHOT;
                        }
                        ConstraintAnchor opposite = constraintAnchor7.getOpposite();
                        if (opposite != null && opposite.mGroup != constraintAnchor6.mGroup) {
                            int i8 = constraintAnchor6.mGroup > opposite.mGroup ? opposite.mGroup : constraintAnchor6.mGroup;
                            constraintAnchor6.mGroup = i8;
                            opposite.mGroup = i8;
                            z3 = USE_SNAPSHOT;
                        }
                    }
                    i5++;
                    i = 5;
                }
                i4++;
                z2 = z3;
                i = 5;
            }
            z = z2;
        }
        int[] iArr = new int[(this.mChildren.size() * typeArr.length) + 1];
        Arrays.fill(iArr, -1);
        int i9 = 0;
        for (int i10 = 0; i10 < size; i10++) {
            ConstraintWidget constraintWidget3 = this.mChildren.get(i10);
            ConstraintAnchor constraintAnchor8 = constraintWidget3.mLeft;
            if (constraintAnchor8.mGroup != Integer.MAX_VALUE) {
                int i11 = constraintAnchor8.mGroup;
                if (iArr[i11] == -1) {
                    iArr[i11] = i9;
                    i9++;
                }
                constraintAnchor8.mGroup = iArr[i11];
            }
            ConstraintAnchor constraintAnchor9 = constraintWidget3.mTop;
            if (constraintAnchor9.mGroup != Integer.MAX_VALUE) {
                int i12 = constraintAnchor9.mGroup;
                if (iArr[i12] == -1) {
                    iArr[i12] = i9;
                    i9++;
                }
                constraintAnchor9.mGroup = iArr[i12];
            }
            ConstraintAnchor constraintAnchor10 = constraintWidget3.mRight;
            if (constraintAnchor10.mGroup != Integer.MAX_VALUE) {
                int i13 = constraintAnchor10.mGroup;
                if (iArr[i13] == -1) {
                    iArr[i13] = i9;
                    i9++;
                }
                constraintAnchor10.mGroup = iArr[i13];
            }
            ConstraintAnchor constraintAnchor11 = constraintWidget3.mBottom;
            if (constraintAnchor11.mGroup != Integer.MAX_VALUE) {
                int i14 = constraintAnchor11.mGroup;
                if (iArr[i14] == -1) {
                    iArr[i14] = i9;
                    i9++;
                }
                constraintAnchor11.mGroup = iArr[i14];
            }
            ConstraintAnchor constraintAnchor12 = constraintWidget3.mBaseline;
            if (constraintAnchor12.mGroup != Integer.MAX_VALUE) {
                int i15 = constraintAnchor12.mGroup;
                if (iArr[i15] == -1) {
                    iArr[i15] = i9;
                    i9++;
                }
                constraintAnchor12.mGroup = iArr[i15];
            }
        }
        return i9;
    }

    /* renamed from: android.support.constraint.solver.widgets.ConstraintWidgetContainer$2  reason: invalid class name */
    /* loaded from: classes.dex */
    static /* synthetic */ class AnonymousClass2 {
        static final /* synthetic */ int[] $SwitchMap$android$support$constraint$solver$widgets$ConstraintAnchor$Type = new int[ConstraintAnchor.Type.values().length];

        static {
            try {
                $SwitchMap$android$support$constraint$solver$widgets$ConstraintAnchor$Type[ConstraintAnchor.Type.LEFT.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$android$support$constraint$solver$widgets$ConstraintAnchor$Type[ConstraintAnchor.Type.TOP.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$android$support$constraint$solver$widgets$ConstraintAnchor$Type[ConstraintAnchor.Type.RIGHT.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$android$support$constraint$solver$widgets$ConstraintAnchor$Type[ConstraintAnchor.Type.BOTTOM.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$android$support$constraint$solver$widgets$ConstraintAnchor$Type[ConstraintAnchor.Type.BASELINE.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
        }
    }

    public void layoutWithGroup(int i) {
        int i2 = this.mX;
        int i3 = this.mY;
        if (this.mParent != null) {
            if (this.mSnapshot == null) {
                this.mSnapshot = new Snapshot(this);
            }
            this.mSnapshot.updateFrom(this);
            this.mX = 0;
            this.mY = 0;
            resetAnchors();
            resetSolverVariables(this.mSystem.getCache());
        } else {
            this.mX = 0;
            this.mY = 0;
        }
        int size = this.mChildren.size();
        for (int i4 = 0; i4 < size; i4++) {
            ConstraintWidget constraintWidget = this.mChildren.get(i4);
            if (constraintWidget instanceof WidgetContainer) {
                ((WidgetContainer) constraintWidget).layout();
            }
        }
        this.mLeft.mGroup = 0;
        this.mRight.mGroup = 0;
        this.mTop.mGroup = 1;
        this.mBottom.mGroup = 1;
        this.mSystem.reset();
        for (int i5 = 0; i5 < i; i5++) {
            try {
                addToSolver(this.mSystem, i5);
                this.mSystem.minimize();
                updateFromSolver(this.mSystem, i5);
            } catch (Exception e) {
                e.printStackTrace();
            }
            updateFromSolver(this.mSystem, -2);
        }
        if (this.mParent != null) {
            int width = getWidth();
            int height = getHeight();
            this.mSnapshot.applyTo(this);
            setWidth(width);
            setHeight(height);
        } else {
            this.mX = i2;
            this.mY = i3;
        }
        if (this == getRootConstraintContainer()) {
            updateDrawPosition();
        }
    }

    public ArrayList<Guideline> getVerticalGuidelines() {
        ArrayList<Guideline> arrayList = new ArrayList<>();
        int size = this.mChildren.size();
        for (int i = 0; i < size; i++) {
            ConstraintWidget constraintWidget = this.mChildren.get(i);
            if (constraintWidget instanceof Guideline) {
                Guideline guideline = (Guideline) constraintWidget;
                if (guideline.getOrientation() == 1) {
                    arrayList.add(guideline);
                }
            }
        }
        return arrayList;
    }

    public ArrayList<Guideline> getHorizontalGuidelines() {
        ArrayList<Guideline> arrayList = new ArrayList<>();
        int size = this.mChildren.size();
        for (int i = 0; i < size; i++) {
            ConstraintWidget constraintWidget = this.mChildren.get(i);
            if (constraintWidget instanceof Guideline) {
                Guideline guideline = (Guideline) constraintWidget;
                if (guideline.getOrientation() == 0) {
                    arrayList.add(guideline);
                }
            }
        }
        return arrayList;
    }

    public LinearSystem getSystem() {
        return this.mSystem;
    }

    private void resetChains() {
        this.mHorizontalChainsSize = 0;
        this.mVerticalChainsSize = 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void addChain(ConstraintWidget constraintWidget, int i) {
        if (i == 0) {
            while (constraintWidget.mLeft.mTarget != null && constraintWidget.mLeft.mTarget.mOwner.mRight.mTarget != null && constraintWidget.mLeft.mTarget.mOwner.mRight.mTarget == constraintWidget.mLeft && constraintWidget.mLeft.mTarget.mOwner != constraintWidget) {
                constraintWidget = constraintWidget.mLeft.mTarget.mOwner;
            }
            addHorizontalChain(constraintWidget);
        } else if (i == 1) {
            while (constraintWidget.mTop.mTarget != null && constraintWidget.mTop.mTarget.mOwner.mBottom.mTarget != null && constraintWidget.mTop.mTarget.mOwner.mBottom.mTarget == constraintWidget.mTop && constraintWidget.mTop.mTarget.mOwner != constraintWidget) {
                constraintWidget = constraintWidget.mTop.mTarget.mOwner;
            }
            addVerticalChain(constraintWidget);
        }
    }

    private void addHorizontalChain(ConstraintWidget constraintWidget) {
        int i = 0;
        while (true) {
            int i2 = this.mHorizontalChainsSize;
            if (i < i2) {
                if (this.mHorizontalChainsArray[i] == constraintWidget) {
                    return;
                }
                i++;
            } else {
                int i3 = i2 + 1;
                ConstraintWidget[] constraintWidgetArr = this.mHorizontalChainsArray;
                if (i3 >= constraintWidgetArr.length) {
                    this.mHorizontalChainsArray = (ConstraintWidget[]) Arrays.copyOf(constraintWidgetArr, constraintWidgetArr.length * 2);
                }
                ConstraintWidget[] constraintWidgetArr2 = this.mHorizontalChainsArray;
                int i4 = this.mHorizontalChainsSize;
                constraintWidgetArr2[i4] = constraintWidget;
                this.mHorizontalChainsSize = i4 + 1;
                return;
            }
        }
    }

    private void addVerticalChain(ConstraintWidget constraintWidget) {
        int i = 0;
        while (true) {
            int i2 = this.mVerticalChainsSize;
            if (i < i2) {
                if (this.mVerticalChainsArray[i] == constraintWidget) {
                    return;
                }
                i++;
            } else {
                int i3 = i2 + 1;
                ConstraintWidget[] constraintWidgetArr = this.mVerticalChainsArray;
                if (i3 >= constraintWidgetArr.length) {
                    this.mVerticalChainsArray = (ConstraintWidget[]) Arrays.copyOf(constraintWidgetArr, constraintWidgetArr.length * 2);
                }
                ConstraintWidget[] constraintWidgetArr2 = this.mVerticalChainsArray;
                int i4 = this.mVerticalChainsSize;
                constraintWidgetArr2[i4] = constraintWidget;
                this.mVerticalChainsSize = i4 + 1;
                return;
            }
        }
    }

    private int countMatchConstraintsChainedWidgets(LinearSystem linearSystem, ConstraintWidget[] constraintWidgetArr, ConstraintWidget constraintWidget, int i, boolean[] zArr) {
        int i2;
        char c;
        char c2;
        zArr[0] = USE_SNAPSHOT;
        zArr[1] = false;
        ConstraintWidget constraintWidget2 = null;
        constraintWidgetArr[0] = null;
        constraintWidgetArr[2] = null;
        constraintWidgetArr[1] = null;
        constraintWidgetArr[3] = null;
        float f = 0.0f;
        int i3 = 5;
        if (i == 0) {
            boolean z = (constraintWidget.mLeft.mTarget == null || constraintWidget.mLeft.mTarget.mOwner == this) ? USE_SNAPSHOT : false;
            constraintWidget.mHorizontalNextWidget = null;
            ConstraintWidget constraintWidget3 = null;
            ConstraintWidget constraintWidget4 = constraintWidget.getVisibility() != 8 ? constraintWidget : null;
            ConstraintWidget constraintWidget5 = constraintWidget4;
            i2 = 0;
            ConstraintWidget constraintWidget6 = constraintWidget;
            while (constraintWidget6.mRight.mTarget != null) {
                constraintWidget6.mHorizontalNextWidget = constraintWidget2;
                if (constraintWidget6.getVisibility() != 8) {
                    if (constraintWidget4 == null) {
                        constraintWidget4 = constraintWidget6;
                    }
                    if (constraintWidget5 != null && constraintWidget5 != constraintWidget6) {
                        constraintWidget5.mHorizontalNextWidget = constraintWidget6;
                    }
                    constraintWidget5 = constraintWidget6;
                } else {
                    linearSystem.addEquality(constraintWidget6.mLeft.mSolverVariable, constraintWidget6.mLeft.mTarget.mSolverVariable, 0, 5);
                    linearSystem.addEquality(constraintWidget6.mRight.mSolverVariable, constraintWidget6.mLeft.mSolverVariable, 0, 5);
                }
                if (constraintWidget6.getVisibility() != 8 && constraintWidget6.mHorizontalDimensionBehaviour == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT) {
                    if (constraintWidget6.mVerticalDimensionBehaviour == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT) {
                        zArr[0] = false;
                    }
                    if (constraintWidget6.mDimensionRatio <= f) {
                        zArr[0] = false;
                        int i4 = i2 + 1;
                        ConstraintWidget[] constraintWidgetArr2 = this.mMatchConstraintsChainedWidgets;
                        if (i4 >= constraintWidgetArr2.length) {
                            this.mMatchConstraintsChainedWidgets = (ConstraintWidget[]) Arrays.copyOf(constraintWidgetArr2, constraintWidgetArr2.length * 2);
                        }
                        this.mMatchConstraintsChainedWidgets[i2] = constraintWidget6;
                        i2 = i4;
                    }
                }
                if (constraintWidget6.mRight.mTarget.mOwner.mLeft.mTarget == null || constraintWidget6.mRight.mTarget.mOwner.mLeft.mTarget.mOwner != constraintWidget6 || constraintWidget6.mRight.mTarget.mOwner == constraintWidget6) {
                    break;
                }
                constraintWidget3 = constraintWidget6.mRight.mTarget.mOwner;
                constraintWidget6 = constraintWidget3;
                constraintWidget2 = null;
                f = 0.0f;
            }
            if (constraintWidget6.mRight.mTarget != null && constraintWidget6.mRight.mTarget.mOwner != this) {
                z = false;
            }
            if (constraintWidget.mLeft.mTarget == null || constraintWidget3.mRight.mTarget == null) {
                c2 = 1;
                zArr[1] = USE_SNAPSHOT;
            } else {
                c2 = 1;
            }
            constraintWidget.mHorizontalChainFixedPosition = z;
            constraintWidget3.mHorizontalNextWidget = null;
            constraintWidgetArr[0] = constraintWidget;
            constraintWidgetArr[2] = constraintWidget4;
            constraintWidgetArr[c2] = constraintWidget3;
            constraintWidgetArr[3] = constraintWidget5;
        } else {
            boolean z2 = (constraintWidget.mTop.mTarget == null || constraintWidget.mTop.mTarget.mOwner == this) ? USE_SNAPSHOT : false;
            ConstraintWidget constraintWidget7 = null;
            constraintWidget.mVerticalNextWidget = null;
            ConstraintWidget constraintWidget8 = null;
            ConstraintWidget constraintWidget9 = constraintWidget.getVisibility() != 8 ? constraintWidget : null;
            ConstraintWidget constraintWidget10 = constraintWidget9;
            int i5 = 0;
            ConstraintWidget constraintWidget11 = constraintWidget;
            while (constraintWidget11.mBottom.mTarget != null) {
                constraintWidget11.mVerticalNextWidget = constraintWidget7;
                if (constraintWidget11.getVisibility() != 8) {
                    if (constraintWidget9 == null) {
                        constraintWidget9 = constraintWidget11;
                    }
                    if (constraintWidget10 != null && constraintWidget10 != constraintWidget11) {
                        constraintWidget10.mVerticalNextWidget = constraintWidget11;
                    }
                    constraintWidget10 = constraintWidget11;
                } else {
                    linearSystem.addEquality(constraintWidget11.mTop.mSolverVariable, constraintWidget11.mTop.mTarget.mSolverVariable, 0, i3);
                    linearSystem.addEquality(constraintWidget11.mBottom.mSolverVariable, constraintWidget11.mTop.mSolverVariable, 0, i3);
                }
                if (constraintWidget11.getVisibility() != 8 && constraintWidget11.mVerticalDimensionBehaviour == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT) {
                    if (constraintWidget11.mHorizontalDimensionBehaviour == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT) {
                        zArr[0] = false;
                    }
                    if (constraintWidget11.mDimensionRatio <= 0.0f) {
                        zArr[0] = false;
                        int i6 = i5 + 1;
                        ConstraintWidget[] constraintWidgetArr3 = this.mMatchConstraintsChainedWidgets;
                        if (i6 >= constraintWidgetArr3.length) {
                            this.mMatchConstraintsChainedWidgets = (ConstraintWidget[]) Arrays.copyOf(constraintWidgetArr3, constraintWidgetArr3.length * 2);
                        }
                        this.mMatchConstraintsChainedWidgets[i5] = constraintWidget11;
                        i5 = i6;
                    }
                }
                if (constraintWidget11.mBottom.mTarget.mOwner.mTop.mTarget == null || constraintWidget11.mBottom.mTarget.mOwner.mTop.mTarget.mOwner != constraintWidget11 || constraintWidget11.mBottom.mTarget.mOwner == constraintWidget11) {
                    break;
                }
                constraintWidget8 = constraintWidget11.mBottom.mTarget.mOwner;
                constraintWidget11 = constraintWidget8;
                constraintWidget7 = null;
                i3 = 5;
            }
            i2 = i5;
            if (constraintWidget11.mBottom.mTarget != null && constraintWidget11.mBottom.mTarget.mOwner != this) {
                z2 = false;
            }
            if (constraintWidget.mTop.mTarget == null || constraintWidget8.mBottom.mTarget == null) {
                c = 1;
                zArr[1] = USE_SNAPSHOT;
            } else {
                c = 1;
            }
            constraintWidget.mVerticalChainFixedPosition = z2;
            constraintWidget8.mVerticalNextWidget = null;
            constraintWidgetArr[0] = constraintWidget;
            constraintWidgetArr[2] = constraintWidget9;
            constraintWidgetArr[c] = constraintWidget8;
            constraintWidgetArr[3] = constraintWidget10;
        }
        return i2;
    }
}
