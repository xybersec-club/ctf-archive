package android.support.constraint.solver.widgets;

import android.support.constraint.solver.Cache;
import android.support.constraint.solver.SolverVariable;
import java.util.ArrayList;
import java.util.HashSet;
/* loaded from: classes.dex */
public class ConstraintAnchor {
    private static final boolean ALLOW_BINARY = false;
    public static final int ANY_GROUP = Integer.MAX_VALUE;
    public static final int APPLY_GROUP_RESULTS = -2;
    public static final int AUTO_CONSTRAINT_CREATOR = 2;
    public static final int SCOUT_CREATOR = 1;
    private static final int UNSET_GONE_MARGIN = -1;
    public static final int USER_CREATOR = 0;
    public static final boolean USE_CENTER_ANCHOR = false;
    final ConstraintWidget mOwner;
    SolverVariable mSolverVariable;
    ConstraintAnchor mTarget;
    final Type mType;
    public int mMargin = 0;
    int mGoneMargin = -1;
    private Strength mStrength = Strength.NONE;
    private ConnectionType mConnectionType = ConnectionType.RELAXED;
    private int mConnectionCreator = 0;
    int mGroup = Integer.MAX_VALUE;

    /* loaded from: classes.dex */
    public enum ConnectionType {
        RELAXED,
        STRICT
    }

    /* loaded from: classes.dex */
    public enum Strength {
        NONE,
        STRONG,
        WEAK
    }

    /* loaded from: classes.dex */
    public enum Type {
        NONE,
        LEFT,
        TOP,
        RIGHT,
        BOTTOM,
        BASELINE,
        CENTER,
        CENTER_X,
        CENTER_Y
    }

    public ConstraintAnchor(ConstraintWidget constraintWidget, Type type) {
        this.mOwner = constraintWidget;
        this.mType = type;
    }

    public SolverVariable getSolverVariable() {
        return this.mSolverVariable;
    }

    public void resetSolverVariable(Cache cache) {
        SolverVariable solverVariable = this.mSolverVariable;
        if (solverVariable == null) {
            this.mSolverVariable = new SolverVariable(SolverVariable.Type.UNRESTRICTED);
        } else {
            solverVariable.reset();
        }
    }

    public void setGroup(int i) {
        this.mGroup = i;
    }

    public int getGroup() {
        return this.mGroup;
    }

    public ConstraintWidget getOwner() {
        return this.mOwner;
    }

    public Type getType() {
        return this.mType;
    }

    public int getMargin() {
        ConstraintAnchor constraintAnchor;
        if (this.mOwner.getVisibility() == 8) {
            return 0;
        }
        if (this.mGoneMargin > -1 && (constraintAnchor = this.mTarget) != null && constraintAnchor.mOwner.getVisibility() == 8) {
            return this.mGoneMargin;
        }
        return this.mMargin;
    }

    public Strength getStrength() {
        return this.mStrength;
    }

    public ConstraintAnchor getTarget() {
        return this.mTarget;
    }

    public ConnectionType getConnectionType() {
        return this.mConnectionType;
    }

    public void setConnectionType(ConnectionType connectionType) {
        this.mConnectionType = connectionType;
    }

    public int getConnectionCreator() {
        return this.mConnectionCreator;
    }

    public void setConnectionCreator(int i) {
        this.mConnectionCreator = i;
    }

    public void reset() {
        this.mTarget = null;
        this.mMargin = 0;
        this.mGoneMargin = -1;
        this.mStrength = Strength.STRONG;
        this.mConnectionCreator = 0;
        this.mConnectionType = ConnectionType.RELAXED;
    }

    public boolean connect(ConstraintAnchor constraintAnchor, int i, Strength strength, int i2) {
        return connect(constraintAnchor, i, -1, strength, i2, false);
    }

    public boolean connect(ConstraintAnchor constraintAnchor, int i, int i2, Strength strength, int i3, boolean z) {
        if (constraintAnchor == null) {
            this.mTarget = null;
            this.mMargin = 0;
            this.mGoneMargin = -1;
            this.mStrength = Strength.NONE;
            this.mConnectionCreator = 2;
            return true;
        } else if (z || isValidConnection(constraintAnchor)) {
            this.mTarget = constraintAnchor;
            if (i > 0) {
                this.mMargin = i;
            } else {
                this.mMargin = 0;
            }
            this.mGoneMargin = i2;
            this.mStrength = strength;
            this.mConnectionCreator = i3;
            return true;
        } else {
            return false;
        }
    }

