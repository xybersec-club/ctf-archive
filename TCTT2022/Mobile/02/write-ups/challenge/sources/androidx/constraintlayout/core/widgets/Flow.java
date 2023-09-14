package androidx.constraintlayout.core.widgets;

import androidx.constraintlayout.core.LinearSystem;
import androidx.constraintlayout.core.widgets.ConstraintWidget;
import java.util.ArrayList;
import java.util.HashMap;
/* loaded from: classes.dex */
public class Flow extends VirtualLayout {
    public static final int HORIZONTAL_ALIGN_CENTER = 2;
    public static final int HORIZONTAL_ALIGN_END = 1;
    public static final int HORIZONTAL_ALIGN_START = 0;
    public static final int VERTICAL_ALIGN_BASELINE = 3;
    public static final int VERTICAL_ALIGN_BOTTOM = 1;
    public static final int VERTICAL_ALIGN_CENTER = 2;
    public static final int VERTICAL_ALIGN_TOP = 0;
    public static final int WRAP_ALIGNED = 2;
    public static final int WRAP_CHAIN = 1;
    public static final int WRAP_CHAIN_NEW = 3;
    public static final int WRAP_NONE = 0;
    private ConstraintWidget[] mDisplayedWidgets;
    private int mHorizontalStyle = -1;
    private int mVerticalStyle = -1;
    private int mFirstHorizontalStyle = -1;
    private int mFirstVerticalStyle = -1;
    private int mLastHorizontalStyle = -1;
    private int mLastVerticalStyle = -1;
    private float mHorizontalBias = 0.5f;
    private float mVerticalBias = 0.5f;
    private float mFirstHorizontalBias = 0.5f;
    private float mFirstVerticalBias = 0.5f;
    private float mLastHorizontalBias = 0.5f;
    private float mLastVerticalBias = 0.5f;
    private int mHorizontalGap = 0;
    private int mVerticalGap = 0;
    private int mHorizontalAlign = 2;
    private int mVerticalAlign = 2;
    private int mWrapMode = 0;
    private int mMaxElementsWrap = -1;
    private int mOrientation = 0;
    private ArrayList<WidgetsList> mChainList = new ArrayList<>();
    private ConstraintWidget[] mAlignedBiggestElementsInRows = null;
    private ConstraintWidget[] mAlignedBiggestElementsInCols = null;
    private int[] mAlignedDimensions = null;
    private int mDisplayedWidgetsCount = 0;

    @Override // androidx.constraintlayout.core.widgets.HelperWidget, androidx.constraintlayout.core.widgets.ConstraintWidget
    public void copy(ConstraintWidget constraintWidget, HashMap<ConstraintWidget, ConstraintWidget> hashMap) {
        super.copy(constraintWidget, hashMap);
        Flow flow = (Flow) constraintWidget;
        this.mHorizontalStyle = flow.mHorizontalStyle;
        this.mVerticalStyle = flow.mVerticalStyle;
        this.mFirstHorizontalStyle = flow.mFirstHorizontalStyle;
        this.mFirstVerticalStyle = flow.mFirstVerticalStyle;
        this.mLastHorizontalStyle = flow.mLastHorizontalStyle;
        this.mLastVerticalStyle = flow.mLastVerticalStyle;
        this.mHorizontalBias = flow.mHorizontalBias;
        this.mVerticalBias = flow.mVerticalBias;
        this.mFirstHorizontalBias = flow.mFirstHorizontalBias;
        this.mFirstVerticalBias = flow.mFirstVerticalBias;
        this.mLastHorizontalBias = flow.mLastHorizontalBias;
        this.mLastVerticalBias = flow.mLastVerticalBias;
        this.mHorizontalGap = flow.mHorizontalGap;
        this.mVerticalGap = flow.mVerticalGap;
        this.mHorizontalAlign = flow.mHorizontalAlign;
        this.mVerticalAlign = flow.mVerticalAlign;
        this.mWrapMode = flow.mWrapMode;
        this.mMaxElementsWrap = flow.mMaxElementsWrap;
        this.mOrientation = flow.mOrientation;
    }

    public void setOrientation(int i) {
        this.mOrientation = i;
    }

    public void setFirstHorizontalStyle(int i) {
        this.mFirstHorizontalStyle = i;
    }

    public void setFirstVerticalStyle(int i) {
        this.mFirstVerticalStyle = i;
    }

    public void setLastHorizontalStyle(int i) {
        this.mLastHorizontalStyle = i;
    }

    public void setLastVerticalStyle(int i) {
        this.mLastVerticalStyle = i;
    }

    public void setHorizontalStyle(int i) {
        this.mHorizontalStyle = i;
    }

    public void setVerticalStyle(int i) {
        this.mVerticalStyle = i;
    }

    public void setHorizontalBias(float f) {
        this.mHorizontalBias = f;
    }

    public void setVerticalBias(float f) {
        this.mVerticalBias = f;
    }

    public void setFirstHorizontalBias(float f) {
        this.mFirstHorizontalBias = f;
    }

    public void setFirstVerticalBias(float f) {
        this.mFirstVerticalBias = f;
    }

    public void setLastHorizontalBias(float f) {
        this.mLastHorizontalBias = f;
    }

    public void setLastVerticalBias(float f) {
        this.mLastVerticalBias = f;
    }

    public void setHorizontalAlign(int i) {
        this.mHorizontalAlign = i;
    }

    public void setVerticalAlign(int i) {
        this.mVerticalAlign = i;
    }

    public void setWrapMode(int i) {
        this.mWrapMode = i;
    }

    public void setHorizontalGap(int i) {
        this.mHorizontalGap = i;
    }

    public void setVerticalGap(int i) {
        this.mVerticalGap = i;
    }

    public void setMaxElementsWrap(int i) {
        this.mMaxElementsWrap = i;
    }

