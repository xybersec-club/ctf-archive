package androidx.constraintlayout.motion.widget;

import androidx.constraintlayout.motion.widget.MotionScene;
import androidx.constraintlayout.widget.ConstraintSet;
/* loaded from: classes.dex */
public class TransitionBuilder {
    private static final String TAG = "TransitionBuilder";

    public static void validate(MotionLayout layout) {
        if (layout.mScene == null) {
            throw new RuntimeException("Invalid motion layout. Layout missing Motion Scene.");
        }
        MotionScene motionScene = layout.mScene;
        if (!motionScene.validateLayout(layout)) {
            throw new RuntimeException("MotionLayout doesn't have the right motion scene.");
        }
        if (motionScene.mCurrentTransition == null || motionScene.getDefinedTransitions().isEmpty()) {
            throw new RuntimeException("Invalid motion layout. Motion Scene doesn't have any transition.");
        }
    }

    public static MotionScene.Transition buildTransition(MotionScene scene, int transitionId, int startConstraintSetId, ConstraintSet startConstraintSet, int endConstraintSetId, ConstraintSet endConstraintSet) {
        MotionScene.Transition transition = new MotionScene.Transition(transitionId, scene, startConstraintSetId, endConstraintSetId);
        updateConstraintSetInMotionScene(scene, transition, startConstraintSet, endConstraintSet);
        return transition;
    }

    private static void updateConstraintSetInMotionScene(MotionScene scene, MotionScene.Transition transition, ConstraintSet startConstraintSet, ConstraintSet endConstraintSet) {
        int startConstraintSetId = transition.getStartConstraintSetId();
        int endConstraintSetId = transition.getEndConstraintSetId();
        scene.setConstraintSet(startConstraintSetId, startConstraintSet);
        scene.setConstraintSet(endConstraintSetId, endConstraintSet);
    }
}