    public boolean connect(ConstraintAnchor constraintAnchor, int i, int i2) {
        return connect(constraintAnchor, i, -1, Strength.STRONG, i2, false);
    }

    public boolean connect(ConstraintAnchor constraintAnchor, int i) {
        return connect(constraintAnchor, i, -1, Strength.STRONG, 0, false);
    }

    public boolean isConnected() {
        return this.mTarget != null;
    }

    public boolean isValidConnection(ConstraintAnchor constraintAnchor) {
        if (constraintAnchor == null) {
            return false;
        }
        Type type = constraintAnchor.getType();
        Type type2 = this.mType;
        if (type == type2) {
            if (type2 == Type.CENTER) {
                return false;
            }
            return this.mType != Type.BASELINE || (constraintAnchor.getOwner().hasBaseline() && getOwner().hasBaseline());
        }
        int i = AnonymousClass1.$SwitchMap$android$support$constraint$solver$widgets$ConstraintAnchor$Type[this.mType.ordinal()];
        if (i == 1) {
            return (type == Type.BASELINE || type == Type.CENTER_X || type == Type.CENTER_Y) ? false : true;
        } else if (i == 2 || i == 3) {
            boolean z = type == Type.LEFT || type == Type.RIGHT;
            return constraintAnchor.getOwner() instanceof Guideline ? z || type == Type.CENTER_X : z;
        } else if (i == 4 || i == 5) {
            boolean z2 = type == Type.TOP || type == Type.BOTTOM;
            return constraintAnchor.getOwner() instanceof Guideline ? z2 || type == Type.CENTER_Y : z2;
        } else {
            return false;
        }
    }

    public boolean isSideAnchor() {
        int i = AnonymousClass1.$SwitchMap$android$support$constraint$solver$widgets$ConstraintAnchor$Type[this.mType.ordinal()];
        return i == 2 || i == 3 || i == 4 || i == 5;
    }

    public boolean isSimilarDimensionConnection(ConstraintAnchor constraintAnchor) {
        Type type = constraintAnchor.getType();
        if (type == this.mType) {
            return true;
        }
        switch (this.mType) {
            case CENTER:
                return type != Type.BASELINE;
            case LEFT:
            case RIGHT:
            case CENTER_X:
                return type == Type.LEFT || type == Type.RIGHT || type == Type.CENTER_X;
            case TOP:
            case BOTTOM:
            case CENTER_Y:
            case BASELINE:
                return type == Type.TOP || type == Type.BOTTOM || type == Type.CENTER_Y || type == Type.BASELINE;
            default:
                return false;
        }
    }

    public void setStrength(Strength strength) {
        if (isConnected()) {
            this.mStrength = strength;
        }
    }

    public void setMargin(int i) {
        if (isConnected()) {
            this.mMargin = i;
        }
    }

    public void setGoneMargin(int i) {
        if (isConnected()) {
            this.mGoneMargin = i;
        }
    }

    public boolean isVerticalAnchor() {
        int i = AnonymousClass1.$SwitchMap$android$support$constraint$solver$widgets$ConstraintAnchor$Type[this.mType.ordinal()];
        return (i == 1 || i == 2 || i == 3 || i == 6) ? false : true;
    }

    public String toString() {
        String str;
        HashSet<ConstraintAnchor> hashSet = new HashSet<>();
        StringBuilder sb = new StringBuilder();
        sb.append(this.mOwner.getDebugName());
        sb.append(":");
        sb.append(this.mType.toString());
        if (this.mTarget != null) {
            str = " connected to " + this.mTarget.toString(hashSet);
        } else {
            str = "";
        }
        sb.append(str);
        return sb.toString();
    }

    private String toString(HashSet<ConstraintAnchor> hashSet) {
        String str;
        if (hashSet.add(this)) {
            StringBuilder sb = new StringBuilder();
            sb.append(this.mOwner.getDebugName());
            sb.append(":");
            sb.append(this.mType.toString());
            if (this.mTarget != null) {
                str = " connected to " + this.mTarget.toString(hashSet);
            } else {
                str = "";
            }
            sb.append(str);
            return sb.toString();
        }
        return "<-";
    }

