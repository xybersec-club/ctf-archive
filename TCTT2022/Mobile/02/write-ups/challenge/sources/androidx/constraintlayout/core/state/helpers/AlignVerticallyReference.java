package androidx.constraintlayout.core.state.helpers;

import androidx.constraintlayout.core.state.ConstraintReference;
import androidx.constraintlayout.core.state.HelperReference;
import androidx.constraintlayout.core.state.State;
import java.util.Iterator;
/* loaded from: classes.dex */
public class AlignVerticallyReference extends HelperReference {
    private float mBias;

    public AlignVerticallyReference(State state) {
        super(state, State.Helper.ALIGN_VERTICALLY);
        this.mBias = 0.5f;
    }

    @Override // androidx.constraintlayout.core.state.HelperReference, androidx.constraintlayout.core.state.ConstraintReference, androidx.constraintlayout.core.state.Reference
    public void apply() {
        Iterator<Object> it = this.mReferences.iterator();
        while (it.hasNext()) {
            ConstraintReference constraints = this.mState.constraints(it.next());
            constraints.clearVertical();
            if (this.mTopToTop != null) {
                constraints.topToTop(this.mTopToTop);
            } else if (this.mTopToBottom != null) {
                constraints.topToBottom(this.mTopToBottom);
            } else {
                constraints.topToTop(State.PARENT);
            }
            if (this.mBottomToTop != null) {
                constraints.bottomToTop(this.mBottomToTop);
            } else if (this.mBottomToBottom != null) {
                constraints.bottomToBottom(this.mBottomToBottom);
            } else {
                constraints.bottomToBottom(State.PARENT);
            }
            float f = this.mBias;
            if (f != 0.5f) {
                constraints.verticalBias(f);
            }
        }
    }
}