    public float getMaxElementsWrap() {
        return this.mMaxElementsWrap;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final int getWidgetWidth(ConstraintWidget constraintWidget, int i) {
        if (constraintWidget == null) {
            return 0;
        }
        if (constraintWidget.getHorizontalDimensionBehaviour() == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT) {
            if (constraintWidget.mMatchConstraintDefaultWidth == 0) {
                return 0;
            }
            if (constraintWidget.mMatchConstraintDefaultWidth == 2) {
                int i2 = (int) (constraintWidget.mMatchConstraintPercentWidth * i);
                if (i2 != constraintWidget.getWidth()) {
                    constraintWidget.setMeasureRequested(true);
                    measure(constraintWidget, ConstraintWidget.DimensionBehaviour.FIXED, i2, constraintWidget.getVerticalDimensionBehaviour(), constraintWidget.getHeight());
                }
                return i2;
            } else if (constraintWidget.mMatchConstraintDefaultWidth == 1) {
                return constraintWidget.getWidth();
            } else {
                if (constraintWidget.mMatchConstraintDefaultWidth == 3) {
                    return (int) ((constraintWidget.getHeight() * constraintWidget.mDimensionRatio) + 0.5f);
                }
            }
        }
        return constraintWidget.getWidth();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final int getWidgetHeight(ConstraintWidget constraintWidget, int i) {
        if (constraintWidget == null) {
            return 0;
        }
        if (constraintWidget.getVerticalDimensionBehaviour() == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT) {
            if (constraintWidget.mMatchConstraintDefaultHeight == 0) {
                return 0;
            }
            if (constraintWidget.mMatchConstraintDefaultHeight == 2) {
                int i2 = (int) (constraintWidget.mMatchConstraintPercentHeight * i);
                if (i2 != constraintWidget.getHeight()) {
                    constraintWidget.setMeasureRequested(true);
                    measure(constraintWidget, constraintWidget.getHorizontalDimensionBehaviour(), constraintWidget.getWidth(), ConstraintWidget.DimensionBehaviour.FIXED, i2);
                }
                return i2;
            } else if (constraintWidget.mMatchConstraintDefaultHeight == 1) {
                return constraintWidget.getHeight();
            } else {
                if (constraintWidget.mMatchConstraintDefaultHeight == 3) {
                    return (int) ((constraintWidget.getWidth() * constraintWidget.mDimensionRatio) + 0.5f);
                }
            }
        }
        return constraintWidget.getHeight();
    }

    @Override // androidx.constraintlayout.core.widgets.VirtualLayout
    public void measure(int i, int i2, int i3, int i4) {
        int[] iArr;
        boolean z;
        if (this.mWidgetsCount > 0 && !measureChildren()) {
            setMeasure(0, 0);
            needsCallbackFromSolver(false);
            return;
        }
        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();
        int[] iArr2 = new int[2];
        int i5 = (i2 - paddingLeft) - paddingRight;
        int i6 = this.mOrientation;
        if (i6 == 1) {
            i5 = (i4 - paddingTop) - paddingBottom;
        }
        int i7 = i5;
        if (i6 == 0) {
            if (this.mHorizontalStyle == -1) {
                this.mHorizontalStyle = 0;
            }
            if (this.mVerticalStyle == -1) {
                this.mVerticalStyle = 0;
            }
        } else {
            if (this.mHorizontalStyle == -1) {
                this.mHorizontalStyle = 0;
            }
            if (this.mVerticalStyle == -1) {
                this.mVerticalStyle = 0;
            }
        }
        ConstraintWidget[] constraintWidgetArr = this.mWidgets;
        int i8 = 0;
        for (int i9 = 0; i9 < this.mWidgetsCount; i9++) {
            if (this.mWidgets[i9].getVisibility() == 8) {
                i8++;
            }
        }
        int i10 = this.mWidgetsCount;
        if (i8 > 0) {
            constraintWidgetArr = new ConstraintWidget[this.mWidgetsCount - i8];
            int i11 = 0;
            for (int i12 = 0; i12 < this.mWidgetsCount; i12++) {
                ConstraintWidget constraintWidget = this.mWidgets[i12];
                if (constraintWidget.getVisibility() != 8) {
                    constraintWidgetArr[i11] = constraintWidget;
                    i11++;
                }
            }
            i10 = i11;
        }
        this.mDisplayedWidgets = constraintWidgetArr;
        this.mDisplayedWidgetsCount = i10;
        int i13 = this.mWrapMode;
        if (i13 == 0) {
            iArr = iArr2;
            z = true;
            measureNoWrap(constraintWidgetArr, i10, this.mOrientation, i7, iArr2);
        } else if (i13 == 1) {
            z = true;
            iArr = iArr2;
            measureChainWrap(constraintWidgetArr, i10, this.mOrientation, i7, iArr2);
        } else if (i13 == 2) {
            z = true;
            iArr = iArr2;
            measureAligned(constraintWidgetArr, i10, this.mOrientation, i7, iArr2);
        } else if (i13 != 3) {
            z = true;
            iArr = iArr2;
        } else {
            z = true;
            iArr = iArr2;
            measureChainWrap_new(constraintWidgetArr, i10, this.mOrientation, i7, iArr2);
        }
        int i14 = iArr[0] + paddingLeft + paddingRight;
        int i15 = iArr[z ? 1 : 0] + paddingTop + paddingBottom;
        if (i == 1073741824) {
            i14 = i2;
        } else if (i == Integer.MIN_VALUE) {
            i14 = Math.min(i14, i2);
        } else if (i != 0) {
            i14 = 0;
        }
        if (i3 == 1073741824) {
            i15 = i4;
        } else if (i3 == Integer.MIN_VALUE) {
            i15 = Math.min(i15, i4);
        } else if (i3 != 0) {
            i15 = 0;
        }
        setMeasure(i14, i15);
        setWidth(i14);
        setHeight(i15);
        if (this.mWidgetsCount <= 0) {
            z = false;
        }
        needsCallbackFromSolver(z);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class WidgetsList {
        private ConstraintAnchor mBottom;
        private ConstraintAnchor mLeft;
        private int mMax;
        private int mOrientation;
        private int mPaddingBottom;
        private int mPaddingLeft;
        private int mPaddingRight;
        private int mPaddingTop;
        private ConstraintAnchor mRight;
        private ConstraintAnchor mTop;
        private ConstraintWidget biggest = null;
        int biggestDimension = 0;
        private int mWidth = 0;
        private int mHeight = 0;
        private int mStartIndex = 0;
        private int mCount = 0;
        private int mNbMatchConstraintsWidgets = 0;

        public WidgetsList(int i, ConstraintAnchor constraintAnchor, ConstraintAnchor constraintAnchor2, ConstraintAnchor constraintAnchor3, ConstraintAnchor constraintAnchor4, int i2) {
            this.mPaddingLeft = 0;
            this.mPaddingTop = 0;
            this.mPaddingRight = 0;
            this.mPaddingBottom = 0;
            this.mMax = 0;
            this.mOrientation = i;
            this.mLeft = constraintAnchor;
            this.mTop = constraintAnchor2;
            this.mRight = constraintAnchor3;
            this.mBottom = constraintAnchor4;
            this.mPaddingLeft = Flow.this.getPaddingLeft();
            this.mPaddingTop = Flow.this.getPaddingTop();
            this.mPaddingRight = Flow.this.getPaddingRight();
            this.mPaddingBottom = Flow.this.getPaddingBottom();
            this.mMax = i2;
        }

        public void setup(int i, ConstraintAnchor constraintAnchor, ConstraintAnchor constraintAnchor2, ConstraintAnchor constraintAnchor3, ConstraintAnchor constraintAnchor4, int i2, int i3, int i4, int i5, int i6) {
            this.mOrientation = i;
            this.mLeft = constraintAnchor;
            this.mTop = constraintAnchor2;
            this.mRight = constraintAnchor3;
            this.mBottom = constraintAnchor4;
            this.mPaddingLeft = i2;
            this.mPaddingTop = i3;
            this.mPaddingRight = i4;
            this.mPaddingBottom = i5;
            this.mMax = i6;
        }

        public void clear() {
            this.biggestDimension = 0;
            this.biggest = null;
            this.mWidth = 0;
            this.mHeight = 0;
            this.mStartIndex = 0;
            this.mCount = 0;
            this.mNbMatchConstraintsWidgets = 0;
        }

        public void setStartIndex(int i) {
            this.mStartIndex = i;
        }

        public int getWidth() {
            if (this.mOrientation == 0) {
                return this.mWidth - Flow.this.mHorizontalGap;
            }
            return this.mWidth;
        }

        public int getHeight() {
            if (this.mOrientation == 1) {
                return this.mHeight - Flow.this.mVerticalGap;
            }
            return this.mHeight;
        }

        public void add(ConstraintWidget constraintWidget) {
            if (this.mOrientation == 0) {
                int widgetWidth = Flow.this.getWidgetWidth(constraintWidget, this.mMax);
                if (constraintWidget.getHorizontalDimensionBehaviour() == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT) {
                    this.mNbMatchConstraintsWidgets++;
                    widgetWidth = 0;
                }
                this.mWidth += widgetWidth + (constraintWidget.getVisibility() != 8 ? Flow.this.mHorizontalGap : 0);
                int widgetHeight = Flow.this.getWidgetHeight(constraintWidget, this.mMax);
                if (this.biggest == null || this.biggestDimension < widgetHeight) {
                    this.biggest = constraintWidget;
                    this.biggestDimension = widgetHeight;
                    this.mHeight = widgetHeight;
                }
            } else {
                int widgetWidth2 = Flow.this.getWidgetWidth(constraintWidget, this.mMax);
                int widgetHeight2 = Flow.this.getWidgetHeight(constraintWidget, this.mMax);
                if (constraintWidget.getVerticalDimensionBehaviour() == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT) {
                    this.mNbMatchConstraintsWidgets++;
                    widgetHeight2 = 0;
                }
                this.mHeight += widgetHeight2 + (constraintWidget.getVisibility() != 8 ? Flow.this.mVerticalGap : 0);
                if (this.biggest == null || this.biggestDimension < widgetWidth2) {
                    this.biggest = constraintWidget;
                    this.biggestDimension = widgetWidth2;
                    this.mWidth = widgetWidth2;
                }
            }
            this.mCount++;
        }

        public void createConstraints(boolean z, int i, boolean z2) {
            ConstraintWidget constraintWidget;
            char c;
            float f;
            float f2;
            int i2 = this.mCount;
            for (int i3 = 0; i3 < i2 && this.mStartIndex + i3 < Flow.this.mDisplayedWidgetsCount; i3++) {
                ConstraintWidget constraintWidget2 = Flow.this.mDisplayedWidgets[this.mStartIndex + i3];
                if (constraintWidget2 != null) {
                    constraintWidget2.resetAnchors();
                }
            }
            if (i2 == 0 || this.biggest == null) {
                return;
            }
            boolean z3 = z2 && i == 0;
            int i4 = -1;
            int i5 = -1;
            for (int i6 = 0; i6 < i2; i6++) {
                int i7 = z ? (i2 - 1) - i6 : i6;
                if (this.mStartIndex + i7 >= Flow.this.mDisplayedWidgetsCount) {
                    break;
                }
                ConstraintWidget constraintWidget3 = Flow.this.mDisplayedWidgets[this.mStartIndex + i7];
                if (constraintWidget3 != null && constraintWidget3.getVisibility() == 0) {
                    if (i4 == -1) {
                        i4 = i6;
                    }
                    i5 = i6;
                }
            }
            ConstraintWidget constraintWidget4 = null;
            if (this.mOrientation == 0) {
                ConstraintWidget constraintWidget5 = this.biggest;
                constraintWidget5.setVerticalChainStyle(Flow.this.mVerticalStyle);
                int i8 = this.mPaddingTop;
                if (i > 0) {
                    i8 += Flow.this.mVerticalGap;
                }
                constraintWidget5.mTop.connect(this.mTop, i8);
                if (z2) {
                    constraintWidget5.mBottom.connect(this.mBottom, this.mPaddingBottom);
                }
                if (i > 0) {
                    this.mTop.mOwner.mBottom.connect(constraintWidget5.mTop, 0);
                }
                char c2 = 3;
                if (Flow.this.mVerticalAlign == 3 && !constraintWidget5.hasBaseline()) {
                    for (int i9 = 0; i9 < i2; i9++) {
                        int i10 = z ? (i2 - 1) - i9 : i9;
                        if (this.mStartIndex + i10 >= Flow.this.mDisplayedWidgetsCount) {
                            break;
                        }
                        constraintWidget = Flow.this.mDisplayedWidgets[this.mStartIndex + i10];
                        if (constraintWidget.hasBaseline()) {
                            break;
                        }
                    }
                }
                constraintWidget = constraintWidget5;
                int i11 = 0;
                while (i11 < i2) {
                    int i12 = z ? (i2 - 1) - i11 : i11;
                    if (this.mStartIndex + i12 >= Flow.this.mDisplayedWidgetsCount) {
                        return;
                    }
                    ConstraintWidget constraintWidget6 = Flow.this.mDisplayedWidgets[this.mStartIndex + i12];
                    if (constraintWidget6 == null) {
                        constraintWidget6 = constraintWidget4;
                        c = c2;
                    } else {
                        if (i11 == 0) {
                            constraintWidget6.connect(constraintWidget6.mLeft, this.mLeft, this.mPaddingLeft);
                        }
                        if (i12 == 0) {
                            int i13 = Flow.this.mHorizontalStyle;
                            float f3 = Flow.this.mHorizontalBias;
                            if (z) {
                                f3 = 1.0f - f3;
                            }
                            if (this.mStartIndex != 0 || Flow.this.mFirstHorizontalStyle == -1) {
                                if (z2 && Flow.this.mLastHorizontalStyle != -1) {
                                    i13 = Flow.this.mLastHorizontalStyle;
                                    if (z) {
                                        f2 = Flow.this.mLastHorizontalBias;
                                        f = 1.0f - f2;
                                        f3 = f;
                                    } else {
                                        f = Flow.this.mLastHorizontalBias;
                                        f3 = f;
                                    }
                                }
                            } else {
                                i13 = Flow.this.mFirstHorizontalStyle;
                                if (z) {
                                    f2 = Flow.this.mFirstHorizontalBias;
                                    f = 1.0f - f2;
                                    f3 = f;
                                } else {
                                    f = Flow.this.mFirstHorizontalBias;
                                    f3 = f;
                                }
                            }
                            constraintWidget6.setHorizontalChainStyle(i13);
                            constraintWidget6.setHorizontalBiasPercent(f3);
                        }
                        if (i11 == i2 - 1) {
                            constraintWidget6.connect(constraintWidget6.mRight, this.mRight, this.mPaddingRight);
                        }
                        if (constraintWidget4 != null) {
                            constraintWidget6.mLeft.connect(constraintWidget4.mRight, Flow.this.mHorizontalGap);
                            if (i11 == i4) {
                                constraintWidget6.mLeft.setGoneMargin(this.mPaddingLeft);
                            }
                            constraintWidget4.mRight.connect(constraintWidget6.mLeft, 0);
                            if (i11 == i5 + 1) {
                                constraintWidget4.mRight.setGoneMargin(this.mPaddingRight);
                            }
                        }
                        if (constraintWidget6 != constraintWidget5) {
                            c = 3;
                            if (Flow.this.mVerticalAlign != 3 || !constraintWidget.hasBaseline() || constraintWidget6 == constraintWidget || !constraintWidget6.hasBaseline()) {
                                int i14 = Flow.this.mVerticalAlign;
                                if (i14 == 0) {
                                    constraintWidget6.mTop.connect(constraintWidget5.mTop, 0);
                                } else if (i14 == 1) {
                                    constraintWidget6.mBottom.connect(constraintWidget5.mBottom, 0);
                                } else if (z3) {
                                    constraintWidget6.mTop.connect(this.mTop, this.mPaddingTop);
                                    constraintWidget6.mBottom.connect(this.mBottom, this.mPaddingBottom);
                                } else {
                                    constraintWidget6.mTop.connect(constraintWidget5.mTop, 0);
                                    constraintWidget6.mBottom.connect(constraintWidget5.mBottom, 0);
                                }
                            } else {
                                constraintWidget6.mBaseline.connect(constraintWidget.mBaseline, 0);
                            }
                        } else {
                            c = 3;
                        }
                    }
                    i11++;
                    c2 = c;
                    constraintWidget4 = constraintWidget6;
                }
                return;
            }
            ConstraintWidget constraintWidget7 = this.biggest;
            constraintWidget7.setHorizontalChainStyle(Flow.this.mHorizontalStyle);
            int i15 = this.mPaddingLeft;
            if (i > 0) {
                i15 += Flow.this.mHorizontalGap;
            }
            if (z) {
                constraintWidget7.mRight.connect(this.mRight, i15);
                if (z2) {
                    constraintWidget7.mLeft.connect(this.mLeft, this.mPaddingRight);
                }
                if (i > 0) {
                    this.mRight.mOwner.mLeft.connect(constraintWidget7.mRight, 0);
                }
            } else {
                constraintWidget7.mLeft.connect(this.mLeft, i15);
                if (z2) {
                    constraintWidget7.mRight.connect(this.mRight, this.mPaddingRight);
                }
                if (i > 0) {
                    this.mLeft.mOwner.mRight.connect(constraintWidget7.mLeft, 0);
                }
            }
            for (int i16 = 0; i16 < i2 && this.mStartIndex + i16 < Flow.this.mDisplayedWidgetsCount; i16++) {
                ConstraintWidget constraintWidget8 = Flow.this.mDisplayedWidgets[this.mStartIndex + i16];
                if (constraintWidget8 != null) {
                    if (i16 == 0) {
                        constraintWidget8.connect(constraintWidget8.mTop, this.mTop, this.mPaddingTop);
                        int i17 = Flow.this.mVerticalStyle;
                        float f4 = Flow.this.mVerticalBias;
                        if (this.mStartIndex != 0 || Flow.this.mFirstVerticalStyle == -1) {
                            if (z2 && Flow.this.mLastVerticalStyle != -1) {
                                i17 = Flow.this.mLastVerticalStyle;
                                f4 = Flow.this.mLastVerticalBias;
                            }
                        } else {
                            i17 = Flow.this.mFirstVerticalStyle;
                            f4 = Flow.this.mFirstVerticalBias;
                        }
                        constraintWidget8.setVerticalChainStyle(i17);
                        constraintWidget8.setVerticalBiasPercent(f4);
                    }
                    if (i16 == i2 - 1) {
                        constraintWidget8.connect(constraintWidget8.mBottom, this.mBottom, this.mPaddingBottom);
                    }
                    if (constraintWidget4 != null) {
                        constraintWidget8.mTop.connect(constraintWidget4.mBottom, Flow.this.mVerticalGap);
                        if (i16 == i4) {
                            constraintWidget8.mTop.setGoneMargin(this.mPaddingTop);
                        }
                        constraintWidget4.mBottom.connect(constraintWidget8.mTop, 0);
                        if (i16 == i5 + 1) {
                            constraintWidget4.mBottom.setGoneMargin(this.mPaddingBottom);
                        }
                    }
                    if (constraintWidget8 != constraintWidget7) {
                        if (z) {
                            int i18 = Flow.this.mHorizontalAlign;
                            if (i18 == 0) {
                                constraintWidget8.mRight.connect(constraintWidget7.mRight, 0);
                            } else if (i18 == 1) {
                                constraintWidget8.mLeft.connect(constraintWidget7.mLeft, 0);
                            } else if (i18 == 2) {
                                constraintWidget8.mLeft.connect(constraintWidget7.mLeft, 0);
                                constraintWidget8.mRight.connect(constraintWidget7.mRight, 0);
                            }
                        } else {
                            int i19 = Flow.this.mHorizontalAlign;
                            if (i19 == 0) {
                                constraintWidget8.mLeft.connect(constraintWidget7.mLeft, 0);
                            } else if (i19 == 1) {
                                constraintWidget8.mRight.connect(constraintWidget7.mRight, 0);
                            } else if (i19 == 2) {
                                if (z3) {
                                    constraintWidget8.mLeft.connect(this.mLeft, this.mPaddingLeft);
                                    constraintWidget8.mRight.connect(this.mRight, this.mPaddingRight);
                                } else {
                                    constraintWidget8.mLeft.connect(constraintWidget7.mLeft, 0);
                                    constraintWidget8.mRight.connect(constraintWidget7.mRight, 0);
                                }
                            }
                            constraintWidget4 = constraintWidget8;
                        }
                    }
                    constraintWidget4 = constraintWidget8;
                }
            }
        }

        public void measureMatchConstraints(int i) {
            int i2 = this.mNbMatchConstraintsWidgets;
            if (i2 == 0) {
                return;
            }
            int i3 = this.mCount;
            int i4 = i / i2;
            for (int i5 = 0; i5 < i3 && this.mStartIndex + i5 < Flow.this.mDisplayedWidgetsCount; i5++) {
                ConstraintWidget constraintWidget = Flow.this.mDisplayedWidgets[this.mStartIndex + i5];
                if (this.mOrientation == 0) {
                    if (constraintWidget != null && constraintWidget.getHorizontalDimensionBehaviour() == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT && constraintWidget.mMatchConstraintDefaultWidth == 0) {
                        Flow.this.measure(constraintWidget, ConstraintWidget.DimensionBehaviour.FIXED, i4, constraintWidget.getVerticalDimensionBehaviour(), constraintWidget.getHeight());
                    }
                } else if (constraintWidget != null && constraintWidget.getVerticalDimensionBehaviour() == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT && constraintWidget.mMatchConstraintDefaultHeight == 0) {
                    Flow.this.measure(constraintWidget, constraintWidget.getHorizontalDimensionBehaviour(), constraintWidget.getWidth(), ConstraintWidget.DimensionBehaviour.FIXED, i4);
                }
            }
            recomputeDimensions();
        }

        private void recomputeDimensions() {
            this.mWidth = 0;
            this.mHeight = 0;
            this.biggest = null;
            this.biggestDimension = 0;
            int i = this.mCount;
            for (int i2 = 0; i2 < i && this.mStartIndex + i2 < Flow.this.mDisplayedWidgetsCount; i2++) {
                ConstraintWidget constraintWidget = Flow.this.mDisplayedWidgets[this.mStartIndex + i2];
                if (this.mOrientation != 0) {
                    int widgetWidth = Flow.this.getWidgetWidth(constraintWidget, this.mMax);
                    int widgetHeight = Flow.this.getWidgetHeight(constraintWidget, this.mMax);
                    int i3 = Flow.this.mVerticalGap;
                    if (constraintWidget.getVisibility() == 8) {
                        i3 = 0;
                    }
                    this.mHeight += widgetHeight + i3;
                    if (this.biggest == null || this.biggestDimension < widgetWidth) {
                        this.biggest = constraintWidget;
                        this.biggestDimension = widgetWidth;
                        this.mWidth = widgetWidth;
                    }
                } else {
                    int width = constraintWidget.getWidth();
                    int i4 = Flow.this.mHorizontalGap;
                    if (constraintWidget.getVisibility() == 8) {
                        i4 = 0;
                    }
                    this.mWidth += width + i4;
                    int widgetHeight2 = Flow.this.getWidgetHeight(constraintWidget, this.mMax);
                    if (this.biggest == null || this.biggestDimension < widgetHeight2) {
                        this.biggest = constraintWidget;
                        this.biggestDimension = widgetHeight2;
                        this.mHeight = widgetHeight2;
                    }
                }
            }
        }
    }

    private void measureChainWrap(ConstraintWidget[] constraintWidgetArr, int i, int i2, int i3, int[] iArr) {
        int i4;
        int i5;
        int i6;
        ConstraintAnchor constraintAnchor;
        int paddingRight;
        ConstraintAnchor constraintAnchor2;
        int paddingBottom;
        int i7;
        if (i == 0) {
            return;
        }
        this.mChainList.clear();
        WidgetsList widgetsList = new WidgetsList(i2, this.mLeft, this.mTop, this.mRight, this.mBottom, i3);
        this.mChainList.add(widgetsList);
        if (i2 == 0) {
            i4 = 0;
            int i8 = 0;
            int i9 = 0;
            while (i9 < i) {
                ConstraintWidget constraintWidget = constraintWidgetArr[i9];
                int widgetWidth = getWidgetWidth(constraintWidget, i3);
                if (constraintWidget.getHorizontalDimensionBehaviour() == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT) {
                    i4++;
                }
                int i10 = i4;
                boolean z = (i8 == i3 || (this.mHorizontalGap + i8) + widgetWidth > i3) && widgetsList.biggest != null;
                if (!z && i9 > 0 && (i7 = this.mMaxElementsWrap) > 0 && i9 % i7 == 0) {
                    z = true;
                }
                if (z) {
                    widgetsList = new WidgetsList(i2, this.mLeft, this.mTop, this.mRight, this.mBottom, i3);
                    widgetsList.setStartIndex(i9);
                    this.mChainList.add(widgetsList);
                } else if (i9 > 0) {
                    i8 += this.mHorizontalGap + widgetWidth;
                    widgetsList.add(constraintWidget);
                    i9++;
                    i4 = i10;
                }
                i8 = widgetWidth;
                widgetsList.add(constraintWidget);
                i9++;
                i4 = i10;
            }
        } else {
            i4 = 0;
            int i11 = 0;
            int i12 = 0;
            while (i12 < i) {
                ConstraintWidget constraintWidget2 = constraintWidgetArr[i12];
                int widgetHeight = getWidgetHeight(constraintWidget2, i3);
                if (constraintWidget2.getVerticalDimensionBehaviour() == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT) {
                    i4++;
                }
                int i13 = i4;
                boolean z2 = (i11 == i3 || (this.mVerticalGap + i11) + widgetHeight > i3) && widgetsList.biggest != null;
                if (!z2 && i12 > 0 && (i5 = this.mMaxElementsWrap) > 0 && i12 % i5 == 0) {
                    z2 = true;
                }
                if (z2) {
                    widgetsList = new WidgetsList(i2, this.mLeft, this.mTop, this.mRight, this.mBottom, i3);
                    widgetsList.setStartIndex(i12);
                    this.mChainList.add(widgetsList);
                } else if (i12 > 0) {
                    i11 += this.mVerticalGap + widgetHeight;
                    widgetsList.add(constraintWidget2);
                    i12++;
                    i4 = i13;
                }
                i11 = widgetHeight;
                widgetsList.add(constraintWidget2);
                i12++;
                i4 = i13;
            }
        }
        int size = this.mChainList.size();
        ConstraintAnchor constraintAnchor3 = this.mLeft;
        ConstraintAnchor constraintAnchor4 = this.mTop;
        ConstraintAnchor constraintAnchor5 = this.mRight;
        ConstraintAnchor constraintAnchor6 = this.mBottom;
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight2 = getPaddingRight();
        int paddingBottom2 = getPaddingBottom();
        boolean z3 = getHorizontalDimensionBehaviour() == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT || getVerticalDimensionBehaviour() == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT;
        if (i4 > 0 && z3) {
            for (int i14 = 0; i14 < size; i14++) {
                WidgetsList widgetsList2 = this.mChainList.get(i14);
                if (i2 == 0) {
                    widgetsList2.measureMatchConstraints(i3 - widgetsList2.getWidth());
                } else {
                    widgetsList2.measureMatchConstraints(i3 - widgetsList2.getHeight());
                }
            }
        }
        int i15 = paddingTop;
        int i16 = paddingRight2;
        int i17 = 0;
        int i18 = 0;
        int i19 = 0;
        int i20 = paddingLeft;
        ConstraintAnchor constraintAnchor7 = constraintAnchor4;
        ConstraintAnchor constraintAnchor8 = constraintAnchor3;
        int i21 = paddingBottom2;
        while (i19 < size) {
            WidgetsList widgetsList3 = this.mChainList.get(i19);
            if (i2 == 0) {
                if (i19 < size - 1) {
                    constraintAnchor2 = this.mChainList.get(i19 + 1).biggest.mTop;
                    paddingBottom = 0;
                } else {
                    constraintAnchor2 = this.mBottom;
                    paddingBottom = getPaddingBottom();
                }
                ConstraintAnchor constraintAnchor9 = widgetsList3.biggest.mBottom;
                ConstraintAnchor constraintAnchor10 = constraintAnchor8;
                ConstraintAnchor constraintAnchor11 = constraintAnchor8;
                int i22 = i17;
                ConstraintAnchor constraintAnchor12 = constraintAnchor7;
                int i23 = i18;
                ConstraintAnchor constraintAnchor13 = constraintAnchor5;
                ConstraintAnchor constraintAnchor14 = constraintAnchor5;
                i6 = i19;
                widgetsList3.setup(i2, constraintAnchor10, constraintAnchor12, constraintAnchor13, constraintAnchor2, i20, i15, i16, paddingBottom, i3);
                int max = Math.max(i23, widgetsList3.getWidth());
                i17 = i22 + widgetsList3.getHeight();
                if (i6 > 0) {
                    i17 += this.mVerticalGap;
                }
                constraintAnchor8 = constraintAnchor11;
                i18 = max;
                i15 = 0;
                constraintAnchor7 = constraintAnchor9;
                constraintAnchor = constraintAnchor14;
                int i24 = paddingBottom;
                constraintAnchor6 = constraintAnchor2;
                i21 = i24;
            } else {
                ConstraintAnchor constraintAnchor15 = constraintAnchor8;
                int i25 = i17;
                int i26 = i18;
                i6 = i19;
                if (i6 < size - 1) {
                    constraintAnchor = this.mChainList.get(i6 + 1).biggest.mLeft;
                    paddingRight = 0;
                } else {
                    constraintAnchor = this.mRight;
                    paddingRight = getPaddingRight();
                }
                ConstraintAnchor constraintAnchor16 = widgetsList3.biggest.mRight;
                widgetsList3.setup(i2, constraintAnchor15, constraintAnchor7, constraintAnchor, constraintAnchor6, i20, i15, paddingRight, i21, i3);
                i18 = i26 + widgetsList3.getWidth();
                int max2 = Math.max(i25, widgetsList3.getHeight());
                if (i6 > 0) {
                    i18 += this.mHorizontalGap;
                }
                i17 = max2;
                i20 = 0;
                i16 = paddingRight;
                constraintAnchor8 = constraintAnchor16;
            }
            i19 = i6 + 1;
            constraintAnchor5 = constraintAnchor;
        }
        iArr[0] = i18;
        iArr[1] = i17;
    }

    private void measureChainWrap_new(ConstraintWidget[] constraintWidgetArr, int i, int i2, int i3, int[] iArr) {
        int i4;
        int i5;
        int i6;
        ConstraintAnchor constraintAnchor;
        int paddingRight;
        ConstraintAnchor constraintAnchor2;
        int paddingBottom;
        int i7;
        if (i == 0) {
            return;
        }
        this.mChainList.clear();
        WidgetsList widgetsList = new WidgetsList(i2, this.mLeft, this.mTop, this.mRight, this.mBottom, i3);
        this.mChainList.add(widgetsList);
        if (i2 == 0) {
            int i8 = 0;
            i4 = 0;
            int i9 = 0;
            int i10 = 0;
            while (i10 < i) {
                int i11 = i8 + 1;
                ConstraintWidget constraintWidget = constraintWidgetArr[i10];
                int widgetWidth = getWidgetWidth(constraintWidget, i3);
                if (constraintWidget.getHorizontalDimensionBehaviour() == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT) {
                    i4++;
                }
                int i12 = i4;
                boolean z = (i9 == i3 || (this.mHorizontalGap + i9) + widgetWidth > i3) && widgetsList.biggest != null;
                if (!z && i10 > 0 && (i7 = this.mMaxElementsWrap) > 0 && i11 > i7) {
                    z = true;
                }
                if (z) {
                    widgetsList = new WidgetsList(i2, this.mLeft, this.mTop, this.mRight, this.mBottom, i3);
                    widgetsList.setStartIndex(i10);
                    this.mChainList.add(widgetsList);
                    i8 = i11;
                    i9 = widgetWidth;
                } else {
                    i9 = i10 > 0 ? i9 + this.mHorizontalGap + widgetWidth : widgetWidth;
                    i8 = 0;
                }
                widgetsList.add(constraintWidget);
                i10++;
                i4 = i12;
            }
        } else {
            int i13 = 0;
            i4 = 0;
            int i14 = 0;
            while (i14 < i) {
                ConstraintWidget constraintWidget2 = constraintWidgetArr[i14];
                int widgetHeight = getWidgetHeight(constraintWidget2, i3);
                if (constraintWidget2.getVerticalDimensionBehaviour() == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT) {
                    i4++;
                }
                int i15 = i4;
                boolean z2 = (i13 == i3 || (this.mVerticalGap + i13) + widgetHeight > i3) && widgetsList.biggest != null;
                if (!z2 && i14 > 0 && (i5 = this.mMaxElementsWrap) > 0 && i5 < 0) {
                    z2 = true;
                }
                if (z2) {
                    widgetsList = new WidgetsList(i2, this.mLeft, this.mTop, this.mRight, this.mBottom, i3);
                    widgetsList.setStartIndex(i14);
                    this.mChainList.add(widgetsList);
                } else if (i14 > 0) {
                    i13 += this.mVerticalGap + widgetHeight;
                    widgetsList.add(constraintWidget2);
                    i14++;
                    i4 = i15;
                }
                i13 = widgetHeight;
                widgetsList.add(constraintWidget2);
                i14++;
                i4 = i15;
            }
        }
        int size = this.mChainList.size();
        ConstraintAnchor constraintAnchor3 = this.mLeft;
        ConstraintAnchor constraintAnchor4 = this.mTop;
        ConstraintAnchor constraintAnchor5 = this.mRight;
        ConstraintAnchor constraintAnchor6 = this.mBottom;
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight2 = getPaddingRight();
        int paddingBottom2 = getPaddingBottom();
        boolean z3 = getHorizontalDimensionBehaviour() == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT || getVerticalDimensionBehaviour() == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT;
        if (i4 > 0 && z3) {
            for (int i16 = 0; i16 < size; i16++) {
                WidgetsList widgetsList2 = this.mChainList.get(i16);
                if (i2 == 0) {
                    widgetsList2.measureMatchConstraints(i3 - widgetsList2.getWidth());
                } else {
                    widgetsList2.measureMatchConstraints(i3 - widgetsList2.getHeight());
                }
            }
        }
        int i17 = paddingTop;
        int i18 = paddingRight2;
        int i19 = 0;
        int i20 = 0;
        int i21 = 0;
        int i22 = paddingLeft;
        ConstraintAnchor constraintAnchor7 = constraintAnchor4;
        ConstraintAnchor constraintAnchor8 = constraintAnchor3;
        int i23 = paddingBottom2;
        while (i21 < size) {
            WidgetsList widgetsList3 = this.mChainList.get(i21);
            if (i2 == 0) {
                if (i21 < size - 1) {
                    constraintAnchor2 = this.mChainList.get(i21 + 1).biggest.mTop;
                    paddingBottom = 0;
                } else {
                    constraintAnchor2 = this.mBottom;
                    paddingBottom = getPaddingBottom();
                }
                ConstraintAnchor constraintAnchor9 = widgetsList3.biggest.mBottom;
                ConstraintAnchor constraintAnchor10 = constraintAnchor8;
                ConstraintAnchor constraintAnchor11 = constraintAnchor8;
                int i24 = i19;
                ConstraintAnchor constraintAnchor12 = constraintAnchor7;
                int i25 = i20;
                ConstraintAnchor constraintAnchor13 = constraintAnchor5;
                ConstraintAnchor constraintAnchor14 = constraintAnchor5;
                i6 = i21;
                widgetsList3.setup(i2, constraintAnchor10, constraintAnchor12, constraintAnchor13, constraintAnchor2, i22, i17, i18, paddingBottom, i3);
                int max = Math.max(i25, widgetsList3.getWidth());
                i19 = i24 + widgetsList3.getHeight();
                if (i6 > 0) {
                    i19 += this.mVerticalGap;
                }
                constraintAnchor8 = constraintAnchor11;
                i20 = max;
                i17 = 0;
                constraintAnchor7 = constraintAnchor9;
                constraintAnchor = constraintAnchor14;
                int i26 = paddingBottom;
                constraintAnchor6 = constraintAnchor2;
                i23 = i26;
            } else {
                ConstraintAnchor constraintAnchor15 = constraintAnchor8;
                int i27 = i19;
                int i28 = i20;
                i6 = i21;
                if (i6 < size - 1) {
                    constraintAnchor = this.mChainList.get(i6 + 1).biggest.mLeft;
                    paddingRight = 0;
                } else {
                    constraintAnchor = this.mRight;
                    paddingRight = getPaddingRight();
                }
                ConstraintAnchor constraintAnchor16 = widgetsList3.biggest.mRight;
                widgetsList3.setup(i2, constraintAnchor15, constraintAnchor7, constraintAnchor, constraintAnchor6, i22, i17, paddingRight, i23, i3);
                i20 = i28 + widgetsList3.getWidth();
                int max2 = Math.max(i27, widgetsList3.getHeight());
                if (i6 > 0) {
                    i20 += this.mHorizontalGap;
                }
                i19 = max2;
                i22 = 0;
                i18 = paddingRight;
                constraintAnchor8 = constraintAnchor16;
            }
            i21 = i6 + 1;
            constraintAnchor5 = constraintAnchor;
        }
        iArr[0] = i20;
        iArr[1] = i19;
    }

    private void measureNoWrap(ConstraintWidget[] constraintWidgetArr, int i, int i2, int i3, int[] iArr) {
        WidgetsList widgetsList;
        if (i == 0) {
            return;
        }
        if (this.mChainList.size() == 0) {
            widgetsList = new WidgetsList(i2, this.mLeft, this.mTop, this.mRight, this.mBottom, i3);
            this.mChainList.add(widgetsList);
        } else {
            WidgetsList widgetsList2 = this.mChainList.get(0);
            widgetsList2.clear();
            widgetsList = widgetsList2;
            widgetsList.setup(i2, this.mLeft, this.mTop, this.mRight, this.mBottom, getPaddingLeft(), getPaddingTop(), getPaddingRight(), getPaddingBottom(), i3);
        }
        for (int i4 = 0; i4 < i; i4++) {
            widgetsList.add(constraintWidgetArr[i4]);
        }
        iArr[0] = widgetsList.getWidth();
        iArr[1] = widgetsList.getHeight();
    }

    /* JADX WARN: Removed duplicated region for block: B:45:0x0068  */
    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:105:0x011b -> B:42:0x0063). Please submit an issue!!! */
    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:106:0x011d -> B:42:0x0063). Please submit an issue!!! */
    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:108:0x0123 -> B:42:0x0063). Please submit an issue!!! */
    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:109:0x0125 -> B:42:0x0063). Please submit an issue!!! */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void measureAligned(androidx.constraintlayout.core.widgets.ConstraintWidget[] r17, int r18, int r19, int r20, int[] r21) {
        /*
            Method dump skipped, instructions count: 306
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.constraintlayout.core.widgets.Flow.measureAligned(androidx.constraintlayout.core.widgets.ConstraintWidget[], int, int, int, int[]):void");
    }

    private void createAlignedConstraints(boolean z) {
        ConstraintWidget constraintWidget;
        float f;
        int i;
        if (this.mAlignedDimensions == null || this.mAlignedBiggestElementsInCols == null || this.mAlignedBiggestElementsInRows == null) {
            return;
        }
        for (int i2 = 0; i2 < this.mDisplayedWidgetsCount; i2++) {
            this.mDisplayedWidgets[i2].resetAnchors();
        }
        int[] iArr = this.mAlignedDimensions;
        int i3 = iArr[0];
        int i4 = iArr[1];
        ConstraintWidget constraintWidget2 = null;
        float f2 = this.mHorizontalBias;
        int i5 = 0;
        while (i5 < i3) {
            if (z) {
                i = (i3 - i5) - 1;
                f = 1.0f - this.mHorizontalBias;
            } else {
                f = f2;
                i = i5;
            }
            ConstraintWidget constraintWidget3 = this.mAlignedBiggestElementsInCols[i];
            if (constraintWidget3 != null && constraintWidget3.getVisibility() != 8) {
                if (i5 == 0) {
                    constraintWidget3.connect(constraintWidget3.mLeft, this.mLeft, getPaddingLeft());
                    constraintWidget3.setHorizontalChainStyle(this.mHorizontalStyle);
                    constraintWidget3.setHorizontalBiasPercent(f);
                }
                if (i5 == i3 - 1) {
                    constraintWidget3.connect(constraintWidget3.mRight, this.mRight, getPaddingRight());
                }
                if (i5 > 0 && constraintWidget2 != null) {
                    constraintWidget3.connect(constraintWidget3.mLeft, constraintWidget2.mRight, this.mHorizontalGap);
                    constraintWidget2.connect(constraintWidget2.mRight, constraintWidget3.mLeft, 0);
                }
                constraintWidget2 = constraintWidget3;
            }
            i5++;
            f2 = f;
        }
        for (int i6 = 0; i6 < i4; i6++) {
            ConstraintWidget constraintWidget4 = this.mAlignedBiggestElementsInRows[i6];
            if (constraintWidget4 != null && constraintWidget4.getVisibility() != 8) {
                if (i6 == 0) {
                    constraintWidget4.connect(constraintWidget4.mTop, this.mTop, getPaddingTop());
                    constraintWidget4.setVerticalChainStyle(this.mVerticalStyle);
                    constraintWidget4.setVerticalBiasPercent(this.mVerticalBias);
                }
                if (i6 == i4 - 1) {
                    constraintWidget4.connect(constraintWidget4.mBottom, this.mBottom, getPaddingBottom());
                }
                if (i6 > 0 && constraintWidget2 != null) {
                    constraintWidget4.connect(constraintWidget4.mTop, constraintWidget2.mBottom, this.mVerticalGap);
                    constraintWidget2.connect(constraintWidget2.mBottom, constraintWidget4.mTop, 0);
                }
                constraintWidget2 = constraintWidget4;
            }
        }
        for (int i7 = 0; i7 < i3; i7++) {
            for (int i8 = 0; i8 < i4; i8++) {
                int i9 = (i8 * i3) + i7;
                if (this.mOrientation == 1) {
                    i9 = (i7 * i4) + i8;
                }
                ConstraintWidget[] constraintWidgetArr = this.mDisplayedWidgets;
                if (i9 < constraintWidgetArr.length && (constraintWidget = constraintWidgetArr[i9]) != null && constraintWidget.getVisibility() != 8) {
                    ConstraintWidget constraintWidget5 = this.mAlignedBiggestElementsInCols[i7];
                    ConstraintWidget constraintWidget6 = this.mAlignedBiggestElementsInRows[i8];
                    if (constraintWidget != constraintWidget5) {
                        constraintWidget.connect(constraintWidget.mLeft, constraintWidget5.mLeft, 0);
                        constraintWidget.connect(constraintWidget.mRight, constraintWidget5.mRight, 0);
                    }
                    if (constraintWidget != constraintWidget6) {
                        constraintWidget.connect(constraintWidget.mTop, constraintWidget6.mTop, 0);
                        constraintWidget.connect(constraintWidget.mBottom, constraintWidget6.mBottom, 0);
                    }
                }
            }
        }
    }

    @Override // androidx.constraintlayout.core.widgets.ConstraintWidget
    public void addToSolver(LinearSystem linearSystem, boolean z) {
        super.addToSolver(linearSystem, z);
        boolean z2 = getParent() != null && ((ConstraintWidgetContainer) getParent()).isRtl();
        int i = this.mWrapMode;
        if (i != 0) {
            if (i == 1) {
                int size = this.mChainList.size();
                int i2 = 0;
                while (i2 < size) {
                    this.mChainList.get(i2).createConstraints(z2, i2, i2 == size + (-1));
                    i2++;
                }
            } else if (i == 2) {
                createAlignedConstraints(z2);
            } else if (i == 3) {
                int size2 = this.mChainList.size();
                int i3 = 0;
                while (i3 < size2) {
                    this.mChainList.get(i3).createConstraints(z2, i3, i3 == size2 + (-1));
                    i3++;
                }
            }
        } else if (this.mChainList.size() > 0) {
            this.mChainList.get(0).createConstraints(z2, 0, true);
        }
        needsCallbackFromSolver(false);
    }
}
