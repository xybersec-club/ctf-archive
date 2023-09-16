package android.support.constraint.solver;

import java.io.PrintStream;
import java.util.Arrays;
/* loaded from: classes.dex */
public class ArrayLinkedVariables {
    private static final boolean DEBUG = false;
    private static final int NONE = -1;
    private int[] mArrayIndices;
    private int[] mArrayNextIndices;
    private float[] mArrayValues;
    private final Cache mCache;
    private boolean mDidFillOnce;
    private int mHead;
    private int mLast;
    private final ArrayRow mRow;
    int currentSize = 0;
    private int ROW_SIZE = 8;
    private SolverVariable candidate = null;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ArrayLinkedVariables(ArrayRow arrayRow, Cache cache) {
        int i = this.ROW_SIZE;
        this.mArrayIndices = new int[i];
        this.mArrayNextIndices = new int[i];
        this.mArrayValues = new float[i];
        this.mHead = -1;
        this.mLast = -1;
        this.mDidFillOnce = false;
        this.mRow = arrayRow;
        this.mCache = cache;
    }

    public final void put(SolverVariable solverVariable, float f) {
        if (f == 0.0f) {
            remove(solverVariable);
            return;
        }
        int i = this.mHead;
        if (i == -1) {
            this.mHead = 0;
            float[] fArr = this.mArrayValues;
            int i2 = this.mHead;
            fArr[i2] = f;
            this.mArrayIndices[i2] = solverVariable.id;
            this.mArrayNextIndices[this.mHead] = -1;
            this.currentSize++;
            if (this.mDidFillOnce) {
                return;
            }
            this.mLast++;
            return;
        }
        int i3 = -1;
        for (int i4 = 0; i != -1 && i4 < this.currentSize; i4++) {
            if (this.mArrayIndices[i] == solverVariable.id) {
                this.mArrayValues[i] = f;
                return;
            }
            if (this.mArrayIndices[i] < solverVariable.id) {
                i3 = i;
            }
            i = this.mArrayNextIndices[i];
        }
        int i5 = this.mLast;
        int i6 = i5 + 1;
        if (this.mDidFillOnce) {
            int[] iArr = this.mArrayIndices;
            if (iArr[i5] != -1) {
                i5 = iArr.length;
            }
        } else {
            i5 = i6;
        }
        int[] iArr2 = this.mArrayIndices;
        if (i5 >= iArr2.length && this.currentSize < iArr2.length) {
            int i7 = 0;
            while (true) {
                int[] iArr3 = this.mArrayIndices;
                if (i7 >= iArr3.length) {
                    break;
                } else if (iArr3[i7] == -1) {
                    i5 = i7;
                    break;
                } else {
                    i7++;
                }
            }
        }
        int[] iArr4 = this.mArrayIndices;
        if (i5 >= iArr4.length) {
            i5 = iArr4.length;
            this.ROW_SIZE *= 2;
            this.mDidFillOnce = false;
            this.mLast = i5 - 1;
            this.mArrayValues = Arrays.copyOf(this.mArrayValues, this.ROW_SIZE);
            this.mArrayIndices = Arrays.copyOf(this.mArrayIndices, this.ROW_SIZE);
            this.mArrayNextIndices = Arrays.copyOf(this.mArrayNextIndices, this.ROW_SIZE);
        }
        this.mArrayIndices[i5] = solverVariable.id;
        this.mArrayValues[i5] = f;
        if (i3 != -1) {
            int[] iArr5 = this.mArrayNextIndices;
            iArr5[i5] = iArr5[i3];
            iArr5[i3] = i5;
        } else {
            this.mArrayNextIndices[i5] = this.mHead;
            this.mHead = i5;
        }
        this.currentSize++;
        if (!this.mDidFillOnce) {
            this.mLast++;
        }
        if (this.currentSize >= this.mArrayIndices.length) {
            this.mDidFillOnce = true;
        }
    }