    public int getSnapPriorityLevel() {
        switch (this.mType) {
            case CENTER:
                return 3;
            case LEFT:
            case RIGHT:
                return 1;
            case TOP:
            case BOTTOM:
            case CENTER_X:
                return 0;
            case CENTER_Y:
                return 1;
            case BASELINE:
                return 2;
            default:
                return 0;
        }
    }

    public int getPriorityLevel() {
        switch (this.mType) {
            case CENTER:
            case LEFT:
            case RIGHT:
            case TOP:
            case BOTTOM:
                return 2;
            case CENTER_X:
            case CENTER_Y:
                return 0;
            case BASELINE:
                return 1;
            default:
                return 0;
        }
    }

    public boolean isSnapCompatibleWith(ConstraintAnchor constraintAnchor) {
        if (this.mType == Type.CENTER) {
            return false;
        }
        if (this.mType == constraintAnchor.getType()) {
            return true;
        }
        switch (this.mType) {
            case LEFT:
                int i = AnonymousClass1.$SwitchMap$android$support$constraint$solver$widgets$ConstraintAnchor$Type[constraintAnchor.getType().ordinal()];
                return i == 3 || i == 6;
            case RIGHT:
                int i2 = AnonymousClass1.$SwitchMap$android$support$constraint$solver$widgets$ConstraintAnchor$Type[constraintAnchor.getType().ordinal()];
                return i2 == 2 || i2 == 6;
            case TOP:
                int i3 = AnonymousClass1.$SwitchMap$android$support$constraint$solver$widgets$ConstraintAnchor$Type[constraintAnchor.getType().ordinal()];
                return i3 == 5 || i3 == 7;
            case BOTTOM:
                int i4 = AnonymousClass1.$SwitchMap$android$support$constraint$solver$widgets$ConstraintAnchor$Type[constraintAnchor.getType().ordinal()];
                return i4 == 4 || i4 == 7;
            case CENTER_X:
                int i5 = AnonymousClass1.$SwitchMap$android$support$constraint$solver$widgets$ConstraintAnchor$Type[constraintAnchor.getType().ordinal()];
                return i5 == 2 || i5 == 3;
            case CENTER_Y:
                int i6 = AnonymousClass1.$SwitchMap$android$support$constraint$solver$widgets$ConstraintAnchor$Type[constraintAnchor.getType().ordinal()];
                return i6 == 4 || i6 == 5;
            default:
                return false;
        }
    }

    public boolean isConnectionAllowed(ConstraintWidget constraintWidget, ConstraintAnchor constraintAnchor) {
        return isConnectionAllowed(constraintWidget);
    }

    public boolean isConnectionAllowed(ConstraintWidget constraintWidget) {
        if (isConnectionToMe(constraintWidget, new HashSet<>())) {
            return false;
        }
        ConstraintWidget parent = getOwner().getParent();
        return parent == constraintWidget || constraintWidget.getParent() == parent;
    }

    private boolean isConnectionToMe(ConstraintWidget constraintWidget, HashSet<ConstraintWidget> hashSet) {
        if (hashSet.contains(constraintWidget)) {
            return false;
        }
        hashSet.add(constraintWidget);
        if (constraintWidget == getOwner()) {
            return true;
        }
        ArrayList<ConstraintAnchor> anchors = constraintWidget.getAnchors();
        int size = anchors.size();
        for (int i = 0; i < size; i++) {
            ConstraintAnchor constraintAnchor = anchors.get(i);
            if (constraintAnchor.isSimilarDimensionConnection(this) && constraintAnchor.isConnected() && isConnectionToMe(constraintAnchor.getTarget().getOwner(), hashSet)) {
                return true;
            }
        }
        return false;
    }

    public final ConstraintAnchor getOpposite() {
        int i = AnonymousClass1.$SwitchMap$android$support$constraint$solver$widgets$ConstraintAnchor$Type[this.mType.ordinal()];
        if (i != 2) {
            if (i != 3) {
                if (i != 4) {
                    if (i != 5) {
                        return null;
                    }
                    return this.mOwner.mTop;
                }
                return this.mOwner.mBottom;
            }
            return this.mOwner.mLeft;
        }
        return this.mOwner.mRight;
    }
}
