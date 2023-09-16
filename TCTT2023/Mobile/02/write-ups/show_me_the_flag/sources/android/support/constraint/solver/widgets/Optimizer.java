package android.support.constraint.solver.widgets;

import android.support.constraint.solver.LinearSystem;
import android.support.constraint.solver.SolverVariable;
import android.support.constraint.solver.widgets.ConstraintWidget;
/* loaded from: classes.dex */
public class Optimizer {
    /* JADX INFO: Access modifiers changed from: package-private */
    public static void applyDirectResolutionHorizontalChain(ConstraintWidgetContainer constraintWidgetContainer, LinearSystem linearSystem, int i, ConstraintWidget constraintWidget) {
        int i2;
        float f;
        float f2;
        float width;
        ConstraintWidget constraintWidget2 = constraintWidget;
        ConstraintWidget constraintWidget3 = null;
        int i3 = 0;
        float f3 = 0.0f;
        int i4 = 0;
        while (true) {
            if (constraintWidget2 == null) {
                break;
            }
            if (!(constraintWidget2.getVisibility() == 8)) {
                i3++;
                if (constraintWidget2.mHorizontalDimensionBehaviour != ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT) {
                    i4 = i4 + constraintWidget2.getWidth() + (constraintWidget2.mLeft.mTarget != null ? constraintWidget2.mLeft.getMargin() : 0) + (constraintWidget2.mRight.mTarget != null ? constraintWidget2.mRight.getMargin() : 0);
                } else {
                    f3 += constraintWidget2.mHorizontalWeight;
                }
            }
            ConstraintWidget constraintWidget4 = constraintWidget2.mRight.mTarget != null ? constraintWidget2.mRight.mTarget.mOwner : null;
            if (constraintWidget4 != null && (constraintWidget4.mLeft.mTarget == null || (constraintWidget4.mLeft.mTarget != null && constraintWidget4.mLeft.mTarget.mOwner != constraintWidget2))) {
                constraintWidget4 = null;
            }
            ConstraintWidget constraintWidget5 = constraintWidget4;
            constraintWidget3 = constraintWidget2;
            constraintWidget2 = constraintWidget5;
        }
        if (constraintWidget3 != null) {
            i2 = constraintWidget3.mRight.mTarget != null ? constraintWidget3.mRight.mTarget.mOwner.getX() : 0;
            if (constraintWidget3.mRight.mTarget != null && constraintWidget3.mRight.mTarget.mOwner == constraintWidgetContainer) {
                i2 = constraintWidgetContainer.getRight();
            }
        } else {
            i2 = 0;
        }
        float f4 = (i2 - 0) - i4;
        float f5 = f4 / (i3 + 1);
        if (i == 0) {
            f2 = f5;
            f = f2;
        } else {
            f = f4 / i;
            f2 = 0.0f;
        }
        ConstraintWidget constraintWidget6 = constraintWidget;
        while (constraintWidget6 != null) {
            int margin = constraintWidget6.mLeft.mTarget != null ? constraintWidget6.mLeft.getMargin() : 0;
            int margin2 = constraintWidget6.mRight.mTarget != null ? constraintWidget6.mRight.getMargin() : 0;
            if (constraintWidget6.getVisibility() != 8) {
                float f6 = margin;
                float f7 = f2 + f6;
                linearSystem.addEquality(constraintWidget6.mLeft.mSolverVariable, (int) (f7 + 0.5f));
                if (constraintWidget6.mHorizontalDimensionBehaviour == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT) {
                    width = (f3 == 0.0f ? f - f6 : ((constraintWidget6.mHorizontalWeight * f4) / f3) - f6) - margin2;
                } else {
                    width = constraintWidget6.getWidth();
                }
                float f8 = f7 + width;
                linearSystem.addEquality(constraintWidget6.mRight.mSolverVariable, (int) (0.5f + f8));
                if (i == 0) {
                    f8 += f;
                }
                f2 = f8 + margin2;
            } else {
                int i5 = (int) ((f2 - (f / 2.0f)) + 0.5f);
                linearSystem.addEquality(constraintWidget6.mLeft.mSolverVariable, i5);
                linearSystem.addEquality(constraintWidget6.mRight.mSolverVariable, i5);
            }
            ConstraintWidget constraintWidget7 = constraintWidget6.mRight.mTarget != null ? constraintWidget6.mRight.mTarget.mOwner : null;
            if (constraintWidget7 != null && constraintWidget7.mLeft.mTarget != null && constraintWidget7.mLeft.mTarget.mOwner != constraintWidget6) {
                constraintWidget7 = null;
            }
            constraintWidget6 = constraintWidget7 == constraintWidgetContainer ? null : constraintWidget7;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void applyDirectResolutionVerticalChain(ConstraintWidgetContainer constraintWidgetContainer, LinearSystem linearSystem, int i, ConstraintWidget constraintWidget) {
        int i2;
        float f;
        float f2;
        float height;
        ConstraintWidget constraintWidget2 = constraintWidget;
        ConstraintWidget constraintWidget3 = null;
        int i3 = 0;
        float f3 = 0.0f;
        int i4 = 0;
        while (true) {
            if (constraintWidget2 == null) {
                break;
            }
            if (!(constraintWidget2.getVisibility() == 8)) {
                i3++;
                if (constraintWidget2.mVerticalDimensionBehaviour != ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT) {
                    i4 = i4 + constraintWidget2.getHeight() + (constraintWidget2.mTop.mTarget != null ? constraintWidget2.mTop.getMargin() : 0) + (constraintWidget2.mBottom.mTarget != null ? constraintWidget2.mBottom.getMargin() : 0);
                } else {
                    f3 += constraintWidget2.mVerticalWeight;
                }
            }
            ConstraintWidget constraintWidget4 = constraintWidget2.mBottom.mTarget != null ? constraintWidget2.mBottom.mTarget.mOwner : null;
            if (constraintWidget4 != null && (constraintWidget4.mTop.mTarget == null || (constraintWidget4.mTop.mTarget != null && constraintWidget4.mTop.mTarget.mOwner != constraintWidget2))) {
                constraintWidget4 = null;
            }
            ConstraintWidget constraintWidget5 = constraintWidget4;
            constraintWidget3 = constraintWidget2;
            constraintWidget2 = constraintWidget5;
        }
        if (constraintWidget3 != null) {
            i2 = constraintWidget3.mBottom.mTarget != null ? constraintWidget3.mBottom.mTarget.mOwner.getX() : 0;
            if (constraintWidget3.mBottom.mTarget != null && constraintWidget3.mBottom.mTarget.mOwner == constraintWidgetContainer) {
                i2 = constraintWidgetContainer.getBottom();
            }
        } else {
            i2 = 0;
        }
        float f4 = (i2 - 0) - i4;
        float f5 = f4 / (i3 + 1);
        if (i == 0) {
            f2 = f5;
            f = f2;
        } else {
            f = f4 / i;
            f2 = 0.0f;
        }
        ConstraintWidget constraintWidget6 = constraintWidget;
        while (constraintWidget6 != null) {
            int margin = constraintWidget6.mTop.mTarget != null ? constraintWidget6.mTop.getMargin() : 0;
            int margin2 = constraintWidget6.mBottom.mTarget != null ? constraintWidget6.mBottom.getMargin() : 0;
            if (constraintWidget6.getVisibility() != 8) {
                float f6 = margin;
                float f7 = f2 + f6;
                linearSystem.addEquality(constraintWidget6.mTop.mSolverVariable, (int) (f7 + 0.5f));
                if (constraintWidget6.mVerticalDimensionBehaviour == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT) {
                    height = (f3 == 0.0f ? f - f6 : ((constraintWidget6.mVerticalWeight * f4) / f3) - f6) - margin2;
                } else {
                    height = constraintWidget6.getHeight();
                }
                float f8 = f7 + height;
                linearSystem.addEquality(constraintWidget6.mBottom.mSolverVariable, (int) (0.5f + f8));
                if (i == 0) {
                    f8 += f;
                }
                f2 = f8 + margin2;
            } else {
                int i5 = (int) ((f2 - (f / 2.0f)) + 0.5f);
                linearSystem.addEquality(constraintWidget6.mTop.mSolverVariable, i5);
                linearSystem.addEquality(constraintWidget6.mBottom.mSolverVariable, i5);
            }
            ConstraintWidget constraintWidget7 = constraintWidget6.mBottom.mTarget != null ? constraintWidget6.mBottom.mTarget.mOwner : null;
            if (constraintWidget7 != null && constraintWidget7.mTop.mTarget != null && constraintWidget7.mTop.mTarget.mOwner != constraintWidget6) {
                constraintWidget7 = null;
            }
            constraintWidget6 = constraintWidget7 == constraintWidgetContainer ? null : constraintWidget7;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void checkMatchParent(ConstraintWidgetContainer constraintWidgetContainer, LinearSystem linearSystem, ConstraintWidget constraintWidget) {
        if (constraintWidgetContainer.mHorizontalDimensionBehaviour != ConstraintWidget.DimensionBehaviour.WRAP_CONTENT && constraintWidget.mHorizontalDimensionBehaviour == ConstraintWidget.DimensionBehaviour.MATCH_PARENT) {
            constraintWidget.mLeft.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mLeft);
            constraintWidget.mRight.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mRight);
            int i = constraintWidget.mLeft.mMargin;
            int width = constraintWidgetContainer.getWidth() - constraintWidget.mRight.mMargin;
            linearSystem.addEquality(constraintWidget.mLeft.mSolverVariable, i);
            linearSystem.addEquality(constraintWidget.mRight.mSolverVariable, width);
            constraintWidget.setHorizontalDimension(i, width);
            constraintWidget.mHorizontalResolution = 2;
        }
        if (constraintWidgetContainer.mVerticalDimensionBehaviour == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT || constraintWidget.mVerticalDimensionBehaviour != ConstraintWidget.DimensionBehaviour.MATCH_PARENT) {
            return;
        }
        constraintWidget.mTop.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mTop);
        constraintWidget.mBottom.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mBottom);
        int i2 = constraintWidget.mTop.mMargin;
        int height = constraintWidgetContainer.getHeight() - constraintWidget.mBottom.mMargin;
        linearSystem.addEquality(constraintWidget.mTop.mSolverVariable, i2);
        linearSystem.addEquality(constraintWidget.mBottom.mSolverVariable, height);
        if (constraintWidget.mBaselineDistance > 0 || constraintWidget.getVisibility() == 8) {
            constraintWidget.mBaseline.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mBaseline);
            linearSystem.addEquality(constraintWidget.mBaseline.mSolverVariable, constraintWidget.mBaselineDistance + i2);
        }
        constraintWidget.setVerticalDimension(i2, height);
        constraintWidget.mVerticalResolution = 2;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void checkHorizontalSimpleDependency(ConstraintWidgetContainer constraintWidgetContainer, LinearSystem linearSystem, ConstraintWidget constraintWidget) {
        float relativePercent;
        int width;
        if (constraintWidget.mHorizontalDimensionBehaviour == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT) {
            constraintWidget.mHorizontalResolution = 1;
        } else if (constraintWidgetContainer.mHorizontalDimensionBehaviour != ConstraintWidget.DimensionBehaviour.WRAP_CONTENT && constraintWidget.mHorizontalDimensionBehaviour == ConstraintWidget.DimensionBehaviour.MATCH_PARENT) {
            constraintWidget.mLeft.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mLeft);
            constraintWidget.mRight.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mRight);
            int i = constraintWidget.mLeft.mMargin;
            int width2 = constraintWidgetContainer.getWidth() - constraintWidget.mRight.mMargin;
            linearSystem.addEquality(constraintWidget.mLeft.mSolverVariable, i);
            linearSystem.addEquality(constraintWidget.mRight.mSolverVariable, width2);
            constraintWidget.setHorizontalDimension(i, width2);
            constraintWidget.mHorizontalResolution = 2;
        } else if (constraintWidget.mLeft.mTarget != null && constraintWidget.mRight.mTarget != null) {
            if (constraintWidget.mLeft.mTarget.mOwner == constraintWidgetContainer && constraintWidget.mRight.mTarget.mOwner == constraintWidgetContainer) {
                int margin = constraintWidget.mLeft.getMargin();
                int margin2 = constraintWidget.mRight.getMargin();
                if (constraintWidgetContainer.mHorizontalDimensionBehaviour == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT) {
                    width = constraintWidgetContainer.getWidth() - margin2;
                } else {
                    margin += (int) (((((constraintWidgetContainer.getWidth() - margin) - margin2) - constraintWidget.getWidth()) * constraintWidget.mHorizontalBiasPercent) + 0.5f);
                    width = constraintWidget.getWidth() + margin;
                }
                constraintWidget.mLeft.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mLeft);
                constraintWidget.mRight.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mRight);
                linearSystem.addEquality(constraintWidget.mLeft.mSolverVariable, margin);
                linearSystem.addEquality(constraintWidget.mRight.mSolverVariable, width);
                constraintWidget.mHorizontalResolution = 2;
                constraintWidget.setHorizontalDimension(margin, width);
                return;
            }
            constraintWidget.mHorizontalResolution = 1;
        } else if (constraintWidget.mLeft.mTarget != null && constraintWidget.mLeft.mTarget.mOwner == constraintWidgetContainer) {
            int margin3 = constraintWidget.mLeft.getMargin();
            int width3 = constraintWidget.getWidth() + margin3;
            constraintWidget.mLeft.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mLeft);
            constraintWidget.mRight.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mRight);
            linearSystem.addEquality(constraintWidget.mLeft.mSolverVariable, margin3);
            linearSystem.addEquality(constraintWidget.mRight.mSolverVariable, width3);
            constraintWidget.mHorizontalResolution = 2;
            constraintWidget.setHorizontalDimension(margin3, width3);
        } else if (constraintWidget.mRight.mTarget != null && constraintWidget.mRight.mTarget.mOwner == constraintWidgetContainer) {
            constraintWidget.mLeft.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mLeft);
            constraintWidget.mRight.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mRight);
            int width4 = constraintWidgetContainer.getWidth() - constraintWidget.mRight.getMargin();
            int width5 = width4 - constraintWidget.getWidth();
            linearSystem.addEquality(constraintWidget.mLeft.mSolverVariable, width5);
            linearSystem.addEquality(constraintWidget.mRight.mSolverVariable, width4);
            constraintWidget.mHorizontalResolution = 2;
            constraintWidget.setHorizontalDimension(width5, width4);
        } else if (constraintWidget.mLeft.mTarget != null && constraintWidget.mLeft.mTarget.mOwner.mHorizontalResolution == 2) {
            SolverVariable solverVariable = constraintWidget.mLeft.mTarget.mSolverVariable;
            constraintWidget.mLeft.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mLeft);
            constraintWidget.mRight.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mRight);
            int margin4 = (int) (solverVariable.computedValue + constraintWidget.mLeft.getMargin() + 0.5f);
            int width6 = constraintWidget.getWidth() + margin4;
            linearSystem.addEquality(constraintWidget.mLeft.mSolverVariable, margin4);
            linearSystem.addEquality(constraintWidget.mRight.mSolverVariable, width6);
            constraintWidget.mHorizontalResolution = 2;
            constraintWidget.setHorizontalDimension(margin4, width6);
        } else if (constraintWidget.mRight.mTarget != null && constraintWidget.mRight.mTarget.mOwner.mHorizontalResolution == 2) {
            SolverVariable solverVariable2 = constraintWidget.mRight.mTarget.mSolverVariable;
            constraintWidget.mLeft.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mLeft);
            constraintWidget.mRight.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mRight);
            int margin5 = (int) ((solverVariable2.computedValue - constraintWidget.mRight.getMargin()) + 0.5f);
            int width7 = margin5 - constraintWidget.getWidth();
            linearSystem.addEquality(constraintWidget.mLeft.mSolverVariable, width7);
            linearSystem.addEquality(constraintWidget.mRight.mSolverVariable, margin5);
            constraintWidget.mHorizontalResolution = 2;
            constraintWidget.setHorizontalDimension(width7, margin5);
        } else {
            boolean z = constraintWidget.mLeft.mTarget != null;
            boolean z2 = constraintWidget.mRight.mTarget != null;
            if (z || z2) {
                return;
            }
            if (constraintWidget instanceof Guideline) {
                Guideline guideline = (Guideline) constraintWidget;
                if (guideline.getOrientation() == 1) {
                    constraintWidget.mLeft.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mLeft);
                    constraintWidget.mRight.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mRight);
                    if (guideline.getRelativeBegin() != -1) {
                        relativePercent = guideline.getRelativeBegin();
                    } else if (guideline.getRelativeEnd() != -1) {
                        relativePercent = constraintWidgetContainer.getWidth() - guideline.getRelativeEnd();
                    } else {
                        relativePercent = guideline.getRelativePercent() * constraintWidgetContainer.getWidth();
                    }
                    int i2 = (int) (relativePercent + 0.5f);
                    linearSystem.addEquality(constraintWidget.mLeft.mSolverVariable, i2);
                    linearSystem.addEquality(constraintWidget.mRight.mSolverVariable, i2);
                    constraintWidget.mHorizontalResolution = 2;
                    constraintWidget.mVerticalResolution = 2;
                    constraintWidget.setHorizontalDimension(i2, i2);
                    constraintWidget.setVerticalDimension(0, constraintWidgetContainer.getHeight());
                    return;
                }
                return;
            }
            constraintWidget.mLeft.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mLeft);
            constraintWidget.mRight.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mRight);
            int x = constraintWidget.getX();
            int width8 = constraintWidget.getWidth() + x;
            linearSystem.addEquality(constraintWidget.mLeft.mSolverVariable, x);
            linearSystem.addEquality(constraintWidget.mRight.mSolverVariable, width8);
            constraintWidget.mHorizontalResolution = 2;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void checkVerticalSimpleDependency(ConstraintWidgetContainer constraintWidgetContainer, LinearSystem linearSystem, ConstraintWidget constraintWidget) {
        float relativePercent;
        int height;
        if (constraintWidget.mVerticalDimensionBehaviour == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT) {
            constraintWidget.mVerticalResolution = 1;
        } else if (constraintWidgetContainer.mVerticalDimensionBehaviour != ConstraintWidget.DimensionBehaviour.WRAP_CONTENT && constraintWidget.mVerticalDimensionBehaviour == ConstraintWidget.DimensionBehaviour.MATCH_PARENT) {
            constraintWidget.mTop.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mTop);
            constraintWidget.mBottom.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mBottom);
            int i = constraintWidget.mTop.mMargin;
            int height2 = constraintWidgetContainer.getHeight() - constraintWidget.mBottom.mMargin;
            linearSystem.addEquality(constraintWidget.mTop.mSolverVariable, i);
            linearSystem.addEquality(constraintWidget.mBottom.mSolverVariable, height2);
            if (constraintWidget.mBaselineDistance > 0 || constraintWidget.getVisibility() == 8) {
                constraintWidget.mBaseline.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mBaseline);
                linearSystem.addEquality(constraintWidget.mBaseline.mSolverVariable, constraintWidget.mBaselineDistance + i);
            }
            constraintWidget.setVerticalDimension(i, height2);
            constraintWidget.mVerticalResolution = 2;
        } else if (constraintWidget.mTop.mTarget != null && constraintWidget.mBottom.mTarget != null) {
            if (constraintWidget.mTop.mTarget.mOwner == constraintWidgetContainer && constraintWidget.mBottom.mTarget.mOwner == constraintWidgetContainer) {
                int margin = constraintWidget.mTop.getMargin();
                int margin2 = constraintWidget.mBottom.getMargin();
                if (constraintWidgetContainer.mVerticalDimensionBehaviour == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT) {
                    height = constraintWidget.getHeight();
                } else {
                    margin = (int) (margin + ((((constraintWidgetContainer.getHeight() - margin) - margin2) - constraintWidget.getHeight()) * constraintWidget.mVerticalBiasPercent) + 0.5f);
                    height = constraintWidget.getHeight();
                }
                int i2 = height + margin;
                constraintWidget.mTop.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mTop);
                constraintWidget.mBottom.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mBottom);
                linearSystem.addEquality(constraintWidget.mTop.mSolverVariable, margin);
                linearSystem.addEquality(constraintWidget.mBottom.mSolverVariable, i2);
                if (constraintWidget.mBaselineDistance > 0 || constraintWidget.getVisibility() == 8) {
                    constraintWidget.mBaseline.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mBaseline);
                    linearSystem.addEquality(constraintWidget.mBaseline.mSolverVariable, constraintWidget.mBaselineDistance + margin);
                }
                constraintWidget.mVerticalResolution = 2;
                constraintWidget.setVerticalDimension(margin, i2);
                return;
            }
            constraintWidget.mVerticalResolution = 1;
        } else if (constraintWidget.mTop.mTarget != null && constraintWidget.mTop.mTarget.mOwner == constraintWidgetContainer) {
            int margin3 = constraintWidget.mTop.getMargin();
            int height3 = constraintWidget.getHeight() + margin3;
            constraintWidget.mTop.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mTop);
            constraintWidget.mBottom.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mBottom);
            linearSystem.addEquality(constraintWidget.mTop.mSolverVariable, margin3);
            linearSystem.addEquality(constraintWidget.mBottom.mSolverVariable, height3);
            if (constraintWidget.mBaselineDistance > 0 || constraintWidget.getVisibility() == 8) {
                constraintWidget.mBaseline.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mBaseline);
                linearSystem.addEquality(constraintWidget.mBaseline.mSolverVariable, constraintWidget.mBaselineDistance + margin3);
            }
            constraintWidget.mVerticalResolution = 2;
            constraintWidget.setVerticalDimension(margin3, height3);
        } else if (constraintWidget.mBottom.mTarget != null && constraintWidget.mBottom.mTarget.mOwner == constraintWidgetContainer) {
            constraintWidget.mTop.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mTop);
            constraintWidget.mBottom.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mBottom);
            int height4 = constraintWidgetContainer.getHeight() - constraintWidget.mBottom.getMargin();
            int height5 = height4 - constraintWidget.getHeight();
            linearSystem.addEquality(constraintWidget.mTop.mSolverVariable, height5);
            linearSystem.addEquality(constraintWidget.mBottom.mSolverVariable, height4);
            if (constraintWidget.mBaselineDistance > 0 || constraintWidget.getVisibility() == 8) {
                constraintWidget.mBaseline.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mBaseline);
                linearSystem.addEquality(constraintWidget.mBaseline.mSolverVariable, constraintWidget.mBaselineDistance + height5);
            }
            constraintWidget.mVerticalResolution = 2;
            constraintWidget.setVerticalDimension(height5, height4);
        } else if (constraintWidget.mTop.mTarget != null && constraintWidget.mTop.mTarget.mOwner.mVerticalResolution == 2) {
            SolverVariable solverVariable = constraintWidget.mTop.mTarget.mSolverVariable;
            constraintWidget.mTop.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mTop);
            constraintWidget.mBottom.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mBottom);
            int margin4 = (int) (solverVariable.computedValue + constraintWidget.mTop.getMargin() + 0.5f);
            int height6 = constraintWidget.getHeight() + margin4;
            linearSystem.addEquality(constraintWidget.mTop.mSolverVariable, margin4);
            linearSystem.addEquality(constraintWidget.mBottom.mSolverVariable, height6);
            if (constraintWidget.mBaselineDistance > 0 || constraintWidget.getVisibility() == 8) {
                constraintWidget.mBaseline.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mBaseline);
                linearSystem.addEquality(constraintWidget.mBaseline.mSolverVariable, constraintWidget.mBaselineDistance + margin4);
            }
            constraintWidget.mVerticalResolution = 2;
            constraintWidget.setVerticalDimension(margin4, height6);
        } else if (constraintWidget.mBottom.mTarget != null && constraintWidget.mBottom.mTarget.mOwner.mVerticalResolution == 2) {
            SolverVariable solverVariable2 = constraintWidget.mBottom.mTarget.mSolverVariable;
            constraintWidget.mTop.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mTop);
            constraintWidget.mBottom.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mBottom);
            int margin5 = (int) ((solverVariable2.computedValue - constraintWidget.mBottom.getMargin()) + 0.5f);
            int height7 = margin5 - constraintWidget.getHeight();
            linearSystem.addEquality(constraintWidget.mTop.mSolverVariable, height7);
            linearSystem.addEquality(constraintWidget.mBottom.mSolverVariable, margin5);
            if (constraintWidget.mBaselineDistance > 0 || constraintWidget.getVisibility() == 8) {
                constraintWidget.mBaseline.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mBaseline);
                linearSystem.addEquality(constraintWidget.mBaseline.mSolverVariable, constraintWidget.mBaselineDistance + height7);
            }
            constraintWidget.mVerticalResolution = 2;
            constraintWidget.setVerticalDimension(height7, margin5);
        } else if (constraintWidget.mBaseline.mTarget != null && constraintWidget.mBaseline.mTarget.mOwner.mVerticalResolution == 2) {
            SolverVariable solverVariable3 = constraintWidget.mBaseline.mTarget.mSolverVariable;
            constraintWidget.mTop.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mTop);
            constraintWidget.mBottom.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mBottom);
            int i3 = (int) ((solverVariable3.computedValue - constraintWidget.mBaselineDistance) + 0.5f);
            int height8 = constraintWidget.getHeight() + i3;
            linearSystem.addEquality(constraintWidget.mTop.mSolverVariable, i3);
            linearSystem.addEquality(constraintWidget.mBottom.mSolverVariable, height8);
            constraintWidget.mBaseline.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mBaseline);
            linearSystem.addEquality(constraintWidget.mBaseline.mSolverVariable, constraintWidget.mBaselineDistance + i3);
            constraintWidget.mVerticalResolution = 2;
            constraintWidget.setVerticalDimension(i3, height8);
        } else {
            boolean z = constraintWidget.mBaseline.mTarget != null;
            boolean z2 = constraintWidget.mTop.mTarget != null;
            boolean z3 = constraintWidget.mBottom.mTarget != null;
            if (z || z2 || z3) {
                return;
            }
            if (constraintWidget instanceof Guideline) {
                Guideline guideline = (Guideline) constraintWidget;
                if (guideline.getOrientation() == 0) {
                    constraintWidget.mTop.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mTop);
                    constraintWidget.mBottom.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mBottom);
                    if (guideline.getRelativeBegin() != -1) {
                        relativePercent = guideline.getRelativeBegin();
                    } else if (guideline.getRelativeEnd() != -1) {
                        relativePercent = constraintWidgetContainer.getHeight() - guideline.getRelativeEnd();
                    } else {
                        relativePercent = guideline.getRelativePercent() * constraintWidgetContainer.getHeight();
                    }
                    int i4 = (int) (relativePercent + 0.5f);
                    linearSystem.addEquality(constraintWidget.mTop.mSolverVariable, i4);
                    linearSystem.addEquality(constraintWidget.mBottom.mSolverVariable, i4);
                    constraintWidget.mVerticalResolution = 2;
                    constraintWidget.mHorizontalResolution = 2;
                    constraintWidget.setVerticalDimension(i4, i4);
                    constraintWidget.setHorizontalDimension(0, constraintWidgetContainer.getWidth());
                    return;
                }
                return;
            }
            constraintWidget.mTop.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mTop);
            constraintWidget.mBottom.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mBottom);
            int y = constraintWidget.getY();
            linearSystem.addEquality(constraintWidget.mTop.mSolverVariable, y);
            linearSystem.addEquality(constraintWidget.mBottom.mSolverVariable, constraintWidget.getHeight() + y);
            if (constraintWidget.mBaselineDistance > 0 || constraintWidget.getVisibility() == 8) {
                constraintWidget.mBaseline.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mBaseline);
                linearSystem.addEquality(constraintWidget.mBaseline.mSolverVariable, y + constraintWidget.mBaselineDistance);
            }
            constraintWidget.mVerticalResolution = 2;
        }
    }
}