    public final void add(SolverVariable solverVariable, float f) {
        if (f == 0.0f) {
            return;
        }
        int i = this.mHead;
        if (i == -1) {
            this.mHead = 0;
            float[] fArr = this.mArrayValues;
            int i2 = this.mHead;
            fArr[i2] = f;
            this.mArrayIndices[i2] = solverVariable.id;
            this.mArrayNextIndices[this.mHead] = -1;
            this.currentSize++;
            if (this.mDidFillOnce) {
                return;
            }
            this.mLast++;
            return;
        }
        int i3 = -1;
        for (int i4 = 0; i != -1 && i4 < this.currentSize; i4++) {
            int i5 = this.mArrayIndices[i];
            if (i5 == solverVariable.id) {
                float[] fArr2 = this.mArrayValues;
                fArr2[i] = fArr2[i] + f;
                if (fArr2[i] == 0.0f) {
                    if (i == this.mHead) {
                        this.mHead = this.mArrayNextIndices[i];
                    } else {
                        int[] iArr = this.mArrayNextIndices;
                        iArr[i3] = iArr[i];
                    }
                    this.mCache.mIndexedVariables[i5].removeClientEquation(this.mRow);
                    if (this.mDidFillOnce) {
                        this.mLast = i;
                    }
                    this.currentSize--;
                    return;
                }
                return;
            }
            if (this.mArrayIndices[i] < solverVariable.id) {
                i3 = i;
            }
            i = this.mArrayNextIndices[i];
        }
        int i6 = this.mLast;
        int i7 = i6 + 1;
        if (this.mDidFillOnce) {
            int[] iArr2 = this.mArrayIndices;
            if (iArr2[i6] != -1) {
                i6 = iArr2.length;
            }
        } else {
            i6 = i7;
        }
        int[] iArr3 = this.mArrayIndices;
        if (i6 >= iArr3.length && this.currentSize < iArr3.length) {
            int i8 = 0;
            while (true) {
                int[] iArr4 = this.mArrayIndices;
                if (i8 >= iArr4.length) {
                    break;
                } else if (iArr4[i8] == -1) {
                    i6 = i8;
                    break;
                } else {
                    i8++;
                }
            }
        }
        int[] iArr5 = this.mArrayIndices;
        if (i6 >= iArr5.length) {
            i6 = iArr5.length;
            this.ROW_SIZE *= 2;
            this.mDidFillOnce = false;
            this.mLast = i6 - 1;
            this.mArrayValues = Arrays.copyOf(this.mArrayValues, this.ROW_SIZE);
            this.mArrayIndices = Arrays.copyOf(this.mArrayIndices, this.ROW_SIZE);
            this.mArrayNextIndices = Arrays.copyOf(this.mArrayNextIndices, this.ROW_SIZE);
        }
        this.mArrayIndices[i6] = solverVariable.id;
        this.mArrayValues[i6] = f;
        if (i3 != -1) {
            int[] iArr6 = this.mArrayNextIndices;
            iArr6[i6] = iArr6[i3];
            iArr6[i3] = i6;
        } else {
            this.mArrayNextIndices[i6] = this.mHead;
            this.mHead = i6;
        }
        this.currentSize++;
        if (!this.mDidFillOnce) {
            this.mLast++;
        }
        int i9 = this.mLast;
        int[] iArr7 = this.mArrayIndices;
        if (i9 >= iArr7.length) {
            this.mDidFillOnce = true;
            this.mLast = iArr7.length - 1;
        }
    }

    public final float remove(SolverVariable solverVariable) {
        if (this.candidate == solverVariable) {
            this.candidate = null;
        }
        int i = this.mHead;
        if (i == -1) {
            return 0.0f;
        }
        int i2 = 0;
        int i3 = -1;
        while (i != -1 && i2 < this.currentSize) {
            int i4 = this.mArrayIndices[i];
            if (i4 == solverVariable.id) {
                if (i == this.mHead) {
                    this.mHead = this.mArrayNextIndices[i];
                } else {
                    int[] iArr = this.mArrayNextIndices;
                    iArr[i3] = iArr[i];
                }
                this.mCache.mIndexedVariables[i4].removeClientEquation(this.mRow);
                this.currentSize--;
                this.mArrayIndices[i] = -1;
                if (this.mDidFillOnce) {
                    this.mLast = i;
                }
                return this.mArrayValues[i];
            }
            i2++;
            i3 = i;
            i = this.mArrayNextIndices[i];
        }
        return 0.0f;
    }

