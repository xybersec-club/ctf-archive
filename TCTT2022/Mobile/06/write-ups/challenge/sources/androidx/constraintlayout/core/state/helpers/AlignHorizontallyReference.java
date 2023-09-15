package androidx.constraintlayout.core.state.helpers;

import androidx.constraintlayout.core.state.ConstraintReference;
import androidx.constraintlayout.core.state.HelperReference;
import androidx.constraintlayout.core.state.State;
import java.util.Iterator;
/* loaded from: classes.dex */
public class AlignHorizontallyReference extends HelperReference {
    private float mBias;

    public AlignHorizontallyReference(State state) {
        super(state, State.Helper.ALIGN_VERTICALLY);
        this.mBias = 0.5f;
    }

    @Override // androidx.constraintlayout.core.state.HelperReference, androidx.constraintlayout.core.state.ConstraintReference, androidx.constraintlayout.core.state.Reference
    public void apply() {
        Iterator<Object> it = this.mReferences.iterator();
        while (it.hasNext()) {
            ConstraintReference constraints = this.mState.constraints(it.next());
            constraints.clearHorizontal();
            if (this.mStartToStart != null) {
                constraints.startToStart(this.mStartToStart);
            } else if (this.mStartToEnd != null) {
                constraints.startToEnd(this.mStartToEnd);
            } else {
                constraints.startToStart(State.PARENT);
            }
            if (this.mEndToStart != null) {
                constraints.endToStart(this.mEndToStart);
            } else if (this.mEndToEnd != null) {
                constraints.endToEnd(this.mEndToEnd);
            } else {
                constraints.endToEnd(State.PARENT);
            }
            float f = this.mBias;
            if (f != 0.5f) {
                constraints.horizontalBias(f);
            }
        }
    }
}
