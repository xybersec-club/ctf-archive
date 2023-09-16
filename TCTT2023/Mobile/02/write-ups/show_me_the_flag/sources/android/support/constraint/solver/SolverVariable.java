package android.support.constraint.solver;

import java.util.Arrays;
/* loaded from: classes.dex */
public class SolverVariable {
    private static final boolean INTERNAL_DEBUG = false;
    static final int MAX_STRENGTH = 6;
    public static final int STRENGTH_EQUALITY = 5;
    public static final int STRENGTH_HIGH = 3;
    public static final int STRENGTH_HIGHEST = 4;
    public static final int STRENGTH_LOW = 1;
    public static final int STRENGTH_MEDIUM = 2;
    public static final int STRENGTH_NONE = 0;
    private static int uniqueId = 1;
    public float computedValue;
    int definitionId;
    public int id;
    ArrayRow[] mClientEquations;
    int mClientEquationsCount;
    private String mName;
    Type mType;
    public int strength;
    float[] strengthVector;

    /* loaded from: classes.dex */
    public enum Type {
        UNRESTRICTED,
        CONSTANT,
        SLACK,
        ERROR,
        UNKNOWN
    }

    /* renamed from: android.support.constraint.solver.SolverVariable$1  reason: invalid class name */
    /* loaded from: classes.dex */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$android$support$constraint$solver$SolverVariable$Type = new int[Type.values().length];

        static {
            try {
                $SwitchMap$android$support$constraint$solver$SolverVariable$Type[Type.UNRESTRICTED.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$android$support$constraint$solver$SolverVariable$Type[Type.CONSTANT.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$android$support$constraint$solver$SolverVariable$Type[Type.SLACK.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$android$support$constraint$solver$SolverVariable$Type[Type.ERROR.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
        }
    }

    private static String getUniqueName(Type type) {
        uniqueId++;
        int i = AnonymousClass1.$SwitchMap$android$support$constraint$solver$SolverVariable$Type[type.ordinal()];
        if (i == 1) {
            return "U" + uniqueId;
        } else if (i == 2) {
            return "C" + uniqueId;
        } else if (i == 3) {
            return "S" + uniqueId;
        } else if (i == 4) {
            return "e" + uniqueId;
        } else {
            return "V" + uniqueId;
        }
    }

    public SolverVariable(String str, Type type) {
        this.id = -1;
        this.definitionId = -1;
        this.strength = 0;
        this.strengthVector = new float[6];
        this.mClientEquations = new ArrayRow[8];
        this.mClientEquationsCount = 0;
        this.mName = str;
        this.mType = type;
    }

    public SolverVariable(Type type) {
        this.id = -1;
        this.definitionId = -1;
        this.strength = 0;
        this.strengthVector = new float[6];
        this.mClientEquations = new ArrayRow[8];
        this.mClientEquationsCount = 0;
        this.mType = type;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void clearStrengths() {
        for (int i = 0; i < 6; i++) {
            this.strengthVector[i] = 0.0f;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String strengthsToString() {
        String str = this + "[";
        for (int i = 0; i < this.strengthVector.length; i++) {
            String str2 = str + this.strengthVector[i];
            str = i < this.strengthVector.length - 1 ? str2 + ", " : str2 + "] ";
        }
        return str;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void addClientEquation(ArrayRow arrayRow) {
        int i = 0;
        while (true) {
            int i2 = this.mClientEquationsCount;
            if (i < i2) {
                if (this.mClientEquations[i] == arrayRow) {
                    return;
                }
                i++;
            } else {
                ArrayRow[] arrayRowArr = this.mClientEquations;
                if (i2 >= arrayRowArr.length) {
                    this.mClientEquations = (ArrayRow[]) Arrays.copyOf(arrayRowArr, arrayRowArr.length * 2);
                }
                ArrayRow[] arrayRowArr2 = this.mClientEquations;
                int i3 = this.mClientEquationsCount;
                arrayRowArr2[i3] = arrayRow;
                this.mClientEquationsCount = i3 + 1;
                return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void removeClientEquation(ArrayRow arrayRow) {
        int i = 0;
        for (int i2 = 0; i2 < this.mClientEquationsCount; i2++) {
            if (this.mClientEquations[i2] == arrayRow) {
                while (true) {
                    int i3 = this.mClientEquationsCount;
                    if (i < (i3 - i2) - 1) {
                        ArrayRow[] arrayRowArr = this.mClientEquations;
                        int i4 = i2 + i;
                        arrayRowArr[i4] = arrayRowArr[i4 + 1];
                        i++;
                    } else {
                        this.mClientEquationsCount = i3 - 1;
                        return;
                    }
                }
            }
        }
    }

    public void reset() {
        this.mName = null;
        this.mType = Type.UNKNOWN;
        this.strength = 0;
        this.id = -1;
        this.definitionId = -1;
        this.computedValue = 0.0f;
        this.mClientEquationsCount = 0;
    }

    public String getName() {
        return this.mName;
    }

    public void setName(String str) {
        this.mName = str;
    }

    public void setType(Type type) {
        this.mType = type;
    }

    public String toString() {
        return "" + this.mName;
    }
}