    public final void clear() {
        this.mHead = -1;
        this.mLast = -1;
        this.mDidFillOnce = false;
        this.currentSize = 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean containsKey(SolverVariable solverVariable) {
        int i = this.mHead;
        if (i == -1) {
            return false;
        }
        for (int i2 = 0; i != -1 && i2 < this.currentSize; i2++) {
            if (this.mArrayIndices[i] == solverVariable.id) {
                return true;
            }
            i = this.mArrayNextIndices[i];
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean hasAtLeastOnePositiveVariable() {
        int i = this.mHead;
        for (int i2 = 0; i != -1 && i2 < this.currentSize; i2++) {
            if (this.mArrayValues[i] > 0.0f) {
                return true;
            }
            i = this.mArrayNextIndices[i];
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void invert() {
        int i = this.mHead;
        for (int i2 = 0; i != -1 && i2 < this.currentSize; i2++) {
            float[] fArr = this.mArrayValues;
            fArr[i] = fArr[i] * (-1.0f);
            i = this.mArrayNextIndices[i];
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void divideByAmount(float f) {
        int i = this.mHead;
        for (int i2 = 0; i != -1 && i2 < this.currentSize; i2++) {
            float[] fArr = this.mArrayValues;
            fArr[i] = fArr[i] / f;
            i = this.mArrayNextIndices[i];
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void updateClientEquations(ArrayRow arrayRow) {
        int i = this.mHead;
        for (int i2 = 0; i != -1 && i2 < this.currentSize; i2++) {
            this.mCache.mIndexedVariables[this.mArrayIndices[i]].addClientEquation(arrayRow);
            i = this.mArrayNextIndices[i];
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Removed duplicated region for block: B:18:0x002d  */
    /* JADX WARN: Removed duplicated region for block: B:39:0x0053 A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public android.support.constraint.solver.SolverVariable pickPivotCandidate() {
        /*
            r9 = this;
            int r0 = r9.mHead
            r1 = 0
            r2 = 0
            r3 = r1
        L5:
            r4 = -1
            if (r0 == r4) goto L5a
            int r4 = r9.currentSize
            if (r2 >= r4) goto L5a
            float[] r4 = r9.mArrayValues
            r5 = r4[r0]
            r6 = 981668463(0x3a83126f, float:0.001)
            r7 = 0
            int r8 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
            if (r8 >= 0) goto L22
            r6 = -1165815185(0xffffffffba83126f, float:-0.001)
            int r6 = (r5 > r6 ? 1 : (r5 == r6 ? 0 : -1))
            if (r6 <= 0) goto L29
            r4[r0] = r7
            goto L28
        L22:
            int r6 = (r5 > r6 ? 1 : (r5 == r6 ? 0 : -1))
            if (r6 >= 0) goto L29
            r4[r0] = r7
        L28:
            r5 = 0
        L29:
            int r4 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
            if (r4 == 0) goto L53
            android.support.constraint.solver.Cache r4 = r9.mCache
            android.support.constraint.solver.SolverVariable[] r4 = r4.mIndexedVariables
            int[] r6 = r9.mArrayIndices
            r6 = r6[r0]
            r4 = r4[r6]
            android.support.constraint.solver.SolverVariable$Type r6 = r4.mType
            android.support.constraint.solver.SolverVariable$Type r8 = android.support.constraint.solver.SolverVariable.Type.UNRESTRICTED
            if (r6 != r8) goto L46
            int r5 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
            if (r5 >= 0) goto L42
            return r4
        L42:
            if (r3 != 0) goto L53
            r3 = r4
            goto L53
        L46:
            int r5 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
            if (r5 >= 0) goto L53
            if (r1 == 0) goto L52
            int r5 = r4.strength
            int r6 = r1.strength
            if (r5 >= r6) goto L53
        L52:
            r1 = r4
        L53:
            int[] r4 = r9.mArrayNextIndices
            r0 = r4[r0]
            int r2 = r2 + 1
            goto L5
        L5a:
            if (r3 == 0) goto L5d
            return r3
        L5d:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.constraint.solver.ArrayLinkedVariables.pickPivotCandidate():android.support.constraint.solver.SolverVariable");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void updateFromRow(ArrayRow arrayRow, ArrayRow arrayRow2) {
        int i = this.mHead;
        while (true) {
            for (int i2 = 0; i != -1 && i2 < this.currentSize; i2++) {
                if (this.mArrayIndices[i] == arrayRow2.variable.id) {
                    float f = this.mArrayValues[i];
                    remove(arrayRow2.variable);
                    ArrayLinkedVariables arrayLinkedVariables = arrayRow2.variables;
                    int i3 = arrayLinkedVariables.mHead;
                    for (int i4 = 0; i3 != -1 && i4 < arrayLinkedVariables.currentSize; i4++) {
                        add(this.mCache.mIndexedVariables[arrayLinkedVariables.mArrayIndices[i3]], arrayLinkedVariables.mArrayValues[i3] * f);
                        i3 = arrayLinkedVariables.mArrayNextIndices[i3];
                    }
                    arrayRow.constantValue += arrayRow2.constantValue * f;
                    arrayRow2.variable.removeClientEquation(arrayRow);
                    i = this.mHead;
                } else {
                    i = this.mArrayNextIndices[i];
                }
            }
            return;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void updateFromSystem(ArrayRow arrayRow, ArrayRow[] arrayRowArr) {
        int i = this.mHead;
        while (true) {
            for (int i2 = 0; i != -1 && i2 < this.currentSize; i2++) {
                SolverVariable solverVariable = this.mCache.mIndexedVariables[this.mArrayIndices[i]];
                if (solverVariable.definitionId != -1) {
                    float f = this.mArrayValues[i];
                    remove(solverVariable);
                    ArrayRow arrayRow2 = arrayRowArr[solverVariable.definitionId];
                    if (!arrayRow2.isSimpleDefinition) {
                        ArrayLinkedVariables arrayLinkedVariables = arrayRow2.variables;
                        int i3 = arrayLinkedVariables.mHead;
                        for (int i4 = 0; i3 != -1 && i4 < arrayLinkedVariables.currentSize; i4++) {
                            add(this.mCache.mIndexedVariables[arrayLinkedVariables.mArrayIndices[i3]], arrayLinkedVariables.mArrayValues[i3] * f);
                            i3 = arrayLinkedVariables.mArrayNextIndices[i3];
                        }
                    }
                    arrayRow.constantValue += arrayRow2.constantValue * f;
                    arrayRow2.variable.removeClientEquation(arrayRow);
                    i = this.mHead;
                } else {
                    i = this.mArrayNextIndices[i];
                }
            }
            return;
        }
    }

    SolverVariable getPivotCandidate() {
        SolverVariable solverVariable = this.candidate;
        if (solverVariable == null) {
            int i = this.mHead;
            SolverVariable solverVariable2 = null;
            for (int i2 = 0; i != -1 && i2 < this.currentSize; i2++) {
                if (this.mArrayValues[i] < 0.0f) {
                    SolverVariable solverVariable3 = this.mCache.mIndexedVariables[this.mArrayIndices[i]];
                    if (solverVariable2 == null || solverVariable2.strength < solverVariable3.strength) {
                        solverVariable2 = solverVariable3;
                    }
                }
                i = this.mArrayNextIndices[i];
            }
            return solverVariable2;
        }
        return solverVariable;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final SolverVariable getVariable(int i) {
        int i2 = this.mHead;
        for (int i3 = 0; i2 != -1 && i3 < this.currentSize; i3++) {
            if (i3 == i) {
                return this.mCache.mIndexedVariables[this.mArrayIndices[i2]];
            }
            i2 = this.mArrayNextIndices[i2];
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final float getVariableValue(int i) {
        int i2 = this.mHead;
        for (int i3 = 0; i2 != -1 && i3 < this.currentSize; i3++) {
            if (i3 == i) {
                return this.mArrayValues[i2];
            }
            i2 = this.mArrayNextIndices[i2];
        }
        return 0.0f;
    }

    public final float get(SolverVariable solverVariable) {
        int i = this.mHead;
        for (int i2 = 0; i != -1 && i2 < this.currentSize; i2++) {
            if (this.mArrayIndices[i] == solverVariable.id) {
                return this.mArrayValues[i];
            }
            i = this.mArrayNextIndices[i];
        }
        return 0.0f;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int sizeInBytes() {
        return (this.mArrayIndices.length * 4 * 3) + 0 + 36;
    }

    public void display() {
        int i = this.currentSize;
        System.out.print("{ ");
        for (int i2 = 0; i2 < i; i2++) {
            SolverVariable variable = getVariable(i2);
            if (variable != null) {
                PrintStream printStream = System.out;
                printStream.print(variable + " = " + getVariableValue(i2) + " ");
            }
        }
        System.out.println(" }");
    }

    public String toString() {
        int i = this.mHead;
        String str = "";
        for (int i2 = 0; i != -1 && i2 < this.currentSize; i2++) {
            str = ((str + " -> ") + this.mArrayValues[i] + " : ") + this.mCache.mIndexedVariables[this.mArrayIndices[i]];
            i = this.mArrayNextIndices[i];
        }
        return str;
    }
}
