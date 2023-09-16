package android.support.transition;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
/* loaded from: classes.dex */
class AnimatorUtilsApi19 implements AnimatorUtilsImpl {
    @Override // android.support.transition.AnimatorUtilsImpl
    public void addPauseListener(Animator animator, AnimatorListenerAdapter animatorListenerAdapter) {
        animator.addPauseListener(animatorListenerAdapter);
    }

    @Override // android.support.transition.AnimatorUtilsImpl
    public void pause(Animator animator) {
        animator.pause();
    }

    @Override // android.support.transition.AnimatorUtilsImpl
    public void resume(Animator animator) {
        animator.resume();
    }
}
